CREATE TABLE orders (
    id UUID NOT NULL PRIMARY KEY,
    client_id UUID NOT NULL,
    employee_id UUID,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    closed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_orders_client_id FOREIGN KEY (client_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_orders_employee_id FOREIGN KEY (employee_id) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE TABLE order_items (
    id UUID NOT NULL PRIMARY KEY,
    order_id UUID NOT NULL,
    book_id UUID NOT NULL,
    price_per_unit DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_order_items_order_id FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_book_id FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT
);
