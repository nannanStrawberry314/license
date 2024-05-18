package main

import (
	"github.com/gin-gonic/gin"
	"license/router"
)

func main() {
	// 初始化数据库
	// config.SetupDatabase()

	// 设置 GIN 路由
	r := gin.Default()
	router.SetupRouter(r)

	// 启动服务器
	r.Run(":13000")
}
