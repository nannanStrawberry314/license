package code

// PluginEntity holds information about the plugin entity.
type PluginEntity struct {
	ID              int64 `gorm:"primaryKey"`
	PluginApiDetail string
	PluginID        int64
	PluginName      string
	PluginCode      string
}

// TableName 定义表名
func (PluginEntity) TableName() string {
	return "sys_jetbrains_paid_plugin"
}

// ProductEntity holds information about the product entity.
type ProductEntity struct {
	ID            int64 `gorm:"primaryKey"`
	ProductCode   string
	ProductName   string
	ProductDetail string
}

// TableName 定义表名
func (ProductEntity) TableName() string {
	return "sys_jetbrains_product"
}

type Product struct {
	Code         string
	FallbackDate string
	PaidUpTo     string
	Extended     bool
}

func NewProduct(code, date string) *Product {
	return &Product{
		Code:         code,
		FallbackDate: date,
		PaidUpTo:     date,
		Extended:     true,
	}
}

// LicensePart holds information about the license part.
type LicensePart struct {
	LicenseID          string
	LicenseeName       string
	LicenseeType       string
	AssigneeName       string
	AssigneeEmail      string
	LicenseRestriction string
	CheckConcurrentUse bool
	Products           []*Product
	Metadata           string
	Hash               string
	GracePeriodDays    int
	AutoProlongated    bool
	IsAutoProlongated  bool
	Trial              bool
	AiAllowed          bool
}

func NewLicensePart(licenseID, licenseeName string, codes []string, date string) *LicensePart {
	products := make([]*Product, len(codes))
	for i, code := range codes {
		products[i] = NewProduct(code, date)
	}

	return &LicensePart{
		LicenseID:          licenseID,
		LicenseeName:       licenseeName,
		Products:           products,
		LicenseeType:       "PERSONAL",
		AssigneeName:       "",
		CheckConcurrentUse: false,
		Metadata:           "0120231110PSAA003008",
		Hash:               "51149839/0:-1370131430",
		GracePeriodDays:    7,
		AutoProlongated:    true,
		IsAutoProlongated:  true,
		Trial:              false,
		AiAllowed:          true,
	}
}
