CREATE TABLE orders (
    id UUID NOT NULL PRIMARY KEY,
    client_id UUID NOT NULL,
    employee_id UUID,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    closed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE order_items (
    id UUID NOT NULL PRIMARY KEY,
    order_id UUID NOT NULL,
    book_id UUID NOT NULL,
    book_name VARCHAR(255) NOT NULL,
    book_author VARCHAR(255) NOT NULL,
    price_per_unit DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_book_items_order_id FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
