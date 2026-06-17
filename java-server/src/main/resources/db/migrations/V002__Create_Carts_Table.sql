CREATE TABLE carts (
    id         UUID      NOT NULL PRIMARY KEY,
    owner_id   UUID      NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE cart_items (
    cart_id   UUID         NOT NULL,
    book_id   UUID         NOT NULL,
    book_name VARCHAR(255) NOT NULL,
    quantity  INT          NOT NULL,

    PRIMARY KEY (cart_id, book_id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE
);