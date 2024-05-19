package controller

import (
	"github.com/gin-gonic/gin"
	"license/rpc/service"
	"license/rpc/service/impl"
	"net/http"
)

type RpcController struct {
	JrebelRpcService    service.RpcService
	JetbrainsRpcService service.RpcService
}

func NewRpcController() *RpcController {
	return &RpcController{
		JrebelRpcService: &impl.Service{},
		// JetbrainsRpcService: jetbrainsService,
	}
}

// Ping 处理 /rpc/ping.action 请求
func (c *RpcController) Ping(ctx *gin.Context) {
	machineId := ctx.DefaultQuery("machineId", "")
	salt := ctx.Query("salt")
	isJetbrains := machineId != ""

	var result string
	if isJetbrains {
		result = c.JetbrainsRpcService.Ping(machineId, salt)
	} else {
		result = c.JrebelRpcService.Ping(machineId, salt)
	}

	ctx.String(http.StatusOK, result)
}

// ObtainTicket 处理 /rpc/obtainTicket.action 请求
func (c *RpcController) ObtainTicket(ctx *gin.Context) {
	username := ctx.DefaultQuery("username", "")
	hostName := ctx.DefaultQuery("hostName", "")
	machineId := ctx.DefaultQuery("machineId", "")
	salt := ctx.Query("salt")
	isJetbrains := machineId != ""

	var result string
	if isJetbrains {
		result = c.JetbrainsRpcService.ObtainTicket(username, hostName, machineId, salt)
	} else {
		result = c.JrebelRpcService.ObtainTicket(username, hostName, machineId, salt)
	}

	ctx.String(http.StatusOK, result)
}

// ReleaseTicket 处理 /rpc/releaseTicket.action 请求
func (c *RpcController) ReleaseTicket(ctx *gin.Context) {
	machineId := ctx.DefaultQuery("machineId", "")
	salt := ctx.Query("salt")
	isJetbrains := machineId != ""

	var result string
	if isJetbrains {
		result = c.JetbrainsRpcService.ReleaseTicket(machineId, salt)
	} else {
		result = c.JrebelRpcService.ReleaseTicket(machineId, salt)
	}

	ctx.String(http.StatusOK, result)
}
