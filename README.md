# Firebase Authentication Demo - Android App

A Jetpack Compose Android app that demonstrates Firebase authentication with a Django backend.

## Quick Start

### Prerequisites
- Android Studio
- Firebase project with Authentication enabled
- Django backend running

### Setup

1. **Get Firebase credentials:**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Project Settings → General → Your Apps
   - Download `google-services.json`
   - Place in `android/app/google-services.json`

2. **Update Django server URL:**
   - Open `DjangoApiService.kt`
   - Change `BASE_URL` to your computer's IP address

3. **Build and run:**
   ```bash
   # Open in Android Studio
   # Sync Gradle files
   # Run on device/emulator
   ```

---

**Note:** This is a demonstration project. For production, implement proper security measures. 