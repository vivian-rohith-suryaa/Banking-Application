-- MySQL dump 10.13  Distrib 8.0.42, for Linux (x86_64)
--
-- Host: localhost    Database: viivadb
-- ------------------------------------------------------
-- Server version	8.0.42-0ubuntu0.24.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `account_id` bigint NOT NULL AUTO_INCREMENT,
  `customer_id` bigint NOT NULL,
  `branch_id` bigint NOT NULL,
  `type` enum('CURRENT','SAVINGS','FIXED_DEPOSIT') NOT NULL,
  `balance` decimal(15,2) NOT NULL,
  `status` enum('ACTIVE','INACTIVE','CLOSED') NOT NULL,
  `pin` varchar(255) NOT NULL DEFAULT '000000',
  `created_time` bigint NOT NULL,
  `modified_time` bigint DEFAULT NULL,
  `modified_by` bigint DEFAULT NULL,
  PRIMARY KEY (`account_id`),
  KEY `customer_id` (`customer_id`),
  KEY `branch_id` (`branch_id`),
  CONSTRAINT `account_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `account_ibfk_2` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`branch_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES (1,5,1,'FIXED_DEPOSIT',900.00,'ACTIVE','$2a$12$KGpAcCL5XFqTye6XO5hxzuZWNpuVIcr4qKLZKOzKWOAUSWnkVFuuO',1750309971195,1750775132753,5),(2,5,1,'FIXED_DEPOSIT',1500.00,'ACTIVE','$2a$12$9N1JGVUmWj/TBtvs9VLxIeiCQbyXfH2AjXEOqMxl6HFW56j7S/dIG',1750316429699,1750742837959,5),(3,5,1,'FIXED_DEPOSIT',100.00,'ACTIVE','$2a$12$3U3FTS8/ygVrfYMlqq19W.3WJzmvN3a9SFaIia97qdEBlz97XD0cu',1750316489936,1750742844773,5),(4,8,1,'SAVINGS',400.00,'ACTIVE','$2a$12$4pdTBZI24cfjtoNjWPy/ReumXQsSjELUjkYfun4px1uUNIc.Z0HYq',1750316783191,1750316860542,8),(5,5,1,'SAVINGS',1000.00,'ACTIVE','$2a$12$379DF9C8n2NxeUjeI2jT9.wuW5sc2v/gyUMRO13xZYVeoqpPeTmIa',1750412328038,1750742851493,5),(6,5,1,'CURRENT',2000.00,'ACTIVE','$2a$12$fEHr4zFI3.rbPhuk4b9mPu0UOsvyGddvQQv4rCzjpFydePA5iz4R.',1750412391777,1750742857584,5),(7,7,1,'CURRENT',2000.00,'ACTIVE','$2a$12$lyd2yKM/L124nz6O3jiR7OU/dTFUJyVbOsinQvxUugkinOBsS/krq',1750606208704,1750606284536,7),(8,8,1,'SAVINGS',1000.00,'ACTIVE','000000',1750706394347,NULL,NULL),(9,5,1,'FIXED_DEPOSIT',2000.00,'ACTIVE','000000',1750818840142,NULL,NULL),(10,7,1,'SAVINGS',1000.00,'ACTIVE','000000',1750818856265,NULL,NULL),(11,7,1,'CURRENT',1000.00,'ACTIVE','000000',1750822649220,NULL,NULL),(12,7,1,'FIXED_DEPOSIT',12345.00,'ACTIVE','000000',1750828023771,NULL,NULL),(13,5,1,'SAVINGS',999999999999.00,'ACTIVE','000000',1751441767863,NULL,NULL),(14,5,1,'SAVINGS',999999999999.00,'ACTIVE','000000',1751441779039,NULL,NULL),(15,5,1,'SAVINGS',999999999999.00,'ACTIVE','000000',1751441838995,NULL,NULL),(16,5,1,'SAVINGS',10.00,'ACTIVE','000000',1751442043223,NULL,NULL),(17,5,1,'SAVINGS',10.00,'ACTIVE','000000',1751442075337,NULL,NULL),(18,5,1,'SAVINGS',999999999999.00,'ACTIVE','000000',1751442086504,NULL,NULL),(19,24,1,'SAVINGS',0.00,'ACTIVE','000000',1751442108156,NULL,NULL),(20,5,1,'CURRENT',1000.00,'ACTIVE','000000',1751952045389,NULL,NULL);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `branch`
