package logger

import (
	"github.com/sirupsen/logrus"
	"sync"
)

var (
	instance *LogWrapper
	once     sync.Once
)

// customFormatter 自定义日志格式
type customFormatter struct{}

// Format 构造日志的输出格式，确保没有额外的前缀或字段名称
func (f *customFormatter) Format(entry *logrus.Entry) ([]byte, error) {
	timestamp := entry.Time.Format("2006/01/02 - 15:04:05")
	return []byte("[" + entry.Data["level"].(string) + "] " + timestamp + " " + entry.Message + "\n"), nil
}

// LogWrapper 包装 logrus logger
type LogWrapper struct {
	*logrus.Logger
}

// initLogger 创建和初始化 logger 实例
func initLogger() {
	baseLogger := logrus.New()
	baseLogger.SetFormatter(new(customFormatter)) // 使用自定义格式
	instance = &LogWrapper{baseLogger}
}

// GetInstance 返回单例的 logger 实例
func GetInstance() *LogWrapper {
	once.Do(initLogger)
	return instance
}

// Info 记录 INFO 级别的日志
func Info(message string) {
	GetInstance().Logger.WithField("level", "INFO").Info(message)
}

// Error 记录 ERROR 级别的日志，并附带错误对象
func Error(message string, err error) {
	if err != nil {
		GetInstance().Logger.WithField("level", "ERROR").Errorf("%s | Error: %v", message, err)
	} else {
		GetInstance().Logger.WithField("level", "ERROR").Error(message)
	}
}

// Sys 记录 SYS 级别的日志
func Sys(message string) {
	GetInstance().Logger.WithField("level", "SYS").Info(message)
}
