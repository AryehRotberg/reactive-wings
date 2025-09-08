# ✈️ Flight Subscription Manager

A comprehensive real-time flight monitoring and notification system built with Spring Boot WebFlux. The application tracks Ben Gurion Airport flight data and provides automated email notifications to users when their subscribed flights experience status changes.

## 🚀 Features

- **🔄 Real-time Flight Monitoring**: Automatically syncs flight data from Ben Gurion Airport's API every 60 seconds
- **📱 Flight Subscription Management**: Users can subscribe to specific flights for personalized updates
- **📧 Smart Email Notifications**: Automated HTML email alerts via SendGrid when flight details change
- **🔐 Google OAuth Integration**: Secure user authentication with Google OAuth 2.0
- **🌐 Modern Web Interface**: Responsive single-page application with real-time updates
- **⚡ Reactive Architecture**: Built with Spring WebFlux for high-performance, non-blocking operations
- **🐳 Cloud-Ready Deployment**: Containerized application deployed on Google Kubernetes Engine
- **📊 Comprehensive Monitoring**: Tracks schedule changes, terminal updates, counter assignments, and flight status

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.5.5 with WebFlux (Reactive)
- **Database**: MongoDB Atlas (Cloud-hosted, Reactive)
- **Authentication**: OAuth2 (Google)
- **Email Service**: SendGrid API
- **Frontend**: Vanilla JavaScript with modern CSS
- **Build Tool**: Maven
- **Container**: Docker with Java 21
- **Deployment**: Google Kubernetes Engine (GKE)
- **Java Version**: 21

## 🎯 System Architecture

```mermaid
graph TB
    %% External Systems
    User[👤 User Browser]
    BenGurion[🛫 Ben Gurion Airport API<br/>data.gov.il]
    SendGrid[📧 SendGrid Email Service]
    
    %% Google Cloud Infrastructure
    subgraph "Google Kubernetes Engine GKE"
        subgraph "Load Balancer"
            LB[🔗 Google Load Balancer<br/>34.63.104.140.nip.io:8080]
        end
        
        subgraph "Kubernetes Cluster"
            subgraph "Application Pods"
                App1[📦 Flight Manager Pod 1<br/>Spring Boot WebFlux]
                App2[📦 Flight Manager Pod 2<br/>Spring Boot WebFlux]
                App3[📦 Flight Manager Pod N<br/>Spring Boot WebFlux]
            end
        end
    end
    
    %% External Database
    MongoDB[(🍃 MongoDB Atlas<br/>Cloud Database)]
    
    %% Google OAuth
    GoogleOAuth[🔑 Google OAuth 2.0<br/>Authentication Provider]
    
    %% Connections
    User --> LB
    LB --> App1
    LB --> App2 
    LB --> App3
    
    App1 --> BenGurion
    App1 --> SendGrid
    App1 --> MongoDB
    App1 --> GoogleOAuth
    
    %% Styling
    classDef external fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef gcp fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef data fill:#fff8e1,stroke:#e65100,stroke-width:2px
    
    class User,BenGurion,SendGrid,GoogleOAuth external
    class LB,App1,App2,App3 gcp
    class MongoDB data
```

## 🔌 API Endpoints

### Flight Operations
- `GET /flights` - Retrieve all current flights
- `GET /flights/search` - Search flights with filters:
  - `airline_code` - Filter by airline code (e.g., "EL AL")
  - `flight_number` - Filter by flight number (e.g., "LY001")
  - `scheduled_date` - Filter by scheduled date
  - `scheduled_time` - Filter by scheduled time
  - `planned_date` - Filter by planned date
  - `planned_time` - Filter by planned time

### User Operations (Authentication Required)
- `GET /users/user-info` - Get current user profile and subscriptions
- `POST /users/subscribe` - Subscribe to flight notifications
- `POST /users/unsubscribe` - Remove flight subscription

### Security
- All user endpoints require Google OAuth 2.0 authentication
- Public endpoints: `/health`, `/actuator/**`

## ⚙️ Automated Monitoring

The system includes several automated background processes:

