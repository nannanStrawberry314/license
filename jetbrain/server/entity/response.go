package entity

import (
	"encoding/xml"
	"fmt"
	"license/jetbrain/server/util"
	"net/http"
	"time"
)

type BodySigner interface {
	SignBody(content []byte) []byte
}

type XMLTicket struct {
	Data any
}

func (r XMLTicket) Render(w http.ResponseWriter) error {
	r.WriteContentType(w)
	content, err := xml.Marshal(r.Data)
	if err != nil {
		return err
	}
	if value, ok := r.Data.(BodySigner); ok {
		_, err = w.Write(value.SignBody(content))
		if err != nil {
			return err
		}
		_, _ = w.Write([]byte("\n"))
	}
	_, err = w.Write(content)
	return err
}

func (r XMLTicket) WriteContentType(w http.ResponseWriter) {
	w.Header()["Content-Type"] = []string{"application/xml; charset=utf-8"}
}

func NewXMLTicket(data any) XMLTicket {
	return XMLTicket{Data: data}
}

type TicketResponse struct {
	fakeCert  *util.FakeCert
	MachineId string
}

func (t TicketResponse) SignBody(content []byte) []byte {
	return []byte(fmt.Sprintf("<!-- SHA1withRSA-%s-%s -->", t.fakeCert.SignWithRsaSha1(content), t.fakeCert.LsCARawBase64()))
}

func (t TicketResponse) ConfirmationStamp(machineId string) string {
	timeStamp := time.Now().UnixMilli()
	licenseStr := fmt.Sprintf("%d:%s", timeStamp, machineId)
	signatureBase64 := t.fakeCert.SignWithRsaSha1([]byte(licenseStr))
	return fmt.Sprintf("%s:SHA1withRSA:%s:%s", licenseStr, signatureBase64, t.fakeCert.LsCARawBase64())
}

func (t TicketResponse) leaseSignature(serverLease string) string {
	leaseSignature := t.fakeCert.SignWithRsaSha512([]byte(serverLease))
	return fmt.Sprintf("SHA512withRSA-%s-%s", leaseSignature, t.fakeCert.JpCARawBase64())
}

type BaseRequest struct {
	Salt      string `form:"salt"`
	UserName  string `form:"userName"`
	MachineId string `form:"machineId"`
}

type ObtainTicketResponse struct {
	TicketResponse     `xml:"-"`
	Action             string `xml:"action"`
	ConfirmationStamp  string `xml:"confirmationStamp"`
	LeaseSignature     string `xml:"leaseSignature"`
	Message            string `xml:"message"`
	ProlongationPeriod int    `xml:"prolongationPeriod,omitempty"`
	ResponseCode       string `xml:"responseCode"`
	Salt               string `xml:"salt"`
	ServerLease        string `xml:"serverLease"`
	ServerUid          string `xml:"serverUid"`
	TicketID           string `xml:"ticketId"`
	TicketProperties   string `xml:"ticketProperties"`
	ValidationDeadline int    `xml:"validationDeadlinePeriod"`
	ValidationPeriod   int    `xml:"validationPeriod"`
}

func NewObtainTicketResponse(req *BaseRequest, fakeCert *util.FakeCert) *ObtainTicketResponse {
	serverLease := "4102415999000:" + fakeCert.ServerUID
	ticketId := "12345"
	ticketResponse := TicketResponse{fakeCert: fakeCert}

	return &ObtainTicketResponse{
		TicketResponse:     ticketResponse,
		Action:             "NONE",
		ConfirmationStamp:  ticketResponse.ConfirmationStamp(req.MachineId),
		LeaseSignature:     ticketResponse.leaseSignature(serverLease),
		Message:            "",
		ProlongationPeriod: 600000,
		ResponseCode:       "OK",
		Salt:               req.Salt,
		ServerLease:        serverLease,
		ServerUid:          fakeCert.ServerUID,
		TicketID:           ticketId,
		TicketProperties:   fmt.Sprintf("licensee=%s", req.UserName),
		ValidationDeadline: -1,
		ValidationPeriod:   600000,
	}
	// 	licensee=%s	licenseeType=5	metadata=0120211231PSAN000005
}

type PingResponse struct {
	TicketResponse     `xml:"-"`
	Action             string `xml:"action"`
	ConfirmationStamp  string `xml:"confirmationStamp"`
	LeaseSignature     string `xml:"leaseSignature"`
	Message            string `xml:"message"`
	ResponseCode       string `xml:"responseCode"`
	Salt               string `xml:"salt"`
	ServerLease        string `xml:"serverLease"`
	ServerUid          string `xml:"serverUid"`
	ValidationDeadline int    `xml:"validationDeadlinePeriod"`
	ValidationPeriod   int    `xml:"validationPeriod"`
}

func NewPingResponse(req *BaseRequest, fakeCert *util.FakeCert) *PingResponse {
	serverLease := "4102415999000:" + fakeCert.ServerUID
	ticketResponse := TicketResponse{fakeCert: fakeCert}

	return &PingResponse{
		TicketResponse:     ticketResponse,
		Action:             "NONE",
		ConfirmationStamp:  ticketResponse.ConfirmationStamp(req.MachineId),
		LeaseSignature:     ticketResponse.leaseSignature(serverLease),
		Message:            "",
		ResponseCode:       "OK",
		Salt:               req.Salt,
		ServerLease:        serverLease,
		ServerUid:          fakeCert.ServerUID,
		ValidationDeadline: -1,
		ValidationPeriod:   600000,
	}
}

type ReleaseTicketResponse struct {
	TicketResponse     `xml:"-"`
	Action             string `xml:"action"`
	ConfirmationStamp  string `xml:"confirmationStamp"`
	LeaseSignature     string `xml:"leaseSignature"`
	Message            string `xml:"message"`
	ResponseCode       string `xml:"responseCode"`
	Salt               string `xml:"salt"`
	ServerLease        string `xml:"serverLease"`
	ServerUid          string `xml:"serverUid"`
	ValidationDeadline int    `xml:"validationDeadlinePeriod"`
	ValidationPeriod   int    `xml:"validationPeriod"`
}

func NewReleaseTicketResponse(req *BaseRequest, fakeCert *util.FakeCert) *ReleaseTicketResponse {
	serverLease := "4102415999000:" + fakeCert.ServerUID
	ticketResponse := TicketResponse{fakeCert: fakeCert}

	return &ReleaseTicketResponse{
		TicketResponse:     ticketResponse,
		Action:             "NONE",
		ConfirmationStamp:  ticketResponse.ConfirmationStamp(req.MachineId),
		LeaseSignature:     ticketResponse.leaseSignature(serverLease),
		Message:            "",
		ResponseCode:       "OK",
		Salt:               req.Salt,
		ServerLease:        serverLease,
		ServerUid:          fakeCert.ServerUID,
		ValidationDeadline: -1,
		ValidationPeriod:   600000,
	}
}
