package server

import "encoding/xml"

// License holds information about the license.
type License struct {
	PowerConfRule  string
	ActivationCode string
}

// ObtainTicket holds information about the ticket.
type ObtainTicket struct {
	Action                   string
	ConfirmationStamp        string
	LeaseSignature           string
	Message                  string
	ProlongationPeriod       string
	ResponseCode             string
	Salt                     string
	ServerLease              string
	ServerUid                string
	TicketId                 string
	TicketProperties         string
	ValidationDeadlinePeriod string
	ValidationPeriod         string
}

// Ping holds information about the ping.
type Ping struct {
	Action                   string
	ConfirmationStamp        string
	LeaseSignature           string
	Message                  string
	ResponseCode             string
	Salt                     string
	ServerLease              string
	ServerUid                string
	ValidationDeadlinePeriod string
	ValidationPeriod         string
}

// ProlongTicket holds information about the ticket prolongation.
type ProlongTicket struct {
	XMLName                  xml.Name `xml:"ProlongTicket"`
	Action                   string   `xml:"action"`
	ConfirmationStamp        string   `xml:"confirmationStamp"`
	LeaseSignature           string   `xml:"leaseSignature"`
	Message                  string   `xml:"message"`
	ResponseCode             string   `xml:"responseCode"`
	Salt                     string   `xml:"salt"`
	ServerLease              string   `xml:"serverLease"`
	ServerUid                string   `xml:"serverUid"`
	ValidationDeadlinePeriod string   `xml:"validationDeadlinePeriod"`
	ValidationPeriod         string   `xml:"validationPeriod"`
}

// ReleaseTicket holds information about the ticket release.
type ReleaseTicket struct {
	Action                   string
	ConfirmationStamp        string
	LeaseSignature           string
	Message                  string
	ResponseCode             string
	Salt                     string
	ServerLease              string
	ServerUid                string
	ValidationDeadlinePeriod string
	ValidationPeriod         string
}
