CREATE TABLE IF NOT EXISTS product (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    availability VARCHAR(50),
    status VARCHAR(50),
    price DOUBLE PRECISION NOT NULL
);
