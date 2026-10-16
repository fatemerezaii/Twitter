# Twitter

A Twitter-like social media desktop application developed using Java and JavaFX. The project follows the MVC architecture and uses MySQL for persistent data storage, with socket-based client-server communication for private messaging.

## Overview

Twitter is a desktop social networking application designed to simulate core social media functionality while applying object-oriented programming and software design principles.

The application combines a graphical user interface with a database-backed architecture and network communication between clients and a central server.

## Technologies

- **Java** — Core application logic and object-oriented programming
- **JavaFX** — Desktop graphical user interface
- **MySQL** — Relational database and persistent storage
- **JDBC** — Database connectivity
- **Maven** — Dependency management and project build
- **Socket Programming** — Client-server communication
- **Multithreading** — Concurrent request processing and background message handling
- **MVC Architecture** — Separation of application responsibilities

## Features

- User registration and authentication
- User profiles and social interactions
- Posts and post management
- Following and follower relationships
- Private messaging
- Client-server communication using Java sockets
- Persistent data storage with MySQL
- Background message processing
- Custom exception handling
- Generic repository interfaces for data access
- Java interfaces and object-oriented design principles

## Architecture

The application follows the Model-View-Controller (MVC) architectural pattern.

- **Model:** Represents application entities and their data.
- **View:** Provides the JavaFX graphical interface.
- **Controller:** Handles application operations and coordinates interactions between the view and the underlying logic.
- **Repository:** Encapsulates database access through reusable interfaces.
- **Network:** Manages communication between clients and the central server.

## Prerequisites

Before running the application, make sure you have:

- Java Development Kit (JDK)
- Apache Maven
- MySQL Server
- A configured database and the required tables

## Getting Started

1. Clone the repository:

   ```bash
   git clone https://github.com/fatemerezaii/Twitter.git
   ```

2. Open the project in IntelliJ IDEA or another Java IDE.

3. Configure the MySQL connection settings to match your local environment.

4. Create the required database and tables.

5. Resolve project dependencies using Maven.

6. Start the server and run the JavaFX client according to the application's configuration.

## Project Structure

The project is organized around the following responsibilities:

- `model` — Application entities
- `view` — JavaFX user interface
- `controller` — Application logic and coordination
- `repository` — Database access
- `network` — Client-server communication
- `interfaces` — Shared contracts and abstractions
- `exceptions` — Custom application exceptions

*The exact package structure may vary according to the implementation.*

## Learning Objectives

This project provides practical experience with:

- Object-oriented programming in Java
- JavaFX desktop application development
- MVC architecture
- Relational database design and JDBC
- Client-server networking
- Concurrent programming and thread pools
- Interface-based design and generic programming
- Exception handling and resource management

## Author

**Fateme Rezaii**

GitHub: [@fatemerezaii](https://github.com/fatemerezaii)
