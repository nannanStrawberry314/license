package initialize

import "license/gitlab"

func InitGitLabCert() {
	gitlab.LoadKeys()
}
