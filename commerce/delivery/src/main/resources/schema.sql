CREATE TABLE IF NOT EXISTS addresses (
    address_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    house VARCHAR(255) NOT NULL,
    flat VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS deliveries (
    delivery_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    delivery_state VARCHAR(50) NOT NULL,
    from_address_id UUID,
    to_address_id UUID,
    total_weight NUMERIC(10,2) NOT NULL,
    total_volume NUMERIC(10,2) NOT NULL,
    fragile BOOLEAN NOT NULL,
    CONSTRAINT fk_from_address FOREIGN KEY (from_address_id) REFERENCES addresses(address_id),
    CONSTRAINT fk_to_address FOREIGN KEY (to_address_id) REFERENCES addresses(address_id)
);