- **🔄 Flight Data Synchronization**: Every 60 seconds
  - Fetches latest flight data from Ben Gurion Airport API
  - Updates MongoDB with current flight information
  - Removes outdated flight records
  
- **📊 Subscription Monitoring**: Every 10 seconds
  - Checks all user subscriptions against current flight data
  - Detects changes in: schedule, terminal, counters, check-in zones, status
  - Triggers email notifications for detected changes

- **📧 Email Notifications**: Real-time
  - HTML email templates with flight update details
  - Subscription confirmation emails
  - Asynchronous processing to avoid blocking

## 🚀 Deployment

### Live Application
- **Production URL**: `http://34.63.104.140.nip.io:8080/`
- **Platform**: Google Kubernetes Engine (GKE)
- **Container Registry**: `aryehrotberg709/flights-manager:1.0.11`

### Architecture Components
- **Load Balancer**: Google Cloud Load Balancer
- **Container Orchestration**: Kubernetes with horizontal pod autoscaling
- **Database**: MongoDB Atlas (Cloud-hosted)
- **Email Service**: SendGrid API
- **Authentication**: Google OAuth 2.0

## 🏃‍♂️ Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+
- Docker (for containerization)
- Google Cloud Account (for OAuth setup)
- SendGrid Account (for email notifications)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/AryehRotberg/flights.git
   cd flights
   ```

2. **Configure application properties**
   
   Update `src/main/resources/application.properties`:
   ```properties
   # MongoDB Configuration
   spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/flights
   spring.data.mongodb.database=flights
   
   # Google OAuth2 Configuration
   spring.security.oauth2.client.registration.google.client-id=your-client-id
   spring.security.oauth2.client.registration.google.client-secret=your-client-secret
   
   # Email Configuration (Gmail SMTP - fallback)
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   ```

3. **Set up environment variables**
   ```bash
   export SENDGRID_API_KEY=your-sendgrid-api-key
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Access the application**
   - Web Interface: `http://localhost:8080`
   - API Base: `http://localhost:8080/flights`

### Docker Deployment

1. **Build the application**
   ```bash
   ./mvnw clean package
   ```

2. **Build Docker image**
   ```bash
   docker build -t flights-manager:latest .
   ```

3. **Run with Docker Compose**
   ```bash
   docker-compose up
   ```

### Kubernetes Deployment

1. **Build and push image**
   ```bash
   docker build -t your-registry/flights-manager:latest .
   docker push your-registry/flights-manager:latest
   ```

2. **Deploy to GKE**
   ```bash
   kubectl apply -f k8s/
   ```

## ⚙️ Configuration

### Required Environment Variables

```bash
# SendGrid Email Service
SENDGRID_API_KEY=your-sendgrid-api-key

# Optional: Override MongoDB URI
MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/flights

# Optional: Override OAuth credentials
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

### Google OAuth2 Setup

1. **Create Google Cloud Project**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select existing one

2. **Enable APIs**
   - Enable Google+ API or Google Identity API
   - Enable Google OAuth2 API

3. **Create OAuth2 Credentials**
   - Go to "Credentials" → "Create Credentials" → "OAuth 2.0 Client ID"
   - Application type: Web application
   - Authorized redirect URIs:
     - `http://localhost:8080/login/oauth2/code/google` (local)
     - `http://34.63.104.140.nip.io:8080/login/oauth2/code/google` (production)

4. **Update Configuration**
   - Copy Client ID and Client Secret to `application.properties`

### SendGrid Email Setup

