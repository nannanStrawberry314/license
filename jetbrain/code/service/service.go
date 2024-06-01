package service

import (
	"encoding/json"
	"fmt"
	"io"
	"license/jetbrain/code/entity"
	"license/jetbrain/code/mapper"
	"license/logger"
	"log"
	"math/rand"
	"net/http"
	"time"
)

type ProductService interface {
	FetchLatest() error
}

type ProductServiceImpl struct {
	Mapper mapper.ProductMapper
}

func NewProductService() ProductService {
	return &ProductServiceImpl{
		Mapper: &mapper.GormProductMapper{},
	}
}

func (service *ProductServiceImpl) FetchLatest() error {
	client := &http.Client{}
	req, err := http.NewRequest("GET", "https://data.services.jetbrains.com/products", nil)
	if err != nil {
		logger.Error("Error creating request:", err)
		return err
	}

	req.Header.Set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
	resp, err := client.Do(req)
	if err != nil {
		logger.Error("Error executing request:", err)
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		logger.Error(fmt.Sprintf("Failed to fetch product information with status code: %d", resp.StatusCode), nil)
		return fmt.Errorf("failed to fetch product information with status code: %d", resp.StatusCode)
	}

	var products []map[string]interface{}
	err = json.NewDecoder(resp.Body).Decode(&products)
	if err != nil {
		logger.Error("Error decoding JSON:", err)
		return err
	}

	productList := make([]*entity.ProductEntity, 0, len(products))
	for i, product := range products {
		logger.Info(fmt.Sprintf("待处理产品总数:%d,当前正在处理第:%d个", len(products), i+1))

		// 将 product map 转换为 JSON 字符串
		productJSON, err := json.Marshal(product)
		if err != nil {
			log.Printf("Error marshaling product to JSON: %v", err)
			continue
		}

		productEntity := &entity.ProductEntity{
			ProductDetail: string(productJSON),
			ProductCode:   fmt.Sprint(product["code"]),
			ProductName:   fmt.Sprint(product["name"]),
		}
		productList = append(productList, productEntity)
		// Simulate pause
		time.Sleep(time.Duration(100+rand.Intn(400)) * time.Millisecond)
	}

	if len(productList) > 0 {
		if err := service.Mapper.Truncate(); err != nil {
			return err
		}
		if err := service.Mapper.SaveBatch(productList); err != nil {
			return err
		}
	}

	return nil
}

const (
	paidPluginsURL     = "https://plugins.jetbrains.com/api/searchPlugins?excludeTags=theme&max=500&offset=0&orderBy=downloads&pricingModels=PAID"
	freemiumPluginsURL = "https://plugins.jetbrains.com/api/searchPlugins?excludeTags=theme&max=500&offset=0&orderBy=downloads&pricingModels=FREEMIUM"
	pluginDetailURL    = "https://plugins.jetbrains.com/api/plugins/"
	userAgent          = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"
)

type PluginService interface {
	FetchLatest() error
}

type PluginServiceImpl struct {
	Mapper mapper.PluginMapper
}

func NewPluginService() ProductService {
	return &PluginServiceImpl{
		Mapper: &mapper.GormPluginMapper{},
	}
}

func (s *PluginServiceImpl) fetchPlugins(url string) ([]*entity.PluginEntity, error) {
	client := &http.Client{}
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		logger.Error("Error creating request:", err)
		return nil, err
	}

	req.Header.Set("User-Agent", userAgent)
	resp, err := client.Do(req)
	if err != nil {
		logger.Error("Error on request:", err)
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		logger.Error(fmt.Sprintf("Failed to fetch plugins, status code: %d", resp.StatusCode), nil)
		return nil, fmt.Errorf("failed to fetch plugins, status code: %d", resp.StatusCode)
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		logger.Error("Error reading response body:", err)
		return nil, err
	}

	var data struct {
		Plugins []struct {
			ID   uint64 `json:"id"`
			Name string `json:"name"`
		}
	}

	err = json.Unmarshal(body, &data)
	if err != nil {
		logger.Error("Error unmarshaling JSON:", err)
		return nil, err
	}

	plugins := make([]*entity.PluginEntity, 0, len(data.Plugins))
	for index, p := range data.Plugins {
		logger.Info(fmt.Sprintf("待处理插件总数:%d,当前正在处理第:%d个,插件Id:%d", len(data.Plugins), index+1, p.ID))
		detailResp, err := client.Get(fmt.Sprintf("%s%d", pluginDetailURL, p.ID))
		if err != nil {
			logger.Error(fmt.Sprintf("Error fetching plugin detail for ID %d: %v", p.ID), err)
			continue
		}
		defer detailResp.Body.Close()

		if detailResp.StatusCode != http.StatusOK {
			logger.Error(fmt.Sprintf("Failed to fetch plugin detail for ID %d, status: %d", p.ID, detailResp.StatusCode), nil)
			continue
		}

		detailBody, err := io.ReadAll(detailResp.Body)
		if err != nil {
			logger.Error("Error reading plugin detail response:", err)
			continue
		}

		var detail struct {
			Name         string `json:"name"`
			PurchaseInfo struct {
				ProductCode string `json:"productCode"`
			} `json:"purchaseInfo"`
		}

		json.Unmarshal(detailBody, &detail)

		plugins = append(plugins, &entity.PluginEntity{
			PluginID:        p.ID,
			PluginName:      detail.Name,
			PluginCode:      detail.PurchaseInfo.ProductCode,
			PluginApiDetail: string(detailBody),
		})

		// Simulate pause
		time.Sleep(time.Duration(100+rand.Intn(400)) * time.Millisecond)
	}

	return plugins, nil
}

func (s *PluginServiceImpl) FetchLatest() error {
	// 首先获取付费插件
	paidPlugins, err := s.fetchPlugins(paidPluginsURL)
	if err != nil {
		log.Printf("Error fetching paid plugins: %v", err)
		return err
	}

	// 接着获取免费增值插件
	freemiumPlugins, err := s.fetchPlugins(freemiumPluginsURL)
	if err != nil {
		log.Printf("Error fetching freemium plugins: %v", err)
		return err
	}

	// 合并两次结果
	allPlugins := append(paidPlugins, freemiumPlugins...)

	// 如果获取到的插件列表不为空，则清空表并批量插入
	if len(allPlugins) > 0 {
		if err := s.Mapper.Truncate(); err != nil {
			logger.Error("Error truncating plugin table:", err)
			return err
		}
		if err := s.Mapper.SaveBatch(allPlugins); err != nil {
			logger.Error("Error saving plugin batch:", err)
			return err
		}
	}

	return nil
}
