CREATE TABLE IF NOT EXISTS cart (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_item (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    product_id UUID NOT NULL,
    quantity INTEGER DEFAULT 1 CHECK (quantity > 0),
    cart_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_item_cart
    FOREIGN KEY (cart_id)
    REFERENCES cart (id)
    ON DELETE CASCADE,
    CONSTRAINT fk_cart_item_product
    FOREIGN KEY (product_id)
    REFERENCES product (id)
    ON DELETE RESTRICT
    );

CREATE INDEX IF NOT EXISTS idx_cart_user_id ON cart (user_id);
CREATE INDEX IF NOT EXISTS idx_cart_item_cart_id ON cart_item (cart_id);
CREATE INDEX IF NOT EXISTS idx_cart_item_product_id ON cart_item (product_id);
