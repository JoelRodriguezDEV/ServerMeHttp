# ServerMeHttp: Java Native HTTP Server

> A high-performance, multi-threaded HTTP server built entirely in **Core Java 21**. No Spring Boot, no Tomcat, no external dependencies.

![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVC-blue?style=for-the-badge)
![Threads](https://img.shields.io/badge/Concurrency-Virtual%20Threads-success?style=for-the-badge)

## 📖 About The Project

This project is an engineering challenge to understand the internal mechanics of web frameworks like Spring Boot. Instead of relying on "magic" annotations, I implemented the core components of a web server manually to demonstrate a deep understanding of **Networking, Concurrency, and Metaprogramming**.

It handles HTTP requests using **Java 21 Virtual Threads (Project Loom)**, allowing it to support thousands of concurrent connections with minimal memory footprint compared to traditional thread-per-request models.

## 🛠️ Technical Key Features

* **Zero Dependencies:** Built using only the Java Standard Library (`java.net`, `java.io`, `java.lang.reflect`).
* **Virtual Threads (Project Loom):** Implements the modern thread-per-request model using `Thread.ofVirtual()`, making it highly scalable and lightweight.
* **Custom MVC Architecture:**
    * **Front Controller Pattern:** A central dispatcher that routes requests based on URL paths.
    * **Functional Router:** Uses `Map<String, Function>` and Lambdas for dynamic routing.
* **Reflection-Based JSON Engine:**
    * Includes a custom-built `JsonUtil` class that serializes any Java Object to JSON automatically.
    * Handles generic `List<?>` collections and avoids `InaccessibleObjectException` by intelligently parsing Iterables.
* **Manual HTTP Parsing:** Reads raw bytes from TCP Sockets, parses Headers/Query Parameters (`?id=1`), and constructs valid HTTP/1.1 responses.

## 🧩 Architecture Overview

The system follows a simplified flow of a standard Application Server:

```mermaid
graph LR
    A[Client / Browser] -- HTTP Request --> B(ServerSocket Listener)
    B -- Spawns --> C{Virtual Thread}
    C -- Parses Request --> D[Front Controller / Dispatcher]
    D -- Routes to --> E[UserController]
    E -- Fetches Data --> F[(In-Memory DB)]
    F -- Returns POJO --> E
    E -- Uses Reflection --> G[JsonUtil Serializer]
    G -- JSON String --> D
    D -- HTTP Response --> A
💻 Code Example
1. The Router (Dispatcher)
Using functional interfaces to map URLs to business logic:

Java

// Mapping routes to controller methods using Method References
rutas.put("/api/usuario", UserController::buscarUsuario);
rutas.put("/api/status", UserController::obtenerStatus);
2. Custom JSON Serializer (Reflection)
A snippet of the engine that converts Java Objects to JSON strings dynamically:

Java

// Dynamically accessing private fields to serialize data
for (Field campo : campos) {
    campo.setAccessible(true);
    jsonPairs.add("\"" + campo.getName() + "\": " + campo.get(objeto));
}
How to Run
Clone the repository

Bash

git clone [https://github.com/JoelRodriguezDEV/ServerMeHttp.git](https://github.com/JoelRodriguezDEV/ServerMeHttp.git)
Open in IntelliJ IDEA (Ensure JDK 21 or higher is selected).

Run the HttpServer class.

Test with curl or browser:

Bash

# Get all users
curl http://localhost:8080/api/usuario

# Get specific user by ID
curl "http://localhost:8080/api/usuario?id=2"

Concepts Demonstrated

This project serves as a proof of concept for:

TCP/IP Networking: Handling ServerSocket and Input/OutputStream.

Java Memory Model: Efficient management of streams and buffers.

Modern Concurrency: Utilizing Virtual Threads for high-throughput I/O.

Data Structures: Using Maps and Functional Interfaces for O(1) routing complexity.

Clean Code: Separation of concerns (Server vs. Controller vs. Utility).

Developed by Joel Rodriguez as part of a Backend Architecture Deep Dive.