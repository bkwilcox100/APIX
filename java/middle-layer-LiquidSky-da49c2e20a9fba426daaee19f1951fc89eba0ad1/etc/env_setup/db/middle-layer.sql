
--
-- Current Database: `middle_layer`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `middle_layer` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `middle_layer`;

--
-- Table structure for table `atg_assortment`
--

DROP TABLE IF EXISTS `atg_assortment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `atg_assortment` (
  `store_assort_id` varchar(40) NOT NULL,
  `product_id` varchar(40) DEFAULT NULL,
  `store_id` varchar(40) DEFAULT NULL,
  `location` varchar(40) DEFAULT NULL,
  `marketing_bug_id` varchar(40) DEFAULT NULL,
  `marketing_bug_st_dt` datetime DEFAULT NULL,
  `marketing_bug_end_dt` datetime DEFAULT NULL,
  `CREATION_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LAST_MODIFIED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`store_assort_id`),
  KEY `product_id` (`product_id`),
  KEY `store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `atg_product`
--

DROP TABLE IF EXISTS `atg_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `atg_product` (
  `product_id` varchar(40) NOT NULL,
  `display_name` varchar(256) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `default_sku` varchar(40) DEFAULT NULL,
  `parent_product_id` varchar(40) DEFAULT NULL,
  `parent_product_upc` varchar(40) DEFAULT NULL,
  `pos_product_id` varchar(40) DEFAULT NULL,
  `scene_seven_image` varchar(40) DEFAULT NULL,
  `twelve_upc` varchar(13) DEFAULT NULL,
  `CREATION_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LAST_MODIFIED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


-- Table structure for table `atg_recipe`
--

DROP TABLE IF EXISTS `atg_recipe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `atg_recipe` (
  `RECIPE_ID` varchar(40) NOT NULL,
  `NAME` varchar(100) DEFAULT NULL,
  `CLEAN_NAME` varchar(100) DEFAULT NULL,
  `MARKETING_BUG_ID` varchar(40) DEFAULT NULL,
  `EXTERNAL_RECIPE_ID` varchar(40) DEFAULT NULL,
  `PREPARATION_TIME` varchar(40) DEFAULT NULL,
  `COOK_TIME` varchar(40) DEFAULT NULL,
  `TOTAL_TIME` varchar(40) DEFAULT NULL,
  `NO_OF_SERVINGS` varchar(100) DEFAULT NULL,
  `CREATION_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LAST_MODIFIED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`RECIPE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `atg_store`
--

DROP TABLE IF EXISTS `atg_store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `atg_store` (
  `store_id` varchar(40) NOT NULL,
  `store_number` varchar(40) DEFAULT NULL,
  `store_type` varchar(5) DEFAULT NULL,
  `CREATION_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LAST_MODIFIED_DATE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `heb_app_properties`
--

DROP TABLE IF EXISTS `heb_app_properties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heb_app_properties` (
  `appid` varchar(64) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `creation_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`appid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `heb_app_version`
--

DROP TABLE IF EXISTS `heb_app_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heb_app_version` (
  `app_version_id` varchar(40) NOT NULL,
  `appid` varchar(64) NOT NULL,
  `os_name` varchar(64) DEFAULT NULL,
  `os_version` varchar(64) DEFAULT NULL,
  `creation_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_modified_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`app_version_id`),
  KEY `appid` (`appid`),
  CONSTRAINT `heb_app_version_ibfk_1` FOREIGN KEY (`appid`) REFERENCES `heb_app_properties` (`appid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `heb_id_generator`
--

DROP TABLE IF EXISTS `heb_id_generator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heb_id_generator` (
  `data_type` varchar(40) NOT NULL,
  `next_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`data_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `heb_shopping_list`
--

DROP TABLE IF EXISTS `heb_shopping_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heb_shopping_list` (
  `SHOPPING_LIST_ID` varchar(40) NOT NULL,
  `OWNER_ID` varchar(300) NOT NULL,
  `LIST_NAME` varchar(40) NOT NULL,
  `CREATION_DATE` datetime DEFAULT CURRENT_TIMESTAMP,
  `LAST_MODIFIED_DATE` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `SITE_ID` int(10) unsigned DEFAULT '0',
  PRIMARY KEY (`SHOPPING_LIST_ID`),
  KEY `owner_id` (`OWNER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `heb_shopping_list_coupon`
--

DROP TABLE IF EXISTS `heb_shopping_list_coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heb_shopping_list_coupon` (
  `COUPON_ITEM_ID` varchar(40) NOT NULL,
  `SHOPPING_LIST_ID` varchar(40) NOT NULL,
  `COUPON_ID` varchar(40) NOT NULL,
  `NOTES` varchar(1000) DEFAULT NULL,
  `STATUS` int(10) unsigned DEFAULT '0',
  `CREATION_DATE` datetime DEFAULT CURRENT_TIMESTAMP,
  `LAST_MODIFIED_DATE` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`COUPON_ITEM_ID`),
  KEY `SHOPPING_LIST_ID` (`SHOPPING_LIST_ID`),
  CONSTRAINT `heb_shopping_list_coupon_ibfk_1` FOREIGN KEY (`SHOPPING_LIST_ID`) REFERENCES `heb_shopping_list` (`SHOPPING_LIST_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `heb_shopping_list_freeform`
--

DROP TABLE IF EXISTS `heb_shopping_list_freeform`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heb_shopping_list_freeform` (
  `FREEFORM_ITEM_ID` varchar(40) NOT NULL,
  `SHOPPING_LIST_ID` varchar(40) NOT NULL,
  `FREEFORM_NAME` varchar(100) NOT NULL,
  `NOTES` varchar(1000) DEFAULT NULL,
  `QUANTITY` int(10) unsigned DEFAULT '0',
  `STATUS` int(10) unsigned DEFAULT '0',
  `CREATION_DATE` datetime DEFAULT CURRENT_TIMESTAMP,
  `LAST_MODIFIED_DATE` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`FREEFORM_ITEM_ID`),
  KEY `SHOPPING_LIST_ID` (`SHOPPING_LIST_ID`),
  CONSTRAINT `heb_shopping_list_freeform_ibfk_1` FOREIGN KEY (`SHOPPING_LIST_ID`) REFERENCES `heb_shopping_list` (`SHOPPING_LIST_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `heb_shopping_list_product`
--

DROP TABLE IF EXISTS `heb_shopping_list_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heb_shopping_list_product` (
  `PRODUCT_ITEM_ID` varchar(40) NOT NULL,
  `SHOPPING_LIST_ID` varchar(40) NOT NULL,
  `PRODUCT_ID` varchar(40) NOT NULL,
  `NOTES` varchar(1000) DEFAULT NULL,
  `QUANTITY` int(10) unsigned DEFAULT '0',
  `STATUS` int(10) unsigned DEFAULT '0',
  `CREATION_DATE` datetime DEFAULT CURRENT_TIMESTAMP,
  `LAST_MODIFIED_DATE` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`PRODUCT_ITEM_ID`),
  KEY `SHOPPING_LIST_ID` (`SHOPPING_LIST_ID`),
  CONSTRAINT `heb_shopping_list_product_ibfk_1` FOREIGN KEY (`SHOPPING_LIST_ID`) REFERENCES `heb_shopping_list` (`SHOPPING_LIST_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `heb_shopping_list_recipe`
--

DROP TABLE IF EXISTS `heb_shopping_list_recipe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `heb_shopping_list_recipe` (
  `RECIPE_ITEM_ID` varchar(40) NOT NULL,
  `SHOPPING_LIST_ID` varchar(40) NOT NULL,
  `RECIPE_ID` varchar(40) NOT NULL,
  `NOTES` varchar(1000) DEFAULT NULL,
  `STATUS` int(10) unsigned DEFAULT '0',
  `CREATION_DATE` datetime DEFAULT CURRENT_TIMESTAMP,
  `LAST_MODIFIED_DATE` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`RECIPE_ITEM_ID`),
  KEY `SHOPPING_LIST_ID` (`SHOPPING_LIST_ID`),
  CONSTRAINT `heb_shopping_list_recipe_ibfk_1` FOREIGN KEY (`SHOPPING_LIST_ID`) REFERENCES `heb_shopping_list` (`SHOPPING_LIST_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `visits`
--

DROP TABLE IF EXISTS `visits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `visits` (
  `visit_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_ip` varchar(46) NOT NULL,
  `timestamp` datetime NOT NULL,
  PRIMARY KEY (`visit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


--
-- Create heb user
--
 CREATE USER 'hebdb' IDENTIFIED BY 'hebdb';
 GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER, REFERENCES, CREATE TEMPORARY TABLES ON middle_layer.* TO 'hebdb';