# Chatty
Chatty is an Android app designed as the final project for CS50x, providing users with a platform to connect through chat rooms. The app encompasses essential features such as authentication, real-time messaging, and seamless room management. Built with Jetpack Compose and following the MVVM architecture, Chatty delivers a modern and intuitive user experience.
<p style="text-align:center;"><img src="app\src\main\res\drawable-nodpi\chatty.png" width=100></p>
<p style="text-align:center;"><a href="https://www.freepik.com/free-vector/cute-bot-say-users-hello-chatbot-greets-online-consultation_4015765.htm#&position=0&from_view=author&uuid=cfb046d1-293f-4289-bde8-8e06133eb50f">Image by roserodionova</a> on Freepik</p>

## Key Features
- **Authentication**: Users can securely register and log in, ensuring a personalized and secure chat experience.

- **Room Management**: Create new chat rooms, join existing ones using unique IDs, delete rooms, and leave discussions at your convenience.

- **Real-time Messaging**: Experience dynamic conversations with real-time messaging capabilities, allowing users to send and receive messages instantly within joined chat rooms.

- **Modern UI**: Utilizing Jetpack Compose, Chatty boasts a contemporary user interface that is both visually appealing and user-friendly.

## How to run
- For a complete experience, ensure you have the [chatty-server](https://github.com/aqib-m31/chatty-server) running. Visit the Chatty-server [repository](https://github.com/aqib-m31/chatty-server), follow the instructions to start the server.
- Clone this [repository](https://github.com/aqib-m31/Chatty).
- Create an `env.properties` file in the project's root folder `Chatty`.
- Populate the file with the server URLs:
`SERVER_URL_DEBUG=http://xxx.xxx.xxx.xxx
SERVER_URL_RELEASE=http://xxx.xxx.xxx.xxx`
- Build and run Chatty on your Android device or emulator.

## Screenshots
<section style="text-align:center">
    <img src='screenshots/start.png?raw=true' alt='logo' width='250px' />
    <img src='screenshots/joined_rooms.png?raw=true' alt='joined_room' width='250px' />
    <img src='screenshots/chat.png?raw=true' alt='chat' width='250px' />
    <img src='screenshots/register.png?raw=true' alt='register' width='250px' />
    <img src='screenshots/login.png?raw=true' alt='login' width='250px' />
</section>