#!/bin/bash

reset
echo "Ideally, to make things easier for demo purposes, use the same password/passpharse whenever prompted"
sleep 2
reset

# make a self-signed root CA
echo "Creating self signed root certificate/certification authority"
openssl req -x509 -sha256 -days 1825 -newkey rsa:2048 -keyout self-signed-CA.key -out self-signed-CA.crt
echo "Finished creating certification authority"
echo ""

# make a new key key
echo "Creating a key for the certificate to be generated."
openssl genrsa -out ca-signed-cert.key 2048
echo "Key creation done."
sleep 1
reset


# make a CSR (certificate signing request) using the generated key
echo "Create certificate signing request for generating a new certificate signed by the CA created above."
echo ""
echo "Make sure you don't enter a password when prompted for a password on the CSR. If you enter one,"
echo "a certificate requiring a password to be read will be generated, but nginx cannot prompt for a"
echo "password when running in batch mode."
sleep 2
echo ""
openssl req -key ca-signed-cert.key -new -out ca-signed-cert.csr
echo "CSR generation done."
sleep 1
reset

# make a file with params for cert creation to specify additional stuff, named ca-signed-cert.ext - file contents:
echo "Creating file specifying extensions to be included in the certificate that will be generated."
echo 'authorityKeyIdentifier = keyid,issuer
extendedKeyUsage = serverAuth, clientAuth
basicConstraints=CA:FALSE
subjectAltName = @alt_names
[alt_names]
DNS.1 = kbv.app.tbintra.net
DNS.1 = kbv-passive.app.tbintra.net
DNS.1 = kbv-switchtest.app.tbintra.net
' > ca-signed-cert.ext
echo "Extensions definition file created."
sleep 1
reset

# make a new cert using the CSR, the root CA and the extensions defined above
echo "Creating new certificate based on certificate signing request and extensions definition file."
echo "The initially created CA will be used as signer."
openssl x509 -req -CA self-signed-CA.crt -CAkey self-signed-CA.key -in ca-signed-cert.csr -out ca-signed-cert.crt -days 365 -CAcreateserial -extfile ca-signed-cert.ext
echo "Certificate creation done - this is the certificate that will be used by nginx running locally."
sleep 1
reset

echo "Now you need to copy ca-signed-cert.crt and ca-signed-cert.key into the location used in your nginx.conf file."
echo ""

