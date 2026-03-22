# BugBustersTW - Smart Restaurant Management System

This project is a distributed microservices-based application developed for the **Web Technologies**  course. It provides a comprehensive solution for restaurant management, leveraging modern Java technologies and a containerized architecture.

---

## 🏗️ System Architecture

The project is built using a microservices pattern, orchestrated with Docker and monitored via distributed tracing.

* **Service Discovery (Eureka Server)**: A centralized registry where all microservices are registered, allowing them to discover and communicate with each other dynamically.
* **API Gateway**: The single entry point for all client requests. It features two specialized profiles:
    * **security**: Integrated with Google OAuth2 to provide secure, authenticated access.
    * **postman**: A development profile that allows for direct endpoint testing without external authentication.
* **Distributed Tracing (Zipkin)**: Integrated across all services to monitor request lifecycles and performance spans.
* **Database (PostgreSQL)**: A containerized database instance used for persistent data storage, utilizing a dedicated Docker volume (`postgres_data`).

---

## 🛠️ Core Technologies

* **Language**: Java
* **Frameworks**: Spring Boot, Spring Cloud (Gateway, Eureka)
* **Database**: PostgreSQL 15
* **Containerization**: Docker & Docker Compose
* **Observability**: OpenZipkin
* **Security**: Spring Security & Google OAuth2

---

## 👥 Team and Responsibilities

The project was developed by a team of three members, each responsible for specific core services and their integration:

* **Ruxandra**: Developed the **Menu Service**. This service manages the restaurant's offerings, including categories, dishes, and ingredients. It features a dashboard and utilizes ModelMapper for data transfer objects.
* **Andreea**: Developed the **Order Service**. This component handles the entire lifecycle of customer orders and order details, ensuring smooth communication with other services via Feign Clients.
* **Adriana**: Developed the **User Service**. This service is responsible for user management, profile administration, and role-based access within the restaurant ecosystem.
