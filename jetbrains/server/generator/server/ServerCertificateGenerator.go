package server

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

type ServerCertificateGenerator struct {
}

func (s *ServerCertificateGenerator) generate() {
	privateKey2048 := s.readPrivateKey(server.PrivateKey2048Path)
	publicKey2048 := s.readPublicKey(server.PUBLIC_KEY_2048_PATH)
	privateKey4096 := s.readPrivateKey(server.PRIVATE_KEY_4096_PATH)
	publicKey4096 := s.readPublicKey(server.PUBLIC_KEY_4096_PATH)

	log.Println("========== 生成License Servers CA证书 ==========")
	templateCert := s.readCertificate(server.JETBRAINS_SERVER_CA_PATH)

	log.Println("========== 1、生成License Servers CA证书 ==========")
	s.generateRootCertificate(templateCert, publicKey4096, privateKey4096, server.SERVER_CA_PATH)

	log.Println("========== 2、生成服务器中间证书 ==========")
	myCACert := s.readCertificate(server.SERVER_CA_PATH)
	s.issueChildCertificate("lsrv-prod-till-20280326-intermediate", myCACert, templateCert, publicKey2048, privateKey4096, server.ServerIntermediateCertPath, true)

	log.Println("========== 3、生成服务器证书 ==========")
	myIntermediateCert := s.readCertificate(server.SERVER_INTERMEDIATE_CERT_PATH)
	s.issueChildCertificate("lemon.lsrv.jetbrains.com", myIntermediateCert, myCACert, publicKey2048, privateKey2048, server.SERVER_CERT_PATH, false)
}

func (s *ServerCertificateGenerator) readPrivateKey(path string) *rsa.PrivateKey {
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

func (s *ServerCertificateGenerator) readPublicKey(path string) *rsa.PublicKey {
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

func (s *ServerCertificateGenerator) readCertificate(path string) *x509.Certificate {
	certPEM, err := os.ReadFile(path)
	if err != nil {
		logger.Error("Failed to read certificate", err)
		return nil
	}
	block, _ := pem.Decode(certPEM)
	cert, err := x509.ParseCertificate(block.Bytes)
	if err != nil {
		logger.Error("Failed to parse certificate", err)
		return nil
	}
	return cert
}

func (s *ServerCertificateGenerator) generateRootCertificate(templateCert *x509.Certificate, publicKey *rsa.PublicKey, privateKey *rsa.PrivateKey, certPath string) {
	certTemplate := x509.Certificate{
		SerialNumber: templateCert.SerialNumber,
		Subject:      templateCert.Subject,
		NotBefore:    templateCert.NotBefore,
		NotAfter:     templateCert.NotAfter,
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

func (s *ServerCertificateGenerator) issueChildCertificate(subName string, issuerCert *x509.Certificate, templateCert *x509.Certificate, publicKey *rsa.PublicKey, issuerPrivateKey *rsa.PrivateKey, certPath string, isIntermediate bool) {
	notBefore := time.Now().AddDate(0, 0, -1)
	notAfter := time.Now().AddDate(30, 0, 0)

	subject := pkix.Name{
		CommonName: subName,
	}

	certTemplate := x509.Certificate{
		SerialNumber: big.NewInt(time.Now().Unix()),
		Subject:      subject,
		NotBefore:    notBefore,
		NotAfter:     notAfter,
		KeyUsage:     x509.KeyUsageDigitalSignature | x509.KeyUsageKeyEncipherment | x509.KeyUsageKeyAgreement,
	}

	if isIntermediate {
		certTemplate.IsCA = true
		certTemplate.KeyUsage |= x509.KeyUsageCertSign | x509.KeyUsageCRLSign
		certTemplate.BasicConstraintsValid = true
	}

	certDER, err := x509.CreateCertificate(rand.Reader, &certTemplate, issuerCert, publicKey, issuerPrivateKey)
	if err != nil {
		logger.Error("Failed to create certificate", err)
		return
	}
	s.writeCertificate(certDER, certPath)
}

func (s *ServerCertificateGenerator) writeCertificate(certDER []byte, path string) {
	certPEM := pem.EncodeToMemory(&pem.Block{Type: "CERTIFICATE", Bytes: certDER})
	if err := os.WriteFile(path, certPEM, 0644); err != nil {
		logger.Error("Failed to write certificate", err)
	}
}
