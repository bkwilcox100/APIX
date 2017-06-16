USE middle_layer;

ALTER TABLE heb_product ADD COLUMN CREATION_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE heb_product ADD COLUMN LAST_MODIFIED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE heb_sku ADD COLUMN CREATION_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE heb_sku ADD COLUMN LAST_MODIFIED_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;