-- HU-26 T3 · Bitácora de cambios de rol (HU-26 CA5)
CREATE TABLE auditoria_roles (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id    BIGINT       NOT NULL,
    email_usuario VARCHAR(254) NOT NULL,
    rol_anterior  VARCHAR(30)  NOT NULL,
    rol_nuevo     VARCHAR(30)  NOT NULL,
    realizado_por VARCHAR(254) NOT NULL,
    fecha         TIMESTAMP    NOT NULL
);

CREATE INDEX idx_auditoria_roles_fecha ON auditoria_roles (fecha DESC);
