CREATE TABLE suppliers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    name VARCHAR(160) NOT NULL,
    contact_person VARCHAR(120) NULL,
    email VARCHAR(254) NULL,
    phone VARCHAR(50) NULL,
    address VARCHAR(500) NULL,
    notes TEXT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_suppliers_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    INDEX ix_suppliers_org_name (organization_id, name)
);

CREATE TABLE purchase_orders (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    supplier_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    order_number VARCHAR(40) NOT NULL,
    status VARCHAR(24) NOT NULL,
    total DECIMAL(14,2) NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_purchase_orders_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT fk_purchase_orders_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT fk_purchase_orders_location FOREIGN KEY (location_id) REFERENCES locations(id),
    CONSTRAINT fk_purchase_orders_user FOREIGN KEY (created_by) REFERENCES app_users(id),
    CONSTRAINT uq_purchase_orders_org_number UNIQUE (organization_id, order_number),
    CONSTRAINT chk_purchase_orders_status CHECK (status IN ('DRAFT','ORDERED','PARTIALLY_RECEIVED','RECEIVED','CANCELLED')),
    INDEX ix_purchase_orders_org_date (organization_id, created_at)
);

CREATE TABLE purchase_order_items (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    purchase_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(160) NOT NULL,
    sku VARCHAR(80) NOT NULL,
    ordered_quantity DECIMAL(12,3) NOT NULL,
    received_quantity DECIMAL(12,3) NOT NULL DEFAULT 0,
    unit_cost DECIMAL(12,2) NOT NULL,
    line_total DECIMAL(14,2) NOT NULL,
    CONSTRAINT fk_purchase_items_order FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id),
    CONSTRAINT fk_purchase_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT chk_purchase_items_quantity CHECK (ordered_quantity > 0 AND received_quantity >= 0 AND received_quantity <= ordered_quantity),
    INDEX ix_purchase_items_order (purchase_order_id)
);

CREATE TABLE goods_receipts (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    purchase_order_id BIGINT NOT NULL,
    receipt_reference VARCHAR(120) NOT NULL,
    received_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_goods_receipts_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT fk_goods_receipts_order FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id),
    CONSTRAINT fk_goods_receipts_user FOREIGN KEY (received_by) REFERENCES app_users(id),
    CONSTRAINT uq_goods_receipts_reference UNIQUE (organization_id, receipt_reference),
    INDEX ix_goods_receipts_order (purchase_order_id, created_at)
);

CREATE TABLE goods_receipt_items (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    goods_receipt_id BIGINT NOT NULL,
    purchase_order_item_id BIGINT NOT NULL,
    quantity DECIMAL(12,3) NOT NULL,
    CONSTRAINT fk_receipt_items_receipt FOREIGN KEY (goods_receipt_id) REFERENCES goods_receipts(id),
    CONSTRAINT fk_receipt_items_po_item FOREIGN KEY (purchase_order_item_id) REFERENCES purchase_order_items(id),
    CONSTRAINT chk_receipt_items_quantity CHECK (quantity > 0)
);
