# 📧 Flujo Completo del Sistema de Emails - TecnoWeb

## 🏗️ **Arquitectura General del Sistema**

El sistema funciona como un **servidor de email bidireccional** que:
- ✅ **Recibe emails** desde clientes (usuarios finales)
- ✅ **Procesa comandos** contenidos en esos emails
- ✅ **Ejecuta operaciones** en la base de datos PostgreSQL
- ✅ **Responde** con emails HTML/texto plano

---

## 📋 **Componentes Principales**

### 🔧 **Archivos de Configuración:**
- `run.java` - Punto de entrada y configuración SMTP
- `EmailApp.java` - Lógica principal y coordinador
- `EmailReceipt.java` - Receptor de emails (POP3)
- `EmailSend.java` - Enviador de emails (SMTP)

### 📚 **Librerías de Procesamiento:**
- `Interpreter.java` - Intérprete de comandos
- `Analex.java` - Analizador léxico/sintáctico
- `HtmlRes.java` - Generador de HTML para respuestas

### 💾 **Capa de Datos:**
- `SqlConnection.java` - Conexión a PostgreSQL
- `N*.java` (Negocio) - Lógica de negocio por entidad
- `D*.java` (Data) - Modelos de datos

---

## 🔄 **Flujo Detallado: Desde Email hasta Respuesta**

### **FASE 1: 🚀 Inicio del Sistema**

```
1️⃣ run.java.main()
   ├── Configura credenciales Gmail/SMTP
   ├── Crea instancia EmailApp
   └── Llama EmailApp.start()

2️⃣ EmailApp.start()
   ├── Crea EmailReceipt (hilo receptor)
   ├── Configura listeners
   └── Inicia hilo "Mail Receipt"
```

### **FASE 2: 📥 Recepción de Emails**

```
3️⃣ EmailReceipt.run() [BUCLE INFINITO]
   ├── Conecta a mail.tecnoweb.org.bo:110 (POP3)
   ├── Autentica con grupo11sc/grup011grup011*
   ├── Obtiene cantidad de emails nuevos
   ├── Si hay emails > 0:
   │   ├── Descarga todos los emails
   │   ├── Los elimina del servidor
   │   └── Llama emailListener.onReceiptEmail(emails)
   ├── Cierra conexión
   └── Espera 5 segundos y repite
```

### **FASE 3: 🔍 Procesamiento del Email**

```
4️⃣ EmailApp.onReceiptEmail(emails)
   ├── Para cada email recibido:
   ├── Extrae remitente (email del cliente)
   ├── Extrae asunto (contiene el comando)
   ├── Crea Interpreter(comando, remitente)
   ├── Configura EmailApp como listener
   └── Ejecuta interpreter en nuevo hilo
```

### **FASE 4: 📝 Análisis del Comando**

```
5️⃣ Interpreter.run()
   ├── Crea Analex (analizador léxico)
   ├── Analiza la cadena del comando
   ├── Identifica tokens: ENTIDAD + ACCIÓN + PARÁMETROS
   │   Ejemplo: "producto get" → [PRODUCTO][GET][]
   │   Ejemplo: "carrito add 5, 2" → [CARRITO][ADD]["5","2"]
   ├── Crea Instruccion con tokens procesados
   └── Llama filterEvent(instruccion)

6️⃣ Interpreter.filterEvent()
   ├── Crea ParamsAction con:
   │   ├── sender (email del cliente)
   │   ├── command (comando original)
   │   ├── action (GET/ADD/DELETE)
   │   └── params (parámetros extraídos)
   ├── Según la entidad detectada, llama:
   │   ├── listener.usuario() si es USUARIO
   │   ├── listener.producto() si es PRODUCTO
   │   ├── listener.carrito() si es CARRITO
   │   └── etc.
```

### **FASE 5: 💼 Ejecución de Lógica de Negocio**

```
7️⃣ EmailApp.{entidad}(ParamsAction event)
   ├── Según event.getAction():
   │   ├── GET: Consulta datos
   │   ├── ADD: Inserta nuevos registros
   │   ├── DELETE: Elimina registros
   │   └── UPDATE: Modifica registros
   ├── Llama a la capa de negocio correspondiente:
   │   ├── nProducto.get()/save()/delete()
   │   ├── nCarrito.add()/get()/delete()
   │   └── etc.
   ├── Las clases N* ejecutan consultas SQL
   └── Retornan List<String[]> con resultados
```

### **FASE 6: 🔍 Validaciones y Consultas**

```
8️⃣ Clases de Negocio (N*.java)
   ├── Validan parámetros de entrada
   ├── Ejecutan consultas preparadas en PostgreSQL
   ├── Manejan excepciones SQL
   └── Retornan datos estructurados

9️⃣ Base de Datos PostgreSQL
   ├── mail.tecnoweb.org.bo:5432/db_grupo11sc
   ├── Tablas: usuario, producto, carrito, etc.
   ├── Ejecuta consultas SQL
   └── Retorna resultados
```

### **FASE 7: 📊 Generación de Respuesta**

