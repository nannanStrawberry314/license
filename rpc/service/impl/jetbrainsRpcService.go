package impl

import (
	"github.com/gin-gonic/gin"
	"license/jetbrain/server/entity"
	"license/jetbrain/util"
	"license/rpc/service"
)

// Service represents the JRebel RPC service.
type JetbrainsRpcService struct {
}

// 确保Service实现了common.RpcService接口
var _ service.RpcService = &JetbrainsRpcService{}

// Ping handles the ping XML request and returns a signed response.
func (s *JetbrainsRpcService) Ping(ctx *gin.Context, machineId, salt string) {
	pingReq := &entity.BaseRequest{
		Salt:      salt,
		MachineId: machineId,
	}
	pingResponse := entity.NewPingResponse(pingReq, util.Fake)
	ctx.Render(200, entity.NewXMLTicket(pingResponse))
}

// ObtainTicket handles the ticket obtaining XML request and returns a signed response.
func (s *JetbrainsRpcService) ObtainTicket(ctx *gin.Context, username, hostName, machineId, salt string) {
	if len(username) == 0 {
		username = hostName
	}
	obtainReq := &entity.BaseRequest{
		Salt:      salt,
		UserName:  username,
		MachineId: machineId,
	}
	ticketResponse := entity.NewObtainTicketResponse(obtainReq, util.Fake)
	ctx.Render(200, entity.NewXMLTicket(ticketResponse))
}

// ReleaseTicket handles the ticket release XML request and returns a signed response.
func (s *JetbrainsRpcService) ReleaseTicket(ctx *gin.Context, machineId, salt string) {
	releaseReq := &entity.BaseRequest{
		Salt:      salt,
		MachineId: machineId,
	}
	releaseTicketResponse := entity.NewReleaseTicketResponse(releaseReq, util.Fake)
	ctx.Render(200, entity.NewXMLTicket(releaseTicketResponse))
}
