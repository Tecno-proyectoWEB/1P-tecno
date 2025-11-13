-- ============================================================================
-- Script de Creación de Base de Datos - Sistema MRP Carpintería
-- Base de datos: PostgreSQL
-- ============================================================================

-- Eliminar tablas existentes si es necesario (en orden inverso por dependencias)
DROP TABLE IF EXISTS detalle_devolucion CASCADE;
DROP TABLE IF EXISTS devolucion CASCADE;
DROP TABLE IF EXISTS detalle_pedido CASCADE;
DROP TABLE IF EXISTS detalle_pedido_compra CASCADE;
DROP TABLE IF EXISTS pedido CASCADE;
DROP TABLE IF EXISTS compra CASCADE;
DROP TABLE IF EXISTS item_carrito CASCADE;
DROP TABLE IF EXISTS carrito CASCADE;
DROP TABLE IF EXISTS orden_producto CASCADE;
DROP TABLE IF EXISTS orden_preproducto CASCADE;
DROP TABLE IF EXISTS plano CASCADE;
DROP TABLE IF EXISTS pre_plano CASCADE;
DROP TABLE IF EXISTS producto_material CASCADE;
DROP TABLE IF EXISTS proveedor_material CASCADE;
DROP TABLE IF EXISTS pre_maquinarias CASCADE;
DROP TABLE IF EXISTS maquinaria_carpinteros CASCADE;
DROP TABLE IF EXISTS material CASCADE;
DROP TABLE IF EXISTS producto CASCADE;
DROP TABLE IF EXISTS pre_producto CASCADE;
DROP TABLE IF EXISTS maquinarias CASCADE;
DROP TABLE IF EXISTS bitacora CASCADE;
DROP TABLE IF EXISTS rol_permiso CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;
DROP TABLE IF EXISTS rol CASCADE;
DROP TABLE IF EXISTS permiso CASCADE;
DROP TABLE IF EXISTS proveedor CASCADE;
DROP TABLE IF EXISTS categoria CASCADE;
DROP TABLE IF EXISTS subcategoria CASCADE;
DROP TABLE IF EXISTS sector CASCADE;
DROP TABLE IF EXISTS almacen CASCADE;
DROP TABLE IF EXISTS metodo_pago CASCADE;
DROP TABLE IF EXISTS stripe_payments CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;

-- Eliminar secuencias si existen
DROP SEQUENCE IF EXISTS proveedor_id_seq CASCADE;

-- ============================================================================
-- CREACIÓN DE SECUENCIAS
-- ============================================================================

CREATE SEQUENCE proveedor_id_seq START WITH 1 INCREMENT BY 1;

-- ============================================================================
-- CREACIÓN DE TABLAS PRINCIPALES (Sin dependencias)
-- ============================================================================

-- Tabla: subcategoria
CREATE TABLE subcategoria (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    descripcion VARCHAR(255)
);

-- Tabla: categoria
CREATE TABLE categoria (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    descripcion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    subcategoria_id BIGINT,
    CONSTRAINT fk_categoria_subcategoria FOREIGN KEY (subcategoria_id) REFERENCES subcategoria(id)
);

-- Tabla: almacen
CREATE TABLE almacen (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    capacidad DOUBLE PRECISION
);

-- Tabla: sector
CREATE TABLE sector (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    stock DOUBLE PRECISION,
    capacidad_maxima DOUBLE PRECISION,
    tipo VARCHAR(255),
    descripcion VARCHAR(255),
    almacen_id BIGINT,
    CONSTRAINT fk_sector_almacen FOREIGN KEY (almacen_id) REFERENCES almacen(id)
);

-- Tabla: permiso
CREATE TABLE permiso (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255)
);

-- Tabla: rol
CREATE TABLE rol (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255)
);

-- Tabla: rol_permiso (tabla intermedia)
CREATE TABLE rol_permiso (
    rol_id BIGINT NOT NULL,
    permiso_id BIGINT NOT NULL,
    PRIMARY KEY (rol_id, permiso_id),
    CONSTRAINT fk_rol_permiso_rol FOREIGN KEY (rol_id) REFERENCES rol(id) ON DELETE CASCADE,
    CONSTRAINT fk_rol_permiso_permiso FOREIGN KEY (permiso_id) REFERENCES permiso(id) ON DELETE CASCADE
);

-- Tabla: usuario
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    telefono VARCHAR(255),
    password VARCHAR(255),
    estado BOOLEAN DEFAULT TRUE,
    disponibilidad BOOLEAN DEFAULT TRUE,
    cuenta_no_expirada BOOLEAN DEFAULT TRUE,
    cuenta_no_bloqueada BOOLEAN DEFAULT TRUE,
    credenciales_no_expiradas BOOLEAN DEFAULT TRUE,
    rol_id BIGINT NOT NULL,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES rol(id)
);

