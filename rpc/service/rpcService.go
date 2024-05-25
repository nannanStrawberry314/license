package service

import "github.com/gin-gonic/gin"

// RpcService 定义了与远程过程调用相关的方法
type RpcService interface {
	Ping(ctx *gin.Context, machineId, salt string)
	ObtainTicket(ctx *gin.Context, username, hostName, machineId, salt string)
	ReleaseTicket(ctx *gin.Context, machineId, salt string)
}
