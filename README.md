# Flights Notification System

A Spring Boot application that monitors Ben Gurion Airport flight data and sends email notifications to users when their subscribed flights have status updates.

## Features

- **Real-time Flight Monitoring**: Automatically syncs flight data from Ben Gurion Airport's public API
- **Flight Subscription**: Users can subscribe to specific flights for status updates
- **Email Notifications**: Automatic email alerts when flight details change (time, terminal, status, etc.)
- **Google OAuth Integration**: Secure user authentication via Google
- **REST API**: Comprehensive API for flight search and user management
- **Reactive Architecture**: Built with Spring WebFlux for high-performance, non-blocking operations
- **Docker Support**: Containerized application for easy deployment

## Technology Stack

- **Backend**: Spring Boot 3.5.5 with WebFlux (Reactive)
- **Database**: MongoDB (Reactive)
- **Authentication**: OAuth2 (Google)
- **Email Service**: Spring Mail with Gmail SMTP
- **Build Tool**: Maven
- **Container**: Docker
- **Java Version**: 21

## API Endpoints

### Flight Operations
- `GET /flights/sync` - Manually trigger flight data synchronization
- `GET /flights/search` - Search flights with optional filters:
  - `airline_code` - Filter by airline code
  - `flight_number` - Filter by flight number
  - `scheduled_date` - Filter by scheduled date
  - `scheduled_time` - Filter by scheduled time
  - `planned_date` - Filter by planned date
  - `planned_time` - Filter by planned time

### User Operations
- `POST /users/subscribe` - Subscribe to flight notifications
- User authentication via Google OAuth2

## Automated Monitoring

- **Flight Data Sync**: Every 15 minutes (`0 0/15 * * * *`)
- **Status Monitoring**: Every 10 seconds for subscribed flights
- **Automatic Notifications**: Emails sent when flight details change

## Quick Start

### Prerequisites
- Java 21
- Maven 3.6+
- Docker (optional)
- MongoDB instance

### Running Locally

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd flights
   ```

2. **Configure application properties**
   
   Update `src/main/resources/application.properties` with your configuration:
   ```properties
   # MongoDB Configuration
   spring.data.mongodb.uri=your_mongodb_connection_string
   spring.data.mongodb.database=flights
   
   # Google OAuth2 Configuration
   spring.security.oauth2.client.registration.google.client-id=your_google_client_id
   spring.security.oauth2.client.registration.google.client-secret=your_google_client_secret
   spring.security.oauth2.client.registration.google.redirect-uri=your_redirect_uri
   
   # Email Configuration
   spring.mail.username=your_email@gmail.com
   spring.mail.password=your_app_password
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application**
   - API: `http://localhost:8080`
   - Web Interface: `http://localhost:8080`

### Using Docker

1. **Build and run with Docker Compose**
   ```bash
   docker-compose up --build
   ```

2. **Access the application**
   - API: `http://localhost:8080`

## Configuration

### Environment Variables

The following environment variables can be configured:

- `MONGODB_URI` - MongoDB connection string
- `GOOGLE_CLIENT_ID` - Google OAuth2 client ID
- `GOOGLE_CLIENT_SECRET` - Google OAuth2 client secret
- `EMAIL_USERNAME` - Gmail username for notifications
- `EMAIL_PASSWORD` - Gmail app password

### OAuth2 Setup

1. Create a Google Cloud Project
2. Enable Google+ API
3. Create OAuth2 credentials
4. Add authorized redirect URIs
5. Update application.properties with client credentials

### Email Setup

1. Enable 2-factor authentication on Gmail
2. Generate an app-specific password
3. Use the app password in application.properties

## Data Models

### Flight Model
- Flight number, airline code
- Scheduled and planned times
- Terminal, counters, check-in zone
- Status and airport information
- City and country details

### User Model
- Email (from Google OAuth)
- List of flight subscriptions
- Creation timestamp

### Subscription Model
- Flight details
- Last known status
- Last update timestamp

## API Documentation

Once the application is running, API documentation is available at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Monitoring and Logging

The application includes:
- Console logging for sync operations
- Email notification status logging
- Error handling for API failures
- Scheduled task monitoring

## Deployment

The application is containerized and can be deployed to:
- Google Cloud Run
- AWS ECS
- Kubernetes
- Any Docker-compatible platform

### Docker Image

The application is built as a Docker image and can be pushed to registries:
```bash
docker build -t flights-springboot:latest .
docker tag flights-springboot:latest your-registry/flights-springboot:latest
docker push your-registry/flights-springboot:latest
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is open source and available under the [MIT License](LICENSE).

## Support

For issues and questions:
- Create an issue in the repository
- Check the application logs for error details
- Verify configuration settings

## Data Source

Flight data is sourced from the Israeli Government's open data portal:
- **API**: `https://data.gov.il/api/3/action/datastore_search`
- **Resource ID**: `e83f763b-b7d7-479e-b172-ae981ddc6de5`
- **Airport**: Ben Gurion International Airport

## Security Notes

- OAuth2 integration for secure authentication
- No sensitive data stored in flight records
- Email credentials should use app-specific passwords
- Database connection should use encrypted connections
