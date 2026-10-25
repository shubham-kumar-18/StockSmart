CREATE TABLE inventory (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    quantity DECIMAL(12,3) NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_inventory_location FOREIGN KEY (location_id) REFERENCES locations(id),
    CONSTRAINT uq_inventory_org_product_location UNIQUE (organization_id, product_id, location_id),
    CONSTRAINT chk_inventory_quantity CHECK (quantity >= 0),
    INDEX ix_inventory_location (organization_id, location_id),
    INDEX ix_inventory_product (organization_id, product_id)
);

CREATE TABLE stock_movements (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    movement_type VARCHAR(30) NOT NULL,
    quantity DECIMAL(12,3) NOT NULL,
    source_location_id BIGINT NULL,
    destination_location_id BIGINT NULL,
    reference VARCHAR(120) NULL,
    reason VARCHAR(500) NULL,
    performed_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movements_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT fk_movements_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_movements_source FOREIGN KEY (source_location_id) REFERENCES locations(id),
    CONSTRAINT fk_movements_destination FOREIGN KEY (destination_location_id) REFERENCES locations(id),
    CONSTRAINT fk_movements_user FOREIGN KEY (performed_by) REFERENCES app_users(id),
    CONSTRAINT chk_movements_quantity CHECK (quantity > 0),
    INDEX ix_movements_org_date (organization_id, created_at),
    INDEX ix_movements_product_date (product_id, created_at)
);
