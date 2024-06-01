package api

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha1"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"license/jrebel/constant"
	"license/jrebel/vo"
	"log"
	"net/http"
	"strconv"
)

// LeasesController 定义控制器结构体
type LeasesController struct {
}

// NewLeasesController 创建新的控制器实例
func NewLeasesController() *LeasesController {
	return &LeasesController{}
}

// sign creates a digital signature using RSA-SHA1 algorithm.
func sign(clientRandomness, guid string, offline bool, validFrom, validUntil int64) string {
	signatureBase := clientRandomness + ";" + constant.SERVER_RANDOMNESS + ";" + guid + ";" + strconv.FormatBool(offline)
	if offline {
		// signatureBase += ";" + strconv.FormatInt(*validFrom, 10) + ";" + strconv.FormatInt(*validUntil, 10)
		signatureBase += ";" + strconv.FormatInt(validFrom, 10) + ";" + strconv.FormatInt(validUntil, 10)
	}

	log.Printf("signature: %s", signatureBase)

	block, _ := pem.Decode([]byte(constant.LEASES_PRIVATE_KEY))
	if block == nil {
		log.Println("Failed to decode PEM block containing the private key")
		return ""
	}

	privateKey, err := x509.ParsePKCS1PrivateKey(block.Bytes)
	if err != nil {
		log.Printf("Failed to parse RSA private key: %v", err)
		return ""
	}

	hash := sha1.New()
	hash.Write([]byte(signatureBase))
	hashed := hash.Sum(nil)

	signature, err := rsa.SignPKCS1v15(rand.Reader, privateKey, crypto.SHA1, hashed)
	if err != nil {
		log.Printf("Failed to sign data: %v", err)
		return ""
	}

	return base64.StdEncoding.EncodeToString(signature)
}

// LeasesHandler handles the "/leases" endpoint.
func (controller *LeasesController) LeasesHandler(c *gin.Context) {
	clientRandomness := c.PostForm("randomness")
	username := c.PostForm("username")
	guid := c.PostForm("guid")
	offline, _ := strconv.ParseBool(c.PostForm("offline"))
	clientTime, _ := strconv.ParseInt(c.PostForm("clientTime"), 10, 64)

	var validFrom, validUntil int64
	if offline {
		// 计算180天后的时间，注意这里是以毫秒为单位
		expiration := clientTime + 180*24*60*60*1000
		validFrom = clientTime
		validUntil = expiration
	}

	signature := sign(clientRandomness, guid, offline, validFrom, validUntil)

	vo := vo.LeasesHandlerVO{
		ServerVersion:         constant.SERVER_VERSION,
		ServerProtocolVersion: constant.SERVER_PROTOCOL_VERSION,
		ServerGuid:            constant.SERVER_GUID,
		GroupType:             constant.GROUP_TYPE,
		ID:                    1,
		LicenseType:           1,
		EvaluationLicense:     false,
		Signature:             signature,
		ServerRandomness:      constant.SERVER_RANDOMNESS,
		SeatPoolType:          constant.SEAT_POOL_TYPE,
		StatusCode:            constant.STATUS_CODE,
		Offline:               offline,
		ValidFrom:             validFrom,
		ValidUntil:            validUntil,
		Company:               username,
		OrderId:               uuid.NewString(),
		ZeroIds:               make([]string, 0),
		LicenseValidFrom:      1490544001000,
		LicenseValidUntil:     1691839999000,
	}

	c.JSON(http.StatusOK, vo)
}

// Leases1Handler handles the "/leases/1" endpoint.
func (controller *LeasesController) Leases1Handler(c *gin.Context) {
	username := c.DefaultQuery("username", "")

	vo := vo.LeasesOneHandlerVO{
		ServerVersion:         constant.SERVER_VERSION,
		ServerProtocolVersion: constant.SERVER_PROTOCOL_VERSION,
		ServerGuid:            constant.SERVER_GUID,
		GroupType:             constant.GROUP_TYPE,
		StatusCode:            constant.STATUS_CODE,
		Company:               username,
		Msg:                   "",
		StatusMessage:         "",
	}

	c.JSON(http.StatusOK, vo)
}

// ValidateHandler handles the "/validate-connection" endpoint.
func (controller *LeasesController) ValidateHandler(c *gin.Context) {
	vo := vo.ValidateHandlerVO{
		ServerVersion:         constant.SERVER_VERSION,
		ServerProtocolVersion: constant.SERVER_PROTOCOL_VERSION,
		ServerGuid:            constant.SERVER_GUID,
		GroupType:             constant.GROUP_TYPE,
		StatusCode:            constant.STATUS_CODE,
		Company:               constant.COMPANY,
		CanGetLease:           true,
		LicenseType:           "1",
		EvaluationLicense:     false,
		SeatPoolType:          constant.SEAT_POOL_TYPE,
	}

	c.JSON(http.StatusOK, vo)
}
