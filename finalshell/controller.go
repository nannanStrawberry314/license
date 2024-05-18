package finalshell

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"strings"
)

// Controller 定义控制器结构体
type Controller struct {
	service Service
}

// NewController 创建新的控制器实例
func NewController(service Service) *Controller {
	return &Controller{service: service}
}

// GenerateLicense 生成license的处理函数
func (controller *Controller) GenerateLicense(c *gin.Context) {
	machineCode := c.PostForm("machineCode")
	if strings.TrimSpace(machineCode) == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "machineCode不能为空"})
		return
	}
	licenses := controller.service.GenerateLicense(machineCode)
	c.JSON(http.StatusOK, licenses)
}
