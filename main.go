package main

import (
	"github.com/gin-gonic/gin"
	"license/router"
	"log"
)

func main() {
	// 初始化数据库
	// config.SetupDatabase()

	// 设置 GIN 路由
	r := gin.Default()
	router.SetupRouter(r)

	// 启动服务器
	err := r.Run("0.0.0.0:13000")
	if err != nil {
		log.Printf("启动服务器失败: %v", err)
		return
	} else {
		log.Println("服务器已启动 http://0.0.0.0:13000")
	}
}
