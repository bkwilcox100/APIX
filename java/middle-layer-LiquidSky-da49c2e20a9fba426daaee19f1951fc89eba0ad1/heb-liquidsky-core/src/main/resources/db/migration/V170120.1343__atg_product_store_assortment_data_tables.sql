USE middle_layer;
drop table if exists atg_store;
drop table if exists atg_assortment;
drop table if exists atg_product;

CREATE TABLE atg_store (
	store_id VARCHAR(40),
    store_number VARCHAR(40),
    store_type VARCHAR(5)
);

CREATE TABLE atg_assortment (
	store_assort_id VARCHAR(40),
	product_id VARCHAR(40),
	store_id VARCHAR(40),
	location VARCHAR(40),
	marketing_bug_id VARCHAR(40),
	marketing_bug_st_dt DATETIME NULL DEFAULT NULL,
	marketing_bug_end_dt DATETIME NULL DEFAULT NULL
);

CREATE TABLE atg_product (
	product_id VARCHAR(40),
    display_name VARCHAR(256),
    start_date DATETIME NULL DEFAULT NULL,
    end_date DATETIME NULL DEFAULT NULL,
    description VARCHAR(256),
    default_sku VARCHAR(40),
    parent_product_id VARCHAR(40),
    parent_product_upc VARCHAR(40),
    pos_product_id VARCHAR(40),
    scene_seven_image VARCHAR(40),
    twelve_upc VARCHAR(13)
);

SET @@SESSION.sql_mode='ALLOW_INVALID_DATES';
INSERT INTO atg_assortment (store_assort_id,product_id,store_id,location,marketing_bug_id,marketing_bug_st_dt,marketing_bug_end_dt) VALUES ('1003190_108','1003190','108','In Market with the Cooked Meats','PRIMOPCK',STR_TO_DATE('03-FEB-16 07.05.02.000000 PM', '%d-%b-%y %h.%i.%s.%f %p'),STR_TO_DATE('01-JAN-99 03.09.24.000000 PM', '%d-%b-%y %h.%i.%s.%f %p'));
INSERT INTO atg_assortment (store_assort_id,product_id,store_id,location,marketing_bug_id,marketing_bug_st_dt,marketing_bug_end_dt) VALUES ('1003441_680','1003441','680','','',STR_TO_DATE('', '%d-%b-%y %h.%i.%s.%f %p'),STR_TO_DATE('', '%d-%b-%y %h.%i.%s.%f %p'));

INSERT INTO atg_product (product_id,display_name,start_date,end_date,description,default_sku,parent_product_id,parent_product_upc,pos_product_id,scene_seven_image,twelve_upc) VALUES ('1770373','Tanka Bites Spicy Pepper Bites',STR_TO_DATE('25-FEB-14 12.00.00.000000 AM', '%d-%b-%y %h.%i.%s.%f %p'),STR_TO_DATE('', '%d-%b-%y %h.%i.%s.%f %p'),'Spicy Pepper Bites','89773700103','','','1770373','001770373','897737001031');
INSERT INTO atg_product (product_id,display_name,start_date,end_date,description,default_sku,parent_product_id,parent_product_upc,pos_product_id,scene_seven_image,twelve_upc) VALUES ('1866138','Muscle Milk Pro Series Energy Chew Orange',STR_TO_DATE('26-AUG-15 12.00.00.000000 AM', '%d-%b-%y %h.%i.%s.%f %p'),STR_TO_DATE('', '%d-%b-%y %h.%i.%s.%f %p'),'Pro Series Energy Chew Orange','66072653902','','','1866138','001866138','660726539026');
INSERT INTO atg_product (product_id,display_name,start_date,end_date,description,default_sku,parent_product_id,parent_product_upc,pos_product_id,scene_seven_image,twelve_upc) VALUES ('602762','H-E-B Magnesium 250 mg Tablets',STR_TO_DATE('10-OCT-10 12.00.00.000000 AM', '%d-%b-%y %h.%i.%s.%f %p'),STR_TO_DATE('', '%d-%b-%y %h.%i.%s.%f %p'),'Magnesium 250 mg Tablets','4122082727','','','602762','000602762','041220827275');
INSERT INTO atg_product (product_id,display_name,start_date,end_date,description,default_sku,parent_product_id,parent_product_upc,pos_product_id,scene_seven_image,twelve_upc) VALUES ('674992','Nature Made Potassium Gluconate 550 mg Tablets',STR_TO_DATE('01-JAN-08 12.00.00.000000 AM', '%d-%b-%y %h.%i.%s.%f %p'),STR_TO_DATE('', '%d-%b-%y %h.%i.%s.%f %p'),'Potassium Gluconate 550 mg Tablets','3160401358','','','674992','000674992','031604013585');

INSERT INTO atg_store (store_id,store_number,store_type) VALUES ('449','449','NP');
INSERT INTO atg_store (store_id,store_number,store_type) VALUES ('423','423','NP');
INSERT INTO atg_store (store_id,store_number,store_type) VALUES ('557','557','P');
INSERT INTO atg_store (store_id,store_number,store_type) VALUES ('86','86','NP');

truncate table atg_store;
truncate table atg_assortment;
truncate table atg_product;
