# 📘 FLUJO DEL SISTEMA - Gestión de Ventas por Email

## 🎯 ARQUITECTURA DE BASE DE DATOS

### **Flujo Principal: VENTAS (Clientes)**
```
usuario (rol_id=2) → carrito → item_carrito → pedido → detalle_pedido → nota_venta
```

### **Flujo Secundario: COMPRAS (Admin a Proveedores)**
```
usuario (rol_id=1/3) → compra → detalle_pedido_compra
```

---

## 📊 TABLAS PRINCIPALES

### **1. USUARIO**
```sql
usuario {
    id BIGSERIAL PRIMARY KEY,
    rol_id BIGINT → 1=Admin, 2=Cliente, 3=Cajero
    nombre VARCHAR(255),
    celular VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    genero VARCHAR(50),
    password VARCHAR(255)
}
```

### **2. CARRITO**
```sql
carrito {
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT,
    fecha TIMESTAMP,
    activo BOOLEAN → true=activo, false=procesado
}
```

### **3. ITEM_CARRITO**
```sql
item_carrito {
    id BIGSERIAL PRIMARY KEY,
    carrito_id BIGINT,
    producto_id BIGINT,
    cantidad INTEGER
    -- Precio calculado dinámicamente desde producto.precio_unitario
}
```

### **4. PEDIDO**
```sql
pedido {
    id BIGSERIAL PRIMARY KEY,
    fecha TIMESTAMP,
    descripcion VARCHAR(255),
    importe_total DOUBLE PRECISION,
    importe_total_desc DOUBLE PRECISION,
    estado BOOLEAN,
    metodo_pago_id BIGINT,
    usuario_id BIGINT
}
```

### **5. NOTA_VENTA**
```sql
nota_venta {
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT,  -- Ya NO usa cliente_id
    pedido_id BIGINT,
    fecha TIMESTAMP,
    total DOUBLE PRECISION,
    estado VARCHAR(50),
    observaciones TEXT
}
```

---

## ✅ COMANDOS DISPONIBLES

### **📋 1. CATEGORÍAS**
```bash
# Ver todas las categorías
categoria get

# Ver categoría específica
categoria get <id>

# Crear categoría (activo=true, subcategoria_id=null por defecto)
categoria add <nombre, descripcion>
Ejemplo: categoria add Muebles, Muebles de carpintería artesanal

# Modificar categoría
categoria modify <id, nombre, descripcion>
Ejemplo: categoria modify 1, Muebles Premium, Muebles de alta calidad

# Eliminar categoría
categoria delete <id>
```

---

### **🛍️ 2. PRODUCTOS**
```bash
# Ver todos los productos
producto get

# Ver producto específico
producto get <id>

# Crear producto
producto add <nombre, precio_unitario, imagen, descripcion, categoria_id, stock, stock_minimo>
Ejemplo: producto add Mesa de Roble, 1500.00, mesa.jpg, Mesa artesanal de roble, 1, 10, 2

# Modificar producto
producto modify <id, nombre, precio_unitario, imagen, descripcion, categoria_id, stock, stock_minimo>
Ejemplo: producto modify 1, Mesa de Roble Premium, 1800.00, mesa2.jpg, Mesa premium, 1, 8, 2

# Eliminar producto
producto delete <id>
```


---

### **💳 3. MÉTODOS DE PAGO**
```bash
# Ver todos los métodos de pago
metodoPago get

# Crear método de pago
metodoPago add <nombre, descripcion>
Ejemplo: metodoPago add Efectivo, Pago en efectivo al recibir

# Modificar método de pago
metodoPago modify <id, nombre, descripcion>
Ejemplo: metodoPago modify 1, Efectivo, Pago en efectivo en tienda

# Eliminar método de pago
metodoPago delete <id>
```

---

### **🛒 4. CARRITO DE COMPRAS** (CLIENTES - rol_id=2)
```bash
# Ver MI carrito activo
carrito get

# Agregar producto al carrito
carrito add <producto_id, cantidad>
Ejemplo: carrito add 5, 2

# Eliminar ítem del carrito
carrito delete <item_id>
```

