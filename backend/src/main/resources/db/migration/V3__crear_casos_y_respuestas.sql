-- HU-16 T3 · Casos publicados y sus respuestas
CREATE TABLE casos (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo            VARCHAR(150) NOT NULL,
    descripcion       TEXT         NOT NULL,
    autor_id          BIGINT       NOT NULL REFERENCES usuarios (id),
    fecha_publicacion TIMESTAMP    NOT NULL,
    categoria         VARCHAR(50)  NOT NULL,
    estado            VARCHAR(20)  NOT NULL DEFAULT 'ABIERTO',
    CONSTRAINT ck_casos_estado CHECK (estado IN ('ABIERTO', 'CERRADO', 'RESUELTO'))
);

CREATE INDEX idx_casos_fecha_publicacion ON casos (fecha_publicacion DESC);
CREATE INDEX idx_casos_estado ON casos (estado);
CREATE INDEX idx_casos_categoria ON casos (categoria);

CREATE TABLE respuestas (
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    caso_id   BIGINT    NOT NULL REFERENCES casos (id) ON DELETE CASCADE,
    autor_id  BIGINT    NOT NULL REFERENCES usuarios (id),
    contenido TEXT      NOT NULL,
    fecha     TIMESTAMP NOT NULL
);

CREATE INDEX idx_respuestas_caso ON respuestas (caso_id);
