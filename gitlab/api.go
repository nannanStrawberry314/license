package gitlab

import (
	"github.com/gin-gonic/gin"
)

// Controller 定义控制器结构体
type Controller struct {
}

// NewController 创建新的控制器实例
func NewController() *Controller {
	return &Controller{}
}

// Generate 生成license的处理函数
func (controller *Controller) Generate(ctx *gin.Context) {

	Name := ctx.PostForm("Name")
	Email := ctx.PostForm("Email")
	Company := ctx.PostForm("Company")
	var license = LicenseInfo{
		Name:    Name,
		Email:   Email,
		Company: Company,
	}
	// 生成license
	Generate(ctx, license)
}
