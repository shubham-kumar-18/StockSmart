ALTER TABLE stock_movements
    ADD CONSTRAINT uq_movement_reference_per_product UNIQUE (organization_id, movement_type, product_id, reference);
