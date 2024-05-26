package initialize

import (
	"license/jetbrain/server"
	"license/logger"
)

// 初始化
func InitJetbrains() {
	logger.Info("init fake cert")
	server.Fake.LoadOrGeneratePrivateKey()
	err := server.Fake.LoadRootCA()
	if err != nil {
		logger.Error("load root ca err %e", err)
	}
	err = server.Fake.GenerateJetCA()
	if err != nil {
		logger.Error("generate jet ca err %e", err)
	}

	err = server.Fake.LoadMyCA()
	if err != nil {
		logger.Error("load my ca err %e", err)
	}
	logger.Info("init fake cert done")
}
