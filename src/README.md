#Biblioteca Virtual UNTEC

Este proyecto es un ejercicio de aprendizaje, exploración y moderación.
Aprendizaje en cuánto a los conceptos que se desprenden: Autenticación y Autorización,
uso de patrón MVC, persistencia a través de la capa DAO. Todo lo mencionado mientras se aplican aprendizajes anteriores: aplicaciones responsivas, bases de datos, principios POO.

Exploración en cuánto a lo que el proyecto podría llegar a ser y las tecnologías que se podrían utilizar (a discutir posteriormente) y moderación: un acto de balance entre no sobredimesionar el alcance del proyecto en su carácter educativo y manejar la naturaleza abierta de la especifícación.

##Toma de desiciones

Dada la amplitud de la especifícación, como se mencionó anteriormente y como no se específica ningún rol, determinamos crear un solo usuario administrador con acceso a las funciones de:
    - Autenticarse y obtener usuarios (UsuarioService).
    - Acceder al catálogo y agregar libros a éste (LibroService).
    - Administrar los préstamos de libros (PrestamoService).

Para apoyar estas funciones se crearon los siguientes modelos con sus correspondientes DAOs para persistencia:
    - Usuario (UsuarioDAO).
    - Libro (LibroDAO).
    - Prestamo (PrestamoDAO).

Y finalmente para obtener los datos ingresados por los usuarios y para que estos puedan interactuar con las distintas vistas, se craron los siguientes controladores:
    - DashboardController (Menú principal para acceso a otras vistas)
    - LibroController (Ingreso y visualización de catálogo de libros)
    - LoginController (Autenticación)
    - LogoutController (Cierre de sesión)
    - PrestamosController (Manejo y visualización de préstamo de libros)

Se crearon las siguientes tablas (en plural):



Y los siguientes inserts para fines de demostración:



*Sólo admin se puede autenticar. Los otros usuarios se crearon con fines de demostración de los préstamos de libros.*

En cuánto a la base de datos se eligió el motor h2 por un asunto de simplicidad, aunque bien se podría realizar una carga defensiva de múltiples motores de bases de datos para entregar cierto grado de interoperabilidad y manteniendo la facilidad de uso.

##Estructura de proyecto
.
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── cl
│   │   │       └── untec
│   │   │           └── biblioteca_virtual
│   │   │               ├── config
│   │   │               │   └── dbConnection.java
│   │   │               ├── controller
│   │   │               │   ├── DashboardController.java
│   │   │               │   ├── LibroController.java
│   │   │               │   ├── LoginController.java
│   │   │               │   ├── LogoutController.java
│   │   │               │   ├── PrestamosController.java
│   │   │               │   └── TestDbServlet.java
│   │   │               ├── dao
│   │   │               │   ├── LibroDAO.java
│   │   │               │   ├── PrestamoDAO.java
│   │   │               │   └── UsuarioDAO.java
│   │   │               ├── model
│   │   │               │   ├── Libro.java
│   │   │               │   ├── Prestamo.java
│   │   │               │   └── Usuario.java
│   │   │               └── service
│   │   │                   ├── LibroService.java
│   │   │                   ├── PrestamoService.java
│   │   │                   └── UsuarioService.java
│   │   ├── resources
│   │   │   └── db
│   │   │       └── 001_init.sql
│   │   └── webapp
│   │       ├── assets
│   │       │   └── styles.css
│   │       ├── index.jsp
│   │       └── WEB-INF
│   │           ├── views
│   │           │   ├── catalog.jsp
│   │           │   ├── dashboard.jsp
│   │           │   ├── error.jsp
│   │           │   ├── loans.jsp
│   │           │   ├── login.jsp
│   │           │   └── logout.jsp
│   │           └── web.xml
│   └── README.md

##Como evolucionar el proyecto

Como se mencionó anteriormente, en este proyecto hubo que ejercer cierta mesura en cuanto a las cosas que se podrían hacer con este tipo de proyecto considerando la vagueza de las especifícaciones y los plazos a cumplir. Este proyecto puede evolucionar de muchas maneras, pero debemos entender su carácter educativo para comprender conceptos básicos de desarrollo web y como este marca una diferencia con el siguiente paso. 

También debemos de pensar que cosas mejorarían a este proyecto, y cuáles son exigencias básicas de una aplicación web moderna que van más allá de las responsividad y lo estético. Exigencias tales como considerar el uso de tokens de sesión o hashear los passwords de nuestros usuarios antes de guardarlos en nuestra base de datos en texto plano, tener roles de sesión y sus respectivas funciones claramente establecidas o realizar carga defensiva de múltiples motores de base de datos para manejar un nivel de interoperabilidad. 

Pero en específico de este proyecto si hay cosas que lo llevarían al siguiente nivel, cosas tales como implementar un dto para consumir de una API pública como OpenLibrary o Penguin Publishing integrando un sistema de búsqueda que permita al usuario o administrador acceder a detalles de los libros y mejorar la experienca de uso. Teniendo especificaciones claras como cuántos libros máximo puede pedir un usuario, qué medidas se tomarían en caso de devoluciones atrasadas o de destrucción del libro e incluso que cuerpo burocrático tendría que encargarse de elevar una solicitud para levantar probables medidas disciplinarias. Es más, también podriamos pensar en incorporar un sistema de lectura de código para llevar un inventario interno de libros y si es más factible una solución desarrollada en casa o una solución propietaria. 

En definitiva y perdonando la divagación, este proyecto nos permitió poner en práctica conceptos de desarrollo web, pero también y más importante aún, pensamiento en sistemas.


