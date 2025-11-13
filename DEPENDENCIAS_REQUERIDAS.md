# Dependencias Requeridas para el Sistema

## 🚨 **PROBLEMA IDENTIFICADO**

El sistema no puede ejecutarse porque faltan las siguientes dependencias JAR:

## 📚 **Librerías Necesarias**

### 1. **PostgreSQL JDBC Driver**
- **Archivo:** `postgresql-42.7.1.jar` (o versión compatible)
- **Propósito:** Conexión a base de datos PostgreSQL
- **Descarga:** https://jdbc.postgresql.org/download/
- **Ubicación:** `lib/postgresql-42.7.1.jar`

### 2. **JavaMail API**
- **Archivo:** `javax.mail-1.6.2.jar` (o versión compatible)
- **Propósito:** Envío de correos electrónicos
- **Descarga:** https://github.com/javaee/javamail/releases
- **Ubicación:** `lib/javax.mail-1.6.2.jar`

### 3. **Activation Framework** (si se requiere)
- **Archivo:** `activation-1.1.1.jar`
- **Propósito:** Soporte para JavaMail
- **Ubicación:** `lib/activation-1.1.1.jar`

## 🔧 **Instrucciones de Instalación**

### Opción 1: Descarga Manual
1. Descarga los archivos JAR desde los enlaces proporcionados
2. Colócalos en la carpeta `lib/` del proyecto
3. Verifica que los nombres coincidan exactamente

### Opción 2: Script PowerShell (Windows)
```powershell
# Ejecutar en PowerShell desde la raíz del proyecto
.\download-dependencies.ps1
```

## ✅ **Verificación**

Después de instalar las dependencias, verifica con:
```bash
ls lib/
```

Deberías ver:
- `postgresql-42.7.1.jar`
- `javax.mail-1.6.2.jar`
- `activation-1.1.1.jar` (opcional)

## 🚀 **Compilación y Ejecución**

Una vez instaladas las dependencias:
```bash
# Compilar
javac -cp "lib/*;target/classes" -encoding UTF-8 -d target/classes src/main/java/com/mycompany/parcial1/tecnoweb/*.java src/main/java/negocio/*.java src/main/java/data/*.java

# Ejecutar
java -cp "lib/*;target/classes" com.mycompany.parcial1.tecnoweb.run
```

## 🔍 **Diagnóstico de Errores**

### Error de Conexión BD
```
"conn" is null
```
**Solución:** Instalar `postgresql-42.7.1.jar`

### Error JavaMail
```
ClassNotFoundException: javax.mail.NoSuchProviderException
```
**Solución:** Instalar `javax.mail-1.6.2.jar`

## 📞 **Soporte**
Si continúan los problemas después de instalar las dependencias, verifica:
1. Configuración de base de datos en `DBConnection.java`
2. Conectividad de red a `mail.tecnoweb.org.bo:5432`
3. Credenciales de base de datos correctas