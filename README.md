# Voll.med - Sistema de Gestao de Clinica Medica

Sistema completo de gestao para a clinica Voll.med com API REST, autenticacao JWT, frontend web e suite de testes automatizados. Desenvolvido com Spring Boot 3 e Java 17.

## Funcionalidades

### Autenticacao (`/auth`)

| Metodo | Endpoint | Descricao | Autenticacao |
|--------|----------|-----------|--------------|
| `POST` | `/auth/register` | Registrar novo usuario | Publica |
| `POST` | `/auth/login` | Login (retorna JWT token) | Publica |

**Perfis de acesso:**
- `ADMIN` - Acesso total (CRUD completo + exclusao)
- `RECEPTIONIST` - Leitura, cadastro e edicao (sem permissao de exclusao)

### Medicos (`/doctors`)

| Metodo | Endpoint | Descricao | Permissao |
|--------|----------|-----------|-----------|
| `POST` | `/doctors` | Cadastrar medico | Autenticado |
| `GET` | `/doctors` | Listar medicos ativos (paginado + filtros) | Autenticado |
| `PUT` | `/doctors/{id}` | Atualizar medico | Autenticado |
| `DELETE` | `/doctors/{id}` | Remover medico | ADMIN |
| `PATCH` | `/doctors/{id}/status` | Inativar medico | Autenticado |

**Filtros disponiveis:** `?name=joao&speciality=CARDIOLOGY&page=0&size=10&sort=name`

### Pacientes (`/patients`)

| Metodo | Endpoint | Descricao | Permissao |
|--------|----------|-----------|-----------|
| `POST` | `/patients` | Cadastrar paciente | Autenticado |
| `GET` | `/patients` | Listar pacientes ativos (paginado) | Autenticado |
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
| JWT (java-jwt) | Token de autenticacao na API |
| Spring Data JPA / Hibernate | Persistencia |
| Spring Validation | Validacao de dados |
| Thymeleaf | Templates do frontend |
| Flyway | Migracoes de banco de dados |
| MySQL | Banco de dados (producao) |
| H2 Database | Banco de dados (testes) |
| Lombok | Reducao de boilerplate |
| Maven | Build e dependencias |
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
│   │   ├── login.html
│   │   ├── dashboard.html
│   │   ├── doctors.html
│   │   ├── doctor-form.html
│   │   ├── patients.html
│   │   ├── patient-form.html
│   │   ├── appointments.html
│   │   └── appointment-form.html
│   ├── static/
│   │   ├── css/style.css
│   │   └── js/
│   └── db/migration/                       # 6 migracoes Flyway
└── test/
    ├── java/med/voll/api/
    │   ├── controller/
    │   │   ├── DoctorControllerTest.java   # 10 testes de integracao
    │   │   └── PatientControllerTest.java  # 6 testes de integracao
    │   ├── service/
    │   │   ├── DoctorServiceTest.java      # 9 testes unitarios
    │   │   └── AppointmentServiceTest.java # 8 testes unitarios
    │   └── repository/
    │       └── DoctorRepositoryTest.java   # 4 testes de repositorio
    └── resources/
        └── application-test.properties
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

### Configuracao do Banco de Dados

```sql
CREATE DATABASE voll_med_db;
```

### Execucao

```bash
mvn spring-boot:run
```

A aplicacao estara disponivel em `http://localhost:8080`.

### Primeiro acesso

1. Registre um usuario via API:
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"login": "admin", "password": "admin123", "role": "ADMIN"}'
```

2. Acesse o frontend em `http://localhost:8080/login` com as credenciais criadas.

3. Ou obtenha um token JWT para usar na API:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login": "admin", "password": "admin123"}'
```

4. Use o token nas requisicoes:
```bash
curl -H "Authorization: Bearer SEU_TOKEN" http://localhost:8080/doctors
```

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
  "appointmentDate": "2025-12-20T14:00:00"
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

## Cenarios para Automacao de Testes

Este projeto foi desenhado para maximizar cenarios de automacao:

### Selenium / Robot Framework (Frontend)
- Login com credenciais validas/invalidas
- Navegacao entre paginas (Dashboard, Medicos, Pacientes, Consultas)
- Preenchimento de formularios com validacao
- Filtros e paginacao na listagem de medicos
- Acoes de editar, excluir, inativar
- Mensagens de sucesso/erro
- Modal de confirmacao de exclusao
- Controle de acesso por perfil (ADMIN vs RECEPTIONIST)

### Robot Framework + RequestsLibrary (API)
- CRUD completo de medicos, pacientes e consultas
- Validacao de status codes (200, 201, 204, 400, 403, 404)
- Autenticacao JWT (login, token expirado, token invalido)
- Regras de negocio de agendamento
- Paginacao e filtros via query params

### JMeter / Gatling (Performance)
- Carga em endpoints de listagem
- Stress test no agendamento de consultas
- Teste de concorrencia no mesmo horario

### Postman / Newman (API Collections)
- Workflow completo: registrar -> login -> cadastrar medico -> cadastrar paciente -> agendar -> cancelar
