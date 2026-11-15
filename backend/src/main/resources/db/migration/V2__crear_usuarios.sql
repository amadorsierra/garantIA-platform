-- HU-01 T3 · Cuentas de usuario
CREATE TABLE usuarios (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_usuario VARCHAR(30)  NOT NULL,
    email          VARCHAR(254) NOT NULL,
    password_hash  VARCHAR(100) NOT NULL,
    rol            VARCHAR(30)  NOT NULL DEFAULT 'USUARIO_AFECTADO',
    fecha_registro TIMESTAMP    NOT NULL,
    CONSTRAINT uk_usuarios_email UNIQUE (email),
    CONSTRAINT ck_usuarios_rol CHECK (rol IN ('USUARIO_AFECTADO', 'USUARIO_AYUDANTE', 'MODERADOR', 'ADMINISTRADOR'))
);
