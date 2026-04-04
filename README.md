# Voll.med - Sistema de Gestao de Clinica Medica

Sistema completo de gestao para a clinica Voll.med com API REST, autenticacao JWT, frontend web e suite de testes automatizados. Desenvolvido com Spring Boot 3 e Java 17.

## Funcionalidades

### Autenticacao (`/auth`)

| Metodo | Endpoint | Descricao | Autenticacao |
|--------|----------|-----------|--------------|
| `POST` | `/auth/login` | Login (retorna JWT token) | Publica |
| `POST` | `/auth/register` | Registrar novo usuario | ADMIN |

**Perfis de acesso:**
- `ADMIN` - Acesso total (CRUD completo + exclusao + registro de usuarios)
- `RECEPTIONIST` - Leitura, cadastro e edicao (sem permissao de exclusao)

### Medicos (`/doctors`)

| Metodo | Endpoint | Descricao | Permissao |
|--------|----------|-----------|-----------|
| `POST` | `/doctors` | Cadastrar medico | Autenticado |
| `GET` | `/doctors` | Listar medicos ativos (paginado + filtros) | Autenticado |
| `GET` | `/doctors/{id}` | Buscar medico por ID | Autenticado |
| `PUT` | `/doctors/{id}` | Atualizar medico | Autenticado |
| `DELETE` | `/doctors/{id}` | Remover medico | ADMIN |
| `PATCH` | `/doctors/{id}/status` | Inativar medico | Autenticado |

**Filtros disponiveis:** `?name=joao&speciality=CARDIOLOGY&page=0&size=10&sort=name`

### Pacientes (`/patients`)

| Metodo | Endpoint | Descricao | Permissao |
|--------|----------|-----------|-----------|
| `POST` | `/patients` | Cadastrar paciente | Autenticado |
| `GET` | `/patients` | Listar pacientes ativos (paginado) | Autenticado |
| `GET` | `/patients/{id}` | Buscar paciente por ID | Autenticado |
| `PUT` | `/patients/{id}` | Atualizar paciente | Autenticado |
| `DELETE` | `/patients/{id}` | Remover paciente | ADMIN |
| `PATCH` | `/patients/{id}/status` | Inativar paciente | Autenticado |

### Consultas (`/appointments`)

| Metodo | Endpoint | Descricao | Permissao |
|--------|----------|-----------|-----------|
| `POST` | `/appointments` | Agendar consulta | Autenticado |
| `GET` | `/appointments` | Listar consultas (paginado + filtros) | Autenticado |
| `PATCH` | `/appointments/{id}/cancel` | Cancelar consulta (motivo obrigatorio) | Autenticado |
| `PATCH` | `/appointments/{id}/complete` | Concluir consulta | Autenticado |

**Filtros:** `?doctorId=1` ou `?patientId=1`

**Regras de negocio:**
- Nao permite agendar com medico inativo
- Nao permite agendar com paciente inativo
- Nao permite medico com 2 consultas no mesmo horario
- Nao permite paciente com 2 consultas no mesmo dia
- Cancelamento exige motivo obrigatorio
- Data da consulta deve ser no futuro

### Frontend Web

| Pagina | URL | Descricao |
|--------|-----|-----------|
| Login | `/login` | Tela de autenticacao |
| Dashboard | `/web/dashboard` | Painel com resumo e acesso rapido |
| Medicos | `/web/doctors` | Listagem com filtros, paginacao e acoes |
| Novo Medico | `/web/doctors/new` | Formulario de cadastro com validacao |
| Pacientes | `/web/patients` | Listagem com paginacao e acoes |
| Novo Paciente | `/web/patients/new` | Formulario de cadastro com validacao |
| Consultas | `/web/appointments` | Listagem com status e acoes |
| Nova Consulta | `/web/appointments/new` | Formulario de agendamento |

## Tecnologias

| Tecnologia | Uso |
|-----------|-----|
| Java 17 | Linguagem |
| Spring Boot 3.3.2 | Framework |
| Spring Security | Autenticacao e autorizacao |
| JWT (java-jwt 4.4.0) | Token de autenticacao na API |
| Spring Data JPA / Hibernate | Persistencia |
| Spring Validation | Validacao de dados |
| Thymeleaf | Templates do frontend |
| Flyway | Migracoes de banco de dados |
| MySQL 8 | Banco de dados (producao) |
| H2 Database | Banco de dados (testes) |
| Lombok | Reducao de boilerplate |
| Maven | Build e dependencias |
| Docker / Docker Compose | Containerizacao |
| GitHub Actions | CI/CD |

## Estrutura do Projeto

```
src/
├── main/java/med/voll/api/
│   ├── controller/
│   │   ├── AuthenticationController.java   # Login e registro
│   │   ├── DoctorController.java           # CRUD medicos (API)
│   │   ├── PatientController.java          # CRUD pacientes (API)
│   │   ├── AppointmentController.java      # Consultas (API)
│   │   └── WebController.java              # Frontend (Thymeleaf)
│   ├── dto/                                # Records de transferencia
│   ├── infra/
│   │   ├── GlobalExceptionHandler.java     # Tratamento global de erros
│   │   └── security/
│   │       ├── SecurityConfigurations.java # Config Spring Security
│   │       ├── SecurityFilter.java         # Filtro JWT
│   │       ├── TokenService.java           # Geracao/validacao JWT
│   │       └── AuthenticationService.java  # UserDetailsService
│   ├── model/
│   │   ├── Doctor.java
│   │   ├── Patient.java
│   │   ├── Appointment.java
│   │   ├── User.java
│   │   ├── Address.java
│   │   └── enums/
│   ├── repository/
│   │   ├── DoctorRepository.java
│   │   ├── PatientRepository.java
│   │   ├── AppointmentRepository.java
│   │   └── UserRepository.java
│   └── service/
│       ├── DoctorService.java
│       ├── PatientService.java
│       └── AppointmentService.java
├── main/resources/
│   ├── application.properties
│   ├── templates/                          # Paginas Thymeleaf
│   ├── static/                             # CSS e JS
│   └── db/migration/                       # 8 migracoes Flyway
├── test/
│   └── ...
└── postman/
    └── VollMed_API.postman_collection.json # Collection Postman completa
```

