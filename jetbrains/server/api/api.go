package api

import (
	"github.com/gin-gonic/gin"
	"net/http"

	"license/jetbrains/server"
)

func prolongTicket(c *gin.Context) {
	machineId := c.PostForm("machineId")
	salt := c.PostForm("salt")

	leaseSignature, err := server.GetLeaseSignature()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get lease signature"})
		return
	}

	response := server.ProlongTicket{
		Action:                   "NONE",
		ConfirmationStamp:        "jetbrains.get(machineId)",
		LeaseSignature:           leaseSignature,
		Message:                  "",
		ResponseCode:             "OK",
		Salt:                     salt,
		ServerLease:              "LEASE_CONTENT", // Replace with actual lease content
		ServerUid:                "SERVER_UID",    // Replace with actual server UID
		ValidationDeadlinePeriod: "-1",
		ValidationPeriod:         "600000",
	}

	c.XML(http.StatusOK, response)
}

func getConfirmationStamp(machineId string) string {
	// Implement your logic here to generate confirmation stamp
	return "CONFIRMATION_STAMP"
}

func getLeaseSignature() string {
	// Implement your logic here to generate lease signature
	return "LEASE_SIGNATURE"
}
