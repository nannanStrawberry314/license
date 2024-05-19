FROM golang AS go-builder
WORKDIR /app
ENV GO111MODULE=on \
    GOPROXY=https://goproxy.cn,direct
COPY go.mod go.sum ./
RUN go mod download
COPY . .
ARG TARGETOS
ARG TARGETARCH
ENV CGO_ENABLED=0
RUN GOOS=${TARGETOS} GOARCH=${TARGETARCH} go build -ldflags "-s -w -extldflags '-static'" -o license ./main.go


FROM alpine AS runner
WORKDIR /app
COPY --from=go-builder /app/license ./license
COPY ./files /app/files
RUN apk update \
    && apk upgrade \
    && apk add --no-cache ca-certificates tzdata \
    && update-ca-certificates 2>/dev/null || true \
RUN mkdir -p /data
RUN ["chmod", "+x", "/app/license"]
ENTRYPOINT ["/app/license"]

