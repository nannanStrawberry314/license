package entity

type PluginEntity struct {
	ID              uint64 `gorm:"primaryKey;column:id"`
	PluginApiDetail string `gorm:"column:plugin_api_detail"`
	PluginID        uint64 `gorm:"column:plugin_id"`
	PluginName      string `gorm:"column:plugin_name"`
	PluginCode      string `gorm:"column:plugin_code"`
}

func (PluginEntity) TableName() string {
	return "sys_jetbrains_paid_plugin"
}

type ProductEntity struct {
	ID            uint64 `gorm:"primaryKey;column:id"`
	ProductName   string `gorm:"column:product_name"`
	ProductCode   string `gorm:"column:product_code"`
	ProductDetail string `gorm:"column:product_detail"`
}

func (ProductEntity) TableName() string {
	return "sys_jetbrains_product"
}