**Flujo Interno del Carrito:**
1. Sistema identifica usuario por email
2. Verifica `rol_id = 2` (CLIENTE)
3. Obtiene/crea carrito con `activo = true`
4. Precios calculados **dinámicamente** desde `producto.precio_unitario`
5. NO guarda precio en `item_carrito`

---

### **💰 5. COMPRA / CHECKOUT**
```bash
# Realizar compra del carrito actual
comprar <metodo_pago_id>
Ejemplo: comprar 1
```

**Flujo Completo de Compra:**
```
1. ✅ Valida usuario (rol_id = 2)
2. ✅ Obtiene carrito activo
3. ✅ Valida que tenga productos
4. ✅ Calcula total
5. ✅ Crea PEDIDO (metodo_pago_id, usuario_id, importe_total)
6. ✅ Crea NOTA_VENTA (usuario_id, pedido_id, total)
7. ✅ Marca carrito como activo=false
8. ✅ Envía confirmación por email
```

---

### **📦 6. PEDIDOS**
```bash
# Ver todos los pedidos
pedido get

# Ver pedido específico
pedido get <id>
```

**NOTA:** 
- `pedido add` está DESHABILITADO (los pedidos se crean automáticamente con el comando `comprar`)
- Los pedidos se crean sin `direccion_id` (tabla direccion no existe)

---

### **🧾 7. NOTAS DE VENTA**
```bash
# Ver MIS notas de venta
notaventa get

# Ver nota de venta específica
notaventa get <id>

# Modificar estado de nota de venta
notaventa modify <id, estado, observaciones>
Ejemplo: notaventa modify 1, entregado, Entregado exitosamente
```

**NOTA:** 
- ❌ `delete` DESHABILITADO (no se pueden eliminar notas de venta)
- Usa `usuario_id` en lugar de `cliente_id`

---

## 🔄 FLUJO COMPLETO: CLIENTE REALIZA UNA COMPRA

### **Paso 1: Agregar Productos al Carrito**
```bash
# Ver productos disponibles
producto get

# Agregar productos al carrito
carrito add 1, 2    # 2 unidades del producto ID 1
carrito add 3, 1    # 1 unidad del producto ID 3

# Verificar carrito
carrito get
```

### **Paso 2: Ver Métodos de Pago**
```bash
# Ver métodos de pago disponibles
metodoPago get

# Resultado ejemplo:
# ID | Nombre        | Descripción
# 1  | Efectivo      | Pago en efectivo
# 2  | Transferencia | Transferencia bancaria
# 3  | QR            | Pago por código QR
```

### **Paso 3: Realizar Compra**
```bash
# Comprar con método de pago ID 1 (Efectivo)
comprar 1
```

### **Paso 4: Verificar Pedido y Nota de Venta**
```bash
# Ver mis pedidos
pedido get

# Ver mis notas de venta
notaventa get
```

---

## ❌ FUNCIONALIDADES DESHABILITADAS

### **Tablas que NO existen en la BD:**

```bash
❌ promocion    → Tabla promocion no existe
❌ cliente      → Ahora se usa usuario con rol_id=2
❌ direccion    → Tabla direccion no existe
```

### **Comandos Deshabilitados:**
```bash
❌ promocion get/add/modify/delete    → No implementado
❌ cliente get/add/modify/delete      → Usar usuario directamente
❌ direccion get/add/modify/delete    → No implementado
❌ notaventa delete                   → No permitido
❌ pedido add                         → Usar comando "comprar"
```

---

## 🔑 ROLES DE USUARIO

```sql
rol_id = 1 → ADMIN    (Gestión completa del sistema)
rol_id = 2 → CLIENTE  (Compras, carrito, ver pedidos)
rol_id = 3 → CAJERO   (Registrar ventas, procesar pedidos)
```

### **Validaciones por Rol:**
- **CARRITO:** Solo `rol_id = 2` (CLIENTE)
- **COMPRAR:** Solo `rol_id = 2` (CLIENTE)
- **PEDIDOS:** Todos los roles pueden consultar
- **NOTAS DE VENTA:** Filtradas por `usuario_id` con `rol_id = 2`