```
🔟 EmailApp.tableNotifySuccess() / simpleNotify()
   ├── Determina tipo de respuesta (tabla/simple)
   ├── Busca el usuario en BD para obtener su rol
   ├── Según el rol (ADMIN=1, CLIENTE=2):
   │   ├── Muestra comandos de administrador
   │   └── Muestra comandos de cliente
   ├── Llama HtmlRes.generateTable() para crear HTML
   ├── Crea objeto Email con:
   │   ├── destinatario (remitente original)
   │   ├── asunto = "Request response"
   │   ├── contenido HTML generado
   └── Llama EmailSend.sendEmail(email)
```

### **FASE 8: 🎨 Generación de HTML**

```
1️⃣1️⃣ HtmlRes.generateTable()
   ├── Crea estructura HTML5 completa
   ├── Aplica estilos CSS responsivos
   ├── Genera tabla con headers y datos
   ├── Añade comandos disponibles según rol
   ├── Incluye footer con fecha/hora
   └── Retorna HTML completo como String
```

### **FASE 9: 📤 Envío de Email de Respuesta**

```
1️⃣2️⃣ EmailSend.sendEmail() [NUEVO HILO]
   ├── Configuración SMTP:
   │   ├── Host: smtp.gmail.com:465 (SSL)
   │   ├── Usuario: marcodavidtoledo@gmail.com
   │   ├── Password: vtwavyxqqlusrzbh
   │   └── Propiedades SSL/TLS habilitadas
   ├── Crea MimeMessage con:
   │   ├── From: marcodavidtoledo@gmail.com
   │   ├── To: {email del cliente}
   │   ├── Subject: "Request response"
   │   └── Content: Multipart/Alternative
   ├── Parte 1: Texto plano (convertHtmlToPlainText)
   ├── Parte 2: HTML (contenido rico)
   ├── Envía via Transport.send()
   └── Log de éxito/error
```

---

## 📈 **Ejemplo Práctico: Comando "producto get"**

### **Usuario envía email:**
```
From: cliente@email.com
To: grupo11sc@mail.tecnoweb.org.bo  
Subject: producto get
Body: [vacío o irrelevante]
```

### **Flujo interno:**
```
1. EmailReceipt detecta nuevo email
2. Extrae: sender="cliente@email.com", command="producto get"
3. Interpreter analiza: entidad=PRODUCTO, acción=GET
4. EmailApp.producto() con action=GET
5. nProducto.list() ejecuta: SELECT * FROM producto
6. Retorna: [[1,"Laptop",500],[2,"Mouse",25],...]
7. HtmlRes genera tabla HTML con productos
8. EmailSend envía respuesta a cliente@email.com
```

### **Cliente recibe:**
```
From: marcodavidtoledo@gmail.com
To: cliente@email.com
Subject: Request response
Body: [Tabla HTML con todos los productos + comandos disponibles]
```

---

## 🔧 **Configuraciones Clave**

### **📧 POP3 (Recepción):**
- **Servidor:** mail.tecnoweb.org.bo:110
- **Usuario:** grupo11sc  
- **Password:** grup011grup011*
- **Protocolo:** POP3 sin SSL

### **📤 SMTP (Envío):**
- **Servidor:** smtp.gmail.com:465
- **Usuario:** marcodavidtoledo@gmail.com
- **Password:** vtwavyxqqlusrzbh (App Password)
- **Protocolo:** SMTP con SSL/TLS

### **🗄️ Base de Datos:**
- **Host:** mail.tecnoweb.org.bo:5432
- **Database:** db_grupo11sc
- **Usuario:** grupo11sc
- **Password:** grup011grup011*

---

## ⚡ **Características del Sistema**

### ✅ **Funcionalidades:**
- **Bidireccional:** Recibe y envía emails
- **Multi-hilo:** Recepción y envío en hilos separados
- **Robusto:** Manejo de errores y reconexión automática
- **Responsive:** Emails HTML adaptables a móviles
- **Seguro:** Autenticación SMTP y validaciones SQL

### 🔄 **Comandos Soportados:**
- `help` - Muestra comandos disponibles
- `usuario get/add` - Gestión de usuarios
- `producto get` - Lista de productos  
- `carrito add/get/delete` - Gestión de carrito
- `comprar <metodo_pago_id>` - Proceso de compra
- `notaventa get` - Historial de compras

### 🎯 **Estados del Sistema:**
- **👥 Cliente (rol_id=2):** Comandos de compra limitados
- **⚔️ Admin (rol_id=1):** Acceso completo al sistema
- **🔍 Auto-detección:** Sistema identifica rol por email

---

## 🚨 **Puntos Críticos**

### ⚠️ **Dependencias:**
1. **PostgreSQL** debe estar accesible
2. **Servidor POP3** debe permitir conexiones
3. **Gmail SMTP** requiere App Password válida
4. **Todas las librerías JAR** deben estar en classpath

### 🔧 **Mantenimiento:**
- Monitorear logs para errores de conexión
- Validar que emails no se acumulen en servidor
- Verificar que respuestas lleguen correctamente
- Actualizar credenciales si expiran

---

*Este sistema implementa un **servidor de email transaccional completo** que permite a los usuarios interactuar con una base de datos PostgreSQL únicamente a través de comandos enviados por email, recibiendo respuestas HTML formateadas automáticamente.*