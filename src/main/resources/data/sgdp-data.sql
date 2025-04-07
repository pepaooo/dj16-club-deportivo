INSERT INTO beneficio (nombre, descripcion)
VALUES
    ('Piscina libre', 'Acceso ilimitado a la piscina del club'),
    ('Descuento_tienda', 'Descuento del 10% en la tienda interna'),
    ('Clases_spinning', 'Acceso a clases de spinning'),
    ('Clases_yoga', 'Acceso a clases de yoga'),
    ('Estacionamiento_VIP', 'Lugar de estacionamiento preferente'),
    ('Servicio_toallas', 'Servicio de toallas ilimitado'),
    ('Masajes', 'Sesión mensual de masajes incluida'),
    ('Descuento_restaurante', 'Descuento del 15% en el restaurante'),
    ('Entrenador_personal', 'Sesiones con entrenador personal 1 vez/mes'),
    ('Servicio_lockers', 'Uso de lockers exclusivos');

INSERT INTO membresia (nombre, tarifa, duracion_dias, descripcion)
VALUES
    ('Basica', 300.00, 30, 'Membresía básica con acceso a piscina y estacionamiento'),
    ('Estandar', 500.00, 30, 'Membresía con descuento en tienda y clases de yoga'),
    ('Premium', 700.00, 30, 'Membresía con acceso a piscina, descuento en tienda y clases de yoga'),
    ('Anual_Basica', 3200.00, 365, 'Membresía anual con acceso a piscina, descuento en tienda y servicio de lockers'),
    ('Anual_Premium', 6000.00, 365, 'Membresía anual con acceso a piscina, descuento en tienda, clases de yoga y entrenador personal'),
    ('VIP', 1200.00, 30, 'Membresía VIP con acceso a piscina, estacionamiento VIP y descuento en restaurante'),
    ('Familiar', 900.00, 30, 'Membresía familiar con acceso a piscina y descuento en tienda'),
    ('Estudiantil', 250.00, 30, 'Membresía para estudiantes con descuento en tienda y clases de yoga'),
    ('Oro', 1000.00, 30, 'Membresía Oro con acceso a piscina y sesión mensual de masajes'),
    ('Platino', 2000.00, 30, 'Membresía Platino con acceso a piscina, servicio de toallas y sesión mensual de masajes');


INSERT INTO membresia_beneficio (id_membresia, id_beneficio)
VALUES
-- Basica (1)
(1, 1),  -- Piscina libre
(1, 5),  -- Estacionamiento_VIP

-- Estandar (2)
(2, 1),  -- Piscina libre
(2, 2),  -- Descuento_tienda
(2, 4),  -- Clases_yoga

-- Premium (3)
(3, 1),
(3, 2),
(3, 3),
(3, 4),

-- Anual_Basica (4)
(4, 1),
(4, 2),
(4, 10), -- Servicio_lockers

-- Anual_Premium (5)
(5, 1),
(5, 2),
(5, 3),
(5, 4),
(5, 9), -- Entrenador_personal

-- VIP (6)
(6, 1),
(6, 5),
(6, 8), -- Descuento_restaurante
(6, 9),

-- Familiar (7)
(7, 1),
(7, 2),

-- Estudiantil (8)
(8, 2),
(8, 4),

-- Oro (9)
(9, 1),
(9, 7), -- Masajes

-- Platino (10)
(10, 1),
(10, 6), -- Servicio_toallas
(10, 7);

