# Unravel Project

This is a Java Spring Boot application built with Gradle.

## 📦 Prerequisites

Make sure you have the following installed:

- **Java Development Kit (JDK)**: **Version 8**  
  [Download JDK 8](https://adoptium.net/temurin/releases/?version=8)

- (Optional) **An IDE** such as IntelliJ IDEA, Eclipse, or VS Code with Java support.
- **Docker** (for running Redis)

## ⚙️ Database / Configuration


You can run Redis easily using Docker.

### ✅ Run Redis with Docker:
```bash
docker run --name unravel-redis -p 6379:6379 -d redis
