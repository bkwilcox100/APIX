/*
 * This removes the xref tables that make a many to many relationship from lists to items in favor of a one to many relationship.
 */
USE middle_layer;

DROP TABLE IF EXISTS heb_shopping_list_product_xref;
DROP TABLE IF EXISTS heb_shopping_list_coupon_xref;
DROP TABLE IF EXISTS heb_shopping_list_freeform_xref;
DROP TABLE IF EXISTS heb_shopping_list_recipe_xref;
