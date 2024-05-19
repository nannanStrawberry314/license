package mobaxterm

import (
	"fmt"
)

// License 定义了许可证的详细信息
type License struct {
	LicenseType  LicenseEnum
	UserName     string
	MajorVersion int
	MinorVersion int
	Count        int
	Unknown      int
	OpenGames    bool
	OpenPlugins  bool
}

// NewLicense 创建一个新的License实例
func NewLicense(licenseType LicenseEnum, userName string, majorVersion, minorVersion, count int, openGames, openPlugins bool) *License {
	return &License{
		LicenseType:  licenseType,
		UserName:     userName,
		MajorVersion: majorVersion,
		MinorVersion: minorVersion,
		Count:        count,
		Unknown:      0, // 默认值
		OpenGames:    openGames,
		OpenPlugins:  openPlugins,
	}
}

// GetLicenseKey 生成待计算的key的字符串
func (l *License) GetLicenseKey() string {
	game := 0
	if l.OpenGames {
		game = 1
	}
	plugin := 0
	if l.OpenPlugins {
		plugin = 1
	}
	// 使用同样的字符串格式，需要匹配Java中的格式
	licenseFormat := "%d#%s|%d%d#%d#%d3%d6%d#%d#%d#%d#"
	return fmt.Sprintf(licenseFormat, l.LicenseType.GetCode(), l.UserName, l.MajorVersion, l.MinorVersion, l.Count, l.MajorVersion, l.MinorVersion, l.MinorVersion, l.Unknown, game, plugin)
}
