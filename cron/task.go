package cron

import (
	"github.com/robfig/cron/v3"
	"license/jetbrain/code/task"
	"license/logger"
)

func InitCron() {
	c := cron.New(cron.WithSeconds())

	jetbrainsTask := task.NewTask()

	// 添加定时任务
	_, err := c.AddFunc("0 0 1 * * ?", func() {
		jetbrainsTask.FetchProductLatest()
	})
	if err != nil {
		logger.Error("Failed to add cron job:", err)
	}
	c.Start()
	logger.Sys("Cron job started")
}
