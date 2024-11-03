# Topic: Scrtly - Music Platform with Chat, Posts and Stories.

## Background of the Problem
Nowadays, more and more people use streaming platforms such as Spotify to discover new music and create personalized playlists. However, many users lack features that would allow for better interaction with other listeners and artists. Additionally, popular social media offer limited possibilities for integration with music content. There is a need for a solution that combines music streaming with social features, enabling interaction between users and artists in one place.

The music platform that we propose will allow users not only to listen to their favorite songs, but also communicate with each other via chat and share posts and stories in the "Discovery" tab. Thanks to this feature, users will be able to discover new music through recommendations from friends and artists, and artists will gain a new tool to promote their songs and build an engaged community.
## Challenge
Design and create a music platform that combines streaming of songs with social functions, such as chat and the ability to share posts and stories in the "Discovery" tab. The system must support music library management, playlist creation, communication between users and publishing multimedia content (posts, stories).
## Functional requirements
### User registration and management:
- Users can create an account, edit their profile (including adding profile photos, descriptions), manage their playlists and publish posts and stories..
- Artists can create dedicated profiles, share their songs.
### Music streaming and content management:
- Users can browse and listen to songs from a wide music library and create playlists.
- follow each other
### Social functions (chat and Discovery):
- Users can talk to each other using one-on-one chat.
- The “Discovery” tab allows users to browse posts and stories added by other users and artists, allowing for better discovery of new music and artists.
### Posting and Stories:
- Artists and users can add posts (e.g. reviews, recommendations) and stories with photos or promotional content.
- Ability to comment and react to posts and stories.
## Technology
The project will be implemented using Spring on the backend and React on the frontend. The system will use a PostgreSQL database in the Docker environment. Real-time communication, such as chat, will be implemented using WebSockets with the STOMP protocol.
## Summary
The music platform combines streaming with social features, allowing for more interactive discovery of new music and building relationships with other users and artists.

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
