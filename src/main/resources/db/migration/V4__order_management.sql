ALTER TABLE inventory
    ADD COLUMN reserved_quantity DECIMAL(12,3) NOT NULL DEFAULT 0 AFTER quantity,
    ADD CONSTRAINT chk_inventory_reserved CHECK (reserved_quantity >= 0 AND reserved_quantity <= quantity);

CREATE TABLE customer_orders (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    order_number VARCHAR(40) NOT NULL,
    status VARCHAR(20) NOT NULL,
    subtotal DECIMAL(14,2) NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT fk_orders_location FOREIGN KEY (location_id) REFERENCES locations(id),
    CONSTRAINT fk_orders_user FOREIGN KEY (created_by) REFERENCES app_users(id),
    CONSTRAINT uq_orders_org_number UNIQUE (organization_id, order_number),
    CONSTRAINT chk_orders_status CHECK (status IN ('DRAFT','CONFIRMED','PROCESSING','FULFILLED','CANCELLED')),
    INDEX ix_orders_org_created (organization_id, created_at)
);

CREATE TABLE order_items (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(160) NOT NULL,
    sku VARCHAR(80) NOT NULL,
    quantity DECIMAL(12,3) NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    line_total DECIMAL(14,2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES customer_orders(id),
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT chk_order_items_quantity CHECK (quantity > 0),
    INDEX ix_order_items_order (order_id)
);
