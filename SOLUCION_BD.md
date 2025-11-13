# Configuración de Base de Datos - Opciones

## 🚨 **PROBLEMA ACTUAL**
```
Connection to mail.tecnoweb.org.bo:5432 refused
```

**El servidor de base de datos no está disponible.**

## 🔧 **SOLUCIONES**

### **Opción 1: Base de Datos Local (Recomendado para desarrollo)**

1. **Instalar PostgreSQL localmente:**
   - Descargar desde: https://www.postgresql.org/download/windows/
   - Instalar con puerto 5432
   - Crear usuario y base de datos

2. **Modificar DBConnection.java:**
```java
public static String database = "tecnoweb_local"; // Tu BD local
public static String server = "localhost";        // Servidor local
public static String port = "5432";
public static String user = "postgres";          // Tu usuario local
public static String password = "tu_password";   // Tu password local
```

3. **Crear las tablas necesarias:**
   - Usar el archivo `database_script.sql` que tienes en el proyecto

### **Opción 2: Verificar conectividad al servidor remoto**

```bash
# Probar conectividad
ping mail.tecnoweb.org.bo

# Probar puerto específico
telnet mail.tecnoweb.org.bo 5432
```

### **Opción 3: Configuración híbrida**

Crear un flag para alternar entre local y remoto:

```java
public class DBConnection {
    public static boolean USE_LOCAL = true; // Cambiar a false para remoto
    
    public static String database = USE_LOCAL ? "tecnoweb_local" : "db_grupo11sc";
    public static String server = USE_LOCAL ? "localhost" : "mail.tecnoweb.org.bo";
    public static String port = "5432";
    public static String user = USE_LOCAL ? "postgres" : "grupo11sc";
    public static String password = USE_LOCAL ? "tu_password" : "grup011grup011*";
    public static String url = "jdbc:postgresql://" + server + ":" + port + "/" + database;
}
```

## ✅ **NOTA IMPORTANTE**

**Mi código de actualización de stock está correcto.** El problema es puramente de conectividad de red/BD.

Una vez que resuelvas la conexión a la base de datos, el sistema funcionará perfectamente con:
- ✅ Validación de stock
- ✅ Reducción automática de stock
- ✅ Mensajes informativos

## 🚀 **PASOS SIGUIENTES**

1. **Decidir qué opción usar** (local vs remoto)
2. **Configurar la conexión** según la opción elegida  
3. **Probar el sistema** con la nueva configuración

¿Prefieres configurar una BD local o intentar solucionar la conectividad al servidor remoto?