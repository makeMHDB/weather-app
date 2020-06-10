# Weather Application

Simple weather application which shows temperature history in Vilnius made for learning purposes

## Running application
Create database "weather_app":
```
CREATE DATABASE `weather_app`;
```
Create new table "temp":
```
CREATE TABLE `temp` (
  `id` int NOT NULL AUTO_INCREMENT,
  `temp` int NOT NULL,
  `obstime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
```
Insert [climacell.co](https://www.climacell.co/) API key at MainController.java

Compile and run application

http://localhost:5000
## Built With

* [Spring](https://spring.io/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Thymeleaf](https://www.thymeleaf.org/) - Template engine
* [Bootstrap](https://getbootstrap.com/) - CSS framework
* [canvasJS](https://canvasjs.com/) - Charting library
* [climacell](https://www.climacell.co/) - Weather data API
