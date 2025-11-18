CREATE TABLE orders (
    order_id UUID PRIMARY KEY,
    shopping_cart_id UUID NOT NULL,
    username VARCHAR(255) NOT NULL,

    payment_id UUID,
    delivery_id UUID,

    state VARCHAR(50) NOT NULL,

    delivery_weight NUMERIC(10,2),
    delivery_volume NUMERIC(10,2),
    fragile BOOLEAN,

    total_price NUMERIC(10,2),
    delivery_price NUMERIC(10,2),
    product_price NUMERIC(10,2)
);

CREATE TABLE order_products (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,

    PRIMARY KEY (order_id, product_id),

    CONSTRAINT fk_order_products_order
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
    ON DELETE CASCADE
);
