package jrebel

// LeasesHandlerVO represents a data structure to handle various details related to JRebel leases.
type LeasesHandlerVO struct {
	ServerVersion         string   `json:"serverVersion"`
	ServerProtocolVersion string   `json:"serverProtocolVersion"`
	ServerGuid            string   `json:"serverGuid"`
	GroupType             string   `json:"groupType"`
	ID                    int64    `json:"id"`
	LicenseType           int64    `json:"licenseType"`
	EvaluationLicense     bool     `json:"evaluationLicense"`
	Signature             string   `json:"signature"`
	ServerRandomness      string   `json:"serverRandomness"`
	SeatPoolType          string   `json:"seatPoolType"`
	StatusCode            string   `json:"statusCode"`
	Offline               bool     `json:"offline"`
	ValidFrom             int64    `json:"validFrom"`
	ValidUntil            int64    `json:"validUntil"`
	Company               string   `json:"company"`
	OrderId               string   `json:"orderId"`
	ZeroIds               []string `json:"zeroIds"`
	LicenseValidFrom      int64    `json:"licenseValidFrom"`
	LicenseValidUntil     int64    `json:"licenseValidUntil"`
}

// LeasesOneHandlerVO represents a data structure to handle specific details related to a single JRebel lease.
type LeasesOneHandlerVO struct {
	ServerVersion         string `json:"serverVersion"`
	ServerProtocolVersion string `json:"serverProtocolVersion"`
	ServerGuid            string `json:"serverGuid"`
	GroupType             string `json:"groupType"`
	StatusCode            string `json:"statusCode"`
	Msg                   string `json:"msg"`
	StatusMessage         string `json:"statusMessage"`
	Company               string `json:"company"`
}

// ValidateHandlerVO represents a data structure to handle the validation details for JRebel licenses.
type ValidateHandlerVO struct {
	ServerVersion         string `json:"serverVersion"`
	ServerProtocolVersion string `json:"serverProtocolVersion"`
	ServerGuid            string `json:"serverGuid"`
	GroupType             string `json:"groupType"`
	StatusCode            string `json:"statusCode"`
	Company               string `json:"company"`
	CanGetLease           bool   `json:"canGetLease"`
	LicenseType           string `json:"licenseType"`
	EvaluationLicense     bool   `json:"evaluationLicense"`
	SeatPoolType          string `json:"seatPoolType"`
}
