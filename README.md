# 💊 PharmaConnect

**Pharmacy Management System** — Full-stack application with Spring Boot backend and modern frontend.

## 📋 Overview

PharmaConnect is a complete pharmacy management platform that centralizes:
- Inventory management
- Prescription processing
- Order tracking  
- Patient follow-up
- Role-based access control

## 🏗️ Architecture

| Layer | Technology |
|-------|------------|
| **Backend** | Java Spring Boot 3.2.5 |
| **Database** | MySQL (XAMPP) |
| **Frontend** | HTML5, CSS3, JavaScript |
| **Styling** | Bootstrap 5 + Custom CSS |
| **ORM** | Hibernate / JPA |

## 🔐 Key Concepts Demonstrated

- **Heritage (Inheritance)**: `Utilisateur` → `Patient`, `Pharmacien`, `Administrateur` using JOINED strategy
- **REST API**: Complete CRUD endpoints
- **JPA Mapping**: Entity relationships with foreign keys
- **Session Management**: Secure authentication

## 👥 User Roles

| Role | Access |
|------|--------|
| **Patient** | View medicines, request prescriptions, order history |
| **Pharmacien** | Manage stock, approve prescriptions, update orders |
| **Administrateur** | Full system access |

## 🚀 Installation & Run

### Prerequisites
- XAMPP (Apache + MySQL)
- Java 17+
- Maven

### Steps

1. **Start XAMPP**
   - Start Apache (port 80)
   - Start MySQL (port 3306)
   - Stop Tomcat (Spring Boot uses port 8080)

2. **Create Database**
   ```sql
   -- Import pharma_connect.sql in phpMyAdmin
   CREATE DATABASE pharma_connect;
Run Backend

bash :
cd backend
mvn spring-boot:run

Open Frontend :
http://localhost/PharmaConnect/index.html

📁 Project Structure
text
PharmaConnect/
├── backend/                 # Spring Boot application
│   ├── src/main/java/       # Java source code
│   │   ├── entity/          # JPA entities (JOINED inheritance)
│   │   ├── controller/      # REST endpoints
│   │   ├── service/         # Business logic
│   │   └── repository/      # Data access layer
│   └── pom.xml              # Maven dependencies
├── index.html               # Landing page
├── dashboard.html           # Main dashboard
├── script.js                # Frontend logic
├── api.js                   # API client
├── style.css                # Styles
└── pharma_connect.sql       # Database schema

✨ Features
Feature	Status
User Authentication	✅
Role-based Views	✅
Medicine Catalog	✅
Stock Management	✅
Prescription Requests	✅
Prescription Approval	✅
Order Status Updates	✅
Notifications	✅
Responsive Design	✅
🎯 Validation Checklist
Java Spring Boot backend

MySQL database integration

JOINED inheritance (Utilisateur → Patient/Pharmacien)

REST API endpoints

Session management

Full CRUD operations
