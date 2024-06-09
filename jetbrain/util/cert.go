package util

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha1"
	"crypto/sha512"
	"crypto/x509"
	"crypto/x509/pkix"
	"encoding/base64"
	"encoding/pem"
	"fmt"
	"license/config"
	"license/logger"
	"math/big"
	"os"
	"time"
)

var Fake = &FakeCert{
	ServerUID: "lemon",
}

// GeneratePowerResult 生成power.conf配置
func GeneratePowerResult(cert, rootCA *x509.Certificate) string {
	x := (&big.Int{}).SetBytes(cert.Signature)
	z := rootCA.PublicKey.(*rsa.PublicKey).N
	y := rootCA.PublicKey.(*rsa.PublicKey).E
	r := &big.Int{}
	r.Exp(x, big.NewInt(int64(y)), cert.PublicKey.(*rsa.PublicKey).N)

	return fmt.Sprintf("EQUAL,%d,%d,%d->%d", x, y, z, r)
}

func GenerateRootCertificate(key *rsa.PrivateKey, subject, issuer string) ([]byte, error) {
	serialNumber, err := rand.Int(rand.Reader, new(big.Int).Lsh(big.NewInt(1), 80))
	if err != nil {
		return nil, fmt.Errorf("gen serialNumber err %e", err)
	}

	parent := x509.Certificate{
		SerialNumber: serialNumber,
		Subject:      pkix.Name{CommonName: issuer},
		NotBefore:    time.Now().Add(-24 * time.Hour),
		NotAfter:     time.Now().AddDate(10, 0, 0),
		KeyUsage:     x509.KeyUsageKeyEncipherment | x509.KeyUsageDigitalSignature,
		ExtKeyUsage:  []x509.ExtKeyUsage{x509.ExtKeyUsageServerAuth},
	}
	template := parent
	template.Subject = pkix.Name{CommonName: subject}

	certBytes, err := x509.CreateCertificate(rand.Reader, &template, &parent, &key.PublicKey, key)
	if err != nil {
		return nil, fmt.Errorf("CreateCertificate err %e", err)
	}
	return certBytes, nil
}

func ReadPemFile(filepath string) ([]byte, error) {
	certBytes, err := os.ReadFile(filepath)
	if err != nil {
		return nil, fmt.Errorf("read file %s err %w", filepath, err)
	}
	certBlock, _ := pem.Decode(certBytes)
	return certBlock.Bytes, nil
}

func ReadCertFile(filepath string) (*x509.Certificate, error) {
	pemFile, err := ReadPemFile(filepath)
	if err != nil {
		return nil, err
	}
	return x509.ParseCertificate(pemFile)
}

var (
	CodeRootCertPath   = config.GetConfig().DataDir + "/jetbrainsCodeCACert.pem"
	ServerRootCertPath = config.GetConfig().DataDir + "/jetbrainsServerCACert.pem"
	PrivateKeyPath     = config.GetConfig().DataDir + "/private.pem"
	PublicKeyPath      = config.GetConfig().DataDir + "/public.pem"
	CodeCertPath       = config.GetConfig().DataDir + "/code.pem"
	ServerCertPath     = config.GetConfig().DataDir + "/server.pem"
)

type FakeCert struct {
	CodeRootCert   *x509.Certificate
	ServerRootCert *x509.Certificate
	CodeCert       *x509.Certificate
	ServerCert     *x509.Certificate
	PrivateKey     *rsa.PrivateKey
	PublicKey      *rsa.PublicKey

	ServerUID string
}

