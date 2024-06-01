package api

import (
	"github.com/gin-gonic/gin"
	"license/jetbrain/util"
	"strings"
)

// LicenseServerController 定义控制器结构体
type LicenseServerController struct {
}

// NewLicenseServerController 创建新的控制器实例
func NewLicenseServerController() *LicenseServerController {
	return &LicenseServerController{}
}

// LicenseServerRule 生成license的处理函数
func (controller *LicenseServerController) LicenseServerRule(c *gin.Context) {
	codePower := util.GeneratePowerResult(util.Fake.CodeCert, util.Fake.CodeRootCert)
	serverPower := util.GeneratePowerResult(util.Fake.ServerCert, util.Fake.ServerRootCert)

	// 构造返回结果
	var result strings.Builder
	result.WriteString("[Result]\n; Lemon active by code\n")
	result.WriteString(codePower)
	result.WriteString("\n[Result]\n; Lemon active by server\n")
	result.WriteString(serverPower)
	result.WriteString("\n")
	c.String(200, result.String())

}
