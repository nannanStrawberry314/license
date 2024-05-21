package config

import (
	"gorm.io/driver/mysql"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
	"license/jetbrains/code"
	"license/logger"
)

var DB *gorm.DB

func SetupDatabase() {
	var err error

	driver := GetConfig().DatabaseDriver
	dsn := GetConfig().DatabaseDsn
	if driver == "sqlite" {
		DB, err = gorm.Open(sqlite.Open(dsn), &gorm.Config{})
		if err != nil {
			logger.Error("Failed to connect to database:", err)
		}
	} else if driver == "mysql" {
		DB, err = gorm.Open(mysql.Open(dsn), &gorm.Config{})
		if err != nil {
			logger.Error("Failed to connect to database:", err)
		}
	}

	// 自动迁移数据库模式
	err = DB.AutoMigrate(&code.PluginEntity{}, &code.ProductEntity{})
	if err != nil {
		logger.Error("Failed to migrate database", err)
		return
	}

	logger.Sys("Database Migrated Successfully")
}