INSERT INTO instalacion (nombre, descripcion, estado)
VALUES
    ('Cancha futbol rápido 1', 'Cancha 1 para jugar fútbol rápido', 'Disponible'),
    ('Cancha futbol rápido 2', 'Cancha 2 para jugar fútbol rápido', 'Disponible'),
    ('Cancha futbol 7', 'Cancha para jugar fútbol 7', 'En Mantenimiento'),
    ('Cancha futbol', 'Cancha para jugar fútbol', 'Disponible'),
    ('Cancha futbol sala', 'Cancha techada para futbol sala', 'Disponible'),
    ('Cancha basketbol 1', 'Cancha 1 al aire libre para basketbol', 'Disponible'),
    ('Cancha basketbol 2', 'Cancha al aire libre para basketbol', 'Cerrada'),
    ('Cancha basketbol techada', 'Cancha techada para basketbol', 'Disponible'),
    ('Cancha voleibol techada', 'Cancha techada para voleibol', 'Disponible'),
    ('Cancha tenis', 'Cancha de tenis de arcilla', 'Disponible'),
    ('Cancha squash', 'Instalación cerrada para squash', 'Cerrada'),
    ('Cancha padel', 'Cancha de pádel profesional', 'Disponible'),
    ('Cancha frontenis', 'Cancha para frontenis', 'En Mantenimiento'),
    ('Sala yoga', 'Sala acondicionada para yoga', 'Disponible'),
    ('Sala reuniones', 'Espacio para reuniones y eventos', 'Disponible'),
    ('Gimnasio', 'Zona con equipo de gimnasio', 'Disponible'),
    ('Piscina', 'Piscina semi-olímpica techada', 'Disponible');

INSERT INTO miembro
(nombre, apellido_paterno, apellido_materno, direccion, telefono, correo_electronico, fecha_nacimiento, genero, id_membresia)
VALUES
    ('Juan', 'Perez','Perez', 'Calle 1, Ciudad', '5551234567', 'juan.perez@example.com', '1990-01-01', 'M', 1),
    ('Maria', 'Lopez', 'Lopez', 'Calle 2, Ciudad', '5551234568', 'maria.lopez@example.com', '1992-05-10', 'F', 2),
    ('Carlos', 'Gomez', 'Gomez', 'Calle 3, Ciudad', '5551234569', 'carlos.gomez@example.com', '1985-03-15', 'M', 3),
    ('Lucia', 'Hernandez', 'Hernandez', 'Calle 4, Ciudad', '5551234570', 'lucia.hernandez@example.com', '1995-07-20', 'F', 4),
    ('Miguel', 'Ramirez', 'Ramirez', 'Calle 5, Ciudad', '5551234571', 'miguel.ramirez@example.com', '1988-11-11', 'M', 5),
    ('Sofia', 'Martinez', 'Martinez', 'Calle 6, Ciudad', '5551234572', 'sofia.martinez@example.com', '1993-12-05', 'F', 6),
    ('Roberto', 'Jimenez', 'Jimenez', 'Calle 7, Ciudad', '5551234573', 'roberto.jimenez@example.com', '1980-02-25', 'M', 7),
    ('Ana', 'Castillo', 'Castillo', 'Calle 8, Ciudad', '5551234574', 'ana.castillo@example.com', '1994-09-18', 'F', 8),
    ('David', 'Garcia', 'Garcia', 'Calle 9, Ciudad', '5551234575', 'david.garcia@example.com', '1986-06-30', 'M', 9),
    ('Elena', 'Morales', 'Morales', 'Calle 10, Ciudad', '5551234576', 'elena.morales@example.com', '1991-04-22', 'F', 10);

INSERT INTO usuario
(nombre_usuario, contrasena, estatus, fecha_creacion, ultimo_acceso, id_miembro)
VALUES
    ('admin', 'admin123', 'Activo', '2023-03-01 08:00:00', NULL, NULL),
    ('staff1', 'staffpass', 'Activo', '2023-03-01 08:05:00', '2023-03-10 09:00:00', NULL),
    ('jperez', 'juanpass', 'Activo', '2023-03-02 09:00:00', '2023-03-05 10:00:00', 1),
    ('mlopez', 'mariapass', 'Inactivo', '2023-03-02 09:10:00', NULL, 2),
    ('cgomez', 'carlospass', 'Bloqueado', '2023-03-03 10:00:00', '2023-03-07 11:00:00', 3),
    ('lhernandez', 'luciapass', 'Activo', '2023-03-04 11:00:00', '2023-03-08 12:00:00', 4),
    ('mramirez', 'miguelpass', 'Activo', '2023-03-05 12:00:00', NULL, 5),
    ('smartinez', 'sofiapass', 'Activo', '2023-03-06 13:00:00', NULL, 6),
    ('rjimenez', 'robertopass', 'Activo', '2023-03-07 14:00:00', NULL, 7),
    ('amorales', 'elenapass', 'Activo', '2023-03-08 15:00:00', NULL, 10);

