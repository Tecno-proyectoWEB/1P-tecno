# Resumen de archivos y conexiones — Proyecto tecno-parcial1-smtp

Este documento explica de forma concisa qué hace cada archivo/directorio importante del proyecto y cómo se conectan entre sí. Está pensado para que puedas adaptar el proyecto a tus propios casos de uso.

## Estructura general (resumen)
- `pom.xml` — Dependencias y configuración de compilación (Maven, Java 17, librerías: PostgreSQL JDBC, JavaMail, jBCrypt).
- `README.md` — (archivo raíz, actualmente breve).
- `FILES_SUMMARY.md` — (este archivo) descripción de archivos y relaciones.
- `src/main/java/` — Código fuente Java dividido en paquetes:
  - `com.mycompany.parcial1.tecnoweb` — clase principal del bot por email (EmailApp, run, pruebas, etc.).
  - `data` — clases de acceso a datos (DAO) que ejecutan consultas SQL.
  - `negocio` — lógica de negocio (N* classes) que usan `data`.
  - `librerias` — utilidades: `Email`, `HtmlRes`, `Interpreter`, `ParamsAction`, analizador léxico (`analex`).
  - `interfaces` — contratos para listeners y casos de uso (`ICasoUsoListener`, `IEmailListener`).
  - `postgresConecction` — clases para conexión a PostgreSQL y manejo SMTP/POP3 (EmailSend, EmailReceipt, SqlConnection, DBConnection, ClienteSMTP/POP, etc.).
- `target/` — artefactos de compilación (generado por Maven).

---

## Archivos / paquetes importantes (qué hacen y con quién se conectan)

- `pom.xml`
  - Qué hace: declara el proyecto Maven, la versión de Java (17) y las dependencias necesarias.
  - Se conecta con: el proceso de build; influye en el classpath al ejecutar.

- `src/main/java/com/mycompany/parcial1/tecnoweb/run.java`
  - Qué hace: clase con `main` que arranca la aplicación. Crea `EmailApp` y lanza el hilo que escucha correos.
  - Se conecta con: `EmailApp`.

- `src/main/java/com/mycompany/parcial1/tecnoweb/EmailApp.java`
  - Qué hace: núcleo del bot. Implementa `ICasoUsoListener` y `IEmailListener`. Recibe emails (vía `onReceiptEmail`) y los procesa transformándolos en casos de uso (usuarios, promocion, producto, carrito, nota de venta, pedido, dirección, comprar, etc.). Envía respuestas por email usando `postgresConecction.EmailSend`.
  - Se conecta con:
    - `librerias.Interpreter` / `analex` (para parsear comandos y construir `ParamsAction`).
    - `interfaces.ICasoUsoListener` / `interfaces.IEmailListener` (implementa estos contratos).
    - `negocio.*` (NUsuario, NPromocion, NProducto, etc.) para la lógica de negocio.
    - `postgresConecction.EmailSend` para enviar respuestas por SMTP.
    - `postgresConecction.SqlConnection` / `DBConnection` indirectamente (cuando necesita consultar/actualizar la BD a través de `negocio` y `data`).

- `src/main/java/com/mycompany/parcial1/tecnoweb/pruebas.java` (si existe)
  - Qué hace: probable clase de pruebas/manuales para desarrollador. Útil para experimentos; no necesaria en producción.
  - Se conecta con: código del paquete `tecnoweb`.

- `src/main/java/data/*.java` (DCarrito, DCategoria, DCliente, DDetalleCarrito, DDetalleVenta, DDireccion, DNotaVenta, DPedido, DProducto, DPromocion, DTipoPago, DUsuario)
  - Qué hacen: implementan las consultas SQL (select, insert, update, delete) sobre tablas específicas. Son los DAOs del sistema.
  - Se conectan con: `negocio.*` que invoca estos DAOs; `postgresConecction.SqlConnection` para obtener la conexión JDBC.

- `src/main/java/negocio/*.java` (NCarrito, NCategoria, NCliente, NDireccion, NNotaVenta, NPedido, NProducto, NPromocion, NTipoPago, NUsuario)
  - Qué hacen: contienen reglas de negocio, validaciones y orquestan llamadas a `data.*`. Preparan arrays/listas para que `EmailApp` los formatee y envíe.
  - Se conectan con: `data.*` y con `EmailApp` (que consume los resultados y devuelve notificaciones por email).

