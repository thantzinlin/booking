CREATE DATABASE  IF NOT EXISTS `booking` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `booking`;
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: booking
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booking_time` datetime(6) DEFAULT NULL,
  `check_in_time` datetime(6) DEFAULT NULL,
  `checked_in` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `status` enum('ATTENDED','BOOKED','CANCELLED') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `class_schedule_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `user_package_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtrbsyuifiisw2haq8pi407604` (`class_schedule_id`),
  KEY `FKeyog2oic85xg7hsu2je2lx3s6` (`user_id`),
  KEY `FKk891lpubajvnghgyb9wqwgmbu` (`user_package_id`),
  CONSTRAINT `FKeyog2oic85xg7hsu2je2lx3s6` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKk891lpubajvnghgyb9wqwgmbu` FOREIGN KEY (`user_package_id`) REFERENCES `user_packages` (`id`),
  CONSTRAINT `FKtrbsyuifiisw2haq8pi407604` FOREIGN KEY (`class_schedule_id`) REFERENCES `class_schedule` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (1,'2025-05-05 06:14:20.023795',NULL,_binary '\0','2025-05-05 06:14:20.023795','BOOKED','2025-05-05 06:14:20.023795',1,3,1);
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class_schedule`
--

DROP TABLE IF EXISTS `class_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booked_count` int NOT NULL,
  `class_name` varchar(100) NOT NULL,
  `country` varchar(100) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `end_time` datetime(6) NOT NULL,
  `instructor_name` varchar(100) NOT NULL,
  `location` varchar(200) NOT NULL,
  `max_capacity` int NOT NULL,
  `required_credit` int NOT NULL,
  `start_time` datetime(6) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_schedule`
--

LOCK TABLES `class_schedule` WRITE;
/*!40000 ALTER TABLE `class_schedule` DISABLE KEYS */;
INSERT INTO `class_schedule` VALUES (1,6,'Beginner Yoga','Myanmar','2025-05-05 06:06:58.000000','A beginner-friendly yoga class focused on basic poses and breathing techniques.','2025-06-01 10:30:00.000000','Aye Chan','Yangon Training Center',30,2,'2025-06-01 09:00:00.000000','2025-05-05 06:14:20.042744'),(2,10,'Advanced Cardio','Thailand','2025-05-05 06:06:58.000000','High-intensity cardio training for advanced students.','2025-06-02 18:00:00.000000','Somchai Kiat','Bangkok Fitness Hall',25,3,'2025-06-02 17:00:00.000000','2025-05-05 06:06:58.000000'),(3,3,'Pilates Core Strength','Malaysia','2025-05-05 06:06:58.000000','Build your core strength and improve posture through Pilates.','2025-06-03 08:00:00.000000','Noraini Yusuf','Kuala Lumpur Wellness Hub',20,2,'2025-06-03 07:00:00.000000','2025-05-05 06:06:58.000000'),(4,12,'Zumba Dance','Myanmar','2025-05-05 06:06:58.000000','Fun, fast-paced Zumba session to burn calories and boost energy.','2025-06-04 19:00:00.000000','Thida Win','Mandalay Fitness Zone',40,1,'2025-06-04 18:00:00.000000','2025-05-05 06:06:58.000000');
/*!40000 ALTER TABLE `class_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `packages`
--

DROP TABLE IF EXISTS `packages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `packages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `country` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `credit` int DEFAULT NULL,
  `expiry_date` datetime(6) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `price` decimal(38,2) DEFAULT NULL,
  `purchased_date` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `packages`
--

