package entity

import (
	"fmt"
	"time"
)

type CustomTime struct {
	time.Time
}

func (ct CustomTime) MarshalJSON() ([]byte, error) {
	formatted := fmt.Sprintf("\"%s\"", ct.Format("2006-01-02"))
	return []byte(formatted), nil
}

// Restriction 结构体对应于Java中的Restriction类，用于存储限制信息
type Restriction struct {
	Plan            string `json:"plan"`              // 默认Go的JSON序列化将使用字段名作为JSON键名
	ActiveUserCount int    `json:"active_user_count"` // 使用JsonProperty标签映射JSON字段
}

// LicenseInfo 结构对应于Java中的LicenseInfo类，用于存储许可证信息
type LicenseInfo struct {
	Name    string `json:"Name"`
	Company string `json:"Company"`
	Email   string `json:"Email"`
}

// License 代表一个许可证，对应于Java中的License类
type License struct {
	Version                      int         `json:"version"`
	License                      LicenseInfo `json:"licensee"`
	StartsAt                     CustomTime  `json:"issued_at"`
	ExpiresAt                    CustomTime  `json:"expires_at"`
	NotifyAdminsAt               CustomTime  `json:"notify_admins_at"`
	NotifyUsersAt                CustomTime  `json:"notify_users_at"`
	BlockChangesAt               CustomTime  `json:"block_changes_at"`
	CloudLicensingEnabled        bool        `json:"cloud_licensing_enabled"`
	OfflineCloudLicensingEnabled bool        `json:"offline_cloud_licensing_enabled"`
	AutoRenewEnabled             bool        `json:"auto_renew_enabled"`
	SeatReconciliationEnabled    bool        `json:"seat_reconciliation_enabled"`
	OperationalMetricsEnabled    bool        `json:"operational_metrics_enabled"`
	GeneratedFromCustomersDot    bool        `json:"generated_from_customers_dot"`
	Restrictions                 Restriction `json:"restrictions"`
}

// func (l *License) MarshalJSON() ([]byte, error) {
// 	type Alias License
// 	return json.Marshal(&struct {
// 		*Alias
// 		StartsAt       string `json:"issued_at"`
// 		ExpiresAt      string `json:"expires_at"`
// 		NotifyAdminsAt string `json:"notify_admins_at"`
// 		NotifyUsersAt  string `json:"notify_users_at"`
// 		BlockChangesAt string `json:"block_changes_at"`
// 	}{
// 		Alias:          (*Alias)(l),
// 		StartsAt:       l.StartsAt.Format("2006-01-02 15:04:05"),
// 		ExpiresAt:      l.ExpiresAt.Format("2006-01-02 15:04:05"),
// 		NotifyAdminsAt: l.NotifyAdminsAt.Format("2006-01-02 15:04:05"),
// 		NotifyUsersAt:  l.NotifyUsersAt.Format("2006-01-02 15:04:05"),
// 		BlockChangesAt: l.BlockChangesAt.Format("2006-01-02 15:04:05"),
// 	})
// }