- `src/main/java/interfaces/ICasoUsoListener.java`
  - Qué hace: interfaz que define métodos por caso de uso (ej.: usuario, promocion, producto, carrito, comprar, etc.).
  - Se conecta con: `EmailApp` (implementación) y cualquier otra clase que procese acciones.

- `src/main/java/interfaces/IEmailListener.java`
  - Qué hace: define el contrato para recibir emails procesados (p. ej. `onReceiptEmail(List<Email>)`).
  - Se conecta con: `EmailReceipt` (que detecta correo entrante y llama a este listener) y `EmailApp` (implementador).

- `src/main/java/librerias/Email.java`
  - Qué hace: representación simple de un correo (from, to, subject, message) y utilidades para parsear texto plano del correo.
  - Se conecta con: `EmailReceipt` (usa `Email.getEmail(text)` para parsear), `EmailSend` (recibe `Email` para armar el MimeMessage) y `EmailApp` (usa objetos `Email` para responder).

- `src/main/java/librerias/HtmlRes.java`
  - Qué hace: genera HTML para los correos de respuesta (mensajes de éxito, error, tablas, etc.).
  - Se conecta con: `EmailApp` (construye los bodies HTML para enviar por `EmailSend`).

- `src/main/java/librerias/Interpreter.java` y `src/main/java/librerias/analex/*` (Analex, Token, Cinta, TSParams, Utils)
  - Qué hacen: analizador que transforma el contenido del correo (subject/body) en comandos estructurados (`ParamsAction` con action + params + command + sender). `analex` contiene el tokenizer/lexer.
  - Se conectan con: `EmailApp` (que recibe `ParamsAction`), `librerias.ParamsAction` (contenedor del comando parseado).

- `src/main/java/librerias/ParamsAction.java`
  - Qué hace: objeto que representa la acción parseada (action token, parámetros, comando original, remitente).
  - Se conecta con: `Interpreter` (que lo genera) y `EmailApp` (que lo consume).

- `src/main/java/postgresConecction/DBConnection.java`
  - Qué hace: almacena parámetros estáticos (host, puerto, db, user, password, url). Actualmente con valores hardcodeados.
  - Se conecta con: `SqlConnection` y `data.*` indirectamente; cualquier clase que abra conexión JDBC puede usar estos valores.
  - Nota: para cambios al proyecto, es recomendable sustituir valores hardcode por variables de entorno o `config.properties`.

- `src/main/java/postgresConecction/SqlConnection.java`
  - Qué hace: wrapper simple para obtener una `java.sql.Connection` a PostgreSQL usando los parámetros proporcionados.
  - Se conecta con: `data.*` y `negocio.*` cuando necesitan acceso a la BD.

- `src/main/java/postgresConecction/EmailReceipt.java`
  - Qué hace: cliente POP3 manual (socket) que se conecta al servidor POP (por defecto `mail.tecnoweb.org.bo:110`), autentica (USER/PASS), obtiene correos (STAT, RETR) y convierte cada correo en `librerias.Email`. Luego notifica a `IEmailListener` (por ejemplo `EmailApp`).
  - Se conecta con: servidor POP3, `librerias.Email` (parseo), e invoca `IEmailListener.onReceiptEmail()` (`EmailApp`).
  - Nota: implementación basada en sockets; puede requerir ajustes si el servidor usa SSL/TLS o autenticación diferente.

- `src/main/java/postgresConecction/EmailSend.java`
  - Qué hace: toma un objeto `Email`, arma un `MimeMessage` usando JavaMail `Session` y `Transport.send()` para enviar por SMTP (host por defecto `mail.tecnoweb.org.bo`, puerto 25). En el código actual la autenticación y TLS están desactivadas por propiedades.
  - Se conecta con: servidor SMTP para enviar correo; es invocado por `EmailApp.sendEmail()` en un hilo separado.
  - Nota: si tu SMTP requiere auth/TLS, hay que activar propiedades y usar `Authenticator`.

- `src/main/java/postgresConecction/ClienteSMTP.java` / `ClientePOP.java` / `Servidor.java` / `PostgresSqlConecction.java`
  - Qué hacen: clases utilitarias/ejemplos para probar conexión SMTP/POP/DB (clientes de socket, pruebas). No forman parte del flujo principal, pero sirven de referencia para el protocolo.

