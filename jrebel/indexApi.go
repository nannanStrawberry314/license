package jrebel

import (
	"fmt"
	"github.com/gin-gonic/gin"
	"net/http"
	"strings"
)

const (
	httpScheme  = "http"
	httpPort    = "80"
	httpsScheme = "https"
	httpsPort   = "443"
	endStr      = "/"
)

// IndexController 定义控制器结构体
type IndexController struct {
}

// NewLeasesController 创建新的控制器实例
func NewIndexController() *IndexController {
	return &IndexController{}
}

// IndexHandler using Gin to handle the root endpoint and display license information.
func (controller *IndexController) IndexHandler(c *gin.Context) {
	scheme := httpScheme
	if c.Request.TLS != nil {
		scheme = httpsScheme
	}

	host := c.Request.Host
	if pos := strings.Index(host, ":"); pos != -1 {
		host = host[:pos]
	}
	if host == "" {
		host = "127.0.0.1"
	}

	port := c.Request.URL.Port()
	if port == "" {
		// default to HTTP port, this should be configured or detected
		port = "80"
	}

	requestURI := strings.TrimSuffix(c.Request.RequestURI, endStr)

	licenseURL := fmt.Sprintf("%s://%s%s", scheme, host, requestURI)
	if (scheme == httpScheme && port != httpPort) || (scheme == httpsScheme && port != httpsPort) {
		licenseURL = fmt.Sprintf("%s://%s:%s%s", scheme, host, port, requestURI)
	}

	html := fmt.Sprintf(`
		<h3>使用说明（Instructions for use）</h3>
		<hr/>
		<h1>Hello, This is a Jrebel & JetBrains License Server!</h1>
		<p>License Server started at %s</p>
		<p>JetBrains Activation address was: <span style='color:red'>%s</span></p>
		<p>JRebel 7.1 and earlier version Activation address was: <span style='color:red'>%s/{tokenname}</span>, with any email.</p>
		<p>JRebel 2018.1 and later version Activation address was: %s/{guid} (eg:<span style='color:red'>%s/%s</span>), with any email.</p>
		<hr/>
		<h1>Hello，此地址是 Jrebel & JetBrains License Server!</h1>
		<p>JetBrains许可服务器激活地址 %s</p>
		<p>JetBrains激活地址是: <span style='color:red'>%s</span></p>
		<p>JRebel 7.1 及旧版本激活地址: <span style='color:red'>%s/{tokenname}</span>, 以及任意邮箱地址。</p>
		<p>JRebel 2018.1+ 版本激活地址: %s/{guid} (例如：<span style='color:red'>%s/%s</span>), 以及任意邮箱地址。</p>
	`, licenseURL, licenseURL, licenseURL, licenseURL, licenseURL, c.Query("guid"), licenseURL, licenseURL, licenseURL, licenseURL, licenseURL, c.Query("guid"))

	c.Header("Content-Type", "text/html; charset=utf-8")
	c.Status(http.StatusOK)
	_, err := c.Writer.Write([]byte(html))
	if err != nil {
		return
	}
}
