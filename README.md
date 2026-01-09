# Topic: Scrtly - Music Platform with Chat, Posts and Stories.

## Background of the Problem
Today, more and more users are using music streaming services to discover new songs and create personalized playlists. However, existing solutions often lack satisfactory integration of social features that would enable direct interaction between listeners and creators within a single service. As a result, the need to combine high-quality playback with communication and music promotion methods has been identified as a significant product challenge.

The music platform that we propose will allow users not only to listen to their favorite songs, but also communicate with each other via chat and share posts and stories in the "Discovery" tab. Thanks to this feature, users will be able to discover new music through recommendations from friends and artists, and artists will gain a new tool to promote their songs and build an engaged community.
## Project Goal and Challenge
The project's goal is to design and implement a music platform that combines music streaming with social features—specifically, two-way chat and the ability to publish posts and stories in the "Discover" tab. The system will enable music library management, playlist creation and sharing, user-to-user communication, and the publishing of multimedia content in a user-friendly and secure manner.
## Functional Requirements (Short)
- Registration and management of user and artist accounts; artist profiles will be enriched with discography and statistics sections.
- Browse the catalog, search, and play adaptively, with the ability to create and share playlists.
- Social features: publish posts, comments, reactions, and share stories in the dedicated "Discover" tab.
- Real-time communication: one-on-one chat with multimedia messaging support.
- Monetization mechanisms: premium subscriptions processed through the Stripe payment gateway; access to the highest-quality audio will be limited to subscribers.
- Recommendation system: quick recommendations based on monthly trends (trending artists, songs, albums) and social signals.

## Technologies used (in brief)
The backend will be implemented using the Spring platform (Spring Boot), while the user interface will be implemented in React technology. Real-time communication will be implemented using WebSockets (STOMP protocol), and data persistence will be ensured by a PostgreSQL relational database. The RabbitMQ broker will be used to handle asynchronous tasks and queuing, while fast aggregates and caches will be maintained in Redis. FFmpeg will be used to prepare HLS (HTTP Live Streaming) stream variants. Payment mechanisms will be integrated with the Stripe platform.

## Deployment Architecture and Infrastructure
The application will be deployed on a virtual server (VPS) and run as a set of Docker containers. The production configuration includes seven containers, each representing a PostgreSQL database server, RabbitMQ broker, Redis storage, a backend container, a frontend container, the certbot service for managing TLS certificates, and the Watchtower tool for supporting container image updates. Media content (HLS segments and static assets) will be distributed using the Cloudflare content delivery network (CDN), which will offload the origin (VPS) and reduce playback startup times. A reverse proxy (e.g., nginx) will be configured to terminate TLS, handle API requests, and redirect WebSocket connections.

## Recommendation and Monetization Mechanisms

The recommendation system will be designed with a two-stage model: fast, aggregated recommendations based on monthly trending items (global artist, track, and album rankings) will be generated in batch mode and cached in Redis; personalized suggestions will be prepared in addition to this mechanism using signals related to listens, likes, and social interactions. Payments and subscriptions will be handled by Stripe.

## Summary
The developed platform will combine high-quality streaming functionality with extensive social features, enabling more interactive music discovery and deepening the relationship between artists and audiences. Deployment in a containerized environment on a VPS using Cloudflare as a CDN and predefined components (PostgreSQL, RabbitMQ, Redis, backend, frontend, certbot, Watchtower) will achieve the required levels of performance, availability, and security. Recommendation mechanisms and integration with Stripe will form the basis for the development of a subscription-based business model and creator promotion.

## Sample Screenshots
<img width="1919" height="1079" alt="image" src="https://github.com/user-attachments/assets/b329d92b-3b3b-4396-8b5d-1fe2403b385d" />
<img width="1917" height="909" alt="image" src="https://github.com/user-attachments/assets/d286f533-898a-40a7-8d24-3d8d43d769ed" />
<img width="1919" height="909" alt="image" src="https://github.com/user-attachments/assets/5e7f89d8-8c85-4c21-b61c-922424a9bad1" />
<img width="1919" height="907" alt="image" src="https://github.com/user-attachments/assets/8adcd5f5-ecf8-47d2-9454-fccf6dfcfc83" />
<img width="1918" height="908" alt="image" src="https://github.com/user-attachments/assets/cc7ce6b3-df65-412c-8063-4dab8e4fdcad" />
<img width="1916" height="908" alt="image" src="https://github.com/user-attachments/assets/a99ae1c5-7570-437c-8d55-fe795773acde" />
<img width="1918" height="908" alt="image" src="https://github.com/user-attachments/assets/e168b4ad-234b-44fc-853d-2aa0a9007d76" />
<img width="1918" height="912" alt="image" src="https://github.com/user-attachments/assets/feabd30c-0da5-47e5-9e51-1523ab96d2db" />
<img width="1918" height="904" alt="image" src="https://github.com/user-attachments/assets/2522cb42-93e3-48ed-adc6-1481e17b97f5" />
<img width="1918" height="910" alt="image" src="https://github.com/user-attachments/assets/f20502b7-d38b-4d39-8380-5aa89d9bb754" />

## How to run the backend
### Install Maven dependencies:
- In the terminal in the project directory run the command:
```
mvn clean install
```
### Run the Spring Boot application:
- In the terminal, run the command:
```
mvn spring-boot:run
```
- make sure you have jdk 19 installed
## How to run the Frontend
### Install node Modules:
- In the terminal in the project directory run the command:
```
npm install
```
### Run the React application:
- In the terminal, run the command:
```
npm run dev
```

### Env File 
```
POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=

RABBIT_USER=
RABBIT_PASS=
SPRING_RABBITMQ_HOST=
SPRING_RABBITMQ_PORT=
SPRING_RABBITMQ_STOMP_PORT=
RABBIT_VHOST=

REDIS_PASSWORD=

SERVER_PORT=
SECRET_KEY=

APPLICATION_FILE_CDN=

VITE_APP_BACKEND_URL=

MAIL_USERNAME=
MAIL_PASSWORD=

MAILING_BACKEND_ACTIVATION_URL=
MAILING_BACKEND_RESET_PASSWORD_URL=
MAILING_BACKEND_ARTIST_VERIFY_URL=
MAILING_FRONTEND_REDIRECT_URL=

CERTBOT_EMAIL=
CERTBOT_DOMAIN1=
CERTBOT_DOMAIN2=
CERTBOT_DOMAIN3=

DOCKER_HUB_USERNAME=
DOCKER_HUB_ACCESS_TOKEN=

GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=

OAUTH2_BACKEND_REDIRECT_URL=

STRIPE_API_KEY=
STRIPE_WEBHOOK_SECRET=
STRIPE_PRICE_ID_MONTHLY=
VITE_STRIPE_PUBLIC_KEY=

```
