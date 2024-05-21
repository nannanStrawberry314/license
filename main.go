package main

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"license/config"
	"license/logger"
	"license/router"
)

func main() {
	// 初始化全局配置
	config.InitConfig()

	// 初始化数据库
	config.SetupDatabase()

	// 设置 GIN 路由
	gin.SetMode(gin.ReleaseMode)
	r := gin.Default()
	router.SetupRouter(r)

	server := fmt.Sprintf("%s:%d", config.GetConfig().HttpHost, config.GetConfig().HttpPort)
	logger.Sys(fmt.Sprintf("服务启动中, http://%s", server))
	// 启动服务器
	err := r.Run(server)
	if err != nil {
		logger.Error("服务器启动失败", err)
		return
	}
}
