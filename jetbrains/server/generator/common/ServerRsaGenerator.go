package common

import (
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/pem"
	"fmt"
	"license/jetbrains/server"
	"license/logger"
	"os"
)

func main() {
	generateKeyPair(2048)
	generateKeyPair(4096)
}

// generateKeyPair generates and saves an RSA key pair of the given size.
func generateKeyPair(size int) {
	// Generate RSA Keys
	privateKey, err := rsa.GenerateKey(rand.Reader, size)
	if err != nil {
		logger.Error("Error generating RSA key pair", err)
		return
	}
	publicKey := &privateKey.PublicKey

	// Convert keys to PEM format
	publicKeyPem := pemEncode(x509.MarshalPKCS1PublicKey(publicKey), "PUBLIC KEY")
	privateKeyPem := pemEncode(x509.MarshalPKCS1PrivateKey(privateKey), "RSA PRIVATE KEY")

	// Paths for public and private key files
	publicKeyFile := fmt.Sprintf("%s/publicKey%d.pem", server.PATH, size)
	privateKeyFile := fmt.Sprintf("%s/privateKey%d.pem", server.PATH, size)

	// Save the public key
	if err := os.WriteFile(publicKeyFile, publicKeyPem, 0644); err != nil {
		logger.Error("Failed to write public key to file", err)
		return
	}

	// Save the private key
	if err := os.WriteFile(privateKeyFile, privateKeyPem, 0644); err != nil {
		logger.Error("Failed to write private key to file", err)
		return
	}
}

// pemEncode encodes a DER encoded key into PEM format.
func pemEncode(der []byte, keyType string) []byte {
	block := &pem.Block{
		Type:  keyType,
		Bytes: der,
	}
	return pem.EncodeToMemory(block)
}
