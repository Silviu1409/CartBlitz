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

-- Dump completed on 2024-06-26 23:47:46
