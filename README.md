# P2P Student Tutoring Marketplace

A production-ready peer-to-peer student tutoring marketplace with an internal point/token economy, built with Spring Boot 3.x, React.js, and MySQL/MongoDB.

## Architecture Overview

### Tech Stack
- **Backend**: Java 17+, Spring Boot 3.3.3, Spring Data JPA, Spring Data MongoDB, Maven
- **Databases**: 
  - MySQL: User authentication, points, sessions, ledgers, escrow
  - MongoDB: Session notes, chat logs
- **Frontend**: React.js (Vite), Axios, Tailwind CSS, Lucide-React
- **Testing**: JUnit 5, Mockito, Spring Test

### Design Patterns (GoF)

All 5 required patterns are explicitly implemented in `src/main/java/com/tutormarketplace/pattern/`:

1. **State Pattern** (`pattern/state/`): Manages tutoring session lifecycle
   - `OpenState`: Request posted, awaiting acceptance
   - `InProgressState`: Tutor accepted, session ongoing
   - `InDisputeState`: Session complete but under dispute
   - `ClosedState`: Session settled
   - `SessionStateContext`: Orchestrates state transitions

2. **Strategy Pattern** (`pattern/strategy/`): Point calculation strategies
   - `PointCalculationStrategy`: Interface for strategies
   - `FixedBountyStrategy`: Student sets fixed bounty
   - `TimeBasedStrategy`: Points based on session duration
   - `PointCalculationContext`: Runtime strategy selection

3. **Command Pattern** (`pattern/command/`): Encapsulates point transfers
   - `Command`: Interface for commands
   - `TransferPointsCommand`: Executes/undoes point transfers
   - `CommandInvoker`: Manages command history and undo operations

4. **Observer Pattern** (`pattern/observer/`): Skill-based notifications
   - `SkillNotificationObserver`: Observer interface
   - `StudentSkillNotificationObserver`: Concrete observer
   - `SkillNotificationSubject`: Manages subscriptions and notifications

5. **Facade Pattern** (`pattern/facade/`): Session settlement orchestration
   - `SessionSettlementFacade`: Coordinates multi-step completion
   - Handles: state transitions, point execution, MongoDB persistence, notifications

## Project Structure

```
src/main/java/com/tutormarketplace/
├── config/             # Spring configuration
├── controller/         # REST API controllers
├── dto/               # Data transfer objects
├── model/             # JPA entities (MySQL)
├── model/mongodb/     # MongoDB documents
├── pattern/
│   ├── command/       # Command Pattern
│   ├── facade/        # Facade Pattern
│   ├── observer/      # Observer Pattern
│   ├── state/         # State Pattern
│   └── strategy/      # Strategy Pattern
├── repository/        # JPA repositories
├── repository/mongodb/ # MongoDB repositories
├── security/          # JWT authentication
└── service/           # Business logic services

src/main/resources/
├── application.yml    # Spring configuration
└── schema.sql        # MySQL database schema

src/test/             # Unit tests
frontend/             # React.js application
```

## Database Schema

### MySQL Tables
- **users**: User authentication, roles, profiles
- **point_wallets**: User point balances (optimistic locking enabled)
- **tutoring_sessions**: Request/session lifecycle
- **point_ledger**: Immutable transaction audit trail
- **escrow_locks**: Bounty escrow management (ACID compliant)
- **skill_tags**: User skill tags
- **skill_subscriptions**: Observer subscriptions
- **disputes**: Session dispute tracking
- **notifications**: User notifications

### MongoDB Collections
- **session_notes**: Session review/notes (topic coverage, progress, homework)
- **chat_logs**: Peer messaging during tutoring

## Key Features

### 1. Point Economy System
- Students post requests with bounty points
- Points locked in ACID-compliant escrow when request posted
- Tutor accepts request; session state transitions to IN_PROGRESS
- On completion: points released to tutor, deducted from student
- Immutable ledger records all transactions for audit trails

### 2. Session State Management (State Pattern)
```java
SessionState: OPEN -> IN_PROGRESS -> CLOSED (or DISPUTE)
```
- Enforced state transitions prevent invalid operations
- Easy to add new states without modifying existing code

