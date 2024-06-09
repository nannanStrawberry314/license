package crypto

import (
	"bytes"
	"crypto/aes"
	"crypto/cipher"
	"encoding/base64"
	"errors"
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
