# Voll.med - API REST CRUD

API REST para gerenciamento de médicos da clínica Voll.med, desenvolvida com Spring Boot 3 e Java 17.

## Funcionalidades

### Endpoints - Médicos (`/doctors`)

| Método | Endpoint | Descrição | Status Code |
|--------|----------|-----------|-------------|
| `POST` | `/doctors` | Cadastrar um novo médico | `201 Created` |
| `GET` | `/doctors` | Listar todos os médicos ativos | `200 OK` |
| `PUT` | `/doctors/{id}` | Atualizar dados de um médico | `200 OK` |
| `DELETE` | `/doctors/{id}` | Remover um médico (hard delete) | `204 No Content` |
| `PATCH` | `/doctors/{id}/status` | Inativar um médico (soft delete) | `204 No Content` |

### Validações

- **Nome**: obrigatório, não pode ser vazio
- **Email**: obrigatório, formato válido de email
- **Telefone**: obrigatório, não pode ser vazio
- **CRM**: obrigatório, deve conter exatamente 6 dígitos numéricos
- **Especialidade**: obrigatória (`CARDIOLOGY`, `DERMATOLOGY`, `GYNECOLOGY`, `ORTHOPEDICS`)
- **Endereço**: rua, bairro, CEP (8 dígitos), cidade e estado são obrigatórios

### Tratamento de Erros

- `400 Bad Request` - Erros de validação com detalhes dos campos inválidos
- `404 Not Found` - Médico não encontrado pelo ID informado

## Tecnologias

- **Java 17**
- **Spring Boot 3.3.2**
- **Spring Data JPA / Hibernate** - Persistência de dados
- **Spring Validation** - Validação de DTOs com Bean Validation (Jakarta)
- **Flyway** - Versionamento e migração de banco de dados
- **MySQL** - Banco de dados relacional (produção)
- **H2 Database** - Banco de dados em memória (testes)
- **Lombok** - Redução de código boilerplate
- **Maven** - Gerenciamento de dependências e build

## Estrutura do Projeto

```
src/
├── main/java/med/voll/api/
│   ├── controller/          # Controllers REST
│   │   ├── DoctorController.java
│   │   └── PatientController.java
│   ├── dto/                 # Data Transfer Objects (Records)
│   │   ├── AddressDto.java
│   │   ├── DoctorRegistrationDto.java
│   │   ├── DoctorUpdateDto.java
│   │   ├── DoctorListingDto.java
│   │   └── PatientRegistrationDto.java
│   ├── infra/               # Infraestrutura e configurações
│   │   └── GlobalExceptionHandler.java
│   ├── model/               # Entidades JPA
│   │   ├── Doctor.java
│   │   ├── Address.java
│   │   └── enums/
│   │       └── Speciality.java
│   ├── repository/          # Repositórios Spring Data
│   │   └── DoctorRepository.java
│   ├── service/             # Camada de serviço (regras de negócio)
│   │   └── DoctorService.java
│   └── ApiApplication.java
├── main/resources/
│   ├── application.properties
│   └── db/migration/        # Scripts Flyway
│       ├── V1__create-table-doctors.sql
│       ├── V2__alter-table-doctors-add-column-phone.sql
│       └── V3__alter-table-doctors-add-column-status.sql
└── test/
    ├── java/med/voll/api/
    │   ├── controller/
    │   │   └── DoctorControllerTest.java    # 8 testes de integração
    │   ├── service/
    │   │   └── DoctorServiceTest.java       # 9 testes unitários
    │   ├── repository/
    │   │   └── DoctorRepositoryTest.java    # 4 testes de repositório
    │   └── ApiApplicationTests.java
    └── resources/
        └── application-test.properties
```

## Testes Automatizados

A aplicação possui **21 testes automatizados** distribuídos em 3 camadas:

### Testes de Integração (`DoctorControllerTest`) - 8 testes
- Cadastro de médico com dados válidos (retorna 201)
- Cadastro com dados inválidos (retorna 400)
- Listagem de médicos ativos
- Atualização de médico existente
- Exclusão de médico (hard delete)
- Inativação de médico via PATCH
- Atualização de médico inexistente (retorna 404)
- Exclusão de médico inexistente (retorna 404)

### Testes Unitários (`DoctorServiceTest`) - 9 testes
- Registro de novo médico
- Listagem de médicos ativos
- Listagem vazia quando não há médicos ativos
- Atualização de médico existente
- Exceção ao atualizar médico inexistente
- Exclusão de médico existente
- Exceção ao excluir médico inexistente
- Inativação de médico
- Exceção ao inativar médico inexistente

### Testes de Repositório (`DoctorRepositoryTest`) - 4 testes
- Busca apenas médicos ativos
- Retorno vazio quando todos são inativos
- Salvar e buscar médico por ID
- Exclusão de médico

### Como executar os testes

```bash
mvn test
```

Os testes utilizam banco **H2 em memória** com profile `test`, sem necessidade de MySQL.

## Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- MySQL 8+

### Configuração do Banco de Dados

Crie o banco de dados MySQL:

```sql
CREATE DATABASE voll_med_db;
```

### Execução

```bash
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

## Exemplos de Requisições

### Cadastrar médico
```json
POST /doctors
{
  "name": "Dr. João Silva",
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

### Atualizar médico
```json
PUT /doctors/1
{
  "name": "Dr. João Silva Jr.",
  "phone": "81988888888"
}
```
