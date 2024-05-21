package code

import (
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"crypto/x509/pkix"
	"encoding/pem"
	"license/jetbrains/server"
	"license/logger"
	"log"
	"math/big"
	"os"
	"time"
)

type ServerCodeCertificateGenerator struct {
}

func (s *ServerCodeCertificateGenerator) generate() {
	// privateKey2048 := readPrivateKey(server.PRIVATE_KEY_2048_PATH)
	publicKey2048 := s.readPublicKey(server.PUBLIC_KEY_2048_PATH)
	privateKey4096 := s.readPrivateKey(server.PRIVATE_KEY_4096_PATH)
	publicKey4096 := s.readPublicKey(server.PUBLIC_KEY_4096_PATH)

	templateCert := s.readCertificate(server.JETBRAINS_CODE_CA_PATH)

	s.standardGenerateCaCert(templateCert, publicKey4096, privateKey4096, server.CODE_CA_PATH)

	log.Println("========== 签发子证书 ==========")
	certificate := s.readCertificate(server.CODE_CA_PATH)
	s.issueChildCertificate(certificate, templateCert, publicKey2048, privateKey4096, server.CODE_CERT_PATH)
}

func (s *ServerCodeCertificateGenerator) readPrivateKey(path string) *rsa.PrivateKey {
	keyPEM, err := os.ReadFile(path)
	if err != nil {
		logger.Error("Failed to read private key", err)
		return nil
	}
	block, _ := pem.Decode(keyPEM)
	privateKey, err := x509.ParsePKCS1PrivateKey(block.Bytes)
	if err != nil {
		logger.Error("Failed to parse private key", err)
		return nil
	}
	return privateKey
}

func (s *ServerCodeCertificateGenerator) readPublicKey(path string) *rsa.PublicKey {
	keyPEM, err := os.ReadFile(path)
	if err != nil {
		logger.Error("Failed to read public key", err)
		return nil
	}
	block, _ := pem.Decode(keyPEM)
	publicKey, err := x509.ParsePKCS1PublicKey(block.Bytes)
	if err != nil {
		logger.Error("Failed to parse public key", err)
		return nil
	}
	return publicKey
}

func (s *ServerCodeCertificateGenerator) readCertificate(path string) *x509.Certificate {
	certPEM, err := os.ReadFile(path)
	if err != nil {
		logger.Error("Failed to read certificate", err)
		return nil
	}
	block, _ := pem.Decode(certPEM)
	cert, err := x509.ParseCertificate(block.Bytes)
	if err != nil {
		logger.Error("Failed to parse certificate", err)
	}
	return cert
}

func (s *ServerCodeCertificateGenerator) standardGenerateCaCert(templateCert *x509.Certificate, publicKey *rsa.PublicKey, privateKey *rsa.PrivateKey, certPath string) {
	serialNumber := big.NewInt(time.Now().UnixNano())
	notBefore := templateCert.NotBefore
	notAfter := templateCert.NotAfter

	certTemplate := x509.Certificate{
		SerialNumber: serialNumber,
		Subject:      templateCert.Subject,
		Issuer:       templateCert.Issuer,
		NotBefore:    notBefore,
		NotAfter:     notAfter,
		KeyUsage:     x509.KeyUsageCertSign | x509.KeyUsageCRLSign,
		IsCA:         true,
	}

	certDER, err := x509.CreateCertificate(rand.Reader, &certTemplate, templateCert, publicKey, privateKey)
	if err != nil {
		logger.Error("Failed to create certificate", err)
		return
	}
	s.writeCertificate(certDER, certPath)
}

func (s *ServerCodeCertificateGenerator) issueChildCertificate(rootCaCert, jetbrainsCaCert *x509.Certificate, publicKey *rsa.PublicKey, issuerPrivateKey *rsa.PrivateKey, certPath string) {
	// issuerName := rootCaCert.Subject
	subjectName := pkix.Name{
		CommonName: "CN=lemon-from-20220801",
	}

	serialNumber := big.NewInt(time.Now().UnixNano())
	notBefore := time.Now().AddDate(0, 0, -1)
	notAfter := time.Now().AddDate(30, 0, 0)

	certTemplate := x509.Certificate{
		SerialNumber:          serialNumber,
		Subject:               subjectName,
		NotBefore:             notBefore,
		NotAfter:              notAfter,
		KeyUsage:              x509.KeyUsageDigitalSignature | x509.KeyUsageKeyEncipherment,
		ExtKeyUsage:           []x509.ExtKeyUsage{x509.ExtKeyUsageServerAuth},
		BasicConstraintsValid: true,
		IsCA:                  false,
	}

	authorityKeyId, err := x509.MarshalPKIXPublicKey(jetbrainsCaCert.PublicKey)
	if err != nil {
		logger.Error("Failed to marshal public key", err)
		return
	}
	subjectKeyId, err := x509.MarshalPKIXPublicKey(rootCaCert.PublicKey)
	if err != nil {
		logger.Error("Failed to marshal public key", err)
		return
	}
	certTemplate.SubjectKeyId = subjectKeyId
	certTemplate.AuthorityKeyId = authorityKeyId

	certDER, err := x509.CreateCertificate(rand.Reader, &certTemplate, rootCaCert, publicKey, issuerPrivateKey)
	if err != nil {
		logger.Error("Failed to create certificate", err)
		return
	}
	s.writeCertificate(certDER, certPath)
}

func (s *ServerCodeCertificateGenerator) writeCertificate(certDER []byte, path string) {
	certPEM := pem.EncodeToMemory(&pem.Block{Type: "CERTIFICATE", Bytes: certDER})
	if err := os.WriteFile(path, certPEM, 0644); err != nil {
		logger.Error("Failed to write certificate", err)
	}
}
