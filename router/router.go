package router

import (
	"github.com/gin-gonic/gin"
	"license/finalshell"
	"license/gitlab"
	"license/jetbrain/server"
	"license/jrebel"
	"license/mobaxterm"
	"license/rpc/controller"
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

	// rpc
	rpcApi := controller.NewRpcController()
	rpcGroup := r.Group("/rpc")
	{
		rpcGroup.GET("/ping.action", rpcApi.Ping)
		rpcGroup.GET("/obtainTicket.action", rpcApi.ObtainTicket)
		rpcGroup.GET("/releaseTicket.action", rpcApi.ReleaseTicket)
	}

	// jrebel
	jrebelLeasesApi := jrebel.NewLeasesController()
	jrebelIndexApi := jrebel.NewIndexController()
	jrebelGroup := r.Group("/jrebel")
	{
		jrebelGroup.GET("/", jrebelIndexApi.IndexHandler)
		jrebelGroup.DELETE("/leases/1", jrebelLeasesApi.Leases1Handler)
		jrebelGroup.POST("/leases", jrebelLeasesApi.LeasesHandler)
		jrebelGroup.POST("/validate-connection", jrebelLeasesApi.ValidateHandler)
	}
	jrebelAgentGroup := r.Group("/agent")
	{
		jrebelAgentGroup.DELETE("/leases/1", jrebelLeasesApi.Leases1Handler)
		jrebelAgentGroup.POST("/leases", jrebelLeasesApi.LeasesHandler)
		jrebelAgentGroup.POST("/validate-connection", jrebelLeasesApi.ValidateHandler)
	}

	// mobaxterm
	mobaxtermApi := mobaxterm.NewMobaXtermController()
	mobaxtermGroup := r.Group("/mobaxterm")
	{
		mobaxtermGroup.POST("/generate", mobaxtermApi.GenerateLicense)
	}

	// jetbrains
	jetbrainsApi := server.NewLicenseServerController()
	jetbrainsGroup := r.Group("/jetbrains")
	{
		jetbrainsGroup.GET("/licenseServerRule", jetbrainsApi.LicenseServerRule)
	}
}
