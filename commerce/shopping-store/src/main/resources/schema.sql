CREATE TABLE IF NOT EXISTS products (
    product_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    image_src VARCHAR(500),
    quantity_state VARCHAR(20) NOT NULL,
    product_state VARCHAR(20) NOT NULL,
    product_category VARCHAR(20) NOT NULL,
    price NUMERIC(10, 2) NOT NULL CHECK (price > 0)
);

CREATE INDEX IF NOT EXISTS idx_products_product_state ON products(product_state);
CREATE INDEX IF NOT EXISTS idx_products_product_category ON products(product_category);
CREATE INDEX IF NOT EXISTS idx_products_state_category ON products(product_state, product_category);