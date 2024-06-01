package api

import (
	"github.com/gin-gonic/gin"
	"license/finalshell/service"
	"net/http"
	"strings"
)

// Controller 定义控制器结构体
type Controller struct {
}

// NewController 创建新的控制器实例
func NewController() *Controller {
	return &Controller{}
}

// GenerateLicense 生成license的处理函数
func (controller *Controller) GenerateLicense(c *gin.Context) {
	machineCode := c.PostForm("machineCode")
	if strings.TrimSpace(machineCode) == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "machineCode不能为空"})
		return
	}
	licenses := service.GenerateLicense(machineCode)
	c.JSON(http.StatusOK, licenses)
}
