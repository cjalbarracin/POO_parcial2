# Sistema de Gestión de Inventario (POO)

Este proyecto constituye una solución integral para la administración de inventarios de telefonía móvil. La arquitectura se basa en un diseño modular y desacoplado, implementando el patrón de **Arquitectura en Capas**, lo cual garantiza la escalabilidad, el mantenimiento eficiente y la integridad de la información en entornos de base de datos remotos.



## 🏗️ Análisis Técnico y Arquitectura del Sistema

El software ha sido estructurado para separar las responsabilidades mediante las siguientes capas:

### 1. Capa de Presentación (Frontend - `vistatienda`)
Implementada mediante **Java Swing**, esta capa gestiona la interfaz gráfica y la experiencia de usuario. Su función es actuar como el controlador de eventos: captura las entradas del usuario, realiza validaciones de tipos de datos en tiempo real (evitando que el sistema falle por valores no numéricos) y delega la lógica de negocio a las capas inferiores.

### 2. Capa de Modelo (Entidades - `model`)
Las clases `celular` e `inventario` funcionan como **POJOs** (Plain Old Java Objects). Estas clases encapsulan los datos, permitiendo que la información fluya entre los diferentes componentes del sistema de forma estructurada. El uso de encapsulamiento (atributos privados y métodos accesores) asegura que el estado de los objetos no sea alterado indebidamente por componentes externos.

### 3. Capa de Lógica de Datos (Capa DAO - `TiendaDAO`)
Esta capa es el núcleo funcional del sistema y se encarga de la persistencia de los datos en **PostgreSQL**.
* **Gestión de Transacciones:** Para operaciones de escritura crítica (como registrar un nuevo dispositivo), se implementa una transacción lógica. Se deshabilita el `setAutoCommit(false)` para asegurar que tanto la inserción del `celular` como la del `inventario` ocurran correctamente. Si ocurre cualquier fallo, se ejecuta un `rollback()` para mantener la integridad referencial y evitar datos huérfanos.
* **Optimización de Consultas:** Se utilizan sentencias `JOIN` para obtener reportes consolidados en un solo viaje al servidor, optimizando así el uso de recursos de red.
* **Seguridad:** Toda la comunicación SQL utiliza `PreparedStatement`, lo que blinda el sistema contra ataques de inyección SQL, garantizando que las entradas de usuario se traten como datos y no como comandos ejecutables.

### 4. Capa de Configuración y Conexión (`DBConnection`)
La gestión de credenciales se centraliza mediante el archivo `config.properties`. El sistema utiliza `java.util.Properties` para cargar esta configuración en tiempo de ejecución, logrando un **desacoplamiento total** entre el código fuente y los entornos (desarrollo, pruebas o producción).

## 🛠️ Procesamiento de Reportes y Funcionalidades
El módulo de reportes no solo extrae datos, sino que realiza un procesamiento intermedio:
1. **Extracción:** El contenido del `JTextArea` es capturado tras cualquier consulta.
2. **Transformación:** Se utiliza el almacenamiento intermedio para formatear los resultados.
3. **Persistencia Local:** A través de un `JFileChooser`, el usuario define el destino, y un `BufferedWriter` realiza la escritura física en disco. Este proceso cuenta con un manejo robusto de excepciones (`IOException`) que permite al sistema recuperarse ante bloqueos de archivos o problemas de permisos.

## 🚀 Justificación de Diseño
* **Atomicidad:** Garantizada por el control manual de transacciones.
* **Abstracción:** La interfaz no conoce los detalles de la base de datos; solo interactúa con los métodos definidos en la capa DAO.
* **Flexibilidad:** Gracias al sistema de búsqueda dinámica mediante `ILIKE`, el usuario puede realizar consultas complejas con una latencia mínima.

---
*Desarrollado para la cátedra de Programación Orientada a Objetos.*