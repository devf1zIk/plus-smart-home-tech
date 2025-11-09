CREATE TABLE IF NOT EXISTS product (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    availability VARCHAR(50),
    status VARCHAR(50),
    price NUMERIC(19, 2) NOT NULL CHECK (price > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_product_name ON product(name);
CREATE INDEX idx_product_status ON product(status);