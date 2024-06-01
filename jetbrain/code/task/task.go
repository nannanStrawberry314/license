package task

import (
	"license/jetbrain/code/service"
	"license/logger"
)

type Task struct {
	ProductService service.ProductService
	PluginService  service.PluginService
}

func NewTask() *Task {
	return &Task{
		ProductService: service.NewProductService(),
		PluginService:  service.NewPluginService(),
	}

}

func (t *Task) FetchProductLatest() {
	err := t.ProductService.FetchLatest()
	if err != nil {
		logger.Error("Failed to fetch latest product:", err)
	}
	err = t.PluginService.FetchLatest()
	if err != nil {
		logger.Error("Failed to fetch latest plugin:", err)
	}
}
