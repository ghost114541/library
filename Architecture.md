# Application Architecture

This document describes the architecture of the library management system.

## Overview

The application follows a classic client-server architecture, with a single-page application (SPA) frontend and a monolithic backend.

## Frontend (web)

The frontend is a Vue.js application built with Vite.

*   **Framework:** Vue.js 3
*   **Build Tool:** Vite
*   **Routing:** Vue Router
*   **State Management:** Pinia
*   **UI Library:** Element Plus
*   **HTTP Client:** Axios

The frontend code is organized into the following main directories:

*   `src/api`: Contains the definitions of the API endpoints for communicating with the backend.
*   `src/assets`: Static assets like images and stylesheets.
*   `src/components`: Reusable Vue components.
*   `src/layout`: The main application layout.
*   `src/router`: Vue Router configuration.
*   `src/stores`: Pinia store modules for state management.
*   `src/utils`: Utility functions, including the Axios configuration for HTTP requests.
*   `src/views`: Vue components for each page of the application.

## Backend (admin)

The backend is a Java application built with Spring Boot.

*   **Framework:** Spring Boot
*   **Language:** Java 11
*   **Build Tool:** Maven
*   **Web:** Spring Web (for REST APIs)
*   **Data Access:** Spring Data JPA
*   **Database:** MySQL
*   **Security:** Spring Security with JSON Web Tokens (JWT)

The backend follows a traditional three-tier architecture:

*   **Controller Layer:** Handles incoming HTTP requests and calls the service layer.
*   **Service Layer:** Contains the business logic of the application.
*   **Data Access Layer:** Interacts with the database using Spring Data JPA.

## Interaction

The frontend and backend communicate via a RESTful API. The frontend sends HTTP requests to the backend, which processes the requests and returns JSON responses. Authentication is handled using JWT. The frontend stores the JWT in local storage and sends it in the `Authorization` header of each request.