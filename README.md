# Demo Twitter Application

## Overview

The Demo Twitter Application is a simplified version of a social media platform similar to Twitter. It allows users to:
- Register and log in to the platform.
- Create, edit, and delete posts.
- Like and unlike posts.
- Comment on posts, edit, and delete comments.
- Follow and unfollow other users.
- View a feed of posts from users they follow, including comments and likes.

This project is designed to demonstrate the use of modern frameworks and tools with Groovy for building a RESTful API with authentication, persistence, and documentation.

## Technologies Used

- **Groovy**: Main programming language.
- **Java 17**: Runtime environment.
- **Spring Boot**: Framework for building the application.
    - **Spring Security**: Manages user authentication and authorization.
    - **Spring Data MongoDB**: Database support for MongoDB.
- **MongoDB**: NoSQL database for storing users, posts, and comments.
- **JWT (JSON Web Tokens)**: For secure authentication and session management.
- **Swagger/OpenAPI**: For API documentation and testing.
- **Gradle**: Build automation tool.
- **Spock Framework**: For testing the application (Groovy-based).
- **Docker**: Containerization tool for packaging the application for deployment.

## API Documentation

The API is documented using **Swagger** and can be accessed locally at:
[Swagger UI](http://localhost:8080/swagger-ui.html)

## Prerequisites

Before running the application, ensure you have the following installed:
- Java 17 or higher
- MongoDB (running locally or on a cloud service)
- Gradle (optional, for custom builds)
- Docker (optional, for containerized deployment)

## Getting Started

### Clone the Repository

```
git clone https://github.com/beemaya13/demo-twitter-app.git
cd demo-twitter-app
```
## Configure the Application

1. **MongoDB Configuration**:
    - Make sure your MongoDB is running locally on the default port (`27017`), or change the database connection settings in `application.properties` or `application.yml` file under `src/main/resources`.

2. **JWT Secret**:
    - Set the `jwt.secret` property in `application.properties` with your secret key for JWT token generation.

   Example in `application.properties`:
   ```
   jwt.secret=your_jwt_secret_key
    ```
## Running the Application

You can run the application using Gradle
```
./gradlew bootRun
```
Or if you prefer using the Spring Boot plugin for your IDE:

1. Import the project as a Gradle project.
2. Locate the `DemoTwitterApplication` class.
3. Run the `main` method.

The application will start on `http://localhost:8080`.

## Testing the Application

The project includes unit and integration tests using the **Spock** framework. To run the tests, use: 
```
./gradlew test
```

## Accessing the API

Once the application is running, you can access the Swagger UI for testing and exploring the API:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Common Endpoints

- **User Registration**: `POST /api/users/register`
- **User Login**: `POST /api/users/login`
- **Create a Post**: `POST /api/posts`
- **Get User Feed**: `GET /api/posts/feed`
- **Like a Post**: `POST /api/posts/{postId}/like`
- **Comment on a Post**: `POST /api/comments/{postId}/comments`
- **Follow a User**: `POST /api/users/{id}/subscribe`
- **Unfollow a User**: `DELETE /api/users/{id}/subscribe`

## Project Structure

- `src/main/groovy/com/nilga/demotwitter/`: Main application files.
    - `controller/`: REST API controllers.
    - `service/`: Business logic for the application.
    - `repository/`: MongoDB repositories.
    - `model/`: Data models for users, posts, and comments.
    - `exception/`: Custom exception classes.
    - `security/`: JWT authentication and user details services.
    - `config/`: Application configuration classes.
- `src/test/groovy/com/nilga/demotwitter/`: Spock tests for the application.

