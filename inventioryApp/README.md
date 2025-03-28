# 📦 Inventiory - Backend

Este repositorio contiene el backend del sistema **Inventiory**, un **ERP web orientado a PyMEs** que permite administrar de forma integral productos, clientes, stock, ventas, facturación y reportes rentables de tu negocio.

---

## 🧠 Visión general

El backend proporciona una **API REST segura** que forma parte del ERP **Inventiory**, y está diseñada para ser consumida por el frontend desarrollado con React (repositorio separado).

---

## 🎯 Funcionalidades

- Gestión de productos con precio de venta y compra.
- Control de stock en tiempo real.
- Registro y administración de clientes.
- Facturación automática y generación de comprobantes PDF.
- Registro de ventas y control de ganancias.
- Reportes mensuales y trimestrales.
- Autenticación con JWT y control de roles (Administrador y Empleado).
- Gestión de empresas asociadas a usuarios.

---

## ⚙️ Tecnologías

- **Java 17**
- **Spring Boot 3**
- **Spring Security (JWT)**
- **Spring Data JPA / Hibernate**
- **MySQL**
- **JavaMail (correo SMTP)**
- **Docker y Docker Compose**

---

## 🚀 Instalación

### Requisitos

- Java 17+
- Maven
- MySQL 8+
- Docker (opcional)

### Ejecución local

```bash
mvn clean install
mvn spring-boot:run
```

📌 Asegurate de tener tu base de datos configurada y el archivo `application.properties` con los datos sensibles necesarios.

---

## 🐳 Uso con Docker

```bash
docker compose up -d
```

Esto levanta MySQL y el backend configurado con variables externas.

---

## 🔐 Configuración sensible

Los datos sensibles (DB, JWT, SMTP) se definen en `application.properties`, que **NO debe subirse al repositorio**.  
Usá el archivo `application-example.properties` como plantilla para configurarlos.

---

## 📁 Estructura del proyecto

```
inventiory-backend/
├── src/
│   └── main/
│       ├── java/com/imperial_net/inventioryApp
│       └── resources/
│           ├
│           └── application-example.properties
├── docker-compose.yml
└── README.md
```

---

## 🔗 Frontend relacionado

👉 El frontend del proyecto se encuentra en un repositorio separado:  
[Inventiory - Frontend](https://github.com/MichaelIllescas/inventiory-frontend.git)  


---

## 👨‍💻 Autor

-Michael Jonathan Illescas

---

## 📄 Licencia

    MIT
