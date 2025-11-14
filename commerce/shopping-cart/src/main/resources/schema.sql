CREATE TABLE IF NOT EXISTS shopping_cart (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL
);
CREATE TABLE IF NOT EXISTS cart_product (
    cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    CONSTRAINT fk_cart_product_cart
    FOREIGN KEY (cart_id)
    REFERENCES shopping_cart (id)
    ON DELETE CASCADE,
    PRIMARY KEY (cart_id, product_id)
);
CREATE INDEX IF NOT EXISTS idx_cart_product_cart_id ON cart_product(cart_id);
CREATE INDEX IF NOT EXISTS idx_cart_product_product_id ON cart_product(product_id);