package controller

import (
	"github.com/gin-gonic/gin"
	"license/rpc/service"
	"license/rpc/service/impl"
)

type RpcController struct {
	JrebelRpcService    service.RpcService
	JetbrainsRpcService service.RpcService
}

func NewRpcController() *RpcController {
	return &RpcController{
		JrebelRpcService:    &impl.JrebelRpcService{},
		JetbrainsRpcService: &impl.JetbrainsRpcService{},
	}
}

// Ping 处理 /rpc/ping.action 请求
func (c *RpcController) Ping(ctx *gin.Context) {
	machineId := ctx.DefaultQuery("machineId", "")
	salt := ctx.Query("salt")
	isJetbrains := machineId != ""

	if isJetbrains {
		c.JetbrainsRpcService.Ping(ctx, machineId, salt)
	} else {
		c.JrebelRpcService.Ping(ctx, machineId, salt)
	}

}

// ObtainTicket 处理 /rpc/obtainTicket.action 请求
func (c *RpcController) ObtainTicket(ctx *gin.Context) {
	username := ctx.DefaultQuery("userName", "")
	if len(username) == 0 {
		username = ctx.DefaultQuery("username", "")
	}
	hostName := ctx.DefaultQuery("hostName", "")
	machineId := ctx.DefaultQuery("machineId", "")
	salt := ctx.Query("salt")
	isJetbrains := machineId != ""

	if isJetbrains {
		c.JetbrainsRpcService.ObtainTicket(ctx, username, hostName, machineId, salt)
	} else {
		c.JrebelRpcService.ObtainTicket(ctx, username, hostName, machineId, salt)
	}
}

// ReleaseTicket 处理 /rpc/releaseTicket.action 请求
func (c *RpcController) ReleaseTicket(ctx *gin.Context) {
	machineId := ctx.DefaultQuery("machineId", "")
	salt := ctx.Query("salt")
	isJetbrains := machineId != ""

	if isJetbrains {
		c.JetbrainsRpcService.ReleaseTicket(ctx, machineId, salt)
	} else {
		c.JrebelRpcService.ReleaseTicket(ctx, machineId, salt)
	}
}
