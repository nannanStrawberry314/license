package initialize

import (
	"license/jetbrain/server/util"
	"license/logger"
)

// 初始化
func InitJetbrains() {
	logger.Info("init fake cert")
	util.Fake.LoadOrGeneratePrivateKey()
	err := util.Fake.LoadRootCA()
	if err != nil {
		logger.Error("load root ca err %e", err)
	}
	err = util.Fake.GenerateJetCA()
	if err != nil {
		logger.Error("generate jet ca err %e", err)
	}

	err = util.Fake.LoadMyCA()
	if err != nil {
		logger.Error("load my ca err %e", err)
	}
	logger.Info("init fake cert done")
}
