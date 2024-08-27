# Chatty
Chatty is an Android app designed as the final project for CS50x, providing users with a platform to connect through chat rooms. The app encompasses essential features such as authentication, real-time messaging, and seamless room management. Built with Jetpack Compose and following the MVVM architecture, Chatty delivers a modern and intuitive user experience.

[Project Demo](https://youtu.be/PWJT77xy67c?si=JzBYxVbDB3jAFkF-)
<p style="text-align:center;" align="center"><img src="app\src\main\res\drawable-nodpi\chatty.png" width=100></p>
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


> [!IMPORTANT]
> In `Chatty/app/src/main/AndroidManifest.xml`
> - Set `android:usesCleartextTraffic="true"` to use **HTTP** in  **local development**.
> - Set `android:usesCleartextTraffic="false"` to use **HTTPS** after you've deployed [chatty-server](https://github.com/aqib-m31/chatty-server) to ensure secure communication.


## Screenshots
<section style="text-align:center" align="center">
    <img src='screenshots/start.png?raw=true' alt='logo' width='250px' />
    <img src='screenshots/joined_rooms.png?raw=true' alt='joined_room' width='250px' />
    <img src='screenshots/chat.png?raw=true' alt='chat' width='250px' />
    <img src='screenshots/register.png?raw=true' alt='register' width='250px' />
    <img src='screenshots/login.png?raw=true' alt='login' width='250px' />
</section>


## Setting Up for Release Builds in GitHub Codespaces

To generate a signed APK for the Chatty app, you'll need to replace the following placeholders:

### 1. Adding the Keystore
- The `your-keystore-name.keystore` file will be automatically created in the root of the project by the Codespaces setup. You need to replace it with your **keystore file**. This keystore file will be used for signing the APK.

### 2. `keystore.properties` File
The `keystore.properties` file will be automatically created in the root of the project by the Codespaces setup. You need to replace the placeholders in the file with your actual keystore information:

```properties
STORE_FILE=/workspaces/Chatty/your-keystore-name.keystore
STORE_PASSWORD=your_keystore_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

### 3. `env.properties` File
Ensure the `env.properties` file in the root of the project is populated with the correct server URLs. Replace the placeholders with the actual URLs for your environments:

```properties
SERVER_URL_DEBUG=http://xxx.xxx.xxx.xxx
SERVER_URL_RELEASE=https://xxx.xxx.xxx.xxx
```

### 4. Building the APK
To generate the APK, use the following commands from the project root:

```bash
./gradlew assembleDebug  # For the debug APK
./gradlew assembleRelease  # For the release APK
```

> [!NOTE]
> In case of any issues, check out the steps here to debug: [Chatty | Wiki](https://github.com/aqib-m31/Chatty/wiki)