1. **Create SendGrid Account**
   - Sign up at [SendGrid](https://sendgrid.com/)
   - Verify your sender identity

2. **Generate API Key**
   - Go to Settings → API Keys
   - Create API key with "Full Access" permissions
   - Set as environment variable: `SENDGRID_API_KEY`

3. **Configure Sender Email**
   - Update `EmailSenderService.java` with your verified sender email

## 📊 Data Models

### FlightModel
```java
{
  "id": "unique_flight_id",
  "airline_code": "EL AL",
  "flight_number": "LY001", 
  "airline_name": "El Al Israel Airlines",
  "scheduled_time": "2025-09-07T10:30:00",
  "planned_time": "2025-09-07T10:35:00",
  "direction": "Departure/Arrival",
  "airport_code": "JFK",
  "city_en": "New York",
  "city_he": "ניו יורק",
  "country_en": "United States",
  "country_he": "ארצות הברית",
  "terminal": 3,
  "counters": "301-320",
  "checkin_zone": "A",
  "status_en": "On Time",
  "status_he": "בזמן",
  "lastUpdated": "2025-09-07T15:30:00"
}
```

### UserModel
```java
{
  "id": "user_email@gmail.com",
  "email": "user_email@gmail.com",
  "subscriptions": [
    {
      "airline_code": "EL AL",
      "flight_number": "LY001",
      "scheduled_time": "2025-09-07T10:30:00",
      "planned_time": "2025-09-07T10:35:00",
      "last_status": "On Time",
      "last_updated": "2025-09-07T15:30:00",
      "airport_code": "JFK",
      "city_en": "New York",
      "country_en": "United States",
      "terminal": "3",
      "counters": "301-320",
      "checkin_zone": "A"
    }
  ]
}
```

## 📚 API Documentation

### Flight Search Examples

```bash
# Get all flights
curl http://34.63.104.140.nip.io:8080/flights

# Search by airline
curl "http://34.63.104.140.nip.io:8080/flights/search?airline_code=EL AL"

# Search by flight number
curl "http://34.63.104.140.nip.io:8080/flights/search?flight_number=LY001"

# Search by date
curl "http://34.63.104.140.nip.io:8080/flights/search?scheduled_date=2025-09-07"
```

### User Operations (Requires Authentication)

```bash
# Get user info and subscriptions
curl -H "Authorization: Bearer $TOKEN" \
     http://34.63.104.140.nip.io:8080/users/user-info

# Subscribe to a flight
curl -X POST \
     -H "Authorization: Bearer $TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"airline_code":"EL AL","flight_number":"LY001","scheduled_time":"2025-09-07T10:30:00"}' \
     http://34.63.104.140.nip.io:8080/users/subscribe

# Unsubscribe from a flight
curl -X POST \
     -H "Authorization: Bearer $TOKEN" \
     "http://34.63.104.140.nip.io:8080/users/unsubscribe?airline_code=EL AL&flight_number=LY001&scheduled_date=2025-09-07"
```

## 📈 Monitoring and Logging

### Application Monitoring
- **Flight Sync Logs**: Console output every 60 seconds showing sync status
- **Subscription Monitoring**: Real-time logging of user notifications
- **Email Status**: Success/failure logging for email notifications
- **Error Handling**: Comprehensive error logging for API failures

### Health Checks
- **Endpoint**: `GET /health` (public access)
- **Actuator**: `GET /actuator/**` endpoints for monitoring
- **Database**: Reactive MongoDB connection monitoring
- **External APIs**: Ben Gurion API availability tracking

### Performance Metrics
- **Reactive Processing**: Non-blocking I/O for high throughput
- **Batch Processing**: Flight data processed in configurable batches (default: 50)
- **Async Notifications**: Email processing doesn't block main application flow
- **Connection Pooling**: Optimized database connections

## 🚀 Production Deployment

### Current Production Environment
- **URL**: http://34.63.104.140.nip.io:8080/
- **Platform**: Google Kubernetes Engine (GKE)
- **Region**: europe-west1
- **Container**: `aryehrotberg709/flights-manager:1.0.11`

### Deployment Process
```bash
# Build application
./mvnw clean package

# Build Docker image
docker build -t aryehrotberg709/flights-manager:1.0.11 .

# Push to registry
docker push aryehrotberg709/flights-manager:1.0.11

# Deploy to GKE (using kubectl or GCP Console)
kubectl set image deployment/flights-app flights-app=aryehrotberg709/flights-manager:1.0.11
```

### Scaling Configuration
- **Horizontal Pod Autoscaler**: Automatically scales based on CPU/memory usage
- **Resource Limits**: Configured per pod for optimal resource utilization
- **Load Balancer**: Google Cloud Load Balancer distributes traffic across pods

## 🔧 Development

### Project Structure
```
src/
├── main/
│   ├── java/com/example/flights/
│   │   ├── FlightsApplication.java          # Main application class
│   │   ├── config/SecurityConfig.java       # OAuth2 security configuration
│   │   ├── controller/                      # REST controllers
│   │   │   ├── FlightsController.java       # Flight operations
│   │   │   └── UserController.java          # User management
│   │   ├── model/                          # Data models
│   │   │   ├── FlightModel.java            # Flight entity
│   │   │   ├── UserModel.java              # User entity
│   │   │   └── SubscriptionModel.java      # Subscription entity
│   │   ├── repo/                           # Repository interfaces
│   │   │   ├── FlightRepository.java       # Flight data access
│   │   │   └── UserRepository.java         # User data access
│   │   ├── service/                        # Business logic
│   │   │   ├── BenGurionAPI.java          # External API client
│   │   │   ├── FlightSyncService.java     # Data synchronization
│   │   │   ├── SubscriptionService.java   # Subscription monitoring
│   │   │   └── EmailSenderService.java    # Email notifications
│   │   └── template/EmailTemplates.java    # Email HTML templates
│   └── resources/
│       ├── application.properties          # Configuration
│       └── static/index.html              # Frontend application
└── test/                                   # Unit tests
```

### Key Development Commands
```bash
# Run tests
./mvnw test

# Run application with dev profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Build without tests
./mvnw clean package -DskipTests

# Run with Docker Compose
docker-compose up --build
```

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Make your changes**
4. **Add tests** (if applicable)
5. **Commit your changes**
   ```bash
   git commit -m 'Add amazing feature'
   ```
6. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
7. **Open a Pull Request**

### Development Guidelines
- Follow Spring Boot best practices
- Use reactive programming patterns (Mono/Flux)
- Add appropriate logging
- Include unit tests for new features
- Update documentation for API changes

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support & Troubleshooting

### Common Issues

**Authentication Problems**
- Verify Google OAuth2 credentials are correct
- Check redirect URIs match your deployment URL
- Ensure Google APIs are enabled in your project

**Email Notifications Not Working**
- Verify `SENDGRID_API_KEY` environment variable is set
- Check SendGrid account is verified and has sending permissions
- Review application logs for email service errors

**Flight Data Not Updating**
- Check Ben Gurion Airport API availability
- Review sync service logs for errors
- Verify MongoDB connection and permissions

**Database Connection Issues**
- Ensure MongoDB Atlas cluster is running
- Check firewall settings allow connections
- Verify connection string format and credentials

### Getting Help
- 🐛 **Bug Reports**: Create an issue with detailed reproduction steps
- 💡 **Feature Requests**: Open an issue with your proposed enhancement
- 📚 **Documentation**: Check application logs and API responses
- 🔧 **Configuration**: Review `application.properties` settings

### Contact
- **Repository**: [GitHub - AryehRotberg/flights](https://github.com/AryehRotberg/flights)
- **Issues**: [GitHub Issues](https://github.com/AryehRotberg/flights/issues)

## 📊 Data Source

**Ben Gurion International Airport Flight Data**
- **Provider**: Israeli Government Open Data Portal
- **API Endpoint**: `https://data.gov.il/api/3/action/datastore_search`
- **Resource ID**: `e83f763b-b7d7-479e-b172-ae981ddc6de5`
- **Update Frequency**: Real-time (application syncs every 60 seconds)
- **Data Format**: JSON with Hebrew and English fields
- **Coverage**: All departures and arrivals at Ben Gurion Airport

## 🔒 Security & Privacy

- **Authentication**: OAuth2 with Google - no passwords stored
- **Data Privacy**: Only email addresses and flight preferences stored
- **API Security**: HTTPS in production, rate limiting on external APIs
- **Database Security**: MongoDB Atlas with encryption at rest and in transit
- **Email Security**: SendGrid API with verified sender domains
- **No Sensitive Data**: Flight information is public data from government APIs

---

**Built with ❤️ using Spring Boot WebFlux and deployed on Google Cloud Platform**
