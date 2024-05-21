package server

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"encoding/xml"
	"errors"
	"io/ioutil"
	"os"
)

const (
	ServerUID                  = "lemon"
	LeaseContent               = "4102415999000:" + ServerUID
	PrivateKey2048Path         = "/path/to/private_key_2048.pem"
	CertPath                   = "/path/to/certificate.pem"
	ServerCertPath             = "/path/to/server_certificate.pem"
	ServerIntermediateCertPath = "/path/to/server_intermediate_certificate.pem"
)

// ReadPEMFile reads and decodes a PEM file to a byte slice
func readPEMFile(filePath string) ([]byte, error) {
	file, err := os.Open(filePath)
	if err != nil {
		return nil, err
	}
	defer file.Close()

	pemBytes, err := ioutil.ReadAll(file)
	if err != nil {
		return nil, err
	}

	block, _ := pem.Decode(pemBytes)
	if block == nil {
		return nil, errors.New("failed to decode PEM block")
	}

	return block.Bytes, nil
}

// GetPrivateKey reads a PEM encoded private key from a file
func getPrivateKey(filePath string) (*rsa.PrivateKey, error) {
	bytes, err := readPEMFile(filePath)
	if err != nil {
		return nil, err
	}

	privateKey, err := x509.ParsePKCS1PrivateKey(bytes)
	if err != nil {
		return nil, err
	}

	return privateKey, nil
}

// GetCertBase64 reads a certificate from a file, encodes it in base64
func getCertBase64(path string) (string, error) {
	certBytes, err := readPEMFile(path)
	if err != nil {
		return "", err
	}

	return base64.StdEncoding.EncodeToString(certBytes), nil
}

// SignContent signs the content using the specified RSA private key and returns a base64 encoded signature
func signContent(content string, privateKey *rsa.PrivateKey, hashType string) (string, error) {
	var hasher crypto.Hash
	switch hashType {
	case "SHA1withRSA":
		hasher = crypto.SHA1
	case "SHA256withRSA":
		hasher = crypto.SHA256
	case "SHA512withRSA":
		hasher = crypto.SHA512
	default:
		return "", errors.New("unsupported signature algorithm")
	}

	hasherNew := hasher.New()
	hasherNew.Write([]byte(content))
	hashed := hasherNew.Sum(nil)

	signature, err := rsa.SignPKCS1v15(rand.Reader, privateKey, hasher, hashed)
	if err != nil {
		return "", err
	}

	return base64.StdEncoding.EncodeToString(signature), nil
}

// GetLeaseSignature creates a lease signature
func GetLeaseSignature() (string, error) {
	privateKey, err := getPrivateKey(PrivateKey2048Path)
	if err != nil {
		return "", err
	}
	signature, err := signContent(LeaseContent, privateKey, "SHA512withRSA")
	if err != nil {
		return "", err
	}

	certBase64, err := getCertBase64(CertPath)
	if err != nil {
		return "", err
	}

	return "SHA512withRSA-" + signature + "-" + certBase64, nil
}

// GetSignXml marshals XML and appends a digital signature comment
func GetSignXml(v interface{}) (string, error) {
	xmlOutput, err := xml.MarshalIndent(v, "", "  ")
	if err != nil {
		return "", err
	}

	xmlString := string(xmlOutput)
	privateKey, err := getPrivateKey(PrivateKey2048Path)
	if err != nil {
		return "", err
	}

	signature, err := signContent(xmlString, privateKey, "SHA1withRSA")
	if err != nil {
		return "", err
	}

	certBase64, err := getCertBase64(ServerCertPath)
	if err != nil {
		return "", err
	}

	intermediateCertBase64, err := getCertBase64(ServerIntermediateCertPath)
	if err != nil {
		return "", err
	}

	return "<!-- SHA1withRSA-" + signature + "-" + certBase64 + "-" + intermediateCertBase64 + " -->\n" + xmlString, nil
}