---

## 🛠️ CAMBIOS REALIZADOS EN LA ADAPTACIÓN

### **1. Eliminaciones:**
- ❌ Tabla `cliente` → Usa `usuario.rol_id = 2`
- ❌ Tabla `direccion` → No existe en BD
- ❌ Tabla `promocion` → No existe en BD
- ❌ Tabla `producto_almacen` → Referencia directa a `producto`
- ❌ Campo `nit` en nota_venta

### **2. Cambios en Carrito:**
- `cliente_id` → `usuario_id`
- `estado (VARCHAR)` → `activo (BOOLEAN)`
- `total` → Calculado dinámicamente
- Filtrado: `WHERE u.rol_id = 2`

### **3. Cambios en Productos:**
- Removido: `cod_producto`, `precio_compra`, `precio_venta`
- Agregado: `stock_minimo`, `tiempo`

### **4. Cambios en Categorías:**
- Agregado: `activo (BOOLEAN)`, `subcategoria_id (BIGINT nullable)`

### **5. Cambios en Método de Pago:**
- Agregado: `descripcion` obligatorio

### **6. Método `comprar()` Reimplementado:**
- ✅ Sin dependencia de `direccion`
- ✅ Crea `pedido` directamente con `metodo_pago_id`
- ✅ Crea `nota_venta` desde carrito
- ✅ Marca carrito como `activo = false`

---

## 📝 EJEMPLOS DE USO COMPLETO

### **Ejemplo 1: Cliente compra una mesa**
```bash
# 1. Ver productos
producto get

# 2. Agregar al carrito
carrito add 5, 1    # Mesa ID 5, cantidad 1

# 3. Ver carrito
carrito get

# 4. Ver métodos de pago
metodoPago get

# 5. Comprar
comprar 1    # Efectivo

# 6. Verificar
notaventa get
```

### **Ejemplo 2: Admin agrega nuevo producto**
```bash
# 1. Ver categorías
categoria get

# 2. Crear producto
producto add Silla Roble, 450.00, silla.jpg, Silla artesanal, 1, 20, 5

# 3. Verificar
producto get
```

---

## 🔍 QUERIES SQL IMPORTANTES

### **Ver carrito activo de un usuario:**
```sql
SELECT c.*, u.email, u.nombre 
FROM carrito c 
INNER JOIN usuario u ON c.usuario_id = u.id 
WHERE u.email = 'cliente@email.com' 
  AND c.activo = true 
  AND u.rol_id = 2;
```

### **Ver items del carrito con precios:**
```sql
SELECT 
    ic.id,
    ic.cantidad,
    p.nombre,
    p.precio_unitario,
    (ic.cantidad * p.precio_unitario) as subtotal
FROM item_carrito ic
INNER JOIN producto p ON ic.producto_id = p.id
WHERE ic.carrito_id = ?;
```

### **Ver pedidos de un cliente:**
```sql
SELECT 
    p.*,
    u.nombre,
    u.email,
    mp.nombre as metodo_pago
FROM pedido p
INNER JOIN usuario u ON p.usuario_id = u.id
INNER JOIN metodo_pago mp ON p.metodo_pago_id = mp.id
WHERE u.email = 'cliente@email.com'
  AND u.rol_id = 2
ORDER BY p.fecha DESC;
```

---

## ⚠️ NOTAS IMPORTANTES

1. **Precio Dinámico:** Los precios se calculan en tiempo real desde `producto.precio_unitario`
2. **Rol Obligatorio:** Todas las operaciones de carrito/compra validan `rol_id = 2`
3. **Un Carrito Activo:** Solo puede haber un carrito con `activo=true` por usuario
4. **Sin Direcciones:** El sistema NO maneja direcciones de entrega
5. **Pedidos Automáticos:** Los pedidos se crean automáticamente con el comando `comprar`
6. **Notas de Venta Permanentes:** No se pueden eliminar notas de venta

---

## 📧 CONTACTO Y SOPORTE

Para más información sobre el sistema, contactar al equipo de desarrollo.

**Última actualización:** Noviembre 12, 2025
