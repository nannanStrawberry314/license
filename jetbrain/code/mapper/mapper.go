package mapper

import (
	"license/config"
	"license/jetbrain/code/entity"
	"log"
)

type ProductMapper interface {
	Truncate() error
	SaveBatch(products []*entity.ProductEntity) error
}

// GormProductMapper 是 ProductMapper 接口的 GORM 实现
type GormProductMapper struct{}

// Truncate 用于清空产品表
func (m *GormProductMapper) Truncate() error {
	result := config.DB.Exec("DELETE FROM sys_jetbrains_product")
	if result.Error != nil {
		log.Println("Error truncating product table:", result.Error)
		return result.Error
	}
	return nil
}

// SaveBatch 用于批量保存产品数据到数据库
func (m *GormProductMapper) SaveBatch(products []*entity.ProductEntity) error {
	result := config.DB.CreateInBatches(products, len(products))
	if result.Error != nil {
		log.Println("Error saving products:", result.Error)
		return result.Error
	}
	return nil
}

type PluginMapper interface {
	Truncate() error
	SaveBatch(products []*entity.PluginEntity) error
}

type GormPluginMapper struct{}

func (m *GormPluginMapper) Truncate() error {
	result := config.DB.Exec("DELETE FROM sys_jetbrains_paid_plugin")
	if result.Error != nil {
		log.Println("Error truncating sys_jetbrains_paid_plugin table:", result.Error)
		return result.Error
	}
	return nil
}

func (m *GormPluginMapper) SaveBatch(plugins []*entity.PluginEntity) error {
	result := config.DB.CreateInBatches(plugins, len(plugins))
	if result.Error != nil {
		log.Println("Error saving plugins:", result.Error)
		return result.Error
	}
	return nil
}