INSERT INTO rol
(nombre, descripcion)
VALUES
    ('ADMIN', 'Rol con acceso completo al sistema'),
    ('STAFF', 'Rol para el personal de apoyo'),
    ('MIEMBRO', 'Rol estándar para los miembros del club'),
    ('GERENTE', 'Rol para gestión administrativa y de reportes');

-- Asumimos que los usuarios insertados tienen ids del 1 al 10 (en el orden creado)
-- Roles insertados tienen ids del 1 al 4

INSERT INTO usuario_rol (id_usuario, id_rol)
VALUES
    (1, 1),  -- admin -> ADMIN
    (1, 4),  -- admin -> GERENTE
    (2, 2),  -- staff1 -> STAFF
    (3, 3),  -- jperez -> MIEMBRO
    (4, 3),  -- mlopez -> MIEMBRO
    (5, 3),  -- cgomez -> MIEMBRO
    (6, 3),  -- lhernandez -> MIEMBRO
    (7, 3),  -- mramirez -> MIEMBRO
    (8, 3),  -- smartinez -> MIEMBRO
    (9, 3);  -- rjimenez -> MIEMBRO

INSERT INTO pago_membresia
(id_miembro, id_membresia, monto, fecha_pago, fecha_inicio, fecha_fin, registrado_por)
VALUES
    (1, 1, 300.00, '2023-01-05 10:00:00', '2023-01-05', '2023-02-04', 1),
    (2, 2, 500.00, '2023-01-10 09:30:00', '2023-01-10', '2023-02-09', 1),
    (3, 3, 700.00, '2023-01-15 11:00:00', '2023-01-15', '2023-02-14', 1),
    (4, 4, 3200.00, '2023-01-20 08:45:00', '2023-01-20', '2024-01-19', 1),
    (6, 6, 1200.00, '2023-02-05 16:20:00', '2023-02-05', '2023-03-07', 1),
    (5, 5, 6000.00, '2023-02-01 14:00:00', '2023-02-01', '2024-01-31', 1),
    (7, 7, 900.00,  '2023-02-10 12:00:00', '2023-02-10', '2023-03-12', 1),
    (8, 8, 250.00,  '2023-02-15 17:30:00', '2023-02-15', '2023-03-17', 1),
    (9, 9, 1000.00, '2023-03-01 10:10:00', '2023-03-01', '2023-04-30', 1),
    (10, 10,2000.00, '2023-03-05 09:00:00', '2023-03-05', '2023-06-03', 1);

INSERT INTO pago_membresia
(id_miembro, id_membresia, monto, fecha_pago, fecha_inicio, fecha_fin, registrado_por)
VALUES
    (1, 1, 300.00, '2025-04-01 11:00:00', '2025-04-01', '2025-05-01', 1),
    (2, 2, 500.00, '2025-01-05 07:00:00', '2025-01-05', '2026-01-05', 1);

INSERT INTO pago_ajuste (id_pago, monto_ajuste, descripcion, fecha_ajuste, registrado_por)
VALUES
    (1, -100.00, 'Descuento por promoción', '2023-01-05 10:00:00', 1),
    (2, -200.00, 'Descuento por promoción', '2023-01-10 09:30:00', 1),
    (3, 50.00, 'Ajuste por error', '2023-01-15 11:00:00', 1);

INSERT INTO historial_membresia
(id_miembro, id_membresia, fecha_cambio, descripcion, registrado_por)
VALUES
    (1, 1, '2023-01-05 10:00:00', 'Primera suscripción', 1),
    (2, 2, '2023-01-10 09:30:00', 'Actualización a Estandar', 1),
    (3, 3, '2023-01-15 11:00:00', 'Actualización a Premium', 1),
    (4, 4, '2023-01-20 08:45:00', 'Cambio a Anual Basica', 1),
    (5, 5, '2023-02-01 14:00:00', 'Cambio a Anual Premium', 1),
    (6, 6, '2023-02-05 16:20:00', 'Nuevo registro VIP', 1),
    (7, 7, '2023-02-10 12:00:00', 'Suscripción Familiar', 1),
    (8, 8, '2023-02-15 17:30:00', 'Plan Estudiantil adquirido', 1),
    (9, 9, '2023-03-01 10:10:00', 'Suscripción Oro', 1),
    (10,10,'2023-03-05 09:00:00', 'Cambio a Platino', 1);

