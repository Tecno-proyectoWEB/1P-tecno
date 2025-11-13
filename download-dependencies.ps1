# Script para descargar las dependencias necesarias del proyecto
# Ejecutar desde PowerShell en la raíz del proyecto

Write-Host "Descargando dependencias para el proyecto..." -ForegroundColor Green

# Crear directorio lib si no existe
if (!(Test-Path -Path "lib")) {
    New-Item -ItemType Directory -Path "lib" -Force
    Write-Host "Carpeta lib creada" -ForegroundColor Green
}

# URLs de descarga
$dependencies = @{
    "postgresql-42.7.1.jar" = "https://jdbc.postgresql.org/download/postgresql-42.7.1.jar"
    "javax.mail-1.6.2.jar" = "https://repo1.maven.org/maven2/com/sun/mail/javax.mail/1.6.2/javax.mail-1.6.2.jar"
    "activation-1.1.1.jar" = "https://repo1.maven.org/maven2/javax/activation/activation/1.1.1/activation-1.1.1.jar"
    "jbcrypt-0.4.jar" = "https://repo1.maven.org/maven2/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar"
}

foreach ($jar in $dependencies.Keys) {
    $url = $dependencies[$jar]
    $output = "lib\$jar"
    
    if (Test-Path $output) {
        Write-Host "$jar ya existe, saltando..." -ForegroundColor Yellow
        continue
    }
    
    Write-Host "Descargando $jar..." -ForegroundColor Blue
    
    try {
        Invoke-WebRequest -Uri $url -OutFile $output -ErrorAction Stop
        Write-Host "$jar descargado exitosamente" -ForegroundColor Green
    }
    catch {
        Write-Host "Error descargando $jar`: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "Descarga manual desde: $url" -ForegroundColor Yellow
    }
}

Write-Host "`nVerificando archivos descargados..." -ForegroundColor Cyan
Get-ChildItem -Path "lib" -Name "*.jar" | ForEach-Object {
    Write-Host "$_ encontrado" -ForegroundColor Green
}

Write-Host "`nDependencias listas! Ahora puedes compilar y ejecutar el proyecto." -ForegroundColor Green
Write-Host "Comandos siguientes:" -ForegroundColor Yellow
Write-Host "   javac -cp `"lib/*;target/classes`" -encoding UTF-8 -d target/classes src/main/java/com/mycompany/parcial1/tecnoweb/*.java src/main/java/negocio/*.java src/main/java/data/*.java" -ForegroundColor White
Write-Host "   java -cp `"lib/*;target/classes`" com.mycompany.parcial1.tecnoweb.run" -ForegroundColor White