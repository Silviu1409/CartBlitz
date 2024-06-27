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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-06-26 23:47:45
