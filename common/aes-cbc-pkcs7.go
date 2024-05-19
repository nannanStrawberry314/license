package common

import (
	"bytes"
	"crypto/aes"
	"crypto/cipher"
	"crypto/rand"
	"encoding/base64"
	"errors"
	"log"
)

// AesCbcPkcs7 golang版aes-cbc-pkcs7加密解密base64输入输出
type AesCbcPkcs7 struct {
	Key []byte // 允许16,24,32字节长度
	Iv  []byte // 只允许16字节长度
}

func (s AesCbcPkcs7) Encrypt(text []byte) ([]byte, error) {
	if len(text) == 0 {
		return nil, errors.New("text is empty")
	}
	// 生成cipher.Block 数据块
	block, err := aes.NewCipher(s.Key)
	if err != nil {
		return nil, err
	}
	// 填充内容，如果不足16位字符
	blockSize := block.BlockSize()
	originData := s.pad(text, blockSize)
	// 加密方式
	blockMode := cipher.NewCBCEncrypter(block, s.Iv)
	// 加密，输出到[]byte数组
	encrypt := make([]byte, len(originData))
	blockMode.CryptBlocks(encrypt, originData)
	return encrypt, nil
}

func (s AesCbcPkcs7) Decrypt(text string) ([]byte, error) {
	if len(text) == 0 {
		return []byte(text), nil
	}
	decodeData, err := base64.StdEncoding.DecodeString(text)
	if err != nil {
		return []byte(text), err
	}
	if len(decodeData) == 0 {
		return []byte(text), nil
	}
	// 生成密码数据块cipher.Block
	block, _ := aes.NewCipher(s.Key)
	// 解密模式
	blockMode := cipher.NewCBCDecrypter(block, s.Iv)
	// 输出到[]byte数组
	originData := make([]byte, len(decodeData))
	blockMode.CryptBlocks(originData, decodeData)
	// 去除填充,并返回
	return s.unPad(originData), nil
}

func (s AesCbcPkcs7) pad(ciphertext []byte, blockSize int) []byte {
	padding := blockSize - len(ciphertext)%blockSize
	padText := bytes.Repeat([]byte{byte(padding)}, padding)
	return append(ciphertext, padText...)
}

func (s AesCbcPkcs7) unPad(ciphertext []byte) []byte {
	length := len(ciphertext)
	// 去掉最后一次的padding
	unPadding := int(ciphertext[length-1])
	return ciphertext[:(length - unPadding)]
}

func main() {
	// var text = "this is Aes encryption with pkcs7"
	// aes := AesCbcPkcs7{key: []byte("1111111111111111"), iv: []byte("2222222222222222")}
	// enc, err := aes.Encrypt([]byte(text))
	// fmt.Println("enc", enc, err)
	// dec, err := aes.Decrypt(enc)
	// fmt.Println("dec", string(dec), err)

	// 生成key
	key := make([]byte, 32)
	if _, err := rand.Read(key); err != nil {
		log.Println("key error:", err)
	}
	// 打印base64编码的key
	log.Println("key:", base64.StdEncoding.EncodeToString(key))

	// 生成iv
	iv := make([]byte, aes.BlockSize) // AES的块大小固定为16字节
	if _, err := rand.Read(iv); err != nil {
		log.Println("iv error:", err)
	}
	// 打印base64编码的iv
	log.Println("iv:", base64.StdEncoding.EncodeToString(iv))

	aes := AesCbcPkcs7{Key: key, Iv: iv}
	enc, err := aes.Encrypt([]byte("123456"))
	if err != nil {
		log.Println("Encrypt error:", err)
	}
	log.Println("enc:", enc)

}
