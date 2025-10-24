# Production Setup

This document explains how to run the SET Card Game Server in production mode.

## Production Docker Compose

The `docker-compose.prod.yml` file is configured for production deployment with the following features:

- **No MySQL container**: Assumes external database is available
- **Redis included**: For multiplayer game storage
- **Secrets from file**: Uses `secrets.properties` for sensitive data
- **Restart policy**: Automatically restarts containers on failure

## Prerequisites

1. **External Database**: Ensure your MySQL database is accessible from the Docker network
2. **Environment File**: Create `production.env` from the template with your production values

## Configuration

### Setup Environment File
1. Copy the template file:
   ```bash
   cp production.env.template production.env
   ```

2. Update `production.env` with your actual values:
   ```bash
   # Production Database Configuration
   SPRING_DATASOURCE_URL=jdbc:mysql://your-production-db-host:3306/your-database
   SPRING_DATASOURCE_USERNAME=your-db-username
   SPRING_DATASOURCE_PASSWORD=your-db-password
   
   # JWT Security (use strong secret in production)
   JWT_SECRET=your-very-strong-jwt-secret-key
   JWT_EXPIRATION_TIME=3600000
   ```

## Running in Production

### Start the application:
```bash
docker-compose -f docker-compose.prod.yml up -d
```

### View logs:
```bash
docker-compose -f docker-compose.prod.yml logs -f app
```

### Stop the application:
```bash
docker-compose -f docker-compose.prod.yml down
```

## Security Notes

- The `production.env` file contains sensitive information
- This file is automatically ignored by git (added to .gitignore)
- Use strong passwords and JWT secrets in production
- Consider using external secret management systems for enterprise deployments
- Never commit the `production.env` file to version control

## Ports

- **Application**: 8080
- **Redis**: 6379

## Health Check

The application will be available at:
- **Main Application**: http://your-server:8080
- **Swagger UI**: http://your-server:8080/swagger.html
