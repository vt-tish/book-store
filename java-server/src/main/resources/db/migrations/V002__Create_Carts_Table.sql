CREATE TABLE carts (
    id         UUID      PRIMARY KEY,
    owner_id   UUID      NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE cart_items (
    cart_id        UUID NOT NULL,
    book_id        UUID NOT NULL,
    quantity       INT NOT NULL,
    price_per_unit DECIMAL(10, 2),

    PRIMARY KEY (cart_id, book_id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE
);