-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: localhost    Database: cartblitz
-- ------------------------------------------------------
-- Server version	8.0.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `customer_id` bigint NOT NULL AUTO_INCREMENT,
  `account_non_expired` bit(1) DEFAULT NULL,
  `account_non_locked` bit(1) DEFAULT NULL,
  `credentials_non_expired` bit(1) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `full_name` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,_binary '',_binary '',_binary '','admin',_binary '','admin','$2a$10$hYInXWyVIjhqM32S7L.9/u56BnuHJJ.EGecTQ9p0R505ykPsCMbz6','admin'),(2,_binary '',_binary '',_binary '','test1@test.com',_binary '','User Test One','$2a$10$xAK2fMtowPu.7BCNTOysyuVrpc6YXsQvPhELNGZm7QSz80lBioHBK','user1'),(3,_binary '',_binary '',_binary '','test2@test.com',_binary '','User Test Two','$2a$10$upFM.oddI8CqrYQSDTElnuTbazFCF8ZfravNWtifQGhcgyGyyE2Xm','user2'),(4,_binary '',_binary '',_binary '','test3@test.com',_binary '','User Test Three','$2a$10$vSfZtcssisulXMX.EY4SM./jlraQnS7o4LbVUTMkH4Ibm5C4lo/CW','user3'),(5,_binary '',_binary '',_binary '','test4@test.com',_binary '','User Test Four','$2a$10$FCOfi7Tty03v9mIQ326rg.zH4.h5uj93DhhW4cbrzJLPyc28JqKyW','user4'),(6,_binary '',_binary '',_binary '','test5@test.com',_binary '','User Test Five','$2a$10$OFvS9yzJlvqFj4D8vSi9IuK5RN3pnxXrZXmoEBUbHYVynuwYXGjgK','user5');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-06-26 23:47:45
