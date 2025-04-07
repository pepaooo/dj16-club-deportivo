-- create database clubdep20;
USE clubdep20;

CREATE TABLE IF NOT EXISTS beneficio
(
    id_beneficio INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL UNIQUE,
    descripcion  TEXT         NOT NULL
);

CREATE TABLE IF NOT EXISTS membresia
(
    id_membresia  INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(50)    NOT NULL UNIQUE,
    tarifa        DECIMAL(10, 2) NOT NULL CHECK (tarifa >= 0),
    duracion_dias INT            NOT NULL CHECK (duracion_dias > 0),
    estatus       VARCHAR(20)    NOT NULL DEFAULT 'Activo' CHECK (estatus IN ('Activo', 'Inactivo')),
    descripcion TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS membresia_beneficio
(
    id_membresia INT NOT NULL,
    id_beneficio INT NOT NULL,
    CONSTRAINT pk_tipo_membresia_beneficio PRIMARY KEY (id_membresia, id_beneficio),
    CONSTRAINT fk_tipo_membresia_beneficio_id_tipo_membresia
        FOREIGN KEY (id_membresia) REFERENCES membresia (id_membresia),
    CONSTRAINT fk_tipo_membresia_beneficio_id_beneficio
        FOREIGN KEY (id_beneficio) REFERENCES beneficio (id_beneficio)
);

CREATE TABLE IF NOT EXISTS instalacion
(
    id_instalacion      INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL UNIQUE,
    descripcion         TEXT         NOT NULL,
    estado              VARCHAR(20)  NOT NULL DEFAULT 'Disponible'
        CHECK (estado IN ('Disponible', 'En Mantenimiento', 'Cerrada'))
);

CREATE TABLE IF NOT EXISTS miembro
(
    id_miembro         INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre             VARCHAR(50)  NOT NULL,
    apellido_paterno VARCHAR(50) NOT NULL,
    apellido_materno VARCHAR(50) NOT NULL,
    direccion          VARCHAR(255) NOT NULL,
    telefono           VARCHAR(20)  NOT NULL,
    correo_electronico VARCHAR(100) NOT NULL UNIQUE,
    fecha_nacimiento   DATE         NOT NULL,
    genero             VARCHAR(1)   NOT NULL CHECK (genero IN ('M', 'F', 'O')),
    fecha_inscripcion  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_membresia       INT          NOT NULL,
    CONSTRAINT uq_miembro UNIQUE (nombre, apellido_paterno, apellido_materno, fecha_nacimiento, genero),
    CONSTRAINT fk_miembro_tipo_membresia
        FOREIGN KEY (id_membresia) REFERENCES membresia (id_membresia)
);

CREATE TABLE IF NOT EXISTS usuario
(
    id_usuario     INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50)  NOT NULL UNIQUE,
    contrasena     VARCHAR(255) NOT NULL,
    estatus        VARCHAR(20)  NOT NULL DEFAULT 'Activo'
        CHECK (estatus IN ('Activo', 'Inactivo', 'Bloqueado')),
    fecha_creacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso  DATETIME, -- NULL = el usuario no ha ingresado aún
    id_miembro     INT,      -- NULL = cuenta no ligada a un miembro
    CONSTRAINT fk_usuario_miembro
        FOREIGN KEY (id_miembro) REFERENCES miembro (id_miembro)
);

CREATE TABLE IF NOT EXISTS rol
(
    id_rol      INT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT        NOT NULL
);

CREATE TABLE IF NOT EXISTS usuario_rol
(
    id_usuario INT NOT NULL,
    id_rol     INT NOT NULL,
    CONSTRAINT pk_usuario_rol PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_usuario_rol_id_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
    CONSTRAINT fk_usuario_rol_id_rol
        FOREIGN KEY (id_rol) REFERENCES rol (id_rol)
);

CREATE TABLE IF NOT EXISTS pago_membresia
(
    id_pago        INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_miembro     INT            NOT NULL,
    id_membresia   INT            NOT NULL,
    monto          DECIMAL(10, 2) NOT NULL CHECK (monto >= 0),
    fecha_pago     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_inicio   DATE           NOT NULL,
    fecha_fin      DATE           NOT NULL,
    registrado_por INT            NOT NULL, -- Referencia al usuario que registró el pago
    CONSTRAINT uq_pago UNIQUE (id_miembro, fecha_pago),
    CONSTRAINT fk_pago_miembro
        FOREIGN KEY (id_miembro) REFERENCES miembro (id_miembro),
    CONSTRAINT fk_pago_membresia
        FOREIGN KEY (id_membresia) REFERENCES membresia (id_membresia),
    CONSTRAINT fk_pago_membresia_registrado_por
        FOREIGN KEY (registrado_por) REFERENCES usuario (id_usuario)
);

