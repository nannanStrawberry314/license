package code

import (
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/asn1"
	"encoding/hex"
	"encoding/pem"
	"fmt"
	"license/jetbrains/server"
	"license/logger"
	"math/big"
	"os"
	"strings"
)

type ServerCodePowerConfRuleGenerator struct {
}

// func main() {
// 	rules, err := s.standardGenerateRules()
// 	if err != nil {
// 		logger.Error("Failed to generate rules", err)
// 		return
// 	}
// 	fmt.Println(rules)
// }

func (s *ServerCodePowerConfRuleGenerator) standardGenerateRules() (string, error) {
	certificate, err := s.readCertificate(server.CODE_CERT_PATH)
	if err != nil {
		return "", err
	}

	jbCACert, err := s.readCertificate(server.JETBRAINS_CODE_CA_PATH)
	if err != nil {
		return "", err
	}

	jbPublicKey := jbCACert.PublicKey.(*rsa.PublicKey)

	x := new(big.Int).SetBytes(certificate.Signature)
	y := jbPublicKey.E
	z := jbPublicKey.N

	tbsCertificate := certificate.RawTBSCertificate
	hash := sha256.Sum256(tbsCertificate)
	sha256Str := hex.EncodeToString(hash[:])

	transit, err := s.convertDataToASN1Format(sha256Str)
	if err != nil {
		return "", err
	}

	fillingStr := s.filling512(transit)
	r := new(big.Int)
	r.SetString(fillingStr, 16)

	rules := fmt.Sprintf("[Result]\n; Lemon active by code \nEQUAL,%s,%d,%s->%s", x.String(), y, z.String(), r.String())
	logger.Info("================== PowerConfRule Result ==================")
	logger.Info(rules)

	return rules, nil
}

func (s *ServerCodePowerConfRuleGenerator) readCertificate(path string) (*x509.Certificate, error) {
	certPEM, err := os.ReadFile(path)
	if err != nil {
		return nil, err
	}
	block, _ := pem.Decode(certPEM)
	if block == nil {
		return nil, fmt.Errorf("failed to parse certificate PEM")
	}
	return x509.ParseCertificate(block.Bytes)
}

func (s *ServerCodePowerConfRuleGenerator) convertDataToASN1Format(sha256Data string) (string, error) {
	// algorithmOid := asn1.ObjectIdentifier{2, 16, 840, 1, 101, 3, 4, 2, 1}
	innerSequence := asn1.RawValue{
		Class: asn1.ClassUniversal,
		Tag:   asn1.TagSequence,
		Bytes: []byte{
			0x06, 0x09, 0x60, 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x01, // OID: 2.16.840.1.101.3.4.2.1
			0x05, 0x00, // NULL
		},
	}

	sha256Bytes, err := hex.DecodeString(sha256Data)
	if err != nil {
		return "", err
	}

	octetString := asn1.RawValue{
		Class: asn1.ClassUniversal,
		Tag:   asn1.TagOctetString,
		Bytes: sha256Bytes,
	}

	outerSequence := asn1.RawValue{
		Class: asn1.ClassUniversal,
		Tag:   asn1.TagSequence,
		Bytes: append(innerSequence.Bytes, octetString.Bytes...),
	}

	encodedData, err := asn1.Marshal(outerSequence)
	if err != nil {
		return "", err
	}

	return hex.EncodeToString(encodedData), nil
}

func (s *ServerCodePowerConfRuleGenerator) filling512(target string) string {
	return s.filling(target, 512)
}

func (s *ServerCodePowerConfRuleGenerator) filling(target string, length int) string {
	count := length - len(target)/2 - 3
	return strings.ToUpper("01" + strings.Repeat("ff", count) + "00" + target)
}
