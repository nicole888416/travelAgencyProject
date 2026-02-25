CREATE DATABASE  IF NOT EXISTS `travel_agency_shop` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `travel_agency_shop`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: travel_agency_shop
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `coupon`
--

DROP TABLE IF EXISTS `coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coupon` (
  `id` int NOT NULL AUTO_INCREMENT,
  `coupon_code` varchar(45) NOT NULL,
  `coupon_name` varchar(45) NOT NULL,
  `coupon_type` varchar(45) NOT NULL,
  `min_amount` int NOT NULL DEFAULT '0',
  `percent_off` int DEFAULT NULL,
  `amount_off` int DEFAULT NULL,
  `active` tinyint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_coupon_code` (`coupon_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coupon`
--

LOCK TABLES `coupon` WRITE;
/*!40000 ALTER TABLE `coupon` DISABLE KEYS */;
INSERT INTO `coupon` VALUES (1,'FIRST98','首購券滿15000打98折','PERCENT',15000,98,NULL,1),(2,'BDAY95','生日券滿20000打95折','PERCENT',20000,95,NULL,1),(3,'OFF500','折扣碼500元','AMOUNT',0,NULL,500,1),(4,'OFF1000','折扣碼1000元','AMOUNT',0,NULL,1000,1);
/*!40000 ALTER TABLE `coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` int NOT NULL AUTO_INCREMENT,
  `customer_no` varchar(45) NOT NULL,
  `customer_name` varchar(45) NOT NULL,
  `phone` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `birthday` date NOT NULL,
  `address_city` varchar(45) NOT NULL,
  `address_detail` varchar(45) NOT NULL,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `member_level` varchar(45) NOT NULL DEFAULT 'BRONZE',
  `total_spent` int NOT NULL DEFAULT '0',
  `photo_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_no` (`customer_no`),
  UNIQUE KEY `uk_customer_username` (`username`),
  UNIQUE KEY `uk_customer_password` (`password`),
  UNIQUE KEY `uk_customer_phone` (`phone`),
  UNIQUE KEY `uk_customer_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'C000001','cus1','0900123456','cus1@gmail.com','1911-01-01','台北市','101','cus1','123','PLATINUM',1789800,'C:\\Users\\nicole\\Pictures\\japan.jfif'),(2,'C000002','cus2','0922345678','cus2@gmail.com','1922-02-02','新北市','202','cus2','456','BRONZE',0,'C:\\Users\\nicole\\Pictures\\japan.jfif');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `id` int NOT NULL AUTO_INCREMENT,
  `employee_no` varchar(45) NOT NULL,
  `employee_name` varchar(45) NOT NULL,
  `phone` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `birthday` date NOT NULL,
  `address_city` varchar(45) NOT NULL,
  `address_detail` varchar(45) NOT NULL,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `role` varchar(45) NOT NULL DEFAULT 'STAFF',
  `photo_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_no` (`employee_no`),
  UNIQUE KEY `uk_employee_username` (`username`),
  UNIQUE KEY `uk_employee_password` (`password`),
  UNIQUE KEY `uk_employee_phone` (`phone`),
  UNIQUE KEY `uk_employee_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (1,'E000001','mgr1','0911234567','mgr1@gmail.com','1921-11-11','新北市','222','mgr1','123','GM','C:\\Users\\nicole\\Pictures\\japan.jfif'),(3,'E000003','emp1','0922345678','emp1@gmail.com','1931-11-01','基隆市','333','emp1','456','STAFF','C:\\Users\\nicole\\Pictures\\japan.jfif'),(4,'E000004','emp2','0955678900','emp2@gmail.com','1982-02-22','桃園市','555','emp2','789','STAFF',NULL);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `schedule_id` int NOT NULL,
  `qty` int NOT NULL,
  `unit_price` int NOT NULL,
  `subtotal` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_item_order` (`order_id`),
  KEY `fk_item_product` (`product_id`),
  KEY `fk_item_schedule` (`schedule_id`),
  CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_item_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `fk_item_schedule` FOREIGN KEY (`schedule_id`) REFERENCES `product_schedule` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (1,1,1,2,2,280000,560000),(2,2,1,1,1,280000,280000),(3,3,2,4,1,450000,450000),(4,4,1,1,1,280000,280000),(5,5,2,4,1,450000,450000);
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_no` varchar(45) NOT NULL,
  `customer_id` int NOT NULL,
  `employee_id` int NOT NULL,
  `coupon_id` int DEFAULT NULL,
  `status` varchar(45) NOT NULL DEFAULT 'PENDING',
  `amount` int NOT NULL,
  `discount_amount` int NOT NULL,
  `final_amount` int NOT NULL,
  `order_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_orders_customer` (`customer_id`),
  KEY `idx_orders_employee` (`employee_id`),
  KEY `idx_orders_date` (`order_date`),
  KEY `fk_orders_coupon` (`coupon_id`),
  CONSTRAINT `fk_orders_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupon` (`id`),
  CONSTRAINT `fk_orders_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `fk_orders_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'O20260222213536883',1,3,1,'PENDING',560000,11200,548800,'2026-02-22 21:35:37'),(2,'O20260222225754729',1,3,NULL,'PENDING',280000,42000,238000,'2026-02-22 22:57:54'),(3,'O20260225031439782',1,3,NULL,'PENDING',450000,67500,382500,'2026-02-25 03:14:40'),(4,'O20260225032208781',1,3,NULL,'PENDING',280000,42000,238000,'2026-02-25 03:22:09'),(5,'O20260225032310330',1,3,NULL,'PENDING',450000,67500,382500,'2026-02-25 03:23:11');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_no` varchar(45) NOT NULL,
  `product_name` varchar(45) NOT NULL,
  `product_price` int NOT NULL,
  `product_stock` int NOT NULL DEFAULT '0',
  `description` varchar(5000) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_no` (`product_no`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (1,'p001','日本京都櫻花千年古都封館之賞 5 日',280000,10,'行程簡介：\n第一天：私人專機抵達關西，下榻奢華安縵京都。\n第二天：清水寺清晨私人封館參拜，避開人潮獨享櫻花美景。\n第三天：高台寺私人茶室與當代大師對談，體驗正統茶道。\n第四天：嵐山私人包船遊川，於船上享用特製懷石料理。\n第五天：名店私人選品購物，專車送機圓滿行程。'),(2,'p002','法國巴黎凡爾賽宮晚宴與藝術朝聖 7 日',450000,15,'行程簡介：\n第一天：入住巴黎萊佛士酒店，管家專人迎接。\n第二天：羅浮宮閉館後私人專場導覽，近距離端詳蒙娜麗莎。\n第三天：凡爾賽宮私人參訪王后寢宮，夜間舉辦燭光晚宴。\n第四天：香奈兒高級訂製服工作坊私人參觀。\n第五天：搭乘私人直升機前往聖米歇爾山俯瞰奇景。\n第六天：米其林三星餐廳主廚餐桌，品味極致法菜。\n第七天：專屬司機接送機場退稅與返家。'),(3,'p003','瑞士阿爾卑斯名峰直升機滑雪 8 日',520000,20,'行程簡介：\n第一天：抵達蘇黎世，專車前往聖莫里茲五星宮殿酒店。\n第二天：私人直升機載往馬特洪峰冰河進行高山滑雪。\n第三天：世界文化遺產景觀列車私人包廂品酩。\n第四天：日內瓦頂級製表工坊，親自組裝專屬紀念表。\n第五天：入住布爾根施托克溫泉度假村，眺望琉森湖。\n第六天：私人遊艇巡航琉森湖，於湖心享用龍蝦午宴。\n第七天：蘇黎世名店大道 VIP 購物體驗。\n第八天：私人商務中心辦理登機。'),(4,'p004','義大利托斯卡尼酒莊與超跑馳騁 9 日',420000,0,'行程簡介：\n第一天：米蘭抵達，入住四季酒店。\n第二天：私人導覽米蘭大教堂頂端與達文西最後的晚餐。\n第三天：駕駛法拉利最新車款橫越托斯卡尼丘陵。\n第四天：入住菲拉格慕家族私人酒莊城堡別墅。\n第五天：白松露獵人陪同深入森林尋找大地珍寶。\n第六天：佛羅倫斯私人博物館晚宴，置身大衛像下。\n第七天：比薩斜塔私人專屬登塔時段。\n第八天：名牌直營店私人導購與專員提貨。\n第九天：專車前往羅馬機場。'),(5,'p005','英國倫敦皇室禮儀與貴族生活 7 日',380000,0,'行程簡介：\n第一天：入住倫敦麗池酒店，體驗英式管家服務。\n第二天：私人參觀倫敦塔珠寶室並觀賞開門儀式。\n第三天：受邀參與貴族私人莊園狩獵與下午茶。\n第四天：薩佛街手工西服定製與時尚歷史導覽。\n第五天：溫莎城堡非開放區域私人深入探訪。\n第六天：倫敦眼私人包廂晚餐，俯瞰大笨鐘夜景。\n第七天：私人接送前往希斯洛機場。'),(6,'p006','冰島藍色潟湖與極光直升機 6 日',480000,0,'行程簡介：\n第一天：入住藍色潟湖私人別墅，獨享專屬溫泉池。\n第二天：直升機空降休眠火山內部，探索地球核心。\n第三天：私人專業攝影師隨行，於冰河湖拍攝極光大片。\n第四天：超级吉普車深入內陸高地探索黑色沙灘。\n第五天：私人包船出海觀鯨，在甲板享用海鮮盛宴。\n第六天：雷克雅維克藝術巡禮後送機。');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_image`
