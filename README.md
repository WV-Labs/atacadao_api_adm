# 🏪 Sistema de Gestão de Terminais

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker)
![MapStruct](https://img.shields.io/badge/MapStruct-1.5-red?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

Sistema web moderno para gestão de terminais de produtos por categoria em mercados e supermercados.

[🚀 Como Executar](#-como-executar) • [📚 APIs](#-apis-dos-terminais) • [🛠️ Tecnologias](#️-stack-tecnológica) • [📊 Monitoramento](#-monitoramento)

</div>

---

## ✨ Funcionalidades Implementadas

### 🔐 **Autenticação e Segurança**
- ✅ **Login seguro** com Spring Security
- ✅ **Controle de acesso** baseado em roles
- ✅ **Senhas criptografadas** com BCrypt
- ✅ **Proteção CSRF** automática
- ✅ **Validação robusta** de entrada

### 📦 **Gestão de Produtos**
- ✅ **CRUD completo** com validação Bean Validation
- ✅ **Campos**: nome, descrição, preço, categoria
- ✅ **Busca avançada** por nome/categoria
- ✅✅ ***Disponíveis para próxima feature caso queira por nome/categoria***
- ✅ **Máscaras automáticas para promoção** para preços com promoção
- ✅ **Upload de imagens** com validação de URL
- ✅ **Código de barras único** com validação
- ✅ **Controle de estoque baixo** com alertas visuais

### 🖥️ **Gestão de Tv´s**
- ✅ **URLs automáticas**: `/categoria/numero`
- ✅ **Exemplos**: `/acougue/1`, `/padaria/2`, `/hortifruti/1`
- ✅ **Controle de status** (ativo/inativo)
- ✅ **Localização geográfica** detalhada
- ✅ **Preview de URL** em tempo real
- ✅ **Validação de duplicatas** categoria+número
- ✅ **Botão copiar URL** integrado

### 🏷️ **Gestão de Categorias**
- ✅ **Categorias pré-definidas** (Açougue, Padaria, Hortifruti, etc.)
- ✅ **CRUD com validação** de dependências
- ✅ **Contador automático** de produtos/terminais
- ✅ **Descrições detalhadas**
- ✅ **Validação de nome único**
- ✅ **Verificação antes da exclusão**

### 📅 **Sistema de Agendamentos**
- ✅ **Agendamento intuitivo** de uso das TV´s e conteúdos
- ✅ **Associação inteligente** com categorias

### 🔌 **API REST Completa**
- ✅ **Endpoints para todos** os recursos
- ✅ **Documentação automática** com exemplos
- ✅ **Validação de entrada** com Bean Validation
- ✅ **Respostas padronizadas** em JSON
- ✅ **Rate limiting** configurado no Nginx
- ✅ **CORS configurado** para desenvolvimento
- ✅ **Health checks** automáticos

---

## 🛠️ Stack Tecnológica

### **🎨 Frontend Responsivo**
- **📄 Thymeleaf** - Template engine server-side
- **🎨 Bootstrap 5** - Framework UI responsivo e moderno
- **📊 Chart.js** - Gráficos interativos e animados
- **🎭 Font Awesome 6** - Ícones vetoriais consistentes
- **⚡ JavaScript ES6+** - Interações dinâmicas e validações
- **🔄 AJAX** - Requisições assíncronas para UX fluida

### **🗄️ Banco de Dados Robusto**
- **🐘 PostgreSQL 17** - Banco principal com JSON e full-text search
- **⚡ Redis** - Cache distribuído (opcional)
- **📝 JPA Auditing** - Log automático de alterações
- **🔄 Flyway/Liquibase** - Controle de versão do banco
- **📊 Índices otimizados** - Performance em consultas complexas

### **🐳 DevOps & Deploy**
- **🐳 Docker** - Containerização multi-stage
- **🎼 Docker Compose** - Orquestração de serviços
- **🌐 Nginx** - Proxy reverso, load balancer e SSL
- **🔐 SSL/TLS** - Certificados automáticos e HTTPS
- **💓 Health Checks** - Monitoramento de saúde dos serviços
- **📊 Logging** - Logs estruturados e centralizados

### **🏗️ Arquitetura Moderna**
- **📋 Records (Java 17)** - DTOs imutáveis e concisas
- **🗺️ MapStruct** - Mapeamento compile-time sem reflection
- **⚡ Reactive Patterns** - Performance otimizada
- **🏗️ Clean Architecture** - Separação clara de responsabilidades
- **🔧 Dependency Injection** - Inversão de controle com Spring
- **📐 Repository Pattern** - Abstração da camada de dados

## 🔌 APIs dos Terminais

O sistema gera **automaticamente URLs REST** para cada terminal baseado na categoria e número configurados:

### **🎯 Estrutura das URLs**
```
GET /api/{categoria}/{numero}
```

### **📋 Exemplos Práticos**

| 🏪 Terminal | 🔗 URL | 📝 Descrição |
|-------------|--------|--------------|
| **Açougue Leste** | `GET /api/acougue/1` | Terminal 1 da categoria Açougue |
| **Açougue Oeste** | `GET /api/acougue/2` | Terminal 2 da categoria Açougue |
| **Padaria Central** | `GET /api/padaria/1` | Terminal 1 da categoria Padaria |
| **Padaria Leste** | `GET /api/padaria/2` | Terminal 2 da categoria Padaria |
| **Hortifruti Principal** | `GET /api/hortifruti/1` | Terminal 1 da categoria Hortifruti |
| **Laticínios Norte** | `GET /api/laticinios/1` | Terminal 1 da categoria Laticínios |
| **Bebidas Sul** | `GET /api/bebidas/1` | Terminal 1 da categoria Bebidas |

### **📊 Resposta da API**
```json
{
  "terminal": {
    "id": 1,
    "nome": "Terminal Açougue Leste", 
    "localizacao": "Área Leste - Entrada A",
    "categoria": {
      "id": 1,
      "nome": "Açougue",
      "descricao": "Carnes bovinas, suínas, aves e derivados"
    },
    "numero": 1,
    "url": "/acougue/1",
    "ativo": true
  },
  "produtos": [
    {
      "id": 1,
      "nome": "Picanha Premium",
      "descricao": "Corte nobre bovino Premium",
      "preco": 89.90,
      "categoria": {
        "id": 1,
        "nome": "Açougue",
        "descricao": "Carnes bovinas, suínas, aves e derivados"
      }
    },
    {
      "id": 2,
      "nome": "Frango Inteiro",
      "descricao": "Frango caipira fresco",
      "preco": 12.90,
      "categoria": {
        "id": 1,
        "nome": "Açougue"
        "descricao": "Carnes bovinas, suínas, aves e derivados"
      }
    }
  ],
  "totalVisualizacoes": 127
}
```

### **🔗 Outros Endpoints Disponíveis**

#### **📋 Listagem Geral**
```bash
# Listar todos os terminais ativos
GET /api/terminais

# Listar todos os produtos
GET /api/produtos

# Listar todas as categorias
GET /api/categorias
```

#### **🔍 Busca e Filtros**
```bash
# Buscar produtos por categoria
GET /api/produtos/categoria/{categoriaId}

# Buscar produtos por nome
GET /api/produtos/buscar?termo=picanha

# Buscar terminais por categoria
GET /api/terminais/categoria/{categoriaId}
```

#### **📊 Estatísticas e Dashboard**
```bash
# Estatísticas gerais do sistema
GET /api/stats

# Dados para dashboard
GET /api/dashboard

# Verificar saúde da aplicação
GET /actuator/health
```

## 📋 Dados Pré-carregados

O sistema vem com **dados de demonstração** prontos para uso imediato:

### **👤 Usuários Padrão**
```
Usuário: admin
Senha: admin123  
Role: ADMIN
Email: admin@sistema.com
```

### **🏷️ Categorias (15 categorias)**
| 📂 Categoria       | 📝 Descrição                                 |
|--------------------|----------------------------------------------|
| **🥩 Açougue**     | Carnes bovinas, suínas, aves e derivados     |
| **🍞 Padaria**     | Pães, bolos, doces e produtos de panificação |
| **🥬 Hortifruti**  | Frutas frescas, verduras e legumes           |
| **🥛 Laticínios**  | Leite, queijos, iogurtes e derivados lácteos |
| **🥤 Bebidas**     | Refrigerantes, sucos, águas e bebidas        |
| **🛒 Mercearia**   | Produtos secos, enlatados e mantimentos      |
| **🧽 Limpeza**     | Produtos de limpeza doméstica                |
| **🧴 Higiene**     | Produtos de higiene pessoal e cosméticos     |
| **🧊 Congelados**  | Produtos congelados e sorvetes               |
| **💊 Farmácia**    | Medicamentos e produtos farmacêuticos        |
| **☕ Cafeteria**   | Café, salgado, refrigerantes, etc            |
| **🥗 Alimentação** | Alimentos em geral                           |
| **🔌 Eletrônicos** | Eletrônicos em geral                         |
| **🧀 Frios**       | Frios fatiados, embutidos, etc               |
| **🐟 Peixaria**    | Peixes em geral                              |
