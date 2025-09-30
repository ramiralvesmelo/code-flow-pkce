# 🔐 App PKS – Demo com Keycloak (PKCE + Resource Server)

Este projeto demonstra a integração de aplicações **Spring Boot** com o **Keycloak**, utilizando:

* 🌐 **PKCE (Proof Key for Code Exchange)** para o cliente público (`app-client`)
* ⚙️ **Resource Server (JWT)** para a API protegida (`app-resource`)

---

## 📂 Estrutura do Projeto

```bash
code-flow-pkcs/
├── app-demo-realm.json       # Realm Keycloak inicial
├── docker-compose.yml        # Subida do Keycloak com import automático
├── Dockerfile                # Dockerfile raiz
├── README.md                 # Documentação do projeto
│
├── app-client/               # Aplicação Spring Boot (PKCE Client)
│   ├── pom.xml                # Configuração Maven
│   └── src/
│       ├── main/java/...      # Controllers, configs, segurança
│       └── main/resources/    # application.properties
│
└── app-resource/             # Aplicação Spring Boot (Resource Server)
    ├── pom.xml                # Configuração Maven    
    └── src/
        ├── main/java/...      # Controllers, segurança, validação JWT
        └── main/resources/    # application.properties
```

---

## 🌍 Serviços e Portas

### 🔑 Keycloak

* **Service:** `keycloak`
* **Issuer:** `http://keycloak:8080/realms/app-pks-realm`
* **Ports:** `8080` (UI/Admin), `9000` (health)
* **Host:** `http://localhost:8081/`

### 🖥️ PKCE Client

* **Service:** `app-client`
* **Porta:** `8080:8080`
* **URL:** [http://localhost:8080](http://localhost:8080)

### 🔒 Resource Server

* **Service:** `app-resource`
* **Porta:** `8082:8082`
* **URL:** [http://localhost:8082](http://localhost:8082)

---

## 🗝️ Keycloak – Configuração

* **Realm:** `app-pks-realm`
* **Usuário inicial:** `appclient` / `appclient`
* **Role padrão:** `USER`

### Clients

1. **app-client**

   * Tipo: Público (PKCE)
   * Fluxo: `authorization_code`
   * Redirect URI: `http://localhost:8080/login/oauth2/code/*`
   * Web Origins: `http://localhost:8080`

2. **app-resource**

   * Tipo: Confidencial
   * Apenas valida tokens (`issuer-uri`)
   * Aceita tokens com `aud: app-resource`

---

## ⚙️ Configuração das Aplicações

### 📌 PKCE Client (porta 8080)

```properties
spring.application.name=app-client
server.port=8080
server.servlet.context-path=/

spring.security.oauth2.client.provider.keycloak.issuer-uri=http://localhost:8081/realms/app-pks-realm
spring.security.oauth2.client.registration.keycloak.client-id=app-client
spring.security.oauth2.client.registration.keycloak.client-authentication-method=none
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.keycloak.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}
spring.security.oauth2.client.registration.keycloak.scope=openid,profile,email,roles

# API protegida
app.api.base=http://localhost:8082
```

### 📌 Resource Server (porta 8082)

```properties
spring.application.name=app-resource
server.port=8082
server.servlet.context-path=/

spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8081/realms/app-pks-realm
```

---

## ▶️ Como Rodar

### 1. Buildar as aplicações

```sh
docker build -t app-client:1.0.0 --build-arg APP_MODULE=app-client -f Dockerfile .
docker build -t app-resource:1.0.0 --build-arg APP_MODULE=app-resource -f Dockerfile .
```

### 2. Subir o Keycloak

```sh
docker compose up -d
```

### 3. Acessar o sistema

* Cliente: [http://localhost:8080](http://localhost:8080)
* Login: `appclient` / `appclient`

Após login, o **PKCE Client** obterá o **access_token** e chamará a API em `http://localhost:8082`.
