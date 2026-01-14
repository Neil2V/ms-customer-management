MS Customer Management
Proyecto Spring Boot para la gestión de clientes

Requisitos Previos
Antes de ejecutar el proyecto, asegúrate de tener instalado:
- Java 17
- Gradle 8+
- SQL Server
- Postman (opcional, para probar la colección)

Configuración de la Base de Datos necesarias
- CREATE DATABASE db_customer_management;
- CREATE SCHEMA schema_customer;

Configurar el application.yml con tus datos de conexion

SonarQube
Archivo de evidencia: sonar.png

Postman
Archivo: gestion_clientes.postman_collection.json

Modificaciones en la Base de Datos por medio de liquibase
Si se requiere modificar la estructura de la tabla de clientes:
1. Crear/actualizar el changelog de Liquibase.
2. Si los cambios afectan los campos de la entidad Java (CustomerEntity), actualizar también la clase para reflejar los cambios en la base de datos y evitar errores.
