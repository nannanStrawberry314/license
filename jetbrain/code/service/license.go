package service

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha1"
	"encoding/base64"
	"encoding/json"
	"license/jetbrain/code/dto"
	"license/jetbrain/code/mapper"
	"license/jetbrain/util"
	"license/logger"
	"log"
	"time"
)

var productCodes = []string{"II", "PS", "AC", "DB", "RM", "WS", "RD", "CL", "PC", "GO", "DS", "DC", "DPN", "DM"}

func GenerateLicense(licenseeName, effectiveDate string, codes []string) (string, error) {

	if effectiveDate == "" {
		// 获取当前时间
		now := time.Now()

		// 当天的23:59:59
		endOfToday := time.Date(now.Year(), now.Month(), now.Day(), 23, 59, 59, 0, now.Location())

		// 三年后的同一天
		threeYearsLater := endOfToday.AddDate(3, 0, 0)
		// 格式化日期
		effectiveDate = threeYearsLater.Format("2006-01-02 15:04:05")
	}

	if len(codes) == 0 {
		productMapper := mapper.GormProductMapper{}
		products, err := productMapper.List()
		if err != nil {
			return "", err
		}
		for _, product := range products {
			codes = append(codes, product.ProductCode)
		}

		pluginMapper := mapper.GormPluginMapper{}
		plugins, err := pluginMapper.List()
		if err != nil {
			return "", err
		}
		for _, plugin := range plugins {
			codes = append(codes, plugin.PluginCode)
		}

		// productCodes
		for _, item := range productCodes {
			codes = append(codes, item)
		}
	}

	licenseID, err := randomString(10)
	if err != nil {
		logger.Error("Failed to generate license ID:", err)
		return "", err
	}
	var products []dto.Product
	for _, code := range codes {
		products = append(products, dto.Product{
			Code:         code,
			FallbackDate: effectiveDate,
			PaidUpTo:     effectiveDate,
			Extended:     true,
		})
	}
	licensePart := dto.LicensePart{
		LicenseID:         licenseID,
		LicenseeName:      licenseeName,
		Products:          products,
		AssigneeName:      "",
		Metadata:          "0120231110PSAA003008",
		Hash:              "51149839/0:-1370131430",
		GracePeriodDays:   7,
		AutoProlongated:   true,
		IsAutoProlongated: true,
		Trial:             false,
		AiAllowed:         true,
	}

	licensePartJSON, err := json.Marshal(licensePart)
	if err != nil {
		return "", err
	}
	licensePartBase64 := base64.StdEncoding.EncodeToString(licensePartJSON)
	signatureBase64 := signWithRSA(util.Fake.PrivateKey, licensePartJSON)
	cert := util.Fake.CodeCert
	certBase64 := base64.StdEncoding.EncodeToString(cert.Raw)
	println(signatureBase64)
	return licenseID + "-" + licensePartBase64 + "-" + signatureBase64 + "-" + certBase64, nil
}

func signWithRSA(privateKey *rsa.PrivateKey, data []byte) string {
	// 对数据进行SHA1哈希
	hash := sha1.New()
	_, err := hash.Write(data)
	if err != nil {
		log.Fatalf("哈希计算失败: %v", err)
	}
	hashed := hash.Sum(nil)
	sign, err := rsa.SignPKCS1v15(rand.Reader, privateKey, crypto.SHA1, hashed)
	if err != nil {
		log.Fatalf("Failed to sign: %v", err)
	}
	signature := base64.StdEncoding.EncodeToString(sign)
	return signature
}

// randomString 生成长度为 n 的随机字符串，包含大写字母和数字
func randomString(n int) (string, error) {
	const charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
	byteArray := make([]byte, n) // 创建一个长度为 n 的字节切片

	// 读取随机字节填充byteArray
	if _, err := rand.Read(byteArray); err != nil {
		return "", err // 返回错误信息
	}

	// 将每个字节映射到 charset 中，保证它是大写字母或数字
	for i, b := range byteArray {
		byteArray[i] = charset[b%byte(len(charset))] // 使用模运算将字节转换为charset中的字符
	}

	return string(byteArray), nil // 转换为字符串并返回
}
