CREATE TABLE books (
    id UUID NOT NULL PRIMARY KEY,
    preview_url VARCHAR(2048),
    age_group VARCHAR(20) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    publication_date DATE NOT NULL,
    pages INT NOT NULL,
    language VARCHAR(20) NOT NULL,
    is_archived BOOL NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE book_translations (
    id UUID PRIMARY KEY,
    book_id UUID NOT NULL,
    language_code VARCHAR(10) NOT NULL,
    name VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    author VARCHAR(255) NOT NULL,
    characteristics TEXT,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_book_translations_book_id FOREIGN KEY (book_id) REFERENCES books (id) ON DELETE CASCADE,
    CONSTRAINT u_book_id_language_code UNIQUE (book_id, language_code)
);
