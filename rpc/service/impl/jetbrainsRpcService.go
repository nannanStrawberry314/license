package impl

import (
	"github.com/gin-gonic/gin"
	"license/jetbrain/server"
	"license/rpc/service"
)

// Service represents the JRebel RPC service.
type JetbrainsRpcService struct {
}

// 确保Service实现了common.RpcService接口
var _ service.RpcService = &JetbrainsRpcService{}

// Ping handles the ping XML request and returns a signed response.
func (s *JetbrainsRpcService) Ping(ctx *gin.Context, machineId, salt string) {
	pingReq := &server.BaseRequest{
		Salt:      salt,
		MachineId: machineId,
	}
	pingResponse := server.NewPingResponse(pingReq, server.Fake)
	ctx.Render(200, server.NewXMLTicket(pingResponse))
}

// ObtainTicket handles the ticket obtaining XML request and returns a signed response.
func (s *JetbrainsRpcService) ObtainTicket(ctx *gin.Context, username, hostName, machineId, salt string) {
	if len(username) == 0 {
		username = hostName
	}
	obtainReq := &server.BaseRequest{
		Salt:      salt,
		UserName:  username,
		MachineId: machineId,
	}
	ticketResponse := server.NewObtainTicketResponse(obtainReq, server.Fake)
	ctx.Render(200, server.NewXMLTicket(ticketResponse))
}

// ReleaseTicket handles the ticket release XML request and returns a signed response.
func (s *JetbrainsRpcService) ReleaseTicket(ctx *gin.Context, machineId, salt string) {
	releaseReq := &server.BaseRequest{
		Salt:      salt,
		MachineId: machineId,
	}
	releaseTicketResponse := server.NewReleaseTicketResponse(releaseReq, server.Fake)
	ctx.Render(200, server.NewXMLTicket(releaseTicketResponse))
}
