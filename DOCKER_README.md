# Docker Setup for SET Card Game Server

This project now includes Docker support with Redis for multiplayer game storage.

## Prerequisites

- Docker
- Docker Compose

## Quick Start

1. **Start all services (Redis, MySQL, and Spring Boot app):**
   ```bash
   docker-compose up -d
   ```

2. **View logs:**
   ```bash
   docker-compose logs -f app
   ```

3. **Stop all services:**
   ```bash
   docker-compose down
   ```

## Services

### Redis (Port 6379)
- **Purpose**: Stores multiplayer games in memory
- **Image**: `redis:7-alpine`
- **Data persistence**: Yes (using volume `redis_data`)
- **Configuration**: AOF (Append Only File) enabled for durability

### MySQL (Port 3306)
- **Purpose**: Stores user data, scores, and persistent game data
- **Image**: `mysql:8.0`
- **Database**: `setcardgame`
- **Credentials**: `root/password`

### Spring Boot App (Port 8080)
- **Purpose**: Main application server
- **Profile**: `docker`
- **Dependencies**: Redis and MySQL

## Development

### Local Development with Redis

If you want to run the application locally but use the Docker Redis:

1. Start only Redis and MySQL:
   ```bash
   docker-compose up redis mysql -d
   ```

2. Run the Spring Boot application locally:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

### Environment Variables

The Docker setup uses these environment variables:
- `SPRING_REDIS_HOST=redis`
- `SPRING_REDIS_PORT=6379`
- `SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/setcardgame...`
- `SPRING_DATASOURCE_USERNAME=root`
- `SPRING_DATASOURCE_PASSWORD=password`
- `SUPER_ADMIN_USERNAME=admin`
- `SUPER_ADMIN_PASSWORD=password`

## Data Persistence

- **Redis data**: Stored in Docker volume `redis_data`
- **MySQL data**: Stored in Docker volume `mysql_data`

To completely reset the data:
```bash
docker-compose down -v
```

## Monitoring

### Check Redis status:
```bash
docker-compose exec redis redis-cli ping
```

### Check MySQL status:
```bash
docker-compose exec mysql mysql -u root -ppassword -e "SELECT 1"
```

### View Redis data:
```bash
docker-compose exec redis redis-cli
> KEYS *
> GET game:your-game-id
```