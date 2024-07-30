# Keycloak OTP SMS Authentication

#### 1. Compile dan Build
```
$ mvn -e clean install
```

#### 2. Integrasi dengan Keycloak
```
## Buat Tema baru
mkdir ${KEYCLOAK_HOME}/themes/otpsms
cp ${PROJECT_HOME}/resources/themes/otpsms/* ${KEYCLOAK_HOME}/themes/otpsms/

## Deploy Jar ke Keycloak
cp ${PROJECT_HOME}/target/otp-sms-1.0.0.jar ${KEYCLOAK_HOME}/standalone/deployments/otp-sms-1.0.0.jar
```

#### 3. Restart Keycloak Server