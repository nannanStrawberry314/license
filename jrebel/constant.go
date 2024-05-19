package jrebel

// ServerConstant holds various constants related to the server configuration.
type ServerConstant struct{}

// SERVER_VERSION defines the current version of the server.
const SERVER_VERSION = "3.2.4"

// SERVER_PROTOCOL_VERSION defines the protocol version used by the server.
const SERVER_PROTOCOL_VERSION = "1.1"

// JREBEL_LEASES_HANDLER_SIGNATURE defines the signature for the JRebel leases handler.
// const JREBEL_LEASES_HANDLER_SIGNATURE = "OJE9wGg2xncSb+VgnYT+9HGCFaLOk28tneMFhCbpVMKoC/Iq4LuaDKPirBjG4o394/UjCDGgTBpIrzcXNPdVxVr8PnQzpy7ZSToGO8wv/KIWZT9/ba7bDbA8/RZ4B37YkCeXhjaixpmoyz/CIZMnei4q7oWR7DYUOlOcEWDQhiY="

// SERVER_RANDOMNESS defines a random string used in the server process.
const SERVER_RANDOMNESS = "H2ulzLlh7E0="

// SERVER_GUID defines a globally unique identifier for the server.
const SERVER_GUID = "a1b4aea8-b031-4302-b602-670a990272cb"

// SEAT_POOL_TYPE defines the type of seat pool used.
const SEAT_POOL_TYPE = "standalone"

// GROUP_TYPE defines the type of group management used.
const GROUP_TYPE = "managed"

// STATUS_CODE defines the default status code returned by server operations.
const STATUS_CODE = "SUCCESS"

// COMPANY defines the company name associated with the server.
const COMPANY = "LemonInc"

// LICENSE_VALID_FROM defines the start date for the license validity.
const LEASES_PRIVATE_KEY = `-----BEGIN RSA PRIVATE KEY-----
MIICXAIBAAKBgQDQ93CP6SjEneDizCF1P/MaBGf582voNNFcu8oMhgdTZ/N6qa6O7XJDr1FSCyaDdKSsPCdxPK7Y4Usq/fOPas2kCgYcRS/iebrtPEFZ/7TLfk39H
LuTEjzo0/CNvjVsgWeh9BYznFaxFDLx7fLKqCQ6w1OKScnsdqwjpaXwXqiulwIDAQABAoGATOQvvBSMVsTNQkbgrNcqKdGjPNrwQtJkk13aO/95ZJxkgCc9vwPqPr
OdFbZappZeHa5IyScOI2nLEfe+DnC7V80K2dBtaIQjOeZQt5HoTRG4EHQaWoDh27BWuJoip5WMrOd+1qfkOtZoRjNcHl86LIAh/+3vxYyebkug4UHNGPkCQQD+N4Z
UkhKNQW7mpxX6eecitmOdN7Yt0YH9UmxPiW1LyCEbLwduMR2tfyGfrbZALiGzlKJize38shGC1qYSMvZFAkEA0m6psWWiTUWtaOKMxkTkcUdigalZ9xFSEl6jXFB9
4AD+dlPS3J5gNzTEmbPLc14VIWJFkO+UOrpl77w5uF2dKwJAaMpslhnsicvKMkv31FtBut5iK6GWeEafhdPfD94/bnidpP362yJl8Gmya4cI1GXvwH3pfj8S9hJVA
5EFvgTB3QJBAJP1O1uAGp46X7Nfl5vQ1M7RYnHIoXkWtJ417Kb78YWPLVwFlD2LHhuy/okT4fk8LZ9LeZ5u1cp1RTdLIUqAiAECQC46OwOm87L35yaVfpUIjqg/1g
sNwNsj8HvtXdF/9d30JIM3GwdytCvNRLqP35Ciogb9AO8ke8L6zY83nxPbClM=
-----END RSA PRIVATE KEY-----`
