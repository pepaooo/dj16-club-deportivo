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

-- Cada tipo de membresía puede dar acceso a N instalaciones
INSERT INTO membresia_instalacion (id_membresia, id_instalacion)
VALUES
    (1, 1),  -- Basica -> Cancha futbol rápido 1
    (1, 2),  -- Basica -> Cancha futbol rápido 2
    (1, 3),  -- Basica -> Cancha futbol 7
    (1, 4),  -- Basica -> Cancha futbol
    (1, 5),  -- Basica -> Cancha futbol sala
    (2, 6),  -- Estandar -> Cancha basketbol 1
    (2, 7),  -- Estandar -> Cancha basketbol 2
    (3, 8),  -- Premium -> Cancha basketbol techada
    (4, 9),  -- Anual_Basica -> Cancha voleibol techada
    (5,10),  -- Anual_Premium -> Cancha tenis
    (6,11),  -- VIP -> Cancha squash
    (7,12),  -- Familiar -> Cancha padel
    (8,13),  -- Estudiantil -> Cancha frontenis
    (9,14),  -- Oro -> Sala yoga
    (10,15), -- Platino -> Sala reuniones
    (10,16),  -- Basica -> Gimnasio
    (10,17);  -- Estandar -> Piscina

INSERT INTO miembro
(nombre, apellido_paterno, apellido_materno, direccion, telefono, correo_electronico, fecha_nacimiento, genero)
VALUES
    ('Juan', 'Perez','Perez', 'Calle 1, Ciudad', '5551234567', 'juan.perez@example.com', '1990-01-01', 'M'),
    ('Maria', 'Lopez', 'Lopez', 'Calle 2, Ciudad', '5551234568', 'maria.lopez@example.com', '1992-05-10', 'F'),
    ('Carlos', 'Gomez', 'Gomez', 'Calle 3, Ciudad', '5551234569', 'carlos.gomez@example.com', '1985-03-15', 'M'),
    ('Lucia', 'Hernandez', 'Hernandez', 'Calle 4, Ciudad', '5551234570', 'lucia.hernandez@example.com', '1995-07-20', 'F'),
    ('Miguel', 'Ramirez', 'Ramirez', 'Calle 5, Ciudad', '5551234571', 'miguel.ramirez@example.com', '1988-11-11', 'M'),
    ('Sofia', 'Martinez', 'Martinez', 'Calle 6, Ciudad', '5551234572', 'sofia.martinez@example.com', '1993-12-05', 'F'),
    ('Roberto', 'Jimenez', 'Jimenez', 'Calle 7, Ciudad', '5551234573', 'roberto.jimenez@example.com', '1980-02-25', 'M'),
    ('Ana', 'Castillo', 'Castillo', 'Calle 8, Ciudad', '5551234574', 'ana.castillo@example.com', '1994-09-18', 'F'),
    ('David', 'Garcia', 'Garcia', 'Calle 9, Ciudad', '5551234575', 'david.garcia@example.com', '1986-06-30', 'M'),
    ('Elena', 'Morales', 'Morales', 'Calle 10, Ciudad', '5551234576', 'elena.morales@example.com', '1991-04-22', 'F');

