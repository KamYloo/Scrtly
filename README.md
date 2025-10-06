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
![image1](https://github.com/user-attachments/assets/fc80c89e-cbae-4fb7-a72d-b614a38c9278)
![image2](https://github.com/user-attachments/assets/0500c33d-bf36-4f96-ae29-96f69b850849)
![image3](https://github.com/user-attachments/assets/87a48966-f63d-4352-bc9a-c3447c4b2cb7)
![image4](https://github.com/user-attachments/assets/14cc2f14-1e90-4be1-a108-373673505789)
![image5](https://github.com/user-attachments/assets/ddc1fa2c-da17-43e3-b261-c53238286933)
![image6](https://github.com/user-attachments/assets/d9105059-b81b-4261-8379-66f34d5e143e)
![image7](https://github.com/user-attachments/assets/185b8b1e-a236-4c9d-bd06-b63365f803d5)



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
