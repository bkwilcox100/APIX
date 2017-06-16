USE middle_layer;

ALTER TABLE atg_assortment ADD PRIMARY KEY(store_assort_id);
ALTER TABLE atg_assortment ADD INDEX product_id (product_id);
ALTER TABLE atg_assortment ADD INDEX store_id (store_id);

ALTER TABLE atg_product ADD PRIMARY KEY(product_id);

ALTER TABLE atg_store ADD PRIMARY KEY(store_id);