USE middle_layer;

CREATE TABLE heb_product (
	prod_id VARCHAR(40) NOT NULL,
	title VARCHAR(200),
	PRIMARY KEY (prod_id)
);
CREATE TABLE heb_product_aux (
	prod_id VARCHAR(40) NOT NULL,
	description VARCHAR(2000),
	PRIMARY KEY (prod_id),
	FOREIGN KEY (prod_id) REFERENCES heb_product(prod_id)
);
CREATE TABLE heb_sku (
	sku_id VARCHAR(40) NOT NULL,
	title VARCHAR(200),
	PRIMARY KEY (sku_id)
);
CREATE TABLE heb_sku_aux (
	sku_id VARCHAR(40) NOT NULL,
	description VARCHAR(2000),
	PRIMARY KEY (sku_id),
	FOREIGN KEY (sku_id) REFERENCES heb_sku(sku_id)
);
CREATE TABLE heb_product_sku (
	prod_id VARCHAR(40) NOT NULL,
	sku_id VARCHAR(40) NOT NULL,
	PRIMARY KEY (prod_id, sku_id),
	FOREIGN KEY (prod_id) REFERENCES heb_product(prod_id),
	FOREIGN KEY (sku_id) REFERENCES heb_sku(sku_id)
);
