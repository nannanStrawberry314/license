package code

import "gorm.io/gorm"

type PluginMapper struct {
	db *gorm.DB
}

func NewPluginMapper(db *gorm.DB) *PluginMapper {
	return &PluginMapper{db: db}
}

// Truncate Implementing a method equivalent to the 'truncate' in Java
func (mapper *PluginMapper) Truncate() error {
	return mapper.db.Exec("DELETE FROM sys_jetbrains_paid_plugin").Error
}

type ProductMapper struct {
	db *gorm.DB
}

func NewProductMapper(db *gorm.DB) *ProductMapper {
	return &ProductMapper{db: db}
}

// Truncate Implementing a method equivalent to the 'truncate' in Java
func (mapper *ProductMapper) Truncate() error {
	return mapper.db.Exec("DELETE FROM sys_jetbrains_product").Error
}
