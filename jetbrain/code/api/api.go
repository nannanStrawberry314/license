package api

import (
	"github.com/gin-gonic/gin"
	"license/jetbrain/code/service"
	"license/logger"
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
