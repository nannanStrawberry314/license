package server

import (
	"bytes"
	"crypto/rand"
	"crypto/x509"
	"encoding/pem"
	"io/ioutil"
	"math/big"
	"os"
	"time"
)

// CreateCertificate creates an X509 Certificate from a byte slice
func CreateCertificate(certBytes []byte) (*x509.Certificate, error) {
	cert, err := x509.ParseCertificate(certBytes)
	if err != nil {
		return nil, err
	}
	return cert, nil
}

// X509CertificateToPEM converts an X509 Certificate to a PEM format string
func X509CertificateToPEM(cert *x509.Certificate) (string, error) {
	buf := new(bytes.Buffer)
	err := pem.Encode(buf, &pem.Block{Type: "CERTIFICATE", Bytes: cert.Raw})
	if err != nil {
		return "", err
	}
	return buf.String(), nil
}

// WriteCertificate writes a certificate to a file in PEM format
func WriteCertificate(cert *x509.Certificate, certPath string) error {
	pemData := pem.EncodeToMemory(&pem.Block{Type: "CERTIFICATE", Bytes: cert.Raw})
	if pemData == nil {
		return os.ErrInvalid
	}
	return ioutil.WriteFile(certPath, pemData, 0644)
}

// CopyExtensionsAndCreateNewCertificate copies extensions from one certificate to a new one and signs it with a private key
func CopyExtensionsAndCreateNewCertificate(templateCertificate *x509.Certificate, privateKey interface{}) (*x509.Certificate, error) {
	// Here, you would typically adjust subject and issuer to suit the new certificate
	serialNumber, _ := rand.Int(rand.Reader, new(big.Int).Lsh(big.NewInt(1), 128))
	newCertificate := &x509.Certificate{
		SerialNumber:    serialNumber,
		Issuer:          templateCertificate.Issuer,
		Subject:         templateCertificate.Subject,
		NotBefore:       time.Now(),
		NotAfter:        time.Now().AddDate(1, 0, 0), // 1 year validity
		KeyUsage:        templateCertificate.KeyUsage,
		ExtKeyUsage:     templateCertificate.ExtKeyUsage,
		ExtraExtensions: templateCertificate.Extensions, // copying extensions directly
	}

	// Create the new certificate signed with the private key
	certDER, err := x509.CreateCertificate(rand.Reader, newCertificate, templateCertificate, &templateCertificate.PublicKey, privateKey)
	if err != nil {
		return nil, err
	}

	return x509.ParseCertificate(certDER)
}
