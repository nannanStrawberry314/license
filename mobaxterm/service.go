package mobaxterm

import (
	"archive/zip"
	"fmt"
	"github.com/gin-gonic/gin"
	"license/config"
	"log"
	"strconv"
	"strings"
	"time"

	"os"
)

func GenerateLicense(count int, username, version string, c *gin.Context) {
	// 检查参数是否为空或不符合要求
	if username == "" || version == "" || count <= 0 {
		// panic("参数错误")
		log.Printf("参数错误: %s, %s, %d", username, version, count)
	}

	// 拆分版本号
	versionArr := strings.Split(version, ".")
	if len(versionArr) != 2 {
		log.Printf("版本号格式错误: %s", version)
	}

	// 检查版本号是否为数字
	if _, err := strconv.Atoi(versionArr[0]); err != nil {
		// panic("版本号格式错误")
		log.Printf("版本号格式错误: %s", version)
	}
	if _, err := strconv.Atoi(versionArr[1]); err != nil {
		// panic("版本号格式错误")
		log.Printf("版本号格式错误: %s", version)
	}

	// 提取主次版本号
	major, _ := strconv.ParseInt(versionArr[0], 10, 64)
	minor, _ := strconv.ParseInt(versionArr[1], 10, 64)

	license := generateLicense(1, count, username, major, minor)
	// 先写入文件，解决直接输出压缩包文件大小不对导致无法使用的问题
	toFile(license)
	// 读取文件, 输出到浏览器
	c.FileAttachment(config.GetConfig().DataDir+"/Custom.mxtpro", "Custom.mxtpro")
	// 删除文件
	err := os.Remove(config.GetConfig().DataDir + "/Custom.mxtpro")
	if err != nil {
		log.Printf("删除文件失败: %v", err)
	}
}

func toFile(license []byte) {
	fileName := config.GetConfig().DataDir + "/Custom.mxtpro"
	_ = os.Remove(fileName)
	f, err := os.Create(fileName)
	if err != nil {
		log.Printf("创建文件失败: %v", err)
		return
	}
	defer func(f *os.File) {
		err := f.Close()
		if err != nil {
			log.Printf("关闭文件失败: %v", err)
		}
	}(f)

	zipFile := zip.NewWriter(f)
	defer func(zipFile *zip.Writer) {
		err := zipFile.Close()
		if err != nil {
			log.Printf("关闭ZIP写入器失败: %v", err)
		}
	}(zipFile)
	header := &zip.FileHeader{
		Name:               "Pro.key",
		Method:             zip.Store,
		CompressedSize64:   38,
		UncompressedSize64: 38,
	}
	// TODO 关键点，放到FileHeader会导致文件大小不对
	header.SetModTime(time.Now())
	proFile, err := zipFile.CreateRaw(header)
	if err != nil {
		log.Printf("创建ZIP文件失败: %v", err)
		return
	}
	_, err = proFile.Write(license)
	if err != nil {
		log.Printf("写入ZIP文件失败: %v", err)
		return
	}
	return
}

func generateLicense(userType, count int, username string, major, minor int64) []byte {
	licenseString := fmt.Sprintf("%d#%s|%d%d#%d#%d3%d6%d#%d#%d#%d#", userType, username, major, minor, count, major, minor, minor, 0, 0, 0)
	return variantBase64Encode(encryptBytes(0x787, []byte(licenseString)))
}

func encryptBytes(key int, bs []byte) []byte {
	var result []byte
	for _, b := range bs {
		encryptedByte := b ^ byte((key>>8)&0xff)
		result = append(result, encryptedByte)
		key = (int(result[len(result)-1]) & key) | 0x482D
	}
	return result
}

var (
	variantBase64Table = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
	variantBase64Map   = func() map[int]byte {
		result := make(map[int]byte)
		for i, v := range variantBase64Table {
			result[i] = byte(v)
		}
		return result
	}()
)

func variantBase64Encode(bs []byte) []byte {
	var result []byte
	blocksCount := len(bs) / 3
	leftBytes := len(bs) % 3
	for i := 0; i < blocksCount; i++ {
		var blocks []byte
		codingInt := littleEndianBytes(bs[3*i : 3*i+3])
		block := variantBase64Map[codingInt&0x3f]
		blocks = append(blocks, block)
		block = variantBase64Map[(codingInt>>6)&0x3f]
		blocks = append(blocks, block)
		block = variantBase64Map[(codingInt>>12)&0x3f]
		blocks = append(blocks, block)
		block = variantBase64Map[(codingInt>>18)&0x3f]
		blocks = append(blocks, block)
		result = append(result, blocks...)
	}
	if leftBytes == 0 {
		return result
	} else if leftBytes == 1 {
		var blocks []byte
		codingInt := littleEndianBytes(bs[3*blocksCount:])
		block := variantBase64Map[codingInt&0x3f]
		blocks = append(blocks, block)
		block = variantBase64Map[(codingInt>>6)&0x3f]
		blocks = append(blocks, block)
		result = append(result, blocks...)
		return result
	} else {
		var blocks []byte
		codingInt := littleEndianBytes(bs[3*blocksCount:])
		block := variantBase64Map[codingInt&0x3f]
		blocks = append(blocks, block)
		block = variantBase64Map[(codingInt>>6)&0x3f]
		blocks = append(blocks, block)
		block = variantBase64Map[(codingInt>>12)&0x3f]
		blocks = append(blocks, block)
		result = append(result, blocks...)
		return result
	}
}

func littleEndianBytes(bs []byte) int {
	var result = int(bs[0])
	for i := 1; i < len(bs); i++ {
		result = result | int(bs[i])<<(8*i)
	}
	return result
}
