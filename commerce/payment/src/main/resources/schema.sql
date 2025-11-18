CREATE TABLE payments (
    payment_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_total DECIMAL(10,2),
    delivery_total DECIMAL(10,2),
    fee_total DECIMAL(10,2),
    total_payment DECIMAL(10,2),
    state VARCHAR(20) NOT NULL
);