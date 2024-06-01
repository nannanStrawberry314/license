package api

import (
	"github.com/gin-gonic/gin"
	"license/jetbrain/code/helper"
	"license/jetbrain/code/service"
	"license/jetbrain/util"
	"license/logger"
	"strings"
)

// Controller 定义控制器结构体
type Controller struct {
}

// NewController 创建新的控制器实例
func NewController() *Controller {
	return &Controller{}
}

// FetchLatest 用于获取最新的激活码
func (controller *Controller) FetchProduceLatest(c *gin.Context) {
	productService := service.NewProductService()
	err := productService.FetchLatest()
	if err != nil {
		logger.Error("Failed to fetch latest product:", err)
		return
	}
}

func (controller *Controller) FetchPluginLatest(c *gin.Context) {
	pluginService := service.NewPluginService()
	err := pluginService.FetchLatest()
	if err != nil {
		logger.Error("Failed to fetch latest plugin:", err)
		return
	}
}

func (controller *Controller) Generate(c *gin.Context) {
	// 提取licensesName effectiveDate codes
	licenseeName := c.Query("licenseeName")
	effectiveDate := c.Query("effectiveDate")
	codes := c.Query("codes")

	// 将字符串分割成数组
	var codesArray []string
	if len(codes) > 0 {
		codesArray = strings.Split(codes, ",")
	}

	// 生成license
	activationCode, err := helper.GenerateLicense(licenseeName, effectiveDate, codesArray)
	if err != nil {
		logger.Error("Failed to generate license:", err)
		c.Data(500, "text/plain", []byte("Failed to generate license"))
	}
	// 生成powerconf
	// utlGeneratePowerResult(license, c)
	powerConfRule := util.GeneratePowerResult(util.Fake.CodeCert, util.Fake.CodeRootCert)

	// 组装数据
	var result strings.Builder
	result.WriteString("================== power.conf ==================")
	result.WriteString("\n[Result]")
	result.WriteString("\n; Lemon active by code\n")
	result.WriteString(powerConfRule)
	result.WriteString("\n================== power.conf ==================")
	result.WriteString("\n================== activation code ==================\n")
	result.WriteString(activationCode)
	result.WriteString("\n================== activation code ==================\n")

	c.Data(200, "text/plain", []byte(result.String()))
}
