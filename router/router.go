package router

import (
	"github.com/gin-gonic/gin"
	finalshell "license/finalshell/api"
	gitlab "license/gitlab/api"
	jetbrainCode "license/jetbrain/code/api"
	jetbrainServer "license/jetbrain/server/api"
	jrebel "license/jrebel/api"
	mobaxterm "license/mobaxterm/api"
	rpc "license/rpc/controller"
)

func SetupRouter(r *gin.Engine) {
	serverGroup := r.Group("/server")
	{
		serverGroup.GET("/status", func(c *gin.Context) {
			c.JSON(200, gin.H{
				"status": true,
			})
		})
	}

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
	rpcApi := rpc.NewRpcController()
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
		jrebelGroup.POST("/leases/1", func(c *gin.Context) {
			c.Status(405)
		})
		jrebelGroup.POST("/leases", jrebelLeasesApi.LeasesHandler)
		jrebelGroup.POST("/validate-connection", jrebelLeasesApi.ValidateHandler)
		jrebelGroup.POST("/features", jrebelLeasesApi.ValidateHandler)
		jrebelGroup.GET("/features", jrebelLeasesApi.ValidateHandler)
	}
	jrebelAgentGroup := r.Group("/agent")
	{
		jrebelAgentGroup.DELETE("/leases/1", jrebelLeasesApi.Leases1Handler)
		jrebelAgentGroup.POST("/leases/1", func(c *gin.Context) {
			c.Status(405)
		})
		jrebelAgentGroup.POST("/leases", jrebelLeasesApi.LeasesHandler)
		jrebelAgentGroup.POST("/validate-connection", jrebelLeasesApi.ValidateHandler)
		jrebelAgentGroup.POST("/features", jrebelLeasesApi.ValidateHandler)
		jrebelAgentGroup.GET("/features", jrebelLeasesApi.ValidateHandler)
	}

	// mobaxterm
	mobaxtermApi := mobaxterm.NewMobaXtermController()
	mobaxtermGroup := r.Group("/mobaxterm")
	{
		mobaxtermGroup.POST("/generate", mobaxtermApi.GenerateLicense)
	}

	// jetbrains
	jetbrainsServerApi := jetbrainServer.NewLicenseServerController()
	jetbrainsCodeApi := jetbrainCode.NewController()

	jetbrainsGroup := r.Group("/jetbrains")
	{
		jetbrainsGroup.GET("/licenseServerRule", jetbrainsServerApi.LicenseServerRule)
		jetbrainsGroup.GET("/product/fetchLatest", jetbrainsCodeApi.FetchProduceLatest)
		jetbrainsGroup.GET("/plugin/fetchLatest", jetbrainsCodeApi.FetchPluginLatest)
		jetbrainsGroup.GET("/generate", jetbrainsCodeApi.Generate)
	}
}
