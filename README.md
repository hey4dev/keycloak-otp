# Keycloak OTP SMS Authentication

#### 1. Compile & Build
```
$ mvn -e clean install
```

#### 2. Integration with Keycloak
```
## Create a new theme
mkdir ${KEYCLOAK_HOME}/themes/otpsms
cp ${PROJECT_HOME}/resources/themes/otpsms/* ${KEYCLOAK_HOME}/themes/otpsms/

## Deploy Jar to Keycloak
cp ${PROJECT_HOME}/target/otp-sms-1.0.0.jar ${KEYCLOAK_HOME}/standalone/deployments/otp-sms-1.0.0.jar
```

#### 3. Restart Keycloak Server