LOCK TABLES `packages` WRITE;
/*!40000 ALTER TABLE `packages` DISABLE KEYS */;
INSERT INTO `packages` VALUES (1,'Singapore','2025-05-05 06:06:31.000000',5,'2025-12-31 23:59:59.000000',_binary '','Basic Package SG',19.99,NULL,'2025-05-05 06:06:31.000000'),(2,'Singapore','2025-05-05 06:06:31.000000',10,'2025-12-31 23:59:59.000000',_binary '','Standard Package SG',34.99,NULL,'2025-05-05 06:06:31.000000'),(3,'Malaysia','2025-05-05 06:06:31.000000',5,'2025-12-31 23:59:59.000000',_binary '','Basic Package MY',18.99,NULL,'2025-05-05 06:06:31.000000'),(4,'Malaysia','2025-05-05 06:06:31.000000',20,'2025-12-31 23:59:59.000000',_binary '','Premium Package MY',59.99,NULL,'2025-05-05 06:06:31.000000'),(5,'Myanmar','2025-05-05 06:06:31.000000',5,'2025-12-31 23:59:59.000000',_binary '','Basic Package MM',15000.00,NULL,'2025-05-05 06:06:31.000000'),(6,'Myanmar','2025-05-05 06:06:31.000000',15,'2025-12-31 23:59:59.000000',_binary '','Premium Package MM',40000.00,NULL,'2025-05-05 06:06:31.000000');
/*!40000 ALTER TABLE `packages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_info`
--

DROP TABLE IF EXISTS `payment_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) NOT NULL,
  `card_holder` varchar(255) DEFAULT NULL,
  `card_number` varchar(255) DEFAULT NULL,
  `expiry_date` varchar(255) DEFAULT NULL,
  `payment_date` datetime(6) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `user_package_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbyr5d5rrrhfsy2kp4j3jaeaue` (`user_package_id`),
  CONSTRAINT `FKbyr5d5rrrhfsy2kp4j3jaeaue` FOREIGN KEY (`user_package_id`) REFERENCES `user_packages` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_info`
--

LOCK TABLES `payment_info` WRITE;
/*!40000 ALTER TABLE `payment_info` DISABLE KEYS */;
INSERT INTO `payment_info` VALUES (1,40000.00,'Thant Zin Lin','411115645999811','12/26','2025-05-05 06:13:38.775960','CreditCard',1);
/*!40000 ALTER TABLE `payment_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_packages`
--

DROP TABLE IF EXISTS `user_packages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_packages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `expiry_date` datetime(6) DEFAULT NULL,
  `purchased_date` datetime(6) DEFAULT NULL,
  `remaining_credit` int DEFAULT NULL,
  `status` enum('ACTIVE','EXPIRED') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `package_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKamf4ragc34vb3il9wxrwjxw7b` (`package_id`),
  KEY `FKlhj6dofmpikc4fp9nf3ciksdq` (`user_id`),
  CONSTRAINT `FKamf4ragc34vb3il9wxrwjxw7b` FOREIGN KEY (`package_id`) REFERENCES `packages` (`id`),
  CONSTRAINT `FKlhj6dofmpikc4fp9nf3ciksdq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_packages`
--

LOCK TABLES `user_packages` WRITE;
/*!40000 ALTER TABLE `user_packages` DISABLE KEYS */;
INSERT INTO `user_packages` VALUES (1,'2025-05-05 06:13:38.768941','2025-12-31 23:59:59.000000','2025-05-05 06:13:38.763954',13,'ACTIVE','2025-05-05 06:14:20.050724',6,3);
/*!40000 ALTER TABLE `user_packages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(50) NOT NULL,
  `is_verified` bit(1) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `reset_token` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3g1j96g94xpk3lpxl2qbl985x` (`name`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (3,'2025-05-05 06:11:37.290076','thantzinlin1995@gmail.com',_binary '','Thant Zin Lin','$2a$10$iVCBYXVvnpz1oG24A90clOkYtCaIR1HxV6RiXz9LRI8Ox4R4ed.Du','','2025-05-05 06:12:06.822232');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `waitlist`
--

DROP TABLE IF EXISTS `waitlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `waitlist` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `joined_at` datetime(6) DEFAULT NULL,
  `refunded` bit(1) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `schedule_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `user_package_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK644g7njjjs10jp5cstmfnf8hl` (`schedule_id`),
  KEY `FKc99hy864betkwt5pdekgfxkk1` (`user_id`),
  KEY `FKsi60ttgmgxpkit4o8cwtjwgsq` (`user_package_id`),
  CONSTRAINT `FK644g7njjjs10jp5cstmfnf8hl` FOREIGN KEY (`schedule_id`) REFERENCES `class_schedule` (`id`),
  CONSTRAINT `FKc99hy864betkwt5pdekgfxkk1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKsi60ttgmgxpkit4o8cwtjwgsq` FOREIGN KEY (`user_package_id`) REFERENCES `user_packages` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `waitlist`
--

LOCK TABLES `waitlist` WRITE;
/*!40000 ALTER TABLE `waitlist` DISABLE KEYS */;
/*!40000 ALTER TABLE `waitlist` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-05  6:20:51