### 3. Flexible Point Calculation (Strategy Pattern)
```java
// Use at runtime
context.calculatePoints(session, "FIXED_BOUNTY")  // Fixed bounty
context.calculatePoints(session, "TIME_BASED")    // Hourly rate
```

### 4. Transactional Point Transfers (Command Pattern)
```java
// Execute with undo support for disputes
TransferPointsCommand command = TransferPointsCommand.builder()
    .studentId(...)
    .tutorId(...)
    .amount(bountyPoints)
    .transferType(TransferType.LOCK_BOUNTY)
    .build();
    
commandInvoker.execute(command);  // Can be undone
commandInvoker.undo();             // Reverses transaction
```

### 5. Skill-Based Notifications (Observer Pattern)
- Students subscribe to skills they want to tutor
- When new request posted: system notifies all subscribed students
- Eliminates need for polling; event-driven architecture

### 6. Session Settlement Orchestration (Facade Pattern)
```java
// One method handles: state transition, point transfer, 
// MongoDB persistence, and notifications
settlementFacade.settleCompletedSession(sessionId, sessionNote);
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### Tutoring Sessions
- `POST /api/sessions` - Post new tutoring request
- `GET /api/sessions` - List all open requests
- `GET /api/sessions/skill/{skillTopic}` - Filter by skill
- `POST /api/sessions/{id}/accept` - Accept a request
- `POST /api/sessions/{id}/start` - Start session
- `POST /api/sessions/{id}/complete` - Complete session
- `POST /api/sessions/{id}/dispute` - Raise dispute

### Point Wallet
- `GET /api/wallet/me` - Get my wallet
- `GET /api/wallet/{userId}` - Get user wallet
- `GET /api/wallet/me/transactions` - Transaction history
- `POST /api/wallet/{userId}/add-points` - Add points (admin)

## Setup & Installation

### Prerequisites
- Java 17+
- MySQL 8.0+
- MongoDB 5.0+
- Node.js 16+ (for frontend)
- Maven 3.8+

### Backend Setup

1. **Install MySQL**
   ```bash
   # Create database
   mysql -u root -p < src/main/resources/schema.sql
   ```

2. **Configure application.yml**
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/tutoring_marketplace
       username: root
       password: root
     data:
       mongodb:
         uri: mongodb://localhost:27017/tutoring_marketplace
   ```

3. **Build and run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access Swagger UI**
   - Navigate to: `http://localhost:8080/api/swagger-ui.html`

### Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

## Testing

Run unit tests with JUnit 5 and Mockito:

```bash
mvn test
```

## Key Design Decisions

### ACID Compliance
- Optimistic locking on `point_wallets` table prevents race conditions
- `@Transactional` ensures atomicity of multi-step operations
- MongoDB for non-critical data (chat, notes) allows flexibility

### Immutable Audit Trail
- `point_ledger` table records ALL transactions
- Append-only; never updated or deleted
- Provides complete history for dispute resolution

### No External Payments
- Pure internal point system
- No integration with payment gateways
- All operations deterministic and auditable

### Pattern Usage Rationale
- **State Pattern**: Enforces valid session lifecycle transitions
- **Strategy Pattern**: Allows multiple point calculation methods without hardcoding
- **Command Pattern**: Enables undo/redo for dispute resolution
- **Observer Pattern**: Event-driven notifications for new requests
- **Facade Pattern**: Simplifies complex multi-step settlement process

## Production Checklist

- [ ] Change JWT secret in `application.yml`
- [ ] Configure MySQL backups
- [ ] Enable MongoDB replication
- [ ] Implement rate limiting on APIs
- [ ] Add request logging and monitoring
- [ ] Set up CI/CD pipeline
- [ ] Add comprehensive integration tests
- [ ] Document API in OpenAPI/Swagger
- [ ] Implement caching for frequently accessed data
- [ ] Add email notifications

## Future Enhancements

1. Real-time chat WebSocket integration
2. Video tutoring session support
3. Peer rating and review system
4. Skill verification badges
5. Marketplace analytics dashboard
6. Admin dispute resolution UI
7. Scheduled session booking
8. Cancellation policies with penalties

## License

Proprietary - All rights reserved

## Support

For issues and questions, contact the development team.
