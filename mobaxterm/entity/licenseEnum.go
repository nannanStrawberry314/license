package entity

// LicenseEnum 定义了许可证的类型
type LicenseEnum int

const (
	// 定义不同类型的许可证
	Professional LicenseEnum = iota + 1 // 专业版
	Educational                         // 教育版
	Personal                            // 个人版
)

// LicenseEnumNames 用于将LicenseEnum的值映射为描述字符串
var LicenseEnumNames = map[LicenseEnum]string{
	Professional: "专业版",
	Educational:  "教育版",
	Personal:     "个人版",
}

// GetCode 返回LicenseEnum的整数代码
func (le LicenseEnum) GetCode() int {
	return int(le)
}

// GetName 返回LicenseEnum的描述名称
func (le LicenseEnum) GetName() string {
	return LicenseEnumNames[le]
}
