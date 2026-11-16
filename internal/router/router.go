package router

import (
	"license/internal/finalshell"
	"license/internal/gitlab"
	"license/internal/jetbrains"
	"license/internal/jrebel"
	"license/internal/mobaxterm"
	"license/internal/rpc"
	"license/internal/server"

	"github.com/gin-gonic/gin"
)

// SetupExternalRoutes 注册外部工具需要的根路径 API（前端页面已改为 /page/* 前缀）
// 包括：/rpc/*, /agent/*, /server/*, /jrebel/*
func SetupExternalRoutes(r *gin.RouterGroup) {
	// server
	serverApi := server.NewServerController()
	serverGroup := r.Group("/server")
	{
		serverGroup.GET("/status", serverApi.GetStatus)
		serverGroup.GET("/version", serverApi.GetVersion)
	}

	// rpc - JetBrains IDE 激活接口
	rpcApi := rpc.NewController()
	rpcGroup := r.Group("/rpc")
	{
		rpcGroup.GET("/ping.action", rpcApi.Ping)
		rpcGroup.GET("/obtainTicket.action", rpcApi.ObtainTicket)
		rpcGroup.GET("/prolongTicket.action", rpcApi.ProlongTicket)
		rpcGroup.GET("/releaseTicket.action", rpcApi.ReleaseTicket)
	}

	// jrebel - JRebel 激活接口
	jrebelLeasesApi, _ := jrebel.NewLeasesController()
	jrebelIndexApi := jrebel.NewIndexController()
	jrebelGroup := r.Group("/jrebel")
	{
		jrebelGroup.GET("/", jrebelIndexApi.IndexHandler)
		jrebelGroup.DELETE("/leases/1", jrebelLeasesApi.Leases1Handler)
		jrebelGroup.POST("/leases", jrebelLeasesApi.LeasesHandler)
		jrebelGroup.POST("/validate-connection", jrebelLeasesApi.ValidateHandler)
		jrebelGroup.POST("/features", jrebelLeasesApi.ValidateHandler)
		jrebelGroup.GET("/features", jrebelLeasesApi.ValidateHandler)
		jrebelGroup.GET("/performance-stats", jrebelLeasesApi.GetPerformanceStats)
		jrebelGroup.POST("/clear-cache", jrebelLeasesApi.ClearCache)
		jrebelGroup.GET("/health", jrebelLeasesApi.HealthCheck)
		jrebelGroup.POST("/force-gc", jrebelLeasesApi.ForceGC)
		jrebelGroup.POST("/leases/1", func(c *gin.Context) {
			c.Status(405)
		})
	}

	// agent - JRebel agent 接口（兼容路径）
	agentGroup := r.Group("/agent")
	{
		agentGroup.DELETE("/leases/1", jrebelLeasesApi.Leases1Handler)
		agentGroup.POST("/leases", jrebelLeasesApi.LeasesHandler)
		agentGroup.POST("/validate-connection", jrebelLeasesApi.ValidateHandler)
		agentGroup.POST("/features", jrebelLeasesApi.ValidateHandler)
		agentGroup.GET("/features", jrebelLeasesApi.ValidateHandler)
		agentGroup.POST("/leases/1", func(c *gin.Context) {
			c.Status(405)
		})
	}
}

// SetupRouter 注册所有 API 路由（用于 /api/* 前缀）
func SetupRouter(r *gin.RouterGroup) {
	// 注册外部工具路由（包含 jrebel）
	SetupExternalRoutes(r)

	// final-shell
	finalShellApi := finalshell.NewController()
	finalShellGroup := r.Group("/final-shell")
	{
		finalShellGroup.POST("/generateLicense", finalShellApi.GenerateLicense)
		finalShellGroup.GET("/stats", finalShellApi.GetStats)
		finalShellGroup.POST("/clear-cache", finalShellApi.ClearCache)
	}

	// gitlab
	gitlabApi := gitlab.NewController()
	gitlabGroup := r.Group("/gitlab")
	{
		gitlabGroup.POST("/generate", gitlabApi.Generate)
	}

	// mobaxterm
	mobaxtermApi := mobaxterm.NewMobaXtermController()
	mobaxtermGroup := r.Group("/mobaxterm")
	{
		mobaxtermGroup.POST("/generate", mobaxtermApi.GenerateLicense)
		mobaxtermGroup.GET("/versions", func(c *gin.Context) {
			// Add no-cache headers
			c.Header("Cache-Control", "no-cache, no-store, must-revalidate")
			c.Header("Pragma", "no-cache")
			c.Header("Expires", "0")

			mobaxtermApi.FetchVersions(c)
		})
		// 优化版本专有端点
		mobaxtermGroup.GET("/performance-stats", mobaxtermApi.GetPerformanceStats)
		mobaxtermGroup.POST("/clear-cache", mobaxtermApi.ClearCache)
		mobaxtermGroup.GET("/health", mobaxtermApi.HealthCheck)
		mobaxtermGroup.POST("/force-gc", mobaxtermApi.ForceGC)
	}

	// jetbrains
	jetbrainsServerApi := jetbrains.NewServerController()
	jetbrainsCodeApi := jetbrains.NewController()

	jetbrainsGroup := r.Group("/jetbrains")
	{
		// License generation — POST only to keep licenseeName out of URL/access logs
		jetbrainsGroup.POST("/generate", jetbrainsCodeApi.GenerateLicense)

		// Power config
		jetbrainsGroup.GET("/licenseServerRule", jetbrainsServerApi.LicenseServerRule)
		jetbrainsGroup.GET("/powerConfig", jetbrainsCodeApi.GetPowerConfig)

		// Product and plugin management
		jetbrainsGroup.GET("/products", jetbrainsCodeApi.GetProducts)
		jetbrainsGroup.GET("/products/fetchLatest", jetbrainsCodeApi.FetchProductsLatest)
		jetbrainsGroup.GET("/plugins", jetbrainsCodeApi.GetPlugins)
		jetbrainsGroup.GET("/plugins/fetchLatest", jetbrainsCodeApi.FetchPluginsLatest)

		// Health check
		jetbrainsGroup.GET("/health", jetbrainsCodeApi.HealthCheck)

		// Backward compatibility
		jetbrainsGroup.GET("/product/fetchLatest", jetbrainsCodeApi.FetchProductsLatest)
		jetbrainsGroup.GET("/plugin/fetchLatest", jetbrainsCodeApi.FetchPluginsLatest)
	}
}
