USE middle_layer;

DROP TABLE IF EXISTS heb_shopping_list_product_xref;
DROP TABLE IF EXISTS heb_shopping_list_product;

CREATE TABLE heb_shopping_list_product (
	PRODUCT_ITEM_ID VARCHAR(40) NOT NULL,
	SHOPPING_LIST_ID VARCHAR(40) NOT NULL,
	PRODUCT_ID VARCHAR(40) NOT NULL,
	NOTES VARCHAR(1000),
	QUANTITY INT UNSIGNED DEFAULT 0,
	STATUS INT UNSIGNED DEFAULT 0,
	CREATION_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,
	LAST_MODIFIED_DATE DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (PRODUCT_ITEM_ID),
	FOREIGN KEY (SHOPPING_LIST_ID) REFERENCES heb_shopping_list(SHOPPING_LIST_ID)
);

CREATE TABLE heb_shopping_list_product_xref (
	SHOPPING_LIST_ID VARCHAR(40) NOT NULL,
	PRODUCT_ITEM_ID VARCHAR(40) NOT NULL,
	SEQUENCE_NUM INT DEFAULT 0,
	PRIMARY KEY (SHOPPING_LIST_ID, PRODUCT_ITEM_ID),
	FOREIGN KEY (SHOPPING_LIST_ID) REFERENCES heb_shopping_list(SHOPPING_LIST_ID),
	FOREIGN KEY (PRODUCT_ITEM_ID) REFERENCES heb_shopping_list_product(PRODUCT_ITEM_ID)
);


DROP TABLE IF EXISTS heb_shopping_list_coupon_xref;
DROP TABLE IF EXISTS heb_shopping_list_coupon;

CREATE TABLE heb_shopping_list_coupon (
	COUPON_ITEM_ID VARCHAR(40) NOT NULL,
	SHOPPING_LIST_ID VARCHAR(40) NOT NULL,
	COUPON_ID VARCHAR(40) NOT NULL,
	NOTES VARCHAR(1000),
	STATUS INT UNSIGNED DEFAULT 0,
	CREATION_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,
	LAST_MODIFIED_DATE DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (COUPON_ITEM_ID),
	FOREIGN KEY (SHOPPING_LIST_ID) REFERENCES heb_shopping_list(SHOPPING_LIST_ID)
);

CREATE TABLE heb_shopping_list_coupon_xref (
	SHOPPING_LIST_ID VARCHAR(40) NOT NULL,
	COUPON_ITEM_ID VARCHAR(40) NOT NULL,
	SEQUENCE_NUM INT DEFAULT 0,
	PRIMARY KEY (SHOPPING_LIST_ID, COUPON_ITEM_ID),
	FOREIGN KEY (SHOPPING_LIST_ID) REFERENCES heb_shopping_list(SHOPPING_LIST_ID),
	FOREIGN KEY (COUPON_ITEM_ID) REFERENCES heb_shopping_list_coupon(COUPON_ITEM_ID)
);


DROP TABLE IF EXISTS heb_shopping_list_freeform_xref;
DROP TABLE IF EXISTS heb_shopping_list_freeform;

CREATE TABLE heb_shopping_list_freeform (
	FREEFORM_ITEM_ID VARCHAR(40) NOT NULL,
	SHOPPING_LIST_ID VARCHAR(40) NOT NULL,
	FREEFORM_NAME VARCHAR(100) NOT NULL,
	NOTES VARCHAR(1000),
	QUANTITY INT UNSIGNED DEFAULT 0,
	STATUS INT UNSIGNED DEFAULT 0,
	CREATION_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,
	LAST_MODIFIED_DATE DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (FREEFORM_ITEM_ID),
	FOREIGN KEY (SHOPPING_LIST_ID) REFERENCES heb_shopping_list(SHOPPING_LIST_ID)
);

CREATE TABLE heb_shopping_list_freeform_xref (
	SHOPPING_LIST_ID VARCHAR(40) NOT NULL,
	FREEFORM_ITEM_ID VARCHAR(40) NOT NULL,
	SEQUENCE_NUM INT DEFAULT 0,
	PRIMARY KEY (SHOPPING_LIST_ID, FREEFORM_ITEM_ID),
	FOREIGN KEY (SHOPPING_LIST_ID) REFERENCES heb_shopping_list(SHOPPING_LIST_ID),
	FOREIGN KEY (FREEFORM_ITEM_ID) REFERENCES heb_shopping_list_freeform(FREEFORM_ITEM_ID)
);


DROP TABLE IF EXISTS heb_shopping_list_recipe_xref;
DROP TABLE IF EXISTS heb_shopping_list_recipe;

