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

// 生成power.conf配置
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
	JetProfileCAPath    = config.GetConfig().DataDir + "/jetbrainsCodeCACert.pem"
	LicenseServerCAPath = config.GetConfig().DataDir + "/jetbrainsServerCACert.pem"
	PrivateKeyPath      = config.GetConfig().DataDir + "/ca.key"
	JPCAPath            = config.GetConfig().DataDir + "/jp-ca.crt"
	LSCAPath            = config.GetConfig().DataDir + "/ls-ca.crt"
)

type FakeCert struct {
	JetProfileCA    *x509.Certificate
	LicenseServerCA *x509.Certificate
	JpCA            *x509.Certificate
	LsCA            *x509.Certificate
	privateKey      *rsa.PrivateKey

	ServerUID string
}

func (c *FakeCert) LoadOrGeneratePrivateKey() {
	var err error
	pemFile, err := ReadPemFile(PrivateKeyPath)
	if err != nil {
		c.privateKey, err = rsa.GenerateKey(rand.Reader, 4096)
		if err != nil {
			panic(err)
		}
		pkcs1PrivateKey := x509.MarshalPKCS1PrivateKey(c.privateKey)
		privateKeyPEM := pem.EncodeToMemory(&pem.Block{Type: "RSA PRIVATE KEY", Bytes: pkcs1PrivateKey})
		if err = os.WriteFile(PrivateKeyPath, privateKeyPEM, 0600); err != nil {
			panic(err)
		}
	} else {
		c.privateKey, err = x509.ParsePKCS1PrivateKey(pemFile)
		if err != nil {
			panic(err)
		}
	}
}

func (c *FakeCert) LoadRootCA() (err error) {
	c.JetProfileCA, err = ReadCertFile(JetProfileCAPath)
	if err != nil {
		return err
	}
	c.LicenseServerCA, err = ReadCertFile(LicenseServerCAPath)
	if err != nil {
		return err
	}
	return
}

func (c *FakeCert) LoadMyCA() (err error) {
	c.JpCA, err = ReadCertFile(JPCAPath)
	if err != nil {
		return err
	}
	c.LsCA, err = ReadCertFile(LSCAPath)
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

func (c *FakeCert) GenerateJetCA() (err error) {
	// 判断文件是否存在，不存在则生成
	logger.Info("generateJetCA")
	if !fileExists(JPCAPath) {
		jetCert, err := GenerateRootCertificate(c.privateKey, "lemon", c.JetProfileCA.Issuer.CommonName)
		if err != nil {
			return err
		}
		jetCertPEM := pem.EncodeToMemory(&pem.Block{Type: "CERTIFICATE", Bytes: jetCert})
		if err = os.WriteFile(JPCAPath, jetCertPEM, 0600); err != nil {
			return err
		}
	}
	logger.Info("generateJetCA done")

	logger.Info("generateLSCA")
	if !fileExists(LSCAPath) {
		subject := fmt.Sprintf("%s.lsrv.jetbrains.com", "lemon")
		lsCert, err := GenerateRootCertificate(c.privateKey, subject, c.LicenseServerCA.Issuer.CommonName)
		if err != nil {
			return err
		}
		lsCertPEM := pem.EncodeToMemory(&pem.Block{Type: "CERTIFICATE", Bytes: lsCert})
		if err = os.WriteFile(LSCAPath, lsCertPEM, 0600); err != nil {
			return err
		}
	}
	logger.Info("generateLSCA done")
	return nil
}

func (c *FakeCert) generatePower() {
	power1 := GeneratePowerResult(c.JpCA, c.JetProfileCA)
	power2 := GeneratePowerResult(c.LsCA, c.LicenseServerCA)
	_, _ = fmt.Fprintf(os.Stdout, "[Result]\n%s\n[Result]\n%s\n", power1, power2)
}

func (c *FakeCert) SignWithRsaSha1(data []byte) string {
	hashed := sha1.Sum(data)
	signature, err := rsa.SignPKCS1v15(rand.Reader, c.privateKey, crypto.SHA1, hashed[:])
	if err != nil {
		panic(err)
	}
	return base64.StdEncoding.EncodeToString(signature)
}

func (c *FakeCert) SignWithRsaSha512(data []byte) string {
	hashed := sha512.Sum512(data)
	signature, err := rsa.SignPKCS1v15(rand.Reader, c.privateKey, crypto.SHA512, hashed[:])
	if err != nil {
		panic(err)
	}
	return base64.StdEncoding.EncodeToString(signature)
}

func (c *FakeCert) JpCARawBase64() string {
	return base64.StdEncoding.EncodeToString(c.JpCA.Raw)
}

func (c *FakeCert) LsCARawBase64() string {
	return base64.StdEncoding.EncodeToString(c.LsCA.Raw)
}

func (c *FakeCert) Generate() {
	c.LoadOrGeneratePrivateKey()
	err := c.LoadRootCA()
	if err != nil {
		panic(err)
	}
	err = c.GenerateJetCA()
	if err != nil {
		panic(err)
	}

	err = c.LoadMyCA()
	if err != nil {
		panic(err)
	}
	c.generatePower()
}

func (c *FakeCert) Load() {
	c.LoadOrGeneratePrivateKey()
	err := c.LoadRootCA()
	if err != nil {
		panic(err)
	}

	err = c.LoadMyCA()
	if err != nil {
		panic(err)
	}

	c.generatePower()
}
