package v1

var (
	// ErrSuccess common errors
	ErrSuccess             = newError(0, "ok")
	ErrBadRequest          = newError(400, "Bad Request")
	ErrUnauthorized        = newError(401, "Unauthorized")
	ErrForbidden           = newError(403, "Forbidden")
	ErrNotFound            = newError(404, "Not Found")
	ErrMethodNotAllowed    = newError(405, "Method Not Allowed")
	ErrInternalServerError = newError(500, "Internal Server Error")

	// ErrEmailAlreadyUse more biz errors
	ErrEmailAlreadyUse   = newError(1001, "The email is already in use.")
	ErrCannotRefresh     = newError(1002, "Can not refresh account.")
	ErrAccessTokenEmpty  = newError(1003, "Access token is empty.")
	ErrLoginFailed       = newError(1004, "Login failed.")
	ErrCannotDeleteToken = newError(1005, "There are associated accounts, please delete the associated accounts first.")
)
