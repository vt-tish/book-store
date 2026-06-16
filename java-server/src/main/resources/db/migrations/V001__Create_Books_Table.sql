CREATE TABLE books (
    id               UUID           NOT NULL PRIMARY KEY,
    name             VARCHAR(255)   NOT NULL,
    genre            VARCHAR(255)   NOT NULL,
    preview_url      VARCHAR(2048),
    age_group        VARCHAR(10)    NOT NULL,
    price            DECIMAL(10, 2) NOT NULL,
    publication_date DATE           NOT NULL,
    author           VARCHAR(255)   NOT NULL,
    pages            INT            NOT NULL,
    characteristics  TEXT,
    description      TEXT,
    language         VARCHAR(20)    NOT NULL,
    is_archived      BOOL           NOT NULL,
    created_at       TIMESTAMP      NOT NULL,
    updated_at       TIMESTAMP      NOT NULL
);
