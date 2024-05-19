package finalshell

import (
	"crypto/md5"
	"encoding/hex"
	"github.com/ebfe/keccak"
)

func md5Hash(msg string) string {
	hash := md5.New()
	hash.Write([]byte(msg))
	str := hex.EncodeToString(hash.Sum(nil))
	return str[8:24]
}

func keccak384Hash(msg string) string {
	enc := keccak.New384()
	enc.Write([]byte(msg))
	str := hex.EncodeToString(enc.Sum(nil))
	return str[12:28]
}

// GenerateLicense 实现接口的方法
func GenerateLicense(machineCode string) []string {
	var result []string
	result = append(result, "版本号 < 3.9.6 高级版: "+md5Hash("61305"+machineCode+"8552"))
	result = append(result, "版本号 < 3.9.6 专业版: "+md5Hash("2356"+machineCode+"13593"))
	result = append(result, "版本号 >= 3.9.6 高级版: "+keccak384Hash(machineCode+"hSf(78cvVlS5E"))
	result = append(result, "版本号 >= 3.9.6 专业版: "+keccak384Hash(machineCode+"FF3Go(*Xvbb5s2"))
	return result
}
