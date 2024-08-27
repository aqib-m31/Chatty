#!/bin/bash

# Update and install Gradle
sudo apt-get update && sudo apt-get upgrade -y
sudo apt-get install -y gradle

# Set up Android SDK
cd ~
mkdir android-sdk
cd android-sdk
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip commandlinetools-linux-11076708_latest.zip
rm commandlinetools-linux-11076708_latest.zip

# Move contents to cmdline-tools/latest
mkdir -p cmdline-tools/latest
mv cmdline-tools/* cmdline-tools/latest/ 2>/dev/null || true

# Export path
echo 'export PATH="$PATH:/home/codespace/android-sdk/cmdline-tools/latest/bin"' >> ~/.bashrc
source ~/.bashrc

# Install SDK components
yes | sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
yes | sdkmanager --licenses

# Set up local.properties
cd /workspaces/Chatty
echo "sdk.dir=/home/codespace/android-sdk" > local.properties

# Create keystore.properties
cat <<EOF > keystore.properties
STORE_FILE=/workspaces/Chatty/chatty.keystore
STORE_PASSWORD=your_keystore_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
EOF

# Create env.properties
cat <<EOF > env.properties
SERVER_URL_DEBUG=http://xxx.xxx.xxx.xxx
SERVER_URL_RELEASE=https://xxx.xxx.xxx.xxx
EOF

# Create dummy your-keystore-file.keystore
touch your-keystore-file.keystore