func (c *FakeCert) LoadOrGenerate() {
	var err error
	pemFile, err := ReadPemFile(PrivateKeyPath)
	if err != nil {
		c.PrivateKey, err = rsa.GenerateKey(rand.Reader, 4096)
		if err != nil {
			panic(err)
		}
		pkcs1PrivateKey := x509.MarshalPKCS1PrivateKey(c.PrivateKey)
		privateKeyPEM := pem.EncodeToMemory(&pem.Block{Type: "RSA PRIVATE KEY", Bytes: pkcs1PrivateKey})
		if err = os.WriteFile(PrivateKeyPath, privateKeyPEM, 0600); err != nil {
			panic(err)
		}
	} else {
		c.PrivateKey, err = x509.ParsePKCS1PrivateKey(pemFile)
		if err != nil {
			panic(err)
		}
	}

	// Load or generate public key
	pemFile, err = ReadPemFile(PublicKeyPath)
	if err != nil {
		fmt.Println("Public key not found, generating new key...")
		pkixPublicKey, err := x509.MarshalPKIXPublicKey(&c.PrivateKey.PublicKey)
		if err != nil {
			panic(err)
		}
		publicKeyPEM := pem.EncodeToMemory(&pem.Block{Type: "PUBLIC KEY", Bytes: pkixPublicKey})
		if err = os.WriteFile(PublicKeyPath, publicKeyPEM, 0600); err != nil {
			panic(err)
		}
	} else {
		pub, err := x509.ParsePKIXPublicKey(pemFile)
		if err != nil {
			panic(err)
		}
		var ok bool
		c.PublicKey, ok = pub.(*rsa.PublicKey)
		if !ok {
			panic("not an RSA public key")
		}
	}

}

func (c *FakeCert) LoadRootCert() (err error) {
	c.CodeRootCert, err = ReadCertFile(CodeRootCertPath)
	if err != nil {
		return err
	}
	c.ServerRootCert, err = ReadCertFile(ServerRootCertPath)
	if err != nil {
		return err
	}
	return
}

func (c *FakeCert) LoadCert() (err error) {
	c.CodeCert, err = ReadCertFile(CodeCertPath)
	if err != nil {
		return err
	}
	c.ServerCert, err = ReadCertFile(ServerCertPath)
	if err != nil {
		return err
	}
	return
}

// 判断文件是否存在
func fileExists(filename string) bool {
	_, err := os.Stat(filename)
	if os.IsNotExist(err) {
		return false
	}
	return err == nil
}

func (c *FakeCert) GenerateRootCert() (err error) {
	// 判断文件是否存在，不存在则生成
	logger.Info("GenerateCodeCert")
	if !fileExists(CodeCertPath) {
		jetCert, err := GenerateRootCertificate(c.PrivateKey, "lemon", c.CodeRootCert.Issuer.CommonName)
		if err != nil {
			return err
		}
		jetCertPEM := pem.EncodeToMemory(&pem.Block{Type: "CERTIFICATE", Bytes: jetCert})
		if err = os.WriteFile(CodeCertPath, jetCertPEM, 0600); err != nil {
			return err
		}
	}
	logger.Info("GenerateCodeCert done")

	logger.Info("GenerateServerCert")
	if !fileExists(ServerCertPath) {
		subject := fmt.Sprintf("%s.lsrv.jetbrains.com", "lemon")
		lsCert, err := GenerateRootCertificate(c.PrivateKey, subject, c.ServerRootCert.Issuer.CommonName)
		if err != nil {
			return err
		}
		lsCertPEM := pem.EncodeToMemory(&pem.Block{Type: "CERTIFICATE", Bytes: lsCert})
		if err = os.WriteFile(ServerCertPath, lsCertPEM, 0600); err != nil {
			return err
		}
	}
	logger.Info("GenerateServerCert done")
	return nil
}

func (c *FakeCert) SignWithRsaSha1(data []byte) string {
	hashed := sha1.Sum(data)
	signature, err := rsa.SignPKCS1v15(rand.Reader, c.PrivateKey, crypto.SHA1, hashed[:])
	if err != nil {
		panic(err)
	}
	return base64.StdEncoding.EncodeToString(signature)
}

func (c *FakeCert) SignWithRsaSha512(data []byte) string {
	hashed := sha512.Sum512(data)
	signature, err := rsa.SignPKCS1v15(rand.Reader, c.PrivateKey, crypto.SHA512, hashed[:])
	if err != nil {
		panic(err)
	}
	return base64.StdEncoding.EncodeToString(signature)
}

func (c *FakeCert) CodeCertRawBase64() string {
	return base64.StdEncoding.EncodeToString(c.CodeCert.Raw)
}

func (c *FakeCert) ServerCertRawBase64() string {
	return base64.StdEncoding.EncodeToString(c.ServerCert.Raw)
}
