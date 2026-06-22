CREATE TABLE employees (
    user_id UUID NOT NULL PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    birth_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_employees_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
