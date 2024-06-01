package initialize

import (
	"license/gitlab/service"
)

func InitGitLabCert() {
	service.LoadKeys()
}
