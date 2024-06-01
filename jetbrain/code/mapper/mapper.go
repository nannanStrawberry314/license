package mapper

import (
	"license/config"
	"license/jetbrain/code/entity"
	"log"
)

type ProductMapper interface {
	Truncate() error
	SaveBatch(products []*entity.ProductEntity) error
	List() ([]entity.ProductEntity, error)
}

type GormProductMapper struct{}

func (m *GormProductMapper) Truncate() error {
	result := config.DB.Exec("DELETE FROM sys_jetbrains_product")
	if result.Error != nil {
		log.Println("Error truncating product table:", result.Error)
		return result.Error
	}
	return nil
}

func (m *GormProductMapper) SaveBatch(products []*entity.ProductEntity) error {
	result := config.DB.CreateInBatches(products, len(products))
	if result.Error != nil {
		log.Println("Error saving products:", result.Error)
		return result.Error
	}
	return nil
}

func (m *GormProductMapper) List() ([]entity.ProductEntity, error) {
	var products []entity.ProductEntity
	result := config.DB.Find(&products)
	if result.Error != nil {
		log.Println("Error fetching products:", result.Error)
		return nil, result.Error
	}
	return products, nil
}

type PluginMapper interface {
	Truncate() error
	SaveBatch(products []*entity.PluginEntity) error
	List() ([]entity.PluginEntity, error)
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

func (m *GormPluginMapper) List() ([]entity.PluginEntity, error) {
	var plugins []entity.PluginEntity
	result := config.DB.Find(&plugins)
	if result.Error != nil {
		log.Println("Error fetching plugins:", result.Error)
		return nil, result.Error
	}
	return plugins, nil
}
