# 🏦 Bank API

API REST de un sistema bancario digital desarrollada con **Java 21** y **Spring Boot 3.5**. Permite gestionar usuarios, cuentas bancarias y operaciones financieras con autenticación segura mediante JWT.

## ✨ Características

- **Autenticación y autorización** con JWT y roles (ADMIN / CLIENTE)
- **Registro y login** de usuarios con contraseñas encriptadas con BCrypt
- **Gestión de cuentas bancarias** — cuentas de ahorro y corriente con número generado automáticamente
- **Operaciones financieras** — depósitos, retiros y transferencias entre cuentas
- **Transacciones atómicas** con `@Transactional` — si algo falla, se revierte todo automáticamente
- **Historial de movimientos** con filtro por tipo (DEPOSITO, RETIRO, TRANSFERENCIA)
- **Panel de administración** — ver todos los usuarios y activar/desactivar cuentas
- **Soft Delete** — las cuentas nunca se eliminan, solo se desactivan
- **Validaciones** de datos en todos los endpoints
- **Manejo de errores global** con respuestas consistentes y descriptivas
- **Usuario ADMIN** creado automáticamente al iniciar la app
- **Documentación interactiva** con Swagger UI

## 🛠️ Tecnologías utilizadas

* **Java 21**
* **Spring Boot 3.5**
* **Spring Security** para la autenticación y autorización mediante **JWT**.
* **Spring Data JPA / Hibernate** para la persistencia de datos y mapeo de entidades (ORM).
* **PostgreSQL** como base de datos relacional.
* **Lombok** para la reducción de código boilerplate.
* **Validation** para la validación de datos de entrada.
* **OpenAPI** para la documentación automática de la API con Swagger.

### Próximamente (Roadmap)

* **JUnit y Mockito** para la implementación de pruebas unitarias.
* **Docker** para la contenedorización de la aplicación.

## 📁 Estructura del proyecto

```
src/main/java/com/costa/bankapi/
├── controller/          # Endpoints HTTP (recibe peticiones)
│   ├── AuthController
│   ├── CuentaController
│   ├── OperacionController
│   ├── TransaccionController
│   └── AdminController
├── service/             # Lógica de negocio
│   ├── AuthService
│   ├── CuentaService
│   ├── OperacionService
│   └── TransaccionService
├── repository/          # Acceso a la base de datos
│   ├── UsuarioRepository
│   ├── CuentaRepository
│   └── TransaccionRepository
├── entity/              # Tablas de la base de datos
│   ├── Usuario
│   ├── Cuenta
│   └── Transaccion
├── dto/                 # Objetos de transferencia de datos
│   ├── RegisterRequest / LoginRequest / AuthResponse
│   ├── CuentaRequest / CuentaResponse
│   ├── DepositoRequest / RetiroRequest / TransferenciaRequest
│   ├── TransaccionResponse
│   └── UsuarioResponse
├── security/            # Configuración de seguridad
│   ├── JwtService
│   ├── JwtAuthenticationFilter
│   ├── SecurityConfig
│   └── SwaggerConfig
├── exception/           # Manejo de errores global
│   └── GlobalExceptionHandler
└── DataInitializer      # Crea el usuario ADMIN al iniciar
```

## ⚙️ Requisitos previos

Antes de ejecutar el proyecto, asegurate de tener instalado:

- **Java 21** o superior
- **Maven** para la gestión de dependencias
- **PostgreSQL** 
- **Docker** *(opcional, para despliegue con contenedores — próximamente)*

## 🚀 Instalación y configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/lautaro-costa/bank-api.git
cd bank-api
```

### 2. Crear la base de datos

Abrí pgAdmin o cualquier cliente PostgreSQL y ejecutá:

```sql
CREATE DATABASE bank_db;
```

### 3. Configurar las variables de entorno

Editá el archivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bank_db
spring.datasource.username=postgres
spring.datasource.password=TU_CONTRASEÑA

jwt.secret=claveSuperSecretaParaFirmarTokensJWT1234567890
jwt.expiration=86400000

admin.email=admin@bankapi.com
admin.password=admin1234
```

### 4. Ejecutar el proyecto

Abrí el proyecto en IntelliJ IDEA y ejecutá `BankapiApplication.java`, o desde la terminal:

```bash
mvn spring-boot:run
```

### 5. Usuario ADMIN por defecto

Al iniciar la app por primera vez se crea automáticamente un usuario administrador:

| Campo | Valor |
|---|---|
| Email | admin@bankapi.com |
| Contraseña | admin1234 |

## 📋 Endpoints principales

### Autenticación (públicos)

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/auth/register` | Registrar un nuevo usuario |
| POST | `/auth/login` | Iniciar sesión y obtener token JWT |

### Cuentas (requieren token)

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/cuentas` | Crear una cuenta bancaria |
| GET | `/cuentas` | Ver mis cuentas |
| GET | `/cuentas/{id}` | Ver una cuenta específica |

### Operaciones (requieren token)

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/operaciones/depositar` | Depositar dinero |
| POST | `/operaciones/retirar` | Retirar dinero |
| POST | `/operaciones/transferir` | Transferir entre cuentas |

### Transacciones (requieren token)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/transacciones/{cuentaId}` | Ver historial de una cuenta |
| GET | `/transacciones/{cuentaId}/tipo?tipo=DEPOSITO` | Filtrar por tipo |

### Administración (solo ADMIN)

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/admin/usuarios` | Ver todos los usuarios |
| PUT | `/admin/cuentas/{id}/estado?estado=INACTIVA` | Activar/desactivar cuenta |

## 📖 Documentación

La API cuenta con documentación interactiva generada automáticamente con Swagger UI.

Una vez que el proyecto esté corriendo, entrá a:

```
http://localhost:8080/swagger-ui/index.html
```

Para probar endpoints protegidos desde Swagger:
1. Hacé login desde `/auth/login`
2. Copiá el token de la respuesta
3. Hacé clic en **Authorize** arriba a la derecha
4. Pegá el token y hacé clic en **Authorize**

## 🔐 Seguridad

- Las contraseñas se encriptan con **BCrypt** antes de guardarse
- Cada petición se autentica con un **token JWT** que expira a las 24 horas
- Los endpoints de administración solo son accesibles con rol **ADMIN**
- Las cuentas nunca se eliminan — se desactivan (**Soft Delete**)
- Las transferencias son **atómicas** — si algo falla, se revierten todos los cambios

## 🧪 Pruebas

*Próximamente — tests unitarios con JUnit 5 y Mockito.*

## 🐳 Docker

*Próximamente — despliegue con Docker y Docker Compose.*

## 👨‍💻 Autor

**Lautaro Costa** — [GitHub](https://github.com/lautaro-costa)
