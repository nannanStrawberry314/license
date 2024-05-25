package impl

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"fmt"
	"github.com/gin-gonic/gin"
	"license/rpc/service"
	"net/http"
)

// JrebelRpcService represents the JRebel RPC service.
type JrebelRpcService struct {
	PrivateKey *rsa.PrivateKey
}

// 确保Service实现了common.RpcService接口
var _ service.RpcService = &JrebelRpcService{}

// privateKeyStr contains the RSA private key as a string.
const privateKeyStr = `
-----BEGIN RSA PRIVATE KEY-----
MIIBOgIBAAJBALecq3BwAI4YJZwhJ+snnDFj3lF3DMqNPorV6y5ZKXCiCMqj8OeO
mxk4YZW9aaV9ckl/zlAOI0mpB3pDT+Xlj2sCAwEAAQJAW6/aVD05qbsZHMvZuS2A
a5FpNNj0BDlf38hOtkhDzz/hkYb+EBYLLvldhgsD0OvRNy8yhz7EjaUqLCB0juIN
4QIhAOeCQp+NXxfBmfdG/S+XbRUAdv8iHBl+F6O2wr5fA2jzAiEAywlDfGIl6acn
akPrmJE0IL8qvuO3FtsHBrpkUuOnXakCIQCqdr+XvADI/UThTuQepuErFayJMBSA
sNe3NFsw0cUxAQIgGA5n7ZPfdBi3BdM4VeJWb87WrLlkVxPqeDSbcGrCyMkCIFSs
5JyXvFTreWt7IQjDssrKDRIPmALdNjvfETwlNJyY
-----END RSA PRIVATE KEY-----
`

// NewJrebelRpcService initializes a new instance of Service with the private key.
func NewJrebelRpcService() (*JrebelRpcService, error) {
	block, _ := pem.Decode([]byte(privateKeyStr))
	if block == nil {
		return nil, fmt.Errorf("failed to parse PEM block containing the key")
	}

	privateKey, err := x509.ParsePKCS1PrivateKey(block.Bytes)
	if err != nil {
		return nil, err
	}

	return &JrebelRpcService{
		PrivateKey: privateKey,
	}, nil
}

// sign creates a signature for the given content using the private key.
func (s *JrebelRpcService) sign(content string) (string, error) {
	hashed := sha256.Sum256([]byte(content))
	signature, err := rsa.SignPKCS1v15(rand.Reader, s.PrivateKey, crypto.SHA256, hashed[:])
	if err != nil {
		return "", err
	}
	return base64.StdEncoding.EncodeToString(signature), nil
}

// Ping handles the ping XML request and returns a signed response.
func (s *JrebelRpcService) Ping(ctx *gin.Context, machineId, salt string) {
	xmlContent := fmt.Sprintf("<PingResponse><message></message><responseCode>OK</responseCode><salt>%s</salt></PingResponse>", salt)
	xmlSignature, _ := s.sign(xmlContent)
	ctx.String(http.StatusOK, fmt.Sprintf("<!-- %s -->\n%s", xmlSignature, xmlContent))
}

// ObtainTicket handles the ticket obtaining XML request and returns a signed response.
func (s *JrebelRpcService) ObtainTicket(ctx *gin.Context, username, hostName, machineId, salt string) {
	prolongationPeriod := "607875500"
	xmlContent := fmt.Sprintf(`<ObtainTicketResponse><message></message><prolongationPeriod>%s</prolongationPeriod><responseCode>OK</responseCode><salt>%s</salt><ticketId>1</ticketId><ticketProperties>licensee=%s\tlicenseType=0\t</ticketProperties></ObtainTicketResponse>`, prolongationPeriod, salt, username)
	xmlSignature, _ := s.sign(xmlContent)
	ctx.String(http.StatusOK, fmt.Sprintf("<!-- %s -->\n%s", xmlSignature, xmlContent))
}

// ReleaseTicket handles the ticket release XML request and returns a signed response.
func (s *JrebelRpcService) ReleaseTicket(ctx *gin.Context, machineId, salt string) {
	xmlContent := fmt.Sprintf("<ReleaseTicketResponse><message></message><responseCode>OK</responseCode><salt>%s</salt></ReleaseTicketResponse>", salt)
	xmlSignature, _ := s.sign(xmlContent)
	ctx.String(http.StatusOK, fmt.Sprintf("<!-- %s -->\n%s", xmlSignature, xmlContent))
}