# BCrypt Passwords 10 -> Mexico123.
INSERT INTO usuario
(nombre_usuario, contrasena, estatus, fecha_creacion, ultimo_acceso, id_miembro)
VALUES
    ('admin', '$2y$10$LVniyTVwq7.U6q1Doq0j7.bfk/ARxAoi6y508rmX6Gqb0Oezz.Bt.', 'Activo', '2023-03-01 08:00:00', NULL, NULL),
    ('staff1', '$2y$10$YqssLsu768C33owR26799O6GBgynRIyg5HLkbc.ip7OkXyBw583XW', 'Activo', '2023-03-01 08:05:00', '2023-03-10 09:00:00', NULL),
    ('jperez', '$2y$10$pacWqaNePaTMiEobCNGQou0qQW4PooVh6vVjcLK.u3lEYv0TrZdSa', 'Activo', '2023-03-02 09:00:00', '2023-03-05 10:00:00', 1),
    ('mlopez', '$2y$10$DQAg5ZqpLHswsu0pgMCXruO6z9W.9AxFZJhzveXCEoTRfC58vxu1S', 'Inactivo', '2023-03-02 09:10:00', NULL, 2),
    ('cgomez', '$2y$10$DQAg5ZqpLHswsu0pgMCXruO6z9W.9AxFZJhzveXCEoTRfC58vxu1S', 'Inactivo', '2023-03-03 10:00:00', '2023-03-07 11:00:00', 3),
    ('lhernandez', '$2y$10$DQAg5ZqpLHswsu0pgMCXruO6z9W.9AxFZJhzveXCEoTRfC58vxu1S', 'Activo', '2023-03-04 11:00:00', '2023-03-08 12:00:00', 4),
    ('mramirez', '$2y$10$DQAg5ZqpLHswsu0pgMCXruO6z9W.9AxFZJhzveXCEoTRfC58vxu1S', 'Activo', '2023-03-05 12:00:00', NULL, 5),
    ('smartinez', '$2y$10$DQAg5ZqpLHswsu0pgMCXruO6z9W.9AxFZJhzveXCEoTRfC58vxu1S', 'Activo', '2023-03-06 13:00:00', NULL, 6),
    ('rjimenez', '$2y$10$DQAg5ZqpLHswsu0pgMCXruO6z9W.9AxFZJhzveXCEoTRfC58vxu1S', 'Activo', '2023-03-07 14:00:00', NULL, 7),
    ('amorales', '$2y$10$DQAg5ZqpLHswsu0pgMCXruO6z9W.9AxFZJhzveXCEoTRfC58vxu1S', 'Activo', '2023-03-08 15:00:00', NULL, 10);

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
(id_miembro, id_membresia, monto, fecha_inicio, fecha_fin)
VALUES
    (1, 1, 300.00, '2023-01-05', '2023-02-04'),
    (2, 2, 500.00, '2023-01-10', '2023-02-09'),
    (3, 3, 700.00, '2023-01-15', '2023-02-14'),
    (4, 4, 3200.00, '2023-01-20', '2024-01-19'),
    (6, 6, 1200.00, '2023-02-05', '2023-03-07'),
    (5, 5, 6000.00, '2023-02-01', '2024-01-31'),
    (7, 7, 900.00, '2023-02-10', '2023-03-12'),
    (8, 8, 250.00, '2023-02-15', '2023-03-17'),
    (9, 9, 1000.00, '2023-03-01', '2023-04-30'),
    (10, 10, 2000.00, '2023-03-05', '2023-06-03');

INSERT INTO pago_ajuste (id_pago, monto_ajuste, descripcion, fecha_ajuste)
VALUES
    (1, -100.00, 'Descuento por promoción', '2023-01-05 10:00:00'),
    (2, -200.00, 'Descuento por promoción', '2023-01-10 09:30:00'),
    (3, 50.00, 'Ajuste por error', '2023-01-15 11:00:00');

INSERT INTO historial_membresia
(id_miembro, id_membresia, fecha_cambio, descripcion)
VALUES
    (1, 1, '2023-01-05 10:00:00', 'Primera suscripción'),
    (2, 2, '2023-01-10 09:30:00', 'Actualización a Estandar'),
    (3, 3, '2023-01-15 11:00:00', 'Actualización a Premium'),
    (4, 4, '2023-01-20 08:45:00', 'Cambio a Anual Basica'),
    (5, 5, '2023-02-01 14:00:00', 'Cambio a Anual Premium'),
    (6, 6, '2023-02-05 16:20:00', 'Nuevo registro VIP'),
    (7, 7, '2023-02-10 12:00:00', 'Suscripción Familiar'),
    (8, 8, '2023-02-15 17:30:00', 'Plan Estudiantil adquirido'),
    (9, 9, '2023-03-01 10:10:00', 'Suscripción Oro'),
    (10,10,'2023-03-05 09:00:00', 'Cambio a Platino');