-- Tabla: proveedor
CREATE TABLE proveedor (
    id BIGINT PRIMARY KEY DEFAULT nextval('proveedor_id_seq'),
    nombre VARCHAR(255),
    ruc VARCHAR(255),
    direccion VARCHAR(255),
    telefono VARCHAR(255),
    email VARCHAR(255),
    persona_contacto VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE
);

-- Tabla: metodo_pago
CREATE TABLE metodo_pago (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    descripcion VARCHAR(255)
);

-- Tabla: material
CREATE TABLE material (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    descripcion VARCHAR(255),
    unidad_medida VARCHAR(255),
    precio DOUBLE PRECISION,
    stock_actual INTEGER,
    stock_minimo INTEGER,
    punto_reorden INTEGER,
    categoria_text VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    imagen VARCHAR(255),
    categoria_id BIGINT,
    sector_id BIGINT,
    CONSTRAINT fk_material_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    CONSTRAINT fk_material_sector FOREIGN KEY (sector_id) REFERENCES sector(id)
);

-- Tabla: producto
CREATE TABLE producto (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    descripcion VARCHAR(255),
    stock INTEGER,
    stock_minimo INTEGER,
    imagen VARCHAR(255),
    tiempo VARCHAR(255),
    precio_unitario DOUBLE PRECISION,
    categoria_id BIGINT,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

-- Tabla: pre_producto
CREATE TABLE pre_producto (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    descripcion VARCHAR(255),
    stock INTEGER,
    tiempo VARCHAR(255),
    url_image VARCHAR(255)
);

-- Tabla: maquinarias
CREATE TABLE maquinarias (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    estado VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255) NOT NULL
);


-- ============================================================================
-- CREACIÓN DE TABLAS DE RELACIONES
-- ============================================================================

-- Tabla: producto_material (BOM - Bill of Materials)
CREATE TABLE producto_material (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    cantidad INTEGER,
    CONSTRAINT fk_producto_material_producto FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE CASCADE,
    CONSTRAINT fk_producto_material_material FOREIGN KEY (material_id) REFERENCES material(id) ON DELETE CASCADE
);

-- Tabla: proveedor_material
CREATE TABLE proveedor_material (
    id BIGSERIAL PRIMARY KEY,
    proveedor_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    precio DOUBLE PRECISION,
    cantidad_minima INTEGER,
    descripcion VARCHAR(255),
    CONSTRAINT fk_proveedor_material_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor(id) ON DELETE CASCADE,
    CONSTRAINT fk_proveedor_material_material FOREIGN KEY (material_id) REFERENCES material(id) ON DELETE CASCADE
);

