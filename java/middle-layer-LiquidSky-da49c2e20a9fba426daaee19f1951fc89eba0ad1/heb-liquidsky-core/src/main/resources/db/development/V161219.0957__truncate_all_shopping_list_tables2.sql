USE middle_layer;

SET FOREIGN_KEY_CHECKS = 0; 
TRUNCATE TABLE heb_shopping_list_freeform_xref;
TRUNCATE TABLE heb_shopping_list_coupon_xref;
TRUNCATE TABLE heb_shopping_list_product_xref;
TRUNCATE TABLE heb_shopping_list_recipe_xref;
TRUNCATE TABLE heb_shopping_list_coupon;
TRUNCATE TABLE heb_shopping_list_product;
TRUNCATE TABLE heb_shopping_list_freeform;
TRUNCATE TABLE heb_shopping_list_recipe;
TRUNCATE TABLE heb_shopping_list;
TRUNCATE TABLE heb_id_generator;
SET FOREIGN_KEY_CHECKS = 1;

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

