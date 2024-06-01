package api

import (
	"github.com/gin-gonic/gin"
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

// FetchProduceLatest 用于获取最新的激活码
func (controller *Controller) FetchProduceLatest(c *gin.Context) {
	productService := service.NewProductService()
	err := productService.FetchLatest()
	if err != nil {
		logger.Error("Failed to fetch latest product:", err)
		return
	}
}

// FetchPluginLatest 用于获取最新的插件
func (controller *Controller) FetchPluginLatest(c *gin.Context) {
	pluginService := service.NewPluginService()
	err := pluginService.FetchLatest()
	if err != nil {
		logger.Error("Failed to fetch latest plugin:", err)
		return
	}
}

// Generate 用于生成激活码
func (controller *Controller) Generate(c *gin.Context) {
	licenseeName := c.Query("licenseeName")
	effectiveDate := c.Query("effectiveDate")
	codes := c.Query("codes")

	// 将字符串分割成数组
	var codesArray []string
	if len(codes) > 0 {
		codesArray = strings.Split(codes, ",")
	}

	// 生成license
	activationCode, err := service.GenerateLicense(licenseeName, effectiveDate, codesArray)
	if err != nil {
		logger.Error("Failed to generate license:", err)
		c.String(500, "Failed to generate license")
	}
	// 生成powerConf
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

	c.String(200, result.String())
}