- `target/` (directorio generado)
  - Qué hace: contiene clases compiladas y empaquetadas; no editar.
  - Se conecta con: ejecución `java -cp target/classes ...` y empaquetado `mvn package`.

---

## Cómo se conectan las piezas (flujo básico)

1. `EmailReceipt` (POP3) conecta al servidor de correo y recibe mensajes.
2. Por cada mensaje `EmailReceipt` crea un `librerias.Email` y llama `IEmailListener.onReceiptEmail(emails)`.
3. `EmailApp` implementa `IEmailListener` y `ICasoUsoListener`:
   - `EmailApp` invoca `librerias.Interpreter` / `analex` para parsear el contenido del correo y obtener un `ParamsAction`.
   - Según el `action` y `command`, `EmailApp` llama al método correspondiente (ej. `usuario`, `promocion`, `producto`, `carrito`, `comprar`, etc.).
4. Los métodos en `EmailApp` delegan a clases en `negocio.*` (NUsuario, NProducto, etc.) para ejecutar la lógica.
5. `negocio.*` llama a `data.*` para ejecutar consultas SQL a la BD usando `SqlConnection`/`DBConnection`.
6. `EmailApp` formatea la respuesta (usar `HtmlRes`) y crea un objeto `librerias.Email` para respuesta.
7. `EmailApp` llama a `postgresConecction.EmailSend` (en un hilo) para enviar la respuesta vía SMTP.

---

## Configuraciones a revisar antes de adaptar a tu proyecto
- `postgresConecction.DBConnection` contiene credenciales y host de BD. Cámbialas por variables de entorno o config.
- `postgresConecction.EmailReceipt` y `EmailSend` contienen host/puerto/usuario/contraseñas. Actualízalos si usas otro servidor de correo. Si el servidor requiere TLS/SSL o autenticación, modifica `EmailSend` para habilitar `mail.smtp.auth` y `mail.smtp.starttls.enable` y proveer `Authenticator`.
- Revisa `librerias.Interpreter` y `analex` si vas a cambiar la sintaxis de comandos.
- Revisa `data/*.java` y `negocio/*.java` para conocer las tablas y columnas requeridas en la BD.

---

## Recomendaciones rápidas para empezar a adaptar (pasos prácticos)
1. Definir los nuevos comandos y casos de uso que quieres soportar.
2. Actualizar `interfaces/ICasoUsoListener` si agregas métodos nuevos.
3. Actualizar `librerias.Interpreter` / `analex` para mapear la sintaxis de tus comandos a `ParamsAction`.
4. Implementar los handlers en `EmailApp` o crear clases por caso de uso y delegar.
5. Implementar/ajustar `negocio.*` y `data.*` y actualizar/crear el schema SQL necesario.
6. Parametrizar credenciales: mover strings hardcode a variables de entorno o `config.properties`.

---

## Cómo compilar y ejecutar (resumen)
- Compilar con Maven (desde la raíz del proyecto):

```powershell
mvn clean compile
mvn package
```

- Ejecutar la app (clase `run`):

```powershell
# Ejecutar desde la raíz (ejemplo simple si sólo usas target/classes)
java -cp target\classes;target\dependency\* com.mycompany.parcial1.tecnoweb.run
```

(Nota: si necesitas dependencias en tiempo de ejecución, usa `mvn dependency:copy-dependencies` para copiar jars a `target/dependency` o ejecuta via `mvn exec:java` configurando el plugin exec en el `pom.xml`).

---

## Notas finales
- Muchas configuraciones están hardcodeadas; para seguridad y flexibilidad mueve credenciales a variables de entorno.
- Si tu servidor de correo usa SSL/TLS, tendrás que adaptar `EmailReceipt` (POP3S puerto 995) o usar la API JavaMail para POP/IMAP en vez de sockets manuales.
- Si quieres, puedo generar una versión inicial que lea la configuración desde variables de entorno o `config.properties` y un stub para un nuevo caso de uso (por ejemplo `inventario`) para que lo adaptes.

---

Si deseas, actualizo el `README.md` del proyecto directamente con este contenido o genero además un `config.example.properties` y cambios mínimos en `DBConnection` para leer variables de entorno. ¿Qué prefieres que haga ahora?

---

## Ejemplo de flujo completo (caso de uso: `usuario`)

Este ejemplo muestra, paso a paso, qué archivos y funciones intervienen cuando un usuario envía un correo con un comando relacionado con usuarios (por ejemplo: `usuario add`, `usuario get`, `usuario modify`, `usuario delete`).

