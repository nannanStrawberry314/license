package config

import (
	"fmt"
	"log"
	"os"
	"strconv"
	"time"

	"github.com/joho/godotenv"
)

type Config struct {
	HttpHost       string
	HttpPort       int
	DataDir        string
	DatabaseDriver string
	DatabaseDsn    string
	StartTime      time.Time
}

var globalConfig *Config

// InitConfig 初始化全局配置
func InitConfig() {
	// 加载.env文件
	err := godotenv.Load()
	if err != nil {
		log.Println("No .env file found")
	}

	dataDir := getEnvStr("DATA_DIR", "/data")
	databaseDsn := fmt.Sprintf("%s/%s", dataDir, getEnvStr("DATABASE_DSN", "license.db"))

	globalConfig = &Config{
		HttpHost:       getEnvStr("HTTP_HOST", "0.0.0.0"),
		HttpPort:       getEnvInt("HTTP_PORT", 5000),
		DataDir:        dataDir,
		DatabaseDriver: getEnvStr("DATABASE_DRIVER", "sqlite"),
		DatabaseDsn:    databaseDsn,
		StartTime:      time.Now(),
	}
}

// getEnvStr 读取环境变量或返回默认值
func getEnvStr(key, defaultValue string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return defaultValue
}

// getEnvInt 读取环境变量或返回默认值
func getEnvInt(key string, defaultValue int) int {
	if value, exists := os.LookupEnv(key); exists {
		intValue, err := strconv.Atoi(value)
		if err == nil {
			return intValue
		}
	}
	return defaultValue
}

// getEnvBool 读取环境变量或返回默认值
func getEnvBool(key string, defaultValue bool) bool {
	if value, exists := os.LookupEnv(key); exists {
		boolValue, err := strconv.ParseBool(value)
		if err == nil {
			return boolValue
		}
	}
	return defaultValue
}

// GetConfig 提供全局配置的访问
func GetConfig() *Config {
	if globalConfig == nil {
		log.Fatal("Config is not initialized")
	}
	return globalConfig
}
