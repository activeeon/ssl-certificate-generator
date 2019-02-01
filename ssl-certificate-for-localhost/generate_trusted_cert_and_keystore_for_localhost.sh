#!/bin/bash

######################################################################
######## Generate Activeeon root certificate (CA certificate) ########
######################################################################
## Generate root key
openssl genrsa -passout file:secret -des3 -out ActiveeonCA.key 4096

## Generate root certificate
openssl req -passin file:secret -x509 -new -nodes -key ActiveeonCA.key -sha256 -days 1095 -out ActiveeonRootCA.crt -subj '/C=FR/ST=Nice/L=Sophia-Antipolis/O=ActiveeonCA/OU=ActiveeonCA/emailAddress=activeeonCA@activeeon.com/CN=localhost'

######################################################################
############ Generate certificate signing request (csr) ##############
######################################################################
## Generate signing key for localhost
openssl genrsa -out localhost.key 2048

## Generate csr
openssl req -new -sha256 -key localhost.key -out localhost.csr -subj '/C=FR/ST=Nice/L=Sophia-Antipolis/O=Activeeon/OU=Activeeon/emailAddress=activeeon@activeeon.com/CN=localhost'

## Check csr output
openssl req -in localhost.csr -noout -text

######################################################################
# Generate localhost certificate using the CSR and root certificate ##
######################################################################
## Generate certificate for localhost
openssl x509 -passin file:secret -req -in localhost.csr -CA ActiveeonRootCA.crt -CAkey ActiveeonCA.key -CAcreateserial -out localhost.crt -days 1095 -sha256 -extfile v3.ext 

## Check certificate output
openssl x509 -in localhost.crt -text -noout

######################################################################
#### Store localhost private key and certificate in a keystore #######
######################################################################
## Generate key store
openssl pkcs12  -passout file:secret  -inkey localhost.key -in localhost.crt -export -out keystore.pkcs12

keytool -importkeystore -srcstorepass $(< secret) -deststorepass $(< secret) -srckeystore keystore.pkcs12 -srcstoretype PKCS12 -destkeystore keystore
