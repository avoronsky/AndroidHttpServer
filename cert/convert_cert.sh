#!/bin/bash

PRIVKEY_FILE="privkey.pem"
FULLCHAIN_FILE="fullchain.pem"

if [ ! -f "$PRIVKEY_FILE" ]; then
    echo "ERROR: $PRIVKEY_FILE doesn't exists in a dir."
    exit 1
fi

if [ ! -f "$FULLCHAIN_FILE" ]; then
  echo "ERROR: $FULLCHAIN_FILE doesn't exists in a dir."
  exit 1
fi

ALIAS="demo_alias"
PASSWD="demo_password"

# Generate a PKCS12 certificate bundle from the cert and private key
openssl pkcs12 -export -out demo_keystore.p12 -inkey $PRIVKEY_FILE -in $FULLCHAIN_FILE -name $ALIAS \
  -passin pass:$PASSWD -passout pass:$PASSWD

# Convert to BKS
keytool -importkeystore -alias $ALIAS -srckeystore demo_keystore.p12 -srcstoretype PKCS12 \
  -srcstorepass $PASSWD -storepass $PASSWD \
  -deststoretype BKS -providerpath bcprov-jdk15on-166.jar \
  -provider org.bouncycastle.jce.provider.BouncyCastleProvider -destkeystore demo_keystore.bks
