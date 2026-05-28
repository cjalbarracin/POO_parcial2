# Sistema de Gestión de Inventario de Celulares

Este proyecto es una aplicación desarrollada en Java bajo el patrón de diseño DAO (Data Access Object), diseñada para gestionar una base de datos PostgreSQL alojada en Neon.

## Características
- Arquitectura modular (DAO).
- Persistencia en la nube (PostgreSQL/Neon).
- Configuración externa de credenciales.
- Seguridad mediante .gitignore.

## Requisitos
- JDK 17 o superior.
- IntelliJ IDEA.
- Driver JDBC para PostgreSQL.

## Configuración
1. Cree un archivo llamado config.properties en la raíz del proyecto.
2. Agregue las siguientes líneas con sus credenciales:

db.url=jdbc:postgresql://tu-servidor-neon.com:5432/nombre_db
db.user=tu_usuario
db.pass=tu_contraseña

## Estructura del Proyecto
- src/db/: Contiene la clase DBConnection.
- src/db/operaciones/: Contiene los archivos DAO.
- src/model/: Entidades del negocio (celular, inventario).
- Principal.java: Punto de entrada y menú.

## Instrucciones
1. Clone el repositorio.
2. Configure el archivo config.properties en la raíz.
3. Ejecute la clase Principal.java.
4. Utilice el menú interactivo para gestionar los registros.

---
Desarrollado como parte del taller de POO.