--

DROP TABLE IF EXISTS `product_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_image` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `img_index` int NOT NULL,
  `img_path` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_img` (`product_id`,`img_index`),
  CONSTRAINT `fk_product_image_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_image`
--

LOCK TABLES `product_image` WRITE;
/*!40000 ALTER TABLE `product_image` DISABLE KEYS */;
INSERT INTO `product_image` VALUES (1,1,1,'C:\\Users\\nicole\\Documents\\work\\travel_shop\\resources\\Kyoto-01.jpg'),(2,1,2,'C:\\Users\\nicole\\Documents\\work\\travel_shop\\resources\\Kyoto-02.jpg'),(3,1,3,'C:\\Users\\nicole\\Documents\\work\\travel_shop\\resources\\Kyoto-03.jpg'),(4,1,4,'C:\\Users\\nicole\\Documents\\work\\travel_shop\\resources\\Kyoto-04.jpg'),(5,1,5,'C:\\Users\\nicole\\Documents\\work\\travel_shop\\resources\\Kyoto-05.jpg'),(11,2,1,'C:\\Users\\nicole\\Documents\\work\\travel_shop\\resources\\Paris_1.jpg'),(12,2,2,'C:\\Users\\nicole\\Documents\\work\\travel_shop\\resources\\Paris_2.jfif'),(13,2,3,'C:\\Users\\nicole\\Documents\\work\\travel_shop\\resources\\Paris_3.jpg'),(14,2,4,'C:\\Users\\nicole\\Documents\\work\\travel_shop\\resources\\Paris_4.jpg'),(15,2,5,'C:\\Users\\nicole\\Documents\\work\\travel_shop\\resources\\Paris_5.jpg');
/*!40000 ALTER TABLE `product_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_schedule`
--

DROP TABLE IF EXISTS `product_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_schedule` (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL,
  `depart_date` date NOT NULL,
  `return_date` date NOT NULL,
  `seat_stock` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_schedule_product` (`product_id`),
  CONSTRAINT `fk_schedule_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_schedule`
--

LOCK TABLES `product_schedule` WRITE;
/*!40000 ALTER TABLE `product_schedule` DISABLE KEYS */;
INSERT INTO `product_schedule` VALUES (1,1,'2026-02-24','2026-02-28',8),(2,1,'2026-03-01','2026-03-05',8),(3,1,'2026-03-06','2026-03-10',10),(4,2,'2026-03-01','2026-03-07',13),(5,2,'2026-03-08','2026-03-14',15),(6,3,'2026-04-01','2026-02-08',20),(7,3,'2026-04-09','2026-04-16',15),(8,4,'2026-04-05','2026-04-13',10),(9,4,'2026-04-16','2026-04-24',15),(10,5,'2026-04-13','2026-04-19',20),(11,5,'2026-04-18','2026-04-24',15),(12,6,'2026-04-23','2026-04-28',10),(13,6,'2026-04-27','2026-05-02',20);
/*!40000 ALTER TABLE `product_schedule` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-25  5:34:56
