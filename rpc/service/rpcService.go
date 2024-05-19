package service

// RpcService 定义了与远程过程调用相关的方法
type RpcService interface {
	Ping(machineId, salt string) string
	ObtainTicket(username, hostName, machineId, salt string) string
	ReleaseTicket(machineId, salt string) string
}
