# Configuración de Envío de Emails por Gmail SMTP

## Descripción

Se ha implementado una funcionalidad para alternar entre dos modos de envío de correo electrónico:

1. **Servidor TecnoWeb** (mail.tecnoweb.org.bo) - Configuración original
2. **Gmail SMTP** (smtp.gmail.com) - Nueva configuración con SSL

## Cómo usar

### Cambiar entre modos

Edita el archivo `src/main/java/com/mycompany/parcial1/tecnoweb/run.java` y modifica la bandera `USE_GMAIL`:

```java
// Para usar Gmail SMTP
public static final boolean USE_GMAIL = true;

// Para usar el servidor TecnoWeb original
public static final boolean USE_GMAIL = false;
```

### Configuración actual de Gmail

Las credenciales de Gmail están configuradas en el archivo `run.java`:

- **Usuario**: marcodavidtoledo@gmail.com
- **Contraseña de aplicación**: vtwa vyxq qlus rzbh
- **Puerto**: 465 (SSL)
- **Servidor**: smtp.gmail.com

### ⚠️ Importante

**Contraseña de aplicación de Gmail**: La contraseña configurada es una "App Password" de Gmail, NO es la contraseña regular de la cuenta. Esta contraseña de aplicación:
- Se genera desde la configuración de seguridad de Google
- Es específica para aplicaciones de terceros
- Se usa cuando tienes la autenticación de dos factores activada

### Logs del sistema

Al iniciar la aplicación, verás en la consola el modo activo:

```
===================================
Iniciando EmailApp
Modo: GMAIL SMTP
Usuario: marcodavidtoledo@gmail.com
Puerto: 465 (SSL)
===================================
```

o

```
===================================
Iniciando EmailApp
Modo: SERVIDOR TECNOWEB
Servidor: mail.tecnoweb.org.bo
===================================
```

## Diferencias técnicas

### Gmail SMTP (USE_GMAIL = true)
- Host: smtp.gmail.com
- Puerto: 465
- SSL: Habilitado
- Autenticación: Requerida
- Timeout: 10 segundos

### Servidor TecnoWeb (USE_GMAIL = false)
- Host: mail.tecnoweb.org.bo
- Puerto: 25
- SSL: Deshabilitado
- Autenticación: No requerida
- Timeout: 60 segundos

## Modificaciones realizadas

### Archivos modificados:

1. **run.java**
   - Se agregó la bandera `USE_GMAIL`
   - Se agregaron constantes para las credenciales de Gmail
   - Se agregó un mensaje de inicio que muestra el modo activo

2. **EmailSend.java**
   - Se importó la clase `run` para acceder a las configuraciones
   - Se modificó el método `run()` para usar configuración dinámica según la bandera
   - Se implementó configuración SSL/TLS para Gmail
   - Se agregó autenticación con PasswordAuthentication para Gmail
   - Se actualizaron todos los logs para mostrar la configuración activa

## Cómo compilar y ejecutar

```bash
# Compilar
mvn clean compile

# Ejecutar
mvn exec:java -Dexec.mainClass="com.mycompany.parcial1.tecnoweb.run"
```

## Solución de problemas

### Error de autenticación en Gmail
- Verifica que la contraseña de aplicación sea correcta
- Asegúrate de tener activada la autenticación de dos factores en Gmail
- Genera una nueva contraseña de aplicación si es necesario

### No se envían correos
- Revisa los logs en la consola
- Verifica que `USE_GMAIL` esté configurado correctamente
- Asegúrate de tener conexión a internet
- Verifica que el puerto 465 no esté bloqueado por el firewall

### Cambiar credenciales de Gmail

Si necesitas usar otra cuenta de Gmail, edita las constantes en `run.java`:

```java
public static final String GMAIL_USER = "tu_email@gmail.com";
public static final String GMAIL_APP_PASSWORD = "tu contraseña de aplicación";
```