--

DROP TABLE IF EXISTS `branch`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `branch` (
  `branch_id` bigint NOT NULL AUTO_INCREMENT,
  `branch_name` varchar(30) NOT NULL,
  `manager_id` bigint DEFAULT NULL,
  `ifsc_code` varchar(20) NOT NULL,
  `locality` varchar(50) NOT NULL,
  `district` varchar(50) NOT NULL,
  `state` varchar(50) NOT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint DEFAULT NULL,
  `modified_by` bigint DEFAULT NULL,
  PRIMARY KEY (`branch_id`),
  UNIQUE KEY `ifsc_code` (`ifsc_code`),
  KEY `manager_id` (`manager_id`),
  CONSTRAINT `branch_ibfk_1` FOREIGN KEY (`manager_id`) REFERENCES `employee` (`employee_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `branch`
--

LOCK TABLES `branch` WRITE;
/*!40000 ALTER TABLE `branch` DISABLE KEYS */;
INSERT INTO `branch` VALUES (1,'Anna Nagar Branch',7,'VIIV0000001','Chennai Street','Dindigul','Tamil',1750277700132,1750775463599,6),(2,'Malakottai Branch',10,'VIIV0000002','College Road','Trichy','Tamil Nadu',1750752059951,1750820253511,6),(3,'PWD Street Branch',11,'VIIV0000003','College Road','Dindigul','Tamil Nadu',1750752119445,1750820358909,6),(4,'ABCD Lane Branch',7,'VIIV0000004','ABCD ','Chennai','TN',1750752361662,1750755062805,6);
/*!40000 ALTER TABLE `branch` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `customer_id` bigint NOT NULL,
  `dob` date NOT NULL,
  `aadhar` varchar(12) NOT NULL,
  `pan` varchar(10) NOT NULL,
  `address` varchar(255) NOT NULL,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `aadhar` (`aadhar`),
  UNIQUE KEY `pan` (`pan`),
  CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (4,'2025-06-01','112233445566','ABCDE1234A','123, Trichy, Trichy'),(5,'2025-06-04','112233445555','ABCDE1234M','123, Chennai, Karaikudi - 234'),(6,'1990-01-01','998042667963','CHUPV6455P','21, Example Street, City, State, 600001'),(7,'1990-01-02','111222333444','ABCDE1234H','124, Tiruchendur, 600001'),(8,'1990-02-02','111222333333','ABCDE1234K','124, City, State, 600001'),(9,'2025-06-02','112233445577','ABCDE1234B','123, Trichy, Trichy'),(10,'2025-06-02','665544332211','ABCDE1234R','123, Trichy, Trichy'),(11,'2025-06-04','111111111111','ABCDE1234Z','123, Karur, Karur'),(12,'2025-06-10','123123123000','ABCDE1234I','123, Trichy, Trichy'),(17,'2025-06-29','123123123001','ABCDE1234J','123, Karaikudi, Trichy'),(19,'2036-07-24','123456789012','JKVHE1234W','xcbdfsdfghjk'),(24,'2025-07-24','123456789013','JKVHE1234A','xcbdf');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `employee_id` bigint NOT NULL,
  `branch_id` bigint DEFAULT NULL,
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `employee_id` (`employee_id`),
  KEY `employee_ibfk_2` (`branch_id`),
  CONSTRAINT `employee_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `employee_ibfk_2` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`branch_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (7,1),(6,2),(9,2),(10,2),(12,2),(11,3);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `external_transaction`
--

DROP TABLE IF EXISTS `external_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `external_transaction` (
  `external_transaction_id` bigint NOT NULL AUTO_INCREMENT,
  `transaction_id` bigint NOT NULL,
  `source_account` bigint NOT NULL,
  `target_account` bigint NOT NULL,
  `target_ifsc` varchar(20) NOT NULL,
  `amount` double NOT NULL,
  `transaction_time` bigint NOT NULL,
  `created_by` bigint NOT NULL,
  PRIMARY KEY (`external_transaction_id`),
  KEY `transaction_id` (`transaction_id`),
  CONSTRAINT `external_transaction_ibfk_1` FOREIGN KEY (`transaction_id`) REFERENCES `transaction` (`transaction_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `external_transaction`
--

LOCK TABLES `external_transaction` WRITE;
/*!40000 ALTER TABLE `external_transaction` DISABLE KEYS */;
INSERT INTO `external_transaction` VALUES (1,29,2,14,'ABCD12312',500,1751262138441,5),(2,31,2,14,'ABCD12312',97,1751262918097,5),(3,32,2,14,'ABCD12312',900,1751264337734,5),(4,33,2,14,'ABCD12312',500,1751265240391,5);
/*!40000 ALTER TABLE `external_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `request`
--

DROP TABLE IF EXISTS `request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `request` (
  `request_id` bigint NOT NULL AUTO_INCREMENT,
  `customer_id` bigint NOT NULL,
  `account_type` enum('CURRENT','SAVINGS','FIXED_DEPOSIT') NOT NULL,
  `balance` decimal(15,2) NOT NULL,
  `status` enum('PENDING','APPROVED','REJECTED') NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint DEFAULT NULL,
  `modified_by` bigint DEFAULT NULL,
  `branch_id` bigint NOT NULL,
  PRIMARY KEY (`request_id`),
  KEY `customer_id` (`customer_id`),
  KEY `branch_id` (`branch_id`),
  CONSTRAINT `request_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `request_ibfk_2` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`branch_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request`
--

LOCK TABLES `request` WRITE;
/*!40000 ALTER TABLE `request` DISABLE KEYS */;
INSERT INTO `request` VALUES (3,5,'FIXED_DEPOSIT',100.00,'APPROVED','Approved',1750309781300,1750309971188,7,1),(4,5,'SAVINGS',1000.00,'APPROVED','Approved',1750316266395,1750316783181,7,1),(5,8,'SAVINGS',1000.00,'APPROVED','Approved',1750316709970,1750706394342,7,1),(7,5,'SAVINGS',1000.00,'APPROVED','Approved',1750372825869,1750412328027,7,1),(8,5,'CURRENT',2000.00,'APPROVED','Approved',1750412227760,1750412391767,7,1),(9,7,'CURRENT',1000.00,'APPROVED','Approved',1750606147049,1750606208702,7,1),(10,7,'CURRENT',1000.00,'APPROVED','Approved',1750706425115,1750822649218,7,1),(11,7,'SAVINGS',1000.00,'APPROVED','',1750706434294,1750818856263,7,1),(12,5,'FIXED_DEPOSIT',2000.00,'APPROVED','Approved',1750722563841,1750818840137,7,1),(13,7,'FIXED_DEPOSIT',12345.00,'APPROVED','',1750828008746,1750828023769,7,1),(14,7,'SAVINGS',1000.00,'REJECTED','Rejected',1750828782535,1751326572374,7,1),(15,4,'SAVINGS',12000.00,'PENDING',NULL,1750828806985,NULL,NULL,2),(16,17,'CURRENT',0.00,'PENDING','',1751345505387,1751442220554,7,1),(18,19,'SAVINGS',0.00,'PENDING',NULL,1751438523868,NULL,NULL,1),(19,24,'SAVINGS',0.00,'APPROVED','approved',1751438593558,1751442108155,7,1),(20,5,'SAVINGS',10.00,'APPROVED','',1751441160336,1751442075335,7,1),(21,5,'SAVINGS',999999999999.00,'APPROVED','',1751441216282,1751442086502,7,1),(22,5,'SAVINGS',1000.00,'APPROVED','Approved',1751951761346,1751952045384,7,1);
/*!40000 ALTER TABLE `request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `transaction_id` bigint NOT NULL AUTO_INCREMENT,
  `transaction_reference` bigint DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  `account_id` bigint NOT NULL,
  `transacted_account` bigint DEFAULT NULL,
  `transaction_type` enum('CREDIT','DEBIT') NOT NULL,
  `payment_mode` enum('BANK_TRANSFER','DEPOSIT','WITHDRAWAL','SELF_TRANSFER') NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `closing_balance` decimal(15,2) NOT NULL,
  `transaction_time` bigint NOT NULL,
  `is_external_transfer` tinyint(1) DEFAULT '0',
  `target_ifsc_code` varchar(20) DEFAULT NULL,
  `external_account_id` bigint DEFAULT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `customer_id` (`customer_id`),
  KEY `account_id` (`account_id`),
  KEY `transacted_account` (`transacted_account`),
  CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `transaction_ibfk_2` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `transaction_ibfk_3` FOREIGN KEY (`transacted_account`) REFERENCES `account` (`account_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (2,NULL,5,1,NULL,'CREDIT','DEPOSIT',100.00,200.00,1750316215034,0,NULL,NULL),(3,NULL,5,1,NULL,'DEBIT','WITHDRAWAL',50.00,150.00,1750316240674,0,NULL,NULL),(4,NULL,5,1,2,'CREDIT','SELF_TRANSFER',25.00,125.00,1750316601241,0,NULL,NULL),(5,1000001,8,4,1,'DEBIT','BANK_TRANSFER',500.00,500.00,1750316981910,0,NULL,NULL),(6,1000001,5,1,4,'CREDIT','BANK_TRANSFER',500.00,625.00,1750316981924,0,NULL,NULL),(7,NULL,5,1,NULL,'CREDIT','DEPOSIT',25.00,650.00,1750377121199,0,NULL,NULL),(8,NULL,5,1,NULL,'DEBIT','WITHDRAWAL',50.00,600.00,1750377279653,0,NULL,NULL),(9,NULL,5,1,NULL,'DEBIT','WITHDRAWAL',100.00,500.00,1750401756383,0,NULL,NULL),(10,1000002,8,4,2,'DEBIT','BANK_TRANSFER',100.00,400.00,1750405084837,0,NULL,NULL),(11,1000002,5,2,4,'CREDIT','BANK_TRANSFER',100.00,225.00,1750405084852,0,NULL,NULL),(12,NULL,5,1,NULL,'CREDIT','DEPOSIT',100.00,600.00,1750412114963,0,NULL,NULL),(13,NULL,7,7,NULL,'CREDIT','DEPOSIT',1000.00,2000.00,1750606306856,0,NULL,NULL),(14,NULL,5,2,1,'CREDIT','SELF_TRANSFER',150.00,75.00,1750726276271,0,NULL,NULL),(15,NULL,5,2,NULL,'CREDIT','DEPOSIT',100.00,175.00,1750728577837,0,NULL,NULL),(16,NULL,5,2,NULL,'CREDIT','DEPOSIT',1000.00,1175.00,1750735964195,0,NULL,NULL),(17,NULL,5,2,NULL,'CREDIT','DEPOSIT',1000.00,2175.00,1750737182279,0,NULL,NULL),(18,NULL,5,2,NULL,'CREDIT','DEPOSIT',1222.00,3397.00,1750737362985,0,NULL,NULL),(19,NULL,5,2,NULL,'CREDIT','DEPOSIT',100.00,3497.00,1750737723840,0,NULL,NULL),(20,NULL,5,1,NULL,'CREDIT','DEPOSIT',1000.00,1750.00,1750742869798,0,NULL,NULL),(21,NULL,5,1,NULL,'DEBIT','WITHDRAWAL',100.00,1650.00,1750743274006,0,NULL,NULL),(22,NULL,5,1,NULL,'CREDIT','DEPOSIT',100.00,1750.00,1750743299937,0,NULL,NULL),(23,NULL,5,1,NULL,'CREDIT','DEPOSIT',100.00,1850.00,1750743451664,0,NULL,NULL),(24,NULL,5,1,NULL,'DEBIT','WITHDRAWAL',850.00,1000.00,1750747177842,0,NULL,NULL),(25,NULL,5,1,NULL,'DEBIT','WITHDRAWAL',100.00,900.00,1750775148902,0,NULL,NULL),(29,1000003,5,2,NULL,'DEBIT','BANK_TRANSFER',500.00,2997.00,1751262138438,0,NULL,NULL),(31,1000004,5,2,NULL,'DEBIT','BANK_TRANSFER',97.00,2900.00,1751262918092,1,'ABCD12312',14),(32,1000005,5,2,NULL,'DEBIT','BANK_TRANSFER',900.00,2000.00,1751264337730,1,'ABCD12312',14),(33,1000006,5,2,NULL,'DEBIT','BANK_TRANSFER',500.00,1500.00,1751265240388,1,'ABCD12312',14);
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `before_insert_transaction` BEFORE INSERT ON `transaction` FOR EACH ROW BEGIN
  IF NEW.payment_mode = 'BANK_TRANSFER' AND NEW.transaction_type = 'DEBIT' THEN
    UPDATE transaction_ref_tracker SET latest_ref = latest_ref + 1;
    SELECT latest_ref INTO @ref FROM transaction_ref_tracker;
    SET NEW.transaction_reference = @ref;
  END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `transaction_ref_tracker`
--

DROP TABLE IF EXISTS `transaction_ref_tracker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction_ref_tracker` (
  `latest_ref` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_ref_tracker`
--

LOCK TABLES `transaction_ref_tracker` WRITE;
/*!40000 ALTER TABLE `transaction_ref_tracker` DISABLE KEYS */;
INSERT INTO `transaction_ref_tracker` VALUES (1000006);
/*!40000 ALTER TABLE `transaction_ref_tracker` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(75) NOT NULL,
  `phone` varchar(10) NOT NULL,
  `gender` enum('MALE','FEMALE','OTHERS') NOT NULL,
  `password` varchar(100) NOT NULL,
  `type` tinyint NOT NULL DEFAULT '1',
  `status` tinyint NOT NULL DEFAULT '1',
  `created_time` bigint NOT NULL,
  `modified_time` bigint DEFAULT NULL,
  `modified_by` bigint DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (4,'Belcy V S','belcy@123.com','9988776655','FEMALE','$2a$12$uWayug5dkBhVFOQlY1wej.uKWLPXQeEgz0ZqnUIMfmrsM5qiaT6JS',1,1,1750143268892,1750309216469,4),(5,'Sathiyan R','sathiyan@123.com','9988776666','MALE','$2a$12$JAyPH7Wq11mIsQ3wNkm8guSOJp2VHLxLIwXcdpnszRFPwUWxmBo6W',1,1,1750154566816,1751375051207,5),(6,'Vivian R S','vrohithsuryaa@gmail.com','9345698383','MALE','$2a$12$.1wOb2h57zKletZSusb62.J8ncdiOwU/y6tfEpoTAgwszMB0OU4gu',4,1,1750224263173,1750752217515,6),(7,'Harish I','harish@123.com','6677889900','MALE','$2a$12$p8/7nlz3LFhZwSWzwvzNw.TMDAcV1r2kc.4Wd/IRPFAdTiXssjZna',3,1,1750269999243,1750365046757,7),(8,'Prabha','prabha@123.com','6677889999','MALE','$2a$12$Ao8GfaO.s2KjrDjtWqL27uH1K3CzA92q6MergzJwjDOV9YEZMIuC2',1,1,1750316664776,NULL,NULL),(9,'Arockiya','arockiya@123.com','5566778899','FEMALE','$2a$12$7b24Vhh8lHxOoiSW7YG5EOC1Y8IUhSVT5977q2sEP8Ey39Nid1iPO',2,1,1750404177513,1750790394154,6),(10,'Rohith','rohith@123.com','7766554433','MALE','$2a$12$K0yN0aIOJh/BPCfYnuDUZ.d7jQZgglZKqvQ9f2Eir22BgRzpqMJxa',3,1,1750404960880,1750791329440,6),(11,'Zidi','zidi@123.com','1122331122','MALE','$2a$12$OElYBr79bqRO40jS2wlwGeq/kgUEkKcfExhkeUN1gubDpdh4MYSXi',2,1,1750504527631,1750820458491,6),(12,'Ishaq','ishaq@123.com','1111112222','MALE','$2a$12$.XqoszDVP4xnyhcc/raDtOJPWx6R/cdlmik3NSykpZgCUHxFp/.Qy',2,1,1750505659312,1750820154230,6),(17,'Jayasri','jaya@123.com','2345623456','FEMALE','$2a$12$AB7Wo5epRGNk65eohSaSr.QGTE.mUW///HIx4A//YrjL3vMHRNilO',1,1,1751345505383,NULL,NULL),(19,'Belcyjhv','example@example.comm','1234567890','OTHERS','$2a$12$AyZ8GFlLBBJAQ9uw3M7udu/1eeX3FUMDmNcD2875fnwo94NiPmWn2',1,1,1751438523866,1751439939141,19),(24,'Belcyjhv','example@example.co','1234567891','FEMALE','$2a$12$OkZkfGWNwAyDnRACVCi1juimvrayLUgHdTYRCeaPF5MokF1DvVw46',1,1,1751438593555,NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-08 11:18:00