1. Recepción del correo
  - Archivo/clase: `postgresConecction.EmailReceipt`
  - Método principal: `run()`
  - Qué hace: abre socket POP3, autentica (`authUser`), consulta cantidad de mensajes (`STAT`), obtiene cada correo (`RETR`), y convierte el texto en objetos `librerias.Email` usando `Email.getEmail(plainText)`.
  - Conecta con: el servidor POP3 y, una vez parseado el correo, llama a `IEmailListener.onReceiptEmail(List<Email>)`.

2. Entrega al intérprete
  - Archivo/clase: `com.mycompany.parcial1.tecnoweb.EmailApp`
  - Método: `onReceiptEmail(List<Email>)` (implementación de `IEmailListener`)
  - Qué hace: para cada `Email` recibido crea/ejecuta un `librerias.Interpreter` (o invoca su `run`) pasando el cuerpo/asunto y el remitente.

3. Análisis del comando
  - Archivo/clase: `librerias.Interpreter`
  - Método: `run()` -> usa `librerias.analex.Analex` para tokenizar y construir una `Instruccion` / `ParamsAction`, luego llama a `filterEvent(...)`.
  - Qué hace: convierte el texto del correo en una acción estructurada (ej. `Token.USUARIO` con `Token.ADD` y parámetros). En `filterEvent` se mapea el caso de uso al método concreto en `ICasoUsoListener`.

4. Disparo del caso de uso `usuario`
  - Archivo/clase: `com.mycompany.parcial1.tecnoweb.EmailApp`
  - Método: `usuario(ParamsAction event)` (implementación de `ICasoUsoListener`)
  - Qué hace: según `event.getAction()` (ADD/GET/MODIFY/DELETE) valida parámetros y delega a la capa de negocio `negocio.NUsuario` para realizar la operación requerida.

5. Lógica de negocio y acceso a datos
  - Archivo/clase: `negocio.NUsuario`
  - Métodos típicos: `save(...)`, `get(...)`, `update(...)`, `delete(...)` (nombres conceptuales; implementaciones están en `negocio/NUsuario.java`).
  - Conecta con: `data.DUsuario` (DAO) que ejecuta consultas SQL específicas.
  - Archivo/clase: `data.DUsuario`
  - Qué hace: crea y ejecuta sentencias SQL (`SELECT`, `INSERT`, `UPDATE`, `DELETE`) usando `postgresConecction.SqlConnection`.

6. Conexión a la base de datos
  - Archivo/clase: `postgresConecction.SqlConnection`
  - Método: `connect()`
  - Qué hace: construye la URL JDBC y obtiene una `java.sql.Connection` usando los parámetros en `postgresConecction.DBConnection`.

7. Construcción de la respuesta
  - Archivo/clase: `com.mycompany.parcial1.tecnoweb.EmailApp`
  - Métodos: `simpleNotifySuccess`, `tableNotifySuccess`, `simpleNotify`, `handleError`
  - Qué hacen: formatean el resultado de la operación (HTML) usando `librerias.HtmlRes` y crean un objeto `librerias.Email` con destinatario, asunto y cuerpo.

8. Envío de la respuesta por SMTP
  - Archivo/clase: `postgresConecction.EmailSend`
  - Método: `run()` (la clase implementa `Runnable`) — se ejecuta en un hilo nuevo iniciado por `EmailApp.sendEmail()`.
  - Qué hace: crea una sesión JavaMail (`Session`), construye un `MimeMessage` y llama a `Transport.send()` para enviar la respuesta al remitente.

9. Resumen gráfico del flujo (simplificado)
  - POP3 server -> `EmailReceipt.run()` -> `Email.getEmail()` -> `EmailApp.onReceiptEmail()` -> `Interpreter.run()` -> `EmailApp.usuario(ParamsAction)` -> `negocio.NUsuario` -> `data.DUsuario` -> `SqlConnection.connect()` -> DB
  - Respuesta: `EmailApp` -> `EmailSend` -> SMTP server -> remitente

---

Si quieres, puedo ahora:
- Añadir diagramas ASCII simples o un diagrama mermaid al `FILES_SUMMARY.md` para visualizar el flujo.
- Crear un stub de `NUsuario`/`DUsuario` con comentarios si planeas cambiar los nombres o la estructura.

Indica si quieres el diagrama o el stub y lo agrego al repo.