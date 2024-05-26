package initialize

func ExecuteInitialize() {
	// 初始化证书
	InitCert()
	// 初始化gitlab
	InitGitLabCert()
	// 初始化 Jetbrains
	InitJetbrains()
}
