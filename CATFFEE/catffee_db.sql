-- =====================================================================
-- CATFFEE · Script de creación de la base de datos (MySQL 8+)
-- Ejecutar completo como usuario root.
-- =====================================================================

DROP DATABASE IF EXISTS catffee_db;
CREATE DATABASE catffee_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE catffee_db;

-- ---------------------------------------------------------------------
-- 1. EMPLEADO (Administrador y Vendedor)
--    contrasena_hash guarda el SHA-256 en hexadecimal (64 caracteres),
--    nunca la contraseña en texto plano.
-- ---------------------------------------------------------------------
CREATE TABLE empleado (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100)  NOT NULL,
    usuario         VARCHAR(50)   NOT NULL,
    contrasena_hash CHAR(64)      NOT NULL,
    rol             ENUM('Administrador', 'Vendedor') NOT NULL,
    acceso_total    BOOLEAN       NOT NULL DEFAULT FALSE,
    activo          BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_empleado_usuario UNIQUE (usuario),
    CONSTRAINT ck_empleado_acceso  CHECK (rol = 'Administrador' OR acceso_total = FALSE)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. CLIENTE (la cédula es la llave primaria)
-- ---------------------------------------------------------------------
CREATE TABLE cliente (
    id        VARCHAR(20)  PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    correo    VARCHAR(120),
    telefono  VARCHAR(20),
    CONSTRAINT ck_cliente_id CHECK (id REGEXP '^[0-9]+$')
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. PROVEEDOR
-- ---------------------------------------------------------------------
CREATE TABLE proveedor (
    id       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL,
    contacto VARCHAR(50),
    CONSTRAINT uq_proveedor_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. INSUMO (materia prima con stock)
-- ---------------------------------------------------------------------
CREATE TABLE insumo (
    id           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    descripcion  VARCHAR(100)  NOT NULL,
    stock_actual DECIMAL(10,2) NOT NULL DEFAULT 0,
    stock_minimo DECIMAL(10,2) NOT NULL DEFAULT 0,
    precio_costo DECIMAL(10,2) NOT NULL,
    CONSTRAINT uq_insumo_descripcion UNIQUE (descripcion),
    CONSTRAINT ck_insumo_stock_actual CHECK (stock_actual >= 0),
    CONSTRAINT ck_insumo_stock_minimo CHECK (stock_minimo >= 0),
    CONSTRAINT ck_insumo_precio       CHECK (precio_costo >= 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. PROVEEDOR_INSUMO (catálogo: qué insumos ofrece cada proveedor)
--    Relación muchos a muchos. La PK compuesta evita duplicados.
-- ---------------------------------------------------------------------
CREATE TABLE proveedor_insumo (
    proveedor_id INT UNSIGNED NOT NULL,
    insumo_id    INT UNSIGNED NOT NULL,
    PRIMARY KEY (proveedor_id, insumo_id),
    CONSTRAINT fk_pi_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_pi_insumo    FOREIGN KEY (insumo_id)    REFERENCES insumo(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. PRODUCTO (lo que se vende)
-- ---------------------------------------------------------------------
CREATE TABLE producto (
    id           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100)  NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    activo       BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_producto_nombre UNIQUE (nombre),
    CONSTRAINT ck_producto_precio CHECK (precio_venta > 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 7. RECETA (ingredientes de cada producto)
--    Tabla intermedia: un insumo aparece una sola vez por producto.
-- ---------------------------------------------------------------------
CREATE TABLE receta (
    producto_id INT UNSIGNED  NOT NULL,
    insumo_id   INT UNSIGNED  NOT NULL,
    cantidad    DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (producto_id, insumo_id),
    CONSTRAINT fk_receta_producto FOREIGN KEY (producto_id) REFERENCES producto(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_receta_insumo   FOREIGN KEY (insumo_id)   REFERENCES insumo(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_receta_cantidad CHECK (cantidad > 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 8. VENTA (cliente opcional, vendedor obligatorio)
-- ---------------------------------------------------------------------
CREATE TABLE venta (
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    fecha       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cliente_id  VARCHAR(20)   NULL,
    empleado_id INT UNSIGNED  NOT NULL,
    total       DECIMAL(12,2) NOT NULL DEFAULT 0,
    confirmada  BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_venta_cliente  FOREIGN KEY (cliente_id)  REFERENCES cliente(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_venta_empleado FOREIGN KEY (empleado_id) REFERENCES empleado(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_venta_total CHECK (total >= 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 9. DETALLE_VENTA (líneas de cada venta)
-- ---------------------------------------------------------------------
CREATE TABLE detalle_venta (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    venta_id        INT UNSIGNED  NOT NULL,
    producto_id     INT UNSIGNED  NOT NULL,
    cantidad        INT UNSIGNED  NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_dv_venta    FOREIGN KEY (venta_id)    REFERENCES venta(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_dv_producto FOREIGN KEY (producto_id) REFERENCES producto(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_dv_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_dv_precio   CHECK (precio_unitario >= 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 10. COMPRA (a proveedores, la hace el administrador)
-- ---------------------------------------------------------------------
CREATE TABLE compra (
    id           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    fecha        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    empleado_id  INT UNSIGNED  NOT NULL,
    proveedor_id INT UNSIGNED  NOT NULL,
    total        DECIMAL(12,2) NOT NULL DEFAULT 0,
    confirmada   BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_compra_empleado  FOREIGN KEY (empleado_id)  REFERENCES empleado(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_compra_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_compra_total CHECK (total >= 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 11. DETALLE_COMPRA (líneas de cada compra)
-- ---------------------------------------------------------------------
CREATE TABLE detalle_compra (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    compra_id       INT UNSIGNED  NOT NULL,
    insumo_id       INT UNSIGNED  NOT NULL,
    cantidad        DECIMAL(10,2) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_dc_compra FOREIGN KEY (compra_id) REFERENCES compra(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_dc_insumo FOREIGN KEY (insumo_id) REFERENCES insumo(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_dc_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_dc_precio   CHECK (precio_unitario >= 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- DATOS INICIALES (los mismos de tus archivos cache/*.txt)
-- Las contraseñas son los hash SHA-256 de cada una.
-- Para generar el hash de otra contraseña en la terminal:
--   echo -n "mi_clave" | sha256sum        (Linux / Mac / Git Bash)
--   certutil -hashfile ...               (Windows: mejor usar Git Bash o PowerShell)
-- ---------------------------------------------------------------------
INSERT INTO empleado (nombre, usuario, contrasena_hash, rol, acceso_total) VALUES
 ('Ezequiel Colmenares', 'Ez3',    SHA2('316', 256),  'Administrador', TRUE),
 ('marvin',              'marvin', SHA2('1234', 256), 'Administrador', TRUE),
 ('marco',               'marco',  SHA2('1234', 256), 'Vendedor',      FALSE);

INSERT INTO cliente (id, nombre, telefono) VALUES
 ('4879242', 'ezequiel', '3025887307');

INSERT INTO proveedor (nombre, contacto) VALUES
 ('cafe latam', '11111111'),
 ('lacteos sa', '32154545');

INSERT INTO insumo (descripcion, stock_actual, stock_minimo, precio_costo) VALUES
 ('caffe',             84,   10,  5000),
 ('cafe instantaneo',  5000, 2000, 50000),
 ('leche',             900,  100, 2000);

INSERT INTO proveedor_insumo (proveedor_id, insumo_id)
SELECT p.id, i.id FROM proveedor p, insumo i
WHERE p.nombre = 'lacteos sa' AND i.descripcion = 'leche';

INSERT INTO producto (nombre, precio_venta) VALUES
 ('Capuchino',       8500),
 ('Cheesecake',      9000),
 ('Latte Macchiato', 7500);

INSERT INTO receta (producto_id, insumo_id, cantidad)
SELECT p.id, i.id, 18 FROM producto p, insumo i
WHERE p.nombre = 'Capuchino' AND i.descripcion = 'caffe';

-- ---------------------------------------------------------------------
-- SEGURIDAD: usuario de la aplicación con permisos mínimos
-- NO usar root en la aplicación. Cambia la contraseña por una propia.
-- ---------------------------------------------------------------------
DROP USER IF EXISTS 'catffee_app'@'localhost';
CREATE USER 'catffee_app'@'localhost' IDENTIFIED BY 'CambiaEstaClave#2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON catffee_db.* TO 'catffee_app'@'localhost';
-- Nada de DROP, ALTER, CREATE ni GRANT para la aplicación.
FLUSH PRIVILEGES;