INSERT INTO reserva
(id_instalacion, id_miembro, fecha_hora_inicio, fecha_hora_fin, estado_reserva, registrado_por)
VALUES
    (1, 1, '2023-03-10 09:00:00', '2023-03-10 10:00:00', 'Pendiente', 1),
    (2, 2, '2023-03-10 09:00:00', '2023-03-10 10:30:00', 'Pendiente', 1),
    (3, 3, '2023-03-11 08:00:00', '2023-03-11 09:30:00', 'Confirmada', 1),
    (4, 4, '2023-03-11 09:00:00', '2023-03-11 10:00:00', 'Cancelada', 1),
    (5, 5, '2023-03-12 10:00:00', '2023-03-12 11:00:00', 'Pendiente', 1),
    (6, 6, '2023-03-13 07:00:00', '2023-03-13 08:00:00', 'Pendiente', 1),
    (7, 7, '2023-03-13 10:00:00', '2023-03-13 11:00:00', 'Confirmada', 1),
    (8, 8, '2023-03-14 09:00:00', '2023-03-14 10:30:00', 'Pendiente', 1),
    (9, 9, '2023-03-14 15:00:00', '2023-03-14 16:30:00', 'Confirmada', 1),
    (10,10,'2023-03-15 16:00:00', '2023-03-15 18:00:00', 'Cancelada', 1);

INSERT INTO actividad
(id_usuario, accion, fecha_hora, descripcion)
VALUES
    (1, 'Inicio_sesion', '2023-03-01 08:10:00', 'El administrador inició sesión'),
    (1, 'Creacion_membresia', '2023-03-01 08:15:00', 'El administrador creó la membresía Basica'),
    (2, 'Edicion_instalacion', '2023-03-10 09:05:00', 'Staff editó la instalación de basket'),
    (3, 'Pago_membresia', '2023-03-05 10:05:00', 'jperez realizó un pago de membresía'),
    (5, 'Reserva_instalacion', '2023-03-07 11:05:00', 'cgomez reservó la cancha de tenis'),
    (6, 'Actualizacion_perfil', '2023-03-08 12:05:00', 'lhernandez actualizó su dirección'),
    (7, 'Cancelar_reserva', '2023-03-10 12:00:00', 'mramirez canceló su reserva de fútbol'),
    (8, 'Inicio_sesion', '2023-03-10 13:00:00', 'smartinez inició sesión'),
    (9, 'Inicio_sesion', '2023-03-10 14:00:00', 'rjimenez inició sesión'),
    (1, 'Generar_reporte', '2023-03-11 08:00:00', 'El administrador generó reporte de membresías');

INSERT INTO notificacion (id_miembro, fecha_vencimiento, estado, fecha_envio, mensaje, registrado_por, creado_en)
VALUES
    (1, '2023-02-04', 'Pendiente', NULL, 'Su membresía vence en 1 día', 1, '2023-02-03 10:00:00'),
    (2, '2023-02-09', 'Pendiente', NULL, 'Su membresía vence en 1 día', 1, '2023-02-08 09:30:00'),
    (3, '2023-02-14', 'Pendiente', NULL, 'Su membresía vence en 1 día', 1, '2023-02-13 11:00:00'),
    (4, '2024-01-19', 'Pendiente', NULL, 'Su membresía vence en 1 día', 1, '2024-01-18 08:45:00');

INSERT INTO configuracion_sistema (parametro, valor, descripcion)
VALUES ('notificaciones_activadas', 'true', 'Activa o desactiva el envío de notificaciones por vencimiento de membresías');

INSERT INTO configuracion_sistema (parametro, valor, descripcion)
VALUES
    ('notificacion_periodo', '5,1', 'Día antes de vencimiento para enviar notificación');