CREATE TABLE IF NOT EXISTS pago_ajuste
(
    id_pago_ajuste INT            NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_pago        INT            NOT NULL,
    monto_ajuste   DECIMAL(10, 2) NOT NULL CHECK (monto_ajuste <> 0),
    fecha_ajuste   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion    TEXT           NOT NULL,
    registrado_por INT            NOT NULL,
    CONSTRAINT fk_pago_ajuste_pago_membresia FOREIGN KEY (id_pago) REFERENCES pago_membresia (id_pago),
    CONSTRAINT fk_pago_ajuste_registrado_por FOREIGN KEY (registrado_por) REFERENCES usuario (id_usuario)
);


CREATE TABLE IF NOT EXISTS historial_membresia
(
    id_historial   INT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_miembro     INT      NOT NULL,
    id_membresia   INT      NOT NULL,
    fecha_cambio   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion    TEXT     NOT NULL, -- Para indicar el tipo de acción (cambio, renovación, etc.)
    registrado_por INT      NOT NULL, -- Usuario que realizó el cambio
    CONSTRAINT uq_historial UNIQUE (id_miembro, id_membresia, fecha_cambio),
    CONSTRAINT fk_historial_miembro
        FOREIGN KEY (id_miembro) REFERENCES miembro (id_miembro),
    CONSTRAINT fk_historial_tipo_membresia
        FOREIGN KEY (id_membresia) REFERENCES membresia (id_membresia),
    CONSTRAINT fk_historial_registrado_por
        FOREIGN KEY (registrado_por) REFERENCES usuario (id_usuario)
);

CREATE TABLE IF NOT EXISTS reserva
(
    id_reserva        INT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_instalacion    INT         NOT NULL,
    id_miembro        INT         NOT NULL,
    fecha_hora_inicio DATETIME    NOT NULL,
    fecha_hora_fin    DATETIME    NOT NULL CHECK (fecha_hora_fin > fecha_hora_inicio),
    estado_reserva    VARCHAR(20) NOT NULL DEFAULT 'Pendiente'
        CHECK (estado_reserva IN ('Pendiente', 'Confirmada', 'Cancelada')),
    registrado_por    INT         NOT NULL, -- Usuario que registra la reserva
    CONSTRAINT uq_reserva UNIQUE (id_instalacion, id_miembro, fecha_hora_inicio),
    CONSTRAINT fk_reserva_instalacion
        FOREIGN KEY (id_instalacion) REFERENCES instalacion (id_instalacion),
    CONSTRAINT fk_reserva_miembro
        FOREIGN KEY (id_miembro) REFERENCES miembro (id_miembro),
    CONSTRAINT fk_reserva_registrado_por
        FOREIGN KEY (registrado_por) REFERENCES usuario (id_usuario)
);

CREATE TABLE IF NOT EXISTS actividad
(
    id_actividad INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_usuario   INT          NOT NULL,
    accion       VARCHAR(100) NOT NULL,
    fecha_hora   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion  TEXT         NOT NULL,
    CONSTRAINT fk_actividad_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

CREATE TABLE IF NOT EXISTS notificacion
(
    id_notificacion   INT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_miembro        INT         NOT NULL,
    fecha_vencimiento DATE        NOT NULL,
    estado            VARCHAR(20) NOT NULL DEFAULT 'Pendiente'
        CHECK (estado IN ('Pendiente', 'Enviada', 'Error')),
    fecha_envio       DATETIME,
    mensaje           TEXT,
    registrado_por    INT,
    creado_en         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notificacion_miembro FOREIGN KEY (id_miembro) REFERENCES miembro (id_miembro),
    CONSTRAINT fk_notificacion_usuario FOREIGN KEY (registrado_por) REFERENCES usuario (id_usuario)
);

CREATE TABLE IF NOT EXISTS configuracion_sistema
(
    id_config   INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    parametro   VARCHAR(50)  NOT NULL UNIQUE,
    valor       VARCHAR(100) NOT NULL,
    descripcion TEXT
);