CREATE TABLE heb_shopping_list_recipe (
	RECIPE_ITEM_ID VARCHAR(40) NOT NULL,
	SHOPPING_LIST_ID VARCHAR(40) NOT NULL,
	RECIPE_ID VARCHAR(40) NOT NULL,
	NOTES VARCHAR(1000),
	STATUS INT UNSIGNED DEFAULT 0,
	CREATION_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,
	LAST_MODIFIED_DATE DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (RECIPE_ITEM_ID),
	FOREIGN KEY (SHOPPING_LIST_ID) REFERENCES heb_shopping_list(SHOPPING_LIST_ID)
);

CREATE TABLE heb_shopping_list_recipe_xref (
	SHOPPING_LIST_ID VARCHAR(40) NOT NULL,
	RECIPE_ITEM_ID VARCHAR(40) NOT NULL,
	SEQUENCE_NUM INT DEFAULT 0,
	PRIMARY KEY (SHOPPING_LIST_ID, RECIPE_ITEM_ID),
	FOREIGN KEY (SHOPPING_LIST_ID) REFERENCES heb_shopping_list(SHOPPING_LIST_ID),
	FOREIGN KEY (RECIPE_ITEM_ID) REFERENCES heb_shopping_list_recipe(RECIPE_ITEM_ID)
);

/* Populate some test data */
insert into middle_layer.heb_shopping_list values ('TESTsl27', 'testOwner', 'test list', now(), now());

insert into middle_layer.heb_shopping_list_product values('TESTslp1', 'TESTsl27', 'prd0001', 'This is a note', 2, 0, now(), now());
insert into middle_layer.heb_shopping_list_product values('TESTslp2', 'TESTsl27', 'prd0002', 'This is a note', 32, 0, now(), now());
insert into middle_layer.heb_shopping_list_product values('TESTslp3', 'TESTsl27', 'prd0003', 'This is a note', 44, 0, now(), now());
insert into middle_layer.heb_shopping_list_product values('TESTslp4', 'TESTsl27', 'prd0004', 'This is a note', 99, 0, now(), now());

insert into middle_layer.heb_shopping_list_product_xref values ('TESTsl27', 'TESTslp1', 0);
insert into middle_layer.heb_shopping_list_product_xref values ('TESTsl27', 'TESTslp2', 0);
insert into middle_layer.heb_shopping_list_product_xref values ('TESTsl27', 'TESTslp3', 0);
insert into middle_layer.heb_shopping_list_product_xref values ('TESTsl27', 'TESTslp4', 0);

insert into middle_layer.heb_shopping_list_coupon values('TESTslc1', 'TESTsl27', 'coupon0001', 'This is a note about a coupon', 0, now(), now());
insert into middle_layer.heb_shopping_list_coupon values('TESTslc2', 'TESTsl27', 'coupon0002', 'This is a note about a coupon', 0, now(), now());
insert into middle_layer.heb_shopping_list_coupon values('TESTslc3', 'TESTsl27', 'coupon0003', 'This is a note about a coupon', 0, now(), now());
insert into middle_layer.heb_shopping_list_coupon values('TESTslc4', 'TESTsl27', 'coupon0004', 'This is a note about a coupon', 0, now(), now());
insert into middle_layer.heb_shopping_list_coupon values('TESTslc5', 'TESTsl27', 'coupon0005', 'This is a note about a coupon', 0, now(), now());

insert into middle_layer.heb_shopping_list_coupon_xref values ('TESTsl27', 'TESTslc1', 0);
insert into middle_layer.heb_shopping_list_coupon_xref values ('TESTsl27', 'TESTslc2', 0);
insert into middle_layer.heb_shopping_list_coupon_xref values ('TESTsl27', 'TESTslc3', 0);
insert into middle_layer.heb_shopping_list_coupon_xref values ('TESTsl27', 'TESTslc4', 0);

insert into middle_layer.heb_shopping_list_freeform values('TESTslf1', 'TESTsl27', 'Bananas', 'We need some bananas', 4, 0, now(), now());
insert into middle_layer.heb_shopping_list_freeform values('TESTslf2', 'TESTsl27', 'Beers', 'Get some good ones please', 12, 0, now(), now());
insert into middle_layer.heb_shopping_list_freeform values('TESTslf3', 'TESTsl27', 'Chips', 'Something suitible for dips but cheap.', 4, 0, now(), now());

insert into middle_layer.heb_shopping_list_freeform_xref values ('TESTsl27', 'TESTslf1', 0);
insert into middle_layer.heb_shopping_list_freeform_xref values ('TESTsl27', 'TESTslf2', 0);
insert into middle_layer.heb_shopping_list_freeform_xref values ('TESTsl27', 'TESTslf3', 0);

insert into middle_layer.heb_shopping_list_recipe values('TESTslr1', 'TESTsl27', 'recipe0001', 'This looks yum!', 0, now(), now());

insert into middle_layer.heb_shopping_list_recipe_xref values ('TESTsl27', 'TESTslr1', 0);