## Testes Automatizados - 37 testes

### Testes de Integracao (Controller)
- **DoctorControllerTest (10):** CRUD completo, filtros, paginacao, controle de acesso (ADMIN/RECEPTIONIST), 401/403/404
- **PatientControllerTest (6):** CRUD, validacao CPF, controle de acesso por perfil

### Testes Unitarios (Service)
- **DoctorServiceTest (9):** Registro, listagem, update, delete, inativacao, EntityNotFoundException
- **AppointmentServiceTest (8):** Agendamento, regras de negocio (medico/paciente inativo, conflito horario, duplicata diaria), cancelamento, conclusao

### Testes de Repositorio
- **DoctorRepositoryTest (4):** findAllByActiveTrue, save, findById, delete

```bash
mvn test
```

## Como Executar

### Pre-requisitos
- Java 17+
- Maven 3.8+
- MySQL 8+

### Variaveis de ambiente

| Variavel | Descricao | Valor padrao |
|----------|-----------|--------------|
| `DB_URL` | URL de conexao JDBC | `jdbc:mysql://localhost:3306/voll_med_db` |
| `DB_USERNAME` | Usuario do banco | `root` |
| `DB_SECRET` | Senha do banco | `vollmed123` |
| `JWT_SECRET` | Chave secreta para assinatura JWT | `minha-chave-secreta-temporaria` |

> **Importante:** Em producao, defina `JWT_SECRET` com uma chave forte e unica.

### Configuracao do Banco de Dados

```sql
CREATE DATABASE voll_med_db;
```

### Execucao local

```bash
mvn spring-boot:run
```

A aplicacao estara disponivel em `http://localhost:8082`.

### Execucao com Docker

```bash
# Build do JAR
mvn clean package -DskipTests

# Subir MySQL + aplicacao
docker-compose up -d
```

> **Nota:** Via Docker Compose, a aplicacao sobe na porta `8080` (mapeamento do container). Localmente, a porta configurada e `8082`.

### Primeiro acesso

O endpoint `/auth/register` requer autenticacao com perfil ADMIN. Para criar o primeiro usuario, insira diretamente no banco:

```sql
-- Senha: admin123 (BCrypt hash)
INSERT INTO users (login, password, role) VALUES (
  'admin@vollmed.com',
  '$2a$10$Y3Kb1qGGCniTGEClDTDeg.3v5aJKbGqX4T1r0cNBaTfuSNIBpyESy',
  'ADMIN'
);
```

Depois, faca login na API para obter o token JWT:
```bash
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login": "admin@vollmed.com", "password": "admin123"}'
```

Use o token retornado nas demais requisicoes:
```bash
curl -H "Authorization: Bearer SEU_TOKEN" http://localhost:8082/doctors
```

Ou acesse o frontend em `http://localhost:8082/login` com as credenciais criadas.

## Collection Postman

Uma collection completa esta disponivel em `postman/VollMed_API.postman_collection.json`.

**Como importar:**
1. Abra o Postman
2. Clique em **Import** > arraste o arquivo ou selecione-o
3. A variavel `baseUrl` ja vem configurada como `http://localhost:8082`
4. Execute o request **Login** primeiro — o token JWT e salvo automaticamente para os demais requests

**Fluxo recomendado:** Login > Register (opcional) > Cadastrar medico > Cadastrar paciente > Agendar consulta > Cancelar/Concluir

## Exemplos de Requisicoes (API)

### Cadastrar medico
```json
POST /doctors
Authorization: Bearer {token}

{
  "name": "Dr. Joao Silva",
  "email": "joao@voll.med",
  "phone": "81999999999",
  "crm": "123456",
  "speciality": "CARDIOLOGY",
  "address": {
    "street": "Rua das Flores",
    "neighborhood": "Centro",
    "zipCode": "50000000",
    "city": "Recife",
    "state": "PE",
    "number": "100"
  }
}
```

### Cadastrar paciente
```json
POST /patients
Authorization: Bearer {token}

{
  "name": "Maria Oliveira",
  "email": "maria@email.com",
  "phone": "81988888888",
  "cpf": "12345678901",
  "address": {
    "street": "Rua do Sol",
    "neighborhood": "Boa Vista",
    "zipCode": "50000000",
    "city": "Recife",
    "state": "PE",
    "number": "50"
  }
}
```

### Agendar consulta
```json
POST /appointments
Authorization: Bearer {token}

{
  "doctorId": 1,
  "patientId": 1,
  "appointmentDate": "2026-12-20T14:00:00"
}
```

### Cancelar consulta
```json
PATCH /appointments/1/cancel
Authorization: Bearer {token}

{
  "reason": "Paciente solicitou reagendamento"
}
```
