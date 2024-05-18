package router

import (
	"github.com/gin-gonic/gin"
	"license/finalshell"
)

func SetupRouter(r *gin.Engine) {
	// final-shell
	finalShellService := finalshell.ServiceImpl{}
	finalShellController := finalshell.NewController(finalShellService)

	finalShellGroup := r.Group("/final-shell")
	{
		finalShellGroup.POST("/generateLicense", finalShellController.GenerateLicense)
	}
}
