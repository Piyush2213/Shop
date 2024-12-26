# E-Commerce Platform

A fully functional online shopping platform developed using **React**, **Spring Boot**, **Redis**, **MySQL** and **Docker**. This project allows users to browse products, add them to the cart, and securely complete purchases, all while ensuring high scalability and performance.

---

## **Features**

- User Authentication and Profile Management
- Product Browsing, Cart Management, and Order History
- Secure Payment Flow
- Optimized Performance with Redis Caching

---

## **Technologies Used**

- **Frontend**: React.js
- **Backend**: Spring Boot (REST APIs)
- **Database**: MySQL
- **Caching**: Redis
- **Containerization**: Docker

---

## **Setup Instructions**

### Prerequisites

Before getting started, make sure you have the following installed:

- **Docker**: [Install Docker](https://www.docker.com/get-started)
- **Docker Compose**: [Install Docker Compose](https://docs.docker.com/compose/install/)

### Running the Project with Docker

1. **Clone the repository:**

   Open your terminal and run:

   ```bash
   git clone https://github.com/Piyush2213/shop.git
   cd shop
   ```

2. **Locate the Docker Compose file:**

   Inside the `shop` folder, you'll find the `docker-compose.yml` file that defines all the services required to run the application (React, Spring Boot, Redis, MySQL).

3. **Start the project using Docker Compose:**

   Simply run the following command to build and start all the services:

   ```bash
   docker-compose up --build
   ```

   This will automatically:
   - Build the Docker images for the frontend (React), backend (Spring Boot), Redis, and MySQL.
   - Start the services, including the necessary dependencies for the project.
   - Expose the frontend on `http://localhost:5173` and the backend on `http://localhost:8080`.

4. **Access the application:**

   - **Frontend (React)**: Open `http://localhost:5173` in your browser to view the application.
   - **Backend (Spring Boot API)**: The backend REST API will be available at `http://localhost:8080`.
   
   The MySQL and Redis databases will run in the background, and you don’t need to manually configure them—Docker Compose will handle it for you.

---

## **Project Structure**

```
/shop
|-- /frontend           # React application (UI)
|-- /backend            # Spring Boot application (API and business logic)
|-- /docker             # Docker-related files for containerization
|-- docker-compose.yml  # Docker Compose configuration file for the app
|-- README.md           # Project overview and setup instructions
```

---

## **Contributions**

Feel free to fork the repository and submit pull requests. Contributions are always welcome!



## **Contact**

- **Piyush Yadav**  
  - LinkedIn: [Piyush Yadav](https://www.linkedin.com/in/piyushyadav2213/)

---

This version of the README file is designed to make the setup as easy as possible. It explains that by simply cloning the repo and running `docker-compose up --build`, all dependencies like React, Spring Boot, Redis, and MySQL will be set up automatically, without needing to manually configure or install them.
