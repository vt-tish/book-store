CREATE TABLE carts (
    id UUID NOT NULL PRIMARY KEY,
    owner_id UUID NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE cart_items (
    id UUID NOT NULL PRIMARY KEY,
    cart_id UUID NOT NULL,
    book_id UUID NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_cart_items_cart_id FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE,
    CONSTRAINT u_cart_id_book_id UNIQUE (cart_id, book_id)
);
