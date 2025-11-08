CREATE TABLE IF NOT EXISTS warehouse_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL UNIQUE,
    fragile BOOLEAN NOT NULL,
    weight NUMERIC(10, 2) NOT NULL,
    quantity BIGINT NOT NULL,
    width NUMERIC(10, 2) NOT NULL,
    height NUMERIC(10, 2) NOT NULL,
    depth NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_warehouse_items_product
    FOREIGN KEY (product_id)
    REFERENCES product (id)
    ON DELETE RESTRICT
);
CREATE INDEX idx_warehouse_items_product_id ON warehouse_items(product_id);