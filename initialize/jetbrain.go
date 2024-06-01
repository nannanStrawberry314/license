package initialize

import (
	"license/jetbrain/util"
	"license/logger"
)

// 初始化
func InitJetbrains() {
	logger.Info("init fake cert")
	util.Fake.LoadOrGenerate()
	err := util.Fake.LoadRootCert()
	if err != nil {
		logger.Error("load root ca err %e", err)
	}
	err = util.Fake.GenerateRootCert()
	if err != nil {
		logger.Error("generate jet ca err %e", err)
	}

	err = util.Fake.LoadCert()
	if err != nil {
		logger.Error("load my ca err %e", err)
	}
	logger.Info("init fake cert done")
}
