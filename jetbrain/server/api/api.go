package api

import (
	"github.com/gin-gonic/gin"
	"license/jetbrain/util"
)

// Controller 定义控制器结构体
type LicenseServerController struct {
}

// NewController 创建新的控制器实例
func NewLicenseServerController() *LicenseServerController {
	return &LicenseServerController{}
}

// LicenseServerRule 生成license的处理函数
func (controller *LicenseServerController) LicenseServerRule(c *gin.Context) {
	power1 := util.GeneratePowerResult(util.Fake.CodeCert, util.Fake.CodeRootCert)
	power2 := util.GeneratePowerResult(util.Fake.ServerCert, util.Fake.ServerRootCert)
	// [Result]
	// ; Lemon active by code
	// power1
	// [Result]
	// ; Lemon active by server
	// power2
	// 构造返回结果
	format := "[Result]\n; Lemon active by code\n%s\n[Result]\n; Lemon active by server\n%s\n"
	// result := fmt.Sprintf(format, power1, power2)
	c.String(200, format, power1, power2)

}
