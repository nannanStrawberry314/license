package gitlab

import (
	"archive/zip"
	"crypto/aes"
	"crypto/rand"
	"encoding/base64"
	"encoding/json"
	gorsa "github.com/Lyafei/go-rsa"
	"github.com/gin-gonic/gin"
	"io"
	"license/crypto"
	"log"
	"net/http"
	"os"
	"time"
)

var privateKey string
var publicKey string

// init 在初始化阶段加载RSA密钥对
func init() {
	loadKeys()
}

// loadKeys 读取、解码并解析RSA私钥和公钥。
func loadKeys() {
	// 读取公钥
	publicBytes, err := os.ReadFile("files/.license_encryption_key.pub")
	if err != nil {
		log.Printf("读取公钥文件失败: %v", err)
		return
	}
	// 转成字符串
	publicKey = string(publicBytes)

	// 读取私钥
	privateBytes, err := os.ReadFile("files/.license_decryption_key.pri")
	if err != nil {
		log.Printf("读取私钥文件失败: %v", err)
		return
	}
	// 转成字符串
	privateKey = string(privateBytes)
}

// createLicenseJson 创建许可证的JSON表示
func createLicenseJson(licenseInfo LicenseInfo) (string, error) {

	expirationDate := time.Date(2100, 12, 31, 23, 59, 59, 0, time.UTC)

	license := License{
		Version:                      1,
		License:                      licenseInfo,
		StartsAt:                     CustomTime{Time: time.Now()},
		ExpiresAt:                    CustomTime{Time: expirationDate},
		NotifyAdminsAt:               CustomTime{Time: expirationDate},
		NotifyUsersAt:                CustomTime{Time: expirationDate},
		BlockChangesAt:               CustomTime{Time: expirationDate},
		CloudLicensingEnabled:        false,
		OfflineCloudLicensingEnabled: false,
		AutoRenewEnabled:             false,
		SeatReconciliationEnabled:    false,
		OperationalMetricsEnabled:    false,
		GeneratedFromCustomersDot:    false,
		Restrictions: Restriction{
			ActiveUserCount: 10000,
			Plan:            "ultimate",
		},
	}

	jsonData, err := json.Marshal(license)
	if err != nil {
		return "", err
	}

	return string(jsonData), nil
}

// generateRandomIV 生成随机的初始化向量(IV)
func generateRandomIV() ([]byte, error) {
	iv := make([]byte, aes.BlockSize) // AES的块大小固定为16字节
	if _, err := rand.Read(iv); err != nil {
		return nil, err
	}
	return iv, nil
}

// Encrypt 封装 Encrypt 方法，使用 AES-CBC 加密和 PKCS7 填充
func Encrypt(data, key, iv []byte) ([]byte, error) {
	aesTool := crypto.AesCbcPkcs7{Key: key, Iv: iv}
	enc, err := aesTool.Encrypt(data)
	if err != nil {
		log.Println("Encrypt error:", err)
		return nil, err
	}
	return enc, err
}

// 使用 RSA 私钥"加密"数据
func encryptWithPrivateKey(data string) (string, error) {
	encrypt, err := gorsa.PriKeyEncrypt(data, privateKey)
	if err != nil {
		log.Printf("使用RSA私钥加密数据失败: %v", err)
		return "", err
	}
	return encrypt, nil
}

// encryptLicense 使用AES和RSA加密许可证数据
func encryptLicense(data string) (string, error) {
	// 生成256位AES密钥
	key := make([]byte, 16)
	if _, err := rand.Read(key); err != nil {
		log.Printf("生成AES密钥失败: %v", err)
		return "", err
	}

	// 生成随机IV
	iv, err := generateRandomIV()
	if err != nil {
		log.Printf("生成AES IV失败: %v", err)
		return "", err
	}

	encryptedData, err := Encrypt([]byte(data), key, iv)
	if err != nil {
		log.Printf("加密数据失败: %v", err)
		return "", err
	}

	// 注意：RSA 加密通常是用公钥完成的，但技术上可以用私钥进行加密（即使不推荐）
	encryptedKey, err := encryptWithPrivateKey(string(key))
	if err != nil {
		return "", err
	}

	// 将加密数据编码为Base64
	encryptedDataStr := base64.StdEncoding.EncodeToString(encryptedData)
	ivStr := base64.StdEncoding.EncodeToString(iv)

	// 封装为JSON格式
	result := map[string]string{
		"data": encryptedDataStr,
		"key":  encryptedKey,
		"iv":   ivStr,
	}
	jsonData, err := json.Marshal(result)
	if err != nil {
		log.Printf("封装JSON数据失败: %v", err)
		return "", err
	}

	// 将JSON编码为Base64
	encodedFinal := base64.StdEncoding.EncodeToString(jsonData)
	return encodedFinal, nil
}

