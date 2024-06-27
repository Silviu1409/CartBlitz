CREATE DATABASE  IF NOT EXISTS `cartblitz` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `cartblitz`;
-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: cartblitz
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
-- Table structure for table `authority`
--

DROP TABLE IF EXISTS `authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `authority` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authority`
--

LOCK TABLES `authority` WRITE;
/*!40000 ALTER TABLE `authority` DISABLE KEYS */;
INSERT INTO `authority` VALUES (1,'ROLE_ADMIN'),(2,'ROLE_USER');
/*!40000 ALTER TABLE `authority` ENABLE KEYS */;
UNLOCK TABLES;

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

--
-- Table structure for table `customer_authority`
--

DROP TABLE IF EXISTS `customer_authority`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_authority` (
  `customer_id` bigint NOT NULL,
  `authority_id` bigint NOT NULL,
  PRIMARY KEY (`customer_id`,`authority_id`),
  KEY `FKh7496x7bdurjfaju8s2spo4w7` (`authority_id`),
  CONSTRAINT `FKh7496x7bdurjfaju8s2spo4w7` FOREIGN KEY (`authority_id`) REFERENCES `authority` (`id`),
  CONSTRAINT `FKrsprkrhauqyet0v12v8r73xmj` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_authority`
--

LOCK TABLES `customer_authority` WRITE;
/*!40000 ALTER TABLE `customer_authority` DISABLE KEYS */;
INSERT INTO `customer_authority` VALUES (1,1),(2,2),(3,2),(4,2),(5,2),(6,2);
/*!40000 ALTER TABLE `customer_authority` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `order_id` bigint NOT NULL AUTO_INCREMENT,
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('CART','COMPLETED') NOT NULL DEFAULT 'CART',
  `total_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `customer_id` bigint DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `FK1oduxyuuo3n2g98l3j7754vym` (`customer_id`),
  CONSTRAINT `FK1oduxyuuo3n2g98l3j7754vym` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (1,'2024-06-21 21:53:34','COMPLETED',1141.96,2),(2,'2024-05-12 19:58:57','CART',789.99,4),(8,'2024-05-13 23:47:57','COMPLETED',369.98,2),(10,'2024-06-27 19:59:34','CART',4801.81,2);
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_product`
--

DROP TABLE IF EXISTS `order_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_product` (
  `price` decimal(10,2) NOT NULL,
  `quantity` int NOT NULL,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`order_id`,`product_id`),
  KEY `FKhnfgqyjx3i80qoymrssls3kno` (`product_id`),
  CONSTRAINT `FKhnfgqyjx3i80qoymrssls3kno` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  CONSTRAINT `FKm6igrp4lwucj1me05axmv885c` FOREIGN KEY (`order_id`) REFERENCES `order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_product`
--

LOCK TABLES `order_product` WRITE;
/*!40000 ALTER TABLE `order_product` DISABLE KEYS */;
INSERT INTO `order_product` VALUES (446.99,1,1,1),(184.99,2,1,3),(324.99,1,1,5),(789.99,3,2,2),(184.99,2,8,3),(3219.35,1,10,10),(236.24,2,10,17),(554.99,2,10,18);
/*!40000 ALTER TABLE `order_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `product_id` bigint NOT NULL AUTO_INCREMENT,
  `brand` varchar(50) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `description` text,
  `name` varchar(50) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock_quantity` int NOT NULL,
  `warranty_id` bigint DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `UK_4yihu80rv2ofatfhpw7ic23s8` (`warranty_id`),
  CONSTRAINT `FKta0mw3dplwkiadhr3l59hqhxt` FOREIGN KEY (`warranty_id`) REFERENCES `warranty` (`warranty_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'AMD','CPU','Socket: AM4,Serie: Ryzen 5 4000 Series,Nucleu: Renoir,Frecvență: 3600 Mhz,Tehnologie de fabricație: 7 nm,Putere totală disipată: 65 W','Procesor AMD Ryzen 5 4500',446.99,0,1),(2,'Intel','CPU','Socket: 1200,Serie: Core i5 11th gen,Nucleu: Rocket Lake,Frecvență: 2600 Mhz,Tehnologie de fabricație: 14 nm,Putere totală disipată: 65 W','Procesor Intel Rocket Lake, Core i5 11400',789.99,1,NULL),(3,'Kingston','RAM','Tip: DDR4,Capacitate: 16 GB,Frecvență: 2666 MHz,Latența CAS: 16 CL,Standard: PC4-21300','Memorie Kingston FURY Beast',184.99,10,2),(4,'Kingston','RAM','Tip: DDR5,Capacitate: 16 GB,Frecvență: 6000 MHz,Latența CAS: 40 CL,Standard: PC5-48000','Memorie Kingston FURY Beast',359.99,20,NULL),(5,'GIGABYTE','MDB','Format: mATX,Soclu procesor: AM4,Producător chipset: AMD,Model chipset: A520,Interfață grafică: PCI Express x16 3.0,Tip memorie: DDR4,Tehnologie: Dual channel,Număr sloturi: 2,M.2: 1','Placă de bază GIGABYTE A520M DS3H V2',324.99,0,3),(6,'ASUS','MDB','Format: mATX,Soclu procesor: 1200,Producător chipset: Intel,Model chipset: H470,Interfață grafică: PCI Express x16 4.0,Tip memorie: DDR4,Tehnologie: Dual channel,Număr sloturi: 2,M.2: 1','Placă de bază ASUS PRIME H510M-K R2.0',354.36,14,NULL),(7,'Seasonic','PSU','Putere: 750 W,Număr ventilatoare: 1x 120 mm,PFC: Active,Eficiență: 90 %,Certificare: 80+ Gold','Sursă Seasonic G12 GC-750',449.99,25,NULL),(8,'Samsung','SSD','Seria: 980 PRO,Interfață: PCI Express 4.0 x4,Capacitate: 1 TB,Form factor: M.2 2280,Controller: Samsung Elpis,Citire max.: 7000 MB/s,Scriere max.: 5000 MB/s','SSD Samsung 980 PRO',479.99,30,NULL),(9,'AMD','GPU','Interfață: PCI Express x16 4.0,Seria: Radeon RX 7000,GPU Boost clock: 2544 MHz,Tip memorie: GDDR6,Dimensiune memorie: 12 GB,Frecvență memorie efectivă: 18000 MHz','Placă video Sapphire Radeon RX 7700 XT Pulse',2649.99,15,NULL),(10,'NVIDIA','GPU','Interfață: PCI Express x16 4.0,Seria: GeForce RTX 4000,GPU Boost clock: 2475 MHz,Tip memorie: GDDR6X,Dimensiune memorie: 12 GB,Frecvență memorie efectivă: 21008 MHz','Placă video Palit GeForce RTX 4070 Dual',3219.35,11,NULL),(12,'test','CPU','descriere test bla bla','produs test',100.00,10,5),(17,'Kingston','SSD','Seria: A400,Interfață: SATA-III,Capacitate: 960 GB,Form factor: 2.5 inch,Controller: Phison S11,Citire max.: 500 MB/s,Scriere max.: 450 MB/s','SSD Kingston A400',314.99,50,7),(18,'GIGABYTE','PSU','Putere: 750 W,Număr ventilatoare: 1x 120 mm,PFC: Active,Eficiență: 90 %,Certificare: 80+ Gold','Sursa GIGABYTE UD750GM PG5',554.99,6,8);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_tag`
--

DROP TABLE IF EXISTS `product_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_tag` (
  `product_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  KEY `FK3b3a7hu5g2kh24wf0cwv3lgsm` (`tag_id`),
  KEY `FK2rf7w3d88x20p7vuc2m9mvv91` (`product_id`),
  CONSTRAINT `FK2rf7w3d88x20p7vuc2m9mvv91` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`),
  CONSTRAINT `FK3b3a7hu5g2kh24wf0cwv3lgsm` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_tag`
--

LOCK TABLES `product_tag` WRITE;
/*!40000 ALTER TABLE `product_tag` DISABLE KEYS */;
INSERT INTO `product_tag` VALUES (12,1),(12,2),(17,4),(17,5),(17,3),(18,7),(18,6);
/*!40000 ALTER TABLE `product_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `review_id` bigint NOT NULL AUTO_INCREMENT,
  `comment` varchar(255) DEFAULT NULL,
  `rating` int NOT NULL,
  `review_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `product_id` bigint DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  PRIMARY KEY (`review_id`),
  KEY `FKiyof1sindb9qiqr9o8npj8klt` (`product_id`),
  KEY `FKgce54o0p6uugoc2tev4awewly` (`customer_id`),
  CONSTRAINT `FKgce54o0p6uugoc2tev4awewly` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  CONSTRAINT `FKiyof1sindb9qiqr9o8npj8klt` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (1,'Comment Test 1',1,'2024-01-17 13:18:38',1,2),(2,'Comment Test 2',2,'2024-01-17 13:18:58',2,3),(3,'Comment Test 3',3,'2024-01-17 13:19:05',3,4),(4,'Comment Test 4',4,'2024-01-17 13:19:12',4,5),(5,'Comment Test 5',5,'2024-01-17 13:19:19',5,6),(6,'Comment test user1',4,'2024-05-14 20:36:20',2,2),(7,'Comment test2 user1',3,'2024-05-14 20:45:30',2,2),(9,'Comment test',5,'2024-06-23 11:54:43',1,1);
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `tag_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`tag_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (1,'TAG TEST 1'),(2,'TAG TEST 2'),(3,'KINGSTON'),(4,'SSD'),(5,'SATA-III'),(6,'GIGABYTE'),(7,'80+ GOLD');
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warranty`
--

DROP TABLE IF EXISTS `warranty`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warranty` (
  `warranty_id` bigint NOT NULL AUTO_INCREMENT,
  `duration_months` int NOT NULL,
  `type` varchar(255) NOT NULL,
  `details` varchar(255) NOT NULL,
  `terms` varchar(255) NOT NULL,
  PRIMARY KEY (`warranty_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warranty`
--

LOCK TABLES `warranty` WRITE;
/*!40000 ALTER TABLE `warranty` DISABLE KEYS */;
INSERT INTO `warranty` VALUES (1,24,'Garanție standard','Garantia standard asigură împotriva defectelor de fabricație și materiale în condiții normale de utilizare.','Include reparații gratuite sau înlocuirea pieselor defecte în timpul perioadei de garanție. Daunele cauzate de utilizare necorespunzătoare, accidente sau modificari neautorizate nu sunt acoperite.'),(2,60,'Garanție extinsă','Garantia extinsă oferă o acoperire extinsă pentru toate componentele produsului pe o perioadă de cinci ani.','Suportul tehnic extins este disponibil pe toată durata garanției, asigurând clienților ajutorul necesar pentru orice problemă sau întrebare legată de produs.'),(3,36,'Garanție plus','Garantia plus oferă aceleași beneficii ca și garantia standard, plus servicii aditionale precum transport gratuit și suport tehnic extins.','Oferă reparații prioritare și suport extins pentru clienți. Include acoperirea suplimentară a unor componente și servicii suplimentare fără costuri suplimentare.'),(5,10,'standard','test detalii','test termeni'),(7,36,'Garanție standard','Garantia standard asigură împotriva defectelor de fabricație și materiale în condiții normale de utilizare.','Include reparații gratuite sau înlocuirea pieselor defecte în timpul perioadei de garanție. Daunele cauzate de utilizare necorespunzătoare, accidente sau modificari neautorizate nu sunt acoperite.'),(8,24,'Garanție standard','Garantia standard asigură împotriva defectelor de fabricație și materiale în condiții normale de utilizare.','Include reparații gratuite sau înlocuirea pieselor defecte în timpul perioadei de garanție. Daunele cauzate de utilizare necorespunzătoare, accidente sau modificari neautorizate nu sunt acoperite.');
/*!40000 ALTER TABLE `warranty` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-06-27 23:29:51
