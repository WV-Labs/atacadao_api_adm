# Sistema Mercado - Ambiente Offline

Sistema de gestão de produtos e categorias desenvolvido em Java 17 com Spring Boot para funcionamento em ambiente offline.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring MVC**
- **Thymeleaf** (template engine)
- **PostgreSQL** (banco de dados)
- **Flyway** (migração de banco)
- **Lombok** (redução de boilerplate)
- **Bootstrap 5** (frontend)

## Pré-requisitos para Ambiente Offline

### 1. Java 17
```bash
# Verificar se o Java 17 está instalado
java -version

# Download: https://adoptium.net/temurin/releases/
# Ou Oracle JDK: https://www.oracle.com/java/technologies/downloads/
```

### 2. PostgreSQL
```bash
# Download: https://www.postgresql.org/download/
# Versão recomendada: 14 ou superior

# Configuração padrão esperada:
# Host: localhost
# Porta: 5432
# Usuário: postgres
# Senha: postgres
```

### 3. Maven


## Instalação e Configuração

## Implantação em Ambiente Offline

### 1. Scripts de Inicialização

#### Windows 
##### Para instalar e executar o install

##### Depois de instalar e executar o run
```batch
@echo off
echo Iniciando Sistema Mercado...
java -jar sistema-mercado-0.0.1-SNAPSHOT.jar
pause
```

## Troubleshooting

### Problemas Comuns

0. **NãO Executar como ADMINISTRADOR**

1. **Erro de conexão com PostgreSQL**
    - Verificar se o PostgreSQL está rodando
    - Confirmar usuário e senha
    - Verificar se o banco `mercadoDB` existe

2. **Porta 8080 já em uso**
    - Alterar a porta no `application.properties`:
   ```properties
   server.port=8081
   ```
3. **Erro de Java version**
    - Confirmar que está usando Java 17
    - Configurar JAVA_HOME corretamente
