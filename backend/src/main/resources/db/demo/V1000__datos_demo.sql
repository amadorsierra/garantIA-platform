-- Datos de demostración. Solo se cargan con el perfil "dev" (ver application-dev.yaml).
-- Todas las cuentas tienen la contraseña Demo1234.
INSERT INTO usuarios (nombre_usuario, email, password_hash, rol, fecha_registro) VALUES
    ('admin',     'admin@garantia.local',     '$2a$10$lCKUHWei2edGhLhdfhv8XO/Wvb5VP1YQ/SlZ32hARSaGE85m1W7ji', 'ADMINISTRADOR',    NOW()),
    ('marta',     'marta@garantia.local',     '$2a$10$lCKUHWei2edGhLhdfhv8XO/Wvb5VP1YQ/SlZ32hARSaGE85m1W7ji', 'USUARIO_AFECTADO', NOW()),
    ('pablo',     'pablo@garantia.local',     '$2a$10$lCKUHWei2edGhLhdfhv8XO/Wvb5VP1YQ/SlZ32hARSaGE85m1W7ji', 'USUARIO_AYUDANTE', NOW()),
    ('moderador', 'moderador@garantia.local', '$2a$10$lCKUHWei2edGhLhdfhv8XO/Wvb5VP1YQ/SlZ32hARSaGE85m1W7ji', 'MODERADOR',        NOW());

INSERT INTO casos (titulo, descripcion, autor_id, fecha_publicacion, categoria, estado) VALUES
    ('Portátil que no enciende a los 8 meses',
     'Compré un portátil hace 8 meses y de repente no enciende. La tienda dice que es por un golpe, pero no se ha caído nunca.',
     (SELECT id FROM usuarios WHERE email = 'marta@garantia.local'), NOW() - INTERVAL '6 days', 'Electrónica', 'ABIERTO'),
    ('Lavadora con fuga de agua',
     'La lavadora pierde agua en cada lavado desde el primer mes. El servicio técnico ha venido dos veces sin arreglarla.',
     (SELECT id FROM usuarios WHERE email = 'marta@garantia.local'), NOW() - INTERVAL '5 days', 'Electrodomésticos', 'RESUELTO'),
    ('Móvil con la pantalla rota de fábrica',
     'Al abrir la caja la pantalla tenía una grieta. La tienda online no acepta la devolución porque han pasado 20 días.',
     (SELECT id FROM usuarios WHERE email = 'marta@garantia.local'), NOW() - INTERVAL '4 days', 'Electrónica', 'ABIERTO'),
    ('Zapatillas despegadas en dos semanas',
     'La suela se ha despegado a las dos semanas de uso normal. ¿Cubre esto la garantía?',
     (SELECT id FROM usuarios WHERE email = 'marta@garantia.local'), NOW() - INTERVAL '3 days', 'Ropa y calzado', 'CERRADO'),
    ('Cafetera que deja de calentar',
     'La cafetera no calienta el agua desde la semana pasada. Tiene 14 meses.',
     (SELECT id FROM usuarios WHERE email = 'marta@garantia.local'), NOW() - INTERVAL '2 days', 'Electrodomésticos', 'ABIERTO'),
    ('Auriculares con un lado sin sonido',
     'El auricular izquierdo ha dejado de sonar. El fabricante me pide la factura original y la he perdido.',
     (SELECT id FROM usuarios WHERE email = 'marta@garantia.local'), NOW() - INTERVAL '1 day', 'Electrónica', 'ABIERTO');

INSERT INTO respuestas (caso_id, autor_id, contenido, fecha) VALUES
    ((SELECT id FROM casos WHERE titulo = 'Portátil que no enciende a los 8 meses'),
     (SELECT id FROM usuarios WHERE email = 'pablo@garantia.local'),
     'Durante los primeros años de garantía se presume que el defecto ya existía. Es la tienda la que debe demostrar el golpe.',
     NOW() - INTERVAL '5 days'),
    ((SELECT id FROM casos WHERE titulo = 'Lavadora con fuga de agua'),
     (SELECT id FROM usuarios WHERE email = 'pablo@garantia.local'),
     'Si la reparación no funciona, puedes pedir la sustitución del producto.',
     NOW() - INTERVAL '4 days'),
    ((SELECT id FROM casos WHERE titulo = 'Lavadora con fuga de agua'),
     (SELECT id FROM usuarios WHERE email = 'marta@garantia.local'),
     'Lo pedí por escrito y me la han cambiado. ¡Gracias!',
     NOW() - INTERVAL '3 days');
