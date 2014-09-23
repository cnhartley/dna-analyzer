CREATE DATABASE  IF NOT EXISTS `dnaa` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `dnaa`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win32 (x86)
--
-- Host: localhost    Database: dnaa
-- ------------------------------------------------------
-- Server version	5.6.20

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account_type`
--

DROP TABLE IF EXISTS `account_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL COMMENT 'name should be { LIMITED, STUDENT, EDUCATOR, SUBSCRIPTION }',
  `short_description` text,
  `long_description` text,
  `max_upload_size` int(11) DEFAULT '0',
  `max_download_size` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_type`
--

LOCK TABLES `account_type` WRITE;
/*!40000 ALTER TABLE `account_type` DISABLE KEYS */;
INSERT INTO `account_type` VALUES (1,'DEFAULT','Basic account type.','Provides the user with limited functionality but enough to preview the web application.',0,0),(2,'STUDENT','Verified student account type.','Provides the ability to upload and download limitied size sequences with full viewing capabilities.',4095,4095),(3,'EDUCATOR','Verified educator account type.','Provides an advanced full access to the web application for a discounted subscription rate.',-1,-1),(4,'PROFESSIONAL','Professional account type.','Provides all of the advanced features of the web application and unlimited upload and download access.',-1,-1);
/*!40000 ALTER TABLE `account_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `current_users_view`
--

DROP TABLE IF EXISTS `current_users_view`;
/*!50001 DROP VIEW IF EXISTS `current_users_view`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `current_users_view` (
  `id` tinyint NOT NULL,
  `username` tinyint NOT NULL,
  `first_name` tinyint NOT NULL,
  `last_name` tinyint NOT NULL,
  `create_time` tinyint NOT NULL,
  `email` tinyint NOT NULL,
  `verified` tinyint NOT NULL,
  `expires_time` tinyint NOT NULL,
  `name` tinyint NOT NULL,
  `short_description` tinyint NOT NULL,
  `long_description` tinyint NOT NULL,
  `max_download_size` tinyint NOT NULL,
  `max_upload_size` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `sequence_blocks`
--

DROP TABLE IF EXISTS `sequence_blocks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_blocks` (
  `id` int(11) NOT NULL,
  `sequence_info_id` int(10) unsigned NOT NULL,
  `length` mediumtext NOT NULL,
  `index` mediumtext NOT NULL,
  `data` varchar(4095) NOT NULL,
  PRIMARY KEY (`id`,`sequence_info_id`),
  KEY `fk_sequence_blocks_sequence_info1_idx` (`sequence_info_id`),
  CONSTRAINT `fk_sequence_blocks_sequence_info1` FOREIGN KEY (`sequence_info_id`) REFERENCES `sequence_info` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence_blocks`
--

LOCK TABLES `sequence_blocks` WRITE;
/*!40000 ALTER TABLE `sequence_blocks` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequence_blocks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sequence_info`
--

DROP TABLE IF EXISTS `sequence_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `organism` varchar(80) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`user_id`),
  UNIQUE KEY `id-sequence-info_UNIQUE` (`id`),
  KEY `fk_sequence-info_user_idx` (`user_id`),
  CONSTRAINT `fk_sequence-info_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence_info`
--

LOCK TABLES `sequence_info` WRITE;
/*!40000 ALTER TABLE `sequence_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `sequence_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(16) NOT NULL,
  `password` varchar(32) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `email` varchar(255) NOT NULL,
  `verified` tinyint(1) NOT NULL DEFAULT '0',
  `first_name` varchar(20) DEFAULT NULL,
  `last_name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'chartley','clubhouse66','2014-09-20 04:16:18','chrisnhartley@gmail.com',1,'Chris','Hartley'),(2,'student1','student1','2014-09-20 17:05:49','cnhartle@calpoly.edu',1,'Christopher','Hartley');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_subscription`
--

DROP TABLE IF EXISTS `user_subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_subscription` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `account_type_id` int(10) unsigned NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`,`user_id`,`account_type_id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_user_subscription_account_type1_idx` (`account_type_id`),
  KEY `fk_user_subscription_user1_idx` (`user_id`),
  CONSTRAINT `fk_user_subscription_account_type1` FOREIGN KEY (`account_type_id`) REFERENCES `account_type` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_subscription_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_subscription`
--

LOCK TABLES `user_subscription` WRITE;
/*!40000 ALTER TABLE `user_subscription` DISABLE KEYS */;
INSERT INTO `user_subscription` VALUES (1,1,4,'2014-09-20 04:17:41',NULL),(2,2,2,'2013-09-20 15:27:23','2014-09-20 17:07:04'),(3,2,1,'2013-01-12 19:34:11',NULL);
/*!40000 ALTER TABLE `user_subscription` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'dnaa'
--

--
-- Final view structure for view `current_users_view`
--

/*!50001 DROP TABLE IF EXISTS `current_users_view`*/;
/*!50001 DROP VIEW IF EXISTS `current_users_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `current_users_view` AS select `u`.`id` AS `id`,`u`.`username` AS `username`,`u`.`first_name` AS `first_name`,`u`.`last_name` AS `last_name`,`u`.`create_time` AS `create_time`,`u`.`email` AS `email`,`u`.`verified` AS `verified`,max(`s`.`expires_time`) AS `expires_time`,`t`.`name` AS `name`,`t`.`short_description` AS `short_description`,`t`.`long_description` AS `long_description`,`t`.`max_download_size` AS `max_download_size`,`t`.`max_upload_size` AS `max_upload_size` from ((`user` `u` left join `user_subscription` `s` on((`s`.`user_id` = `u`.`id`))) left join `account_type` `t` on((`t`.`id` = `s`.`account_type_id`))) group by `u`.`id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-09-22 18:14:03