-- Tabla: pedido
CREATE TABLE pedido (
    id BIGSERIAL PRIMARY KEY,
    fecha TIMESTAMP,
    descripcion VARCHAR(255),
    importe_total DOUBLE PRECISION,
    importe_total_desc DOUBLE PRECISION,
    estado BOOLEAN,
    metodo_pago_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_pedido_metodo_pago FOREIGN KEY (metodo_pago_id) REFERENCES metodo_pago(id),
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Tabla: detalle_pedido
CREATE TABLE detalle_pedido (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT,
    pedido_id BIGINT,
    cantidad INTEGER NOT NULL,
    estado BOOLEAN DEFAULT FALSE,
    importe_total DOUBLE PRECISION DEFAULT 0.0,
    importe_total_desc DOUBLE PRECISION DEFAULT 0.0,
    precio_unitario DOUBLE PRECISION,
    CONSTRAINT fk_detalle_pedido_producto FOREIGN KEY (producto_id) REFERENCES producto(id),
    CONSTRAINT fk_detalle_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE
);

-- Tabla: compra
CREATE TABLE compra (
    id BIGSERIAL PRIMARY KEY,
    estado VARCHAR(255),
    fecha TIMESTAMP,
    importe_total DOUBLE PRECISION,
    importe_descuento DOUBLE PRECISION,
    proveedor_id BIGINT,
    usuario_id BIGINT,
    CONSTRAINT fk_compra_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor(id),
    CONSTRAINT fk_compra_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Tabla: detalle_pedido_compra
CREATE TABLE detalle_pedido_compra (
    id BIGSERIAL PRIMARY KEY,
    cantidad INTEGER NOT NULL,
    estado VARCHAR(255),
    importe DOUBLE PRECISION,
    importe_desc DOUBLE PRECISION,
    precio DOUBLE PRECISION,
    compra_id BIGINT,
    material_id BIGINT,
    CONSTRAINT fk_detalle_pedido_compra_compra FOREIGN KEY (compra_id) REFERENCES compra(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_pedido_compra_material FOREIGN KEY (material_id) REFERENCES material(id)
);

-- Tabla: devolucion
CREATE TABLE devolucion (
    id BIGSERIAL PRIMARY KEY,
    fecha TIMESTAMP,
    motivo VARCHAR(255),
    descripcion VARCHAR(255),
    importe_total DOUBLE PRECISION,
    estado BOOLEAN DEFAULT FALSE,
    usuario_id BIGINT,
    pedido_id BIGINT,
    CONSTRAINT fk_devolucion_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_devolucion_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE
);

-- Tabla: detalle_devolucion
CREATE TABLE detalle_devolucion (
    id BIGSERIAL PRIMARY KEY,
    cantidad INTEGER,
    importe_total DOUBLE PRECISION,
    motivo_detalle VARCHAR(255),
    devolucion_id BIGINT NOT NULL,
    detalle_pedido_id BIGINT NOT NULL,
    CONSTRAINT fk_detalle_devolucion_devolucion FOREIGN KEY (devolucion_id) REFERENCES devolucion(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_devolucion_detalle_pedido FOREIGN KEY (detalle_pedido_id) REFERENCES detalle_pedido(id)
);

-- Tabla: orden_producto
CREATE TABLE orden_producto (
    id BIGSERIAL PRIMARY KEY,
    cantidad INTEGER,
    descripcion VARCHAR(255),
    estado VARCHAR(255),
    fecha TIMESTAMP,
    usuario_id BIGINT,
    producto_id BIGINT,
    CONSTRAINT fk_orden_producto_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_orden_producto_producto FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- Tabla: orden_preproducto
CREATE TABLE orden_preproducto (
    id BIGSERIAL PRIMARY KEY,
    cantidad INTEGER,
    descripcion VARCHAR(255),
    estado VARCHAR(255),
    fecha TIMESTAMP,
    usuario_id BIGINT,
    preproducto_id BIGINT,
    CONSTRAINT fk_orden_preproducto_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_orden_preproducto_preproducto FOREIGN KEY (preproducto_id) REFERENCES pre_producto(id)
);

-- Tabla: plano
CREATE TABLE plano (
    id BIGSERIAL PRIMARY KEY,
    cantidad INTEGER,
    descripcion VARCHAR(255),
    tiempo_estimado VARCHAR(255),
    producto_id BIGINT,
    pre_producto_id BIGINT,
    CONSTRAINT fk_plano_producto FOREIGN KEY (producto_id) REFERENCES producto(id),
    CONSTRAINT fk_plano_pre_producto FOREIGN KEY (pre_producto_id) REFERENCES pre_producto(id)
);

-- Tabla: pre_plano
CREATE TABLE pre_plano (
    id BIGSERIAL PRIMARY KEY,
    cantidad INTEGER,
    descripcion VARCHAR(255),
    tiempo_estimado VARCHAR(255),
    pre_producto_id BIGINT,
    material_id BIGINT,
    CONSTRAINT fk_pre_plano_pre_producto FOREIGN KEY (pre_producto_id) REFERENCES pre_producto(id) ON DELETE CASCADE,
    CONSTRAINT fk_pre_plano_material FOREIGN KEY (material_id) REFERENCES material(id)
);

-- Tabla: pre_maquinarias
CREATE TABLE pre_maquinarias (
    id BIGSERIAL PRIMARY KEY,
    cantidad INTEGER,
    descripcion VARCHAR(255),
    tiempo_estimado VARCHAR(255),
    maquinaria_id BIGINT,
    pre_producto_id BIGINT,
    CONSTRAINT fk_pre_maquinarias_maquinaria FOREIGN KEY (maquinaria_id) REFERENCES maquinarias(id) ON DELETE CASCADE,
    CONSTRAINT fk_pre_maquinarias_pre_producto FOREIGN KEY (pre_producto_id) REFERENCES pre_producto(id) ON DELETE CASCADE
);

-- Tabla: maquinaria_carpinteros
CREATE TABLE maquinaria_carpinteros (
    id BIGSERIAL PRIMARY KEY,
    estado VARCHAR(255),
    maquinaria_id BIGINT,
    carpintero_id BIGINT,
    CONSTRAINT fk_maquinaria_carpinteros_maquinaria FOREIGN KEY (maquinaria_id) REFERENCES maquinarias(id) ON DELETE CASCADE,
    CONSTRAINT fk_maquinaria_carpinteros_carpintero FOREIGN KEY (carpintero_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Tabla: carrito
CREATE TABLE carrito (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT,
    fecha_creacion TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_carrito_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Tabla: item_carrito
CREATE TABLE item_carrito (
    id BIGSERIAL PRIMARY KEY,
    carrito_id BIGINT,
    producto_id BIGINT,
    cantidad INTEGER,
    CONSTRAINT fk_item_carrito_carrito FOREIGN KEY (carrito_id) REFERENCES carrito(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_carrito_producto FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- Tabla: bitacora
CREATE TABLE bitacora (
    id BIGSERIAL PRIMARY KEY,
    accion VARCHAR(255) NOT NULL,
    detalles TEXT,
    fecha TIMESTAMP NOT NULL,
    usuario_id BIGINT NOT NULL,
    direccion_ip VARCHAR(255),
    CONSTRAINT fk_bitacora_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

-- Tabla: stripe_payments
CREATE TABLE stripe_payments (
    id BIGSERIAL PRIMARY KEY,
    payment_intent_id VARCHAR(255) UNIQUE NOT NULL,
    order_id VARCHAR(255),
    amount NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255),
    customer_name VARCHAR(255),
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- ============================================================================
-- CREACIÓN DE ÍNDICES
-- ============================================================================

-- Índices para mejorar rendimiento en búsquedas frecuentes
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_rol ON usuario(rol_id);
CREATE INDEX idx_material_categoria ON material(categoria_id);
CREATE INDEX idx_material_sector ON material(sector_id);
CREATE INDEX idx_producto_categoria ON producto(categoria_id);
CREATE INDEX idx_pedido_usuario ON pedido(usuario_id);
CREATE INDEX idx_pedido_fecha ON pedido(fecha);
CREATE INDEX idx_compra_proveedor ON compra(proveedor_id);
CREATE INDEX idx_compra_usuario ON compra(usuario_id);
CREATE INDEX idx_bitacora_usuario ON bitacora(usuario_id);
CREATE INDEX idx_bitacora_fecha ON bitacora(fecha);
CREATE INDEX idx_stripe_payment_intent ON stripe_payments(payment_intent_id);

-- ============================================================================
-- COMENTARIOS EN TABLAS Y COLUMNAS
-- ============================================================================

COMMENT ON TABLE usuario IS 'Tabla de usuarios del sistema con autenticación';
COMMENT ON TABLE rol IS 'Roles del sistema (ADMIN, USER, etc.)';
COMMENT ON TABLE permiso IS 'Permisos individuales del sistema';
COMMENT ON TABLE material IS 'Materiales/insumos utilizados en la producción';
COMMENT ON TABLE producto IS 'Productos finales fabricados';
COMMENT ON TABLE pre_producto IS 'Pre-productos o componentes intermedios';
COMMENT ON TABLE proveedor IS 'Proveedores de materiales';
COMMENT ON TABLE pedido IS 'Pedidos de clientes';
COMMENT ON TABLE compra IS 'Compras de materiales a proveedores';
COMMENT ON TABLE bitacora IS 'Registro de acciones de usuarios para auditoría';

-- ============================================================================
-- DATOS INICIALES (Opcional)
-- ============================================================================

-- Insertar roles básicos
INSERT INTO rol (nombre) VALUES ('ADMIN');
INSERT INTO rol (nombre) VALUES ('USER');
INSERT INTO rol (nombre) VALUES ('CLIENTE');

-- Insertar métodos de pago básicos
INSERT INTO metodo_pago (nombre, descripcion) VALUES ('Efectivo', 'Pago en efectivo');
INSERT INTO metodo_pago (nombre, descripcion) VALUES ('Tarjeta', 'Pago con tarjeta de crédito/débito');
INSERT INTO metodo_pago (nombre, descripcion) VALUES ('Transferencia', 'Pago por transferencia bancaria');

-- ============================================================================
-- INSTRUCCIONES DE USO
-- ============================================================================
-- 
-- Para ejecutar este script:
-- 
-- 1. Desde psql (línea de comandos):
--    psql -U grupo11sc -d db_grupo11sc -h mail.tecnoweb.org.bo -p 5432 -f database_script.sql
--
-- 2. Desde pgAdmin o cualquier cliente PostgreSQL:
--    - Conectarse a la base de datos
--    - Abrir el archivo database_script.sql
--    - Ejecutar todo el script
--
-- 3. Desde una aplicación SQL:
--    - Conectarse a la base de datos
--    - Copiar y pegar el contenido del script
--    - Ejecutar
--
-- NOTA: Este script elimina todas las tablas existentes antes de crearlas.
--       Si desea mantener los datos, modifique o comente las líneas DROP TABLE.
--
-- ============================================================================
-- FIN DEL SCRIPT
-- ============================================================================