INSERT INTO reserva
(id_instalacion, id_miembro, fecha_hora_inicio, fecha_hora_fin, estado_reserva)
VALUES
    (1, 1, '2023-03-10 09:00:00', '2023-03-10 10:00:00', 'Pendiente'),
    (2, 2, '2023-03-10 09:00:00', '2023-03-10 10:30:00', 'Pendiente'),
    (3, 3, '2023-03-11 08:00:00', '2023-03-11 09:30:00', 'Confirmada'),
    (4, 4, '2023-03-11 09:00:00', '2023-03-11 10:00:00', 'Cancelada'),
    (5, 5, '2023-03-12 10:00:00', '2023-03-12 11:00:00', 'Pendiente'),
    (6, 6, '2023-03-13 07:00:00', '2023-03-13 08:00:00', 'Pendiente'),
    (7, 7, '2023-03-13 10:00:00', '2023-03-13 11:00:00', 'Confirmada'),
    (8, 8, '2023-03-14 09:00:00', '2023-03-14 10:30:00', 'Pendiente'),
    (9, 9, '2023-03-14 15:00:00', '2023-03-14 16:30:00', 'Confirmada'),
    (10,10,'2023-03-15 16:00:00', '2023-03-15 18:00:00', 'Cancelada');

INSERT INTO actividad
(id_usuario, tipo_accion, tabla_afectada, id_entidad_afectada, fecha_hora, descripcion, ip_origen, user_agent)
VALUES
    (1, 'INSERT', 'miembro', 1, '2023-03-01 08:00:00', 'Nuevo miembro registrado', '127.0.0.1',  'Mozilla/5.0'),
    (2, 'UPDATE', 'pago_membresia', 1, '2023-03-01 08:05:00', 'Pago actualizado', '127.0.0.1',  'Mozilla/5.0'),
    (3, 'DELETE', 'reserva', 1, '2023-03-02 09:00:00', 'Reserva cancelada', '127.0.0.1',  'Mozilla/5.0'),
    (4, 'INSERT', 'instalacion', 1, '2023-03-02 09:10:00', 'Nueva instalación creada', '127.0.0.1',  'Mozilla/5.0'),
    (5, 'UPDATE', 'usuario', 1, '2023-03-03 10:00:00', 'Usuario actualizado', '127.0.0.1',  'Mozilla/5.0'),
    (6, 'DELETE', 'membresia', 1, '2023-03-04 11:00:00', 'Membresía eliminada', '127.0.0.1',  'Mozilla/5.0'),
    (7, 'INSERT', 'pago_ajuste', 1, '2023-03-05 12:00:00', 'Ajuste de pago registrado', '127.0.0.1',  'Mozilla/5.0');

INSERT INTO notificacion (id_pago, fecha_vencimiento, estado, fecha_envio, mensaje)
VALUES
    (1, '2023-02-04', 'Pendiente', NULL, 'Su membresía vence en 1 día'),
    (2, '2023-02-09', 'Pendiente', NULL, 'Su membresía vence en 1 día'),
    (3, '2023-02-14', 'Pendiente', NULL, 'Su membresía vence en 1 día'),
    (4, '2024-01-19', 'Pendiente', NULL, 'Su membresía vence en 1 día');

INSERT INTO configuracion_sistema (parametro, valor, descripcion)
VALUES ('notificaciones_activadas', 'true', 'Activa o desactiva el envío de notificaciones por vencimiento de membresías');

INSERT INTO configuracion_sistema (parametro, valor, descripcion)
VALUES
    ('notificacion_periodo', '5,1', 'Día antes de vencimiento para enviar notificación');

