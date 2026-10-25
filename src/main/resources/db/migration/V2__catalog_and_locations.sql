CREATE TABLE categories (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    name VARCHAR(80) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_categories_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT uq_categories_org_name UNIQUE (organization_id, name),
    INDEX ix_categories_organization (organization_id)
);

CREATE TABLE products (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    category_id BIGINT NULL,
    name VARCHAR(160) NOT NULL,
    sku VARCHAR(80) NOT NULL,
    description TEXT NULL,
    selling_price DECIMAL(12,2) NOT NULL DEFAULT 0,
    cost_price DECIMAL(12,2) NULL,
    barcode VARCHAR(120) NULL,
    unit VARCHAR(30) NOT NULL DEFAULT 'each',
    reorder_level DECIMAL(12,3) NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT uq_products_org_sku UNIQUE (organization_id, sku),
    CONSTRAINT uq_products_org_barcode UNIQUE (organization_id, barcode),
    CONSTRAINT chk_products_selling_price CHECK (selling_price >= 0),
    CONSTRAINT chk_products_cost_price CHECK (cost_price IS NULL OR cost_price >= 0),
    CONSTRAINT chk_products_reorder_level CHECK (reorder_level >= 0),
    INDEX ix_products_organization (organization_id),
    INDEX ix_products_org_name (organization_id, name)
);

CREATE TABLE locations (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    code VARCHAR(40) NOT NULL,
    address VARCHAR(500) NULL,
    location_type VARCHAR(20) NOT NULL,
    contact_info VARCHAR(160) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_locations_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT uq_locations_org_code UNIQUE (organization_id, code),
    CONSTRAINT chk_locations_type CHECK (location_type IN ('WAREHOUSE', 'STORE')),
    INDEX ix_locations_organization (organization_id)
);
