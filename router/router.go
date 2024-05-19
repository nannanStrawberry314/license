package router

import (
	"github.com/gin-gonic/gin"
	"license/finalshell"
	"license/gitlab"
)

func SetupRouter(r *gin.Engine) {
	// final-shell
	finalShellApi := finalshell.NewController()
	finalShellGroup := r.Group("/final-shell")
	{
		finalShellGroup.POST("/generateLicense", finalShellApi.GenerateLicense)
	}

	// gitlab
	gitlabApi := gitlab.NewController()
	gitlabGroup := r.Group("/gitlab")
	{
		gitlabGroup.POST("/generate", gitlabApi.Generate)
	}

}
