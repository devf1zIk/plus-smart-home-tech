CREATE TABLE IF NOT EXISTS warehouse_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL UNIQUE,
    fragile BOOLEAN NOT NULL,
    weight NUMERIC(10, 2) NOT NULL,
    quantity BIGINT NOT NULL DEFAULT 0,
    width NUMERIC(10, 2) NOT NULL,
    height NUMERIC(10, 2) NOT NULL,
    depth NUMERIC(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS order_bookings (
    booking_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    delivery_id UUID,
    state VARCHAR(255),
    total_weight NUMERIC(10, 2) NOT NULL,
    total_volume NUMERIC(10, 2) NOT NULL,
    fragile BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS order_booking_products (
    order_booking_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (order_booking_id, product_id),
    FOREIGN KEY (order_booking_id) REFERENCES order_bookings(booking_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_warehouse_items_product_id ON warehouse_items(product_id);
CREATE INDEX IF NOT EXISTS idx_order_bookings_order_id ON order_bookings(order_id);
CREATE INDEX IF NOT EXISTS idx_order_bookings_delivery_id ON order_bookings(delivery_id);
CREATE INDEX IF NOT EXISTS idx_order_booking_products_order_id ON order_booking_products(order_booking_id);
CREATE INDEX IF NOT EXISTS idx_order_booking_products_product_id ON order_booking_products(product_id);