// Generate 生成许可证并通过HTTP响应发送
func Generate(ctx *gin.Context, licenseInfo LicenseInfo) {
	createLicense(ctx, licenseInfo)
}

// createLicense 创建并发送许可证
func createLicense(ctx *gin.Context, licenseInfo LicenseInfo) {
	// 创建许可证的JSON数据
	licenseJson, err := createLicenseJson(licenseInfo)
	if err != nil {
		log.Printf("创建许可证JSON失败: %v", err)
		ctx.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": "内部服务器错误"})
		return
	}

	// 对许可证数据进行加密
	encryptedLicense, err := encryptLicense(licenseJson)
	if err != nil {
		log.Printf("加密许可证失败: %v", err)
		ctx.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": "内部服务器错误"})
		return
	}

	// 导出包含加密许可证和公钥文件的ZIP文件
	err = exportZipStream(ctx, encryptedLicense)
	if err != nil {
		log.Printf("导出ZIP文件失败: %v", err)
		ctx.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{"error": "内部服务器错误"})
		return
	}
}

// exportZipStream 创建并发送包含加密许可证和公钥文件的ZIP文件
func exportZipStream(ctx *gin.Context, encryptedLicense string) error {
	// 设置响应头部以便文件下载
	ctx.Header("Content-Disposition", "attachment; filename=license.zip")
	ctx.Header("Content-Type", "application/zip")

	zipWriter := zip.NewWriter(ctx.Writer)
	defer func(zipWriter *zip.Writer) {
		err := zipWriter.Close()
		if err != nil {
			log.Printf("关闭ZIP写入器失败: %v", err)
		}
	}(zipWriter)

	// 添加公钥文件到ZIP
	if err := addFileToZip(zipWriter, "files/.license_encryption_key.pub", "license/.license_encryption_key.pub"); err != nil {
		return err
	}

	// 添加加密的许可证数据到ZIP
	if err := addLicenseToZip(zipWriter, encryptedLicense, "license/license.gitlab-license"); err != nil {
		return err
	}

	return nil
}

// addFileToZip 从文件系统读取文件并添加到ZIP
func addFileToZip(zipWriter *zip.Writer, filePath, zipPath string) error {
	fileToZip, err := os.Open(filePath)
	if err != nil {
		return err
	}
	defer func(fileToZip *os.File) {
		err := fileToZip.Close()
		if err != nil {
			log.Printf("关闭文件失败: %v", err)
		}
	}(fileToZip)

	// 获取文件信息，用于设置ZIP条目的大小和时间戳
	fileInfo, err := fileToZip.Stat()
	if err != nil {
		return err
	}

	// 创建ZIP条目，并手动设置文件时间戳
	header, err := zip.FileInfoHeader(fileInfo)
	if err != nil {
		return err
	}
	header.Name = zipPath
	// 设置压缩方法
	header.Method = zip.Deflate
	// 保留原文件的修改时间
	header.Modified = fileInfo.ModTime()

	zipFile, err := zipWriter.CreateHeader(header)
	if err != nil {
		return err
	}

	// 写入文件数据到ZIP
	_, err = io.Copy(zipFile, fileToZip)
	return err
}

// addLicenseToZip 直接将字符串数据写入ZIP条目
func addLicenseToZip(zipWriter *zip.Writer, data, zipPath string) error {
	// 创建一个新的zip.FileHeader，设置文件名和修改时间
	header := &zip.FileHeader{
		Name:     zipPath,
		Method:   zip.Deflate, // 使用压缩以减小文件大小
		Modified: time.Now(),  // 设置当前时间作为文件修改时间
	}

	// 创建ZIP条目
	zipFile, err := zipWriter.CreateHeader(header)
	if err != nil {
		return err
	}

	// 将数据写入ZIP条目
	_, err = zipFile.Write([]byte(data))
	return err
}
