# Yakshagana Loka 🎭

![GHBanner](https://github.com/user-attachments/assets/0aa67016-6eaf-458a-adb2-6e31a0763ed6)

**The Digital Stage of Coastal Art**

Yakshagana Loka is a modern Android application dedicated to the traditional folk theatre of coastal Karnataka. It serves as a comprehensive platform for fans to track live performances, discover artists, and stay connected with the vibrant world of Yakshagana.

---

## ✨ Features

- **📍 Real-time Performance Map**: Track live and upcoming Yakshagana performances (Melas) across the region with Google Maps integration.
- **👤 Artist Encyclopedia**: Explore a detailed directory of Yakshagana artists, including Bhagavathas, actors, and Himmela players.
- **🔥 Happening Now**: Stay updated with real-time alerts on live events.
- **📱 Modern UI**: A beautiful, fluid interface built entirely with Jetpack Compose and Material 3.
- **🔐 Secure Access**: User authentication supporting Email/Password and Google Sign-In.
- **⭐ Personalized Experience**: Save your favorite artists and manage your profile.
- **📻 Integrated Media**: Future support for Yakshagana radio and performance history.

---

## 🛠️ Technology Stack

### **Core**
- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
- **Async Programming**: Kotlin Coroutines & Flow

### **Backend & Services**
- **Firebase**: 
  - **Firestore**: Real-time NoSQL Database
  - **Auth**: Secure Authentication
  - **Storage**: Media asset hosting
- **Local Storage**: DataStore Preferences

### **Libraries & Integrations**
- **Maps**: Google Maps SDK & Maps Compose
- **Image Loading**: Coil
- **Animations**: Lottie & Compose Animations
- **Media**: Media3 (ExoPlayer) & Android YouTube Player
- **Visuals**: Compose Shimmer for loading states

---

## 🚀 Getting Started

### **Prerequisites**
- Android Studio Iguana (or newer)
- JDK 17
- Android SDK 24+

### **Setup**
1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/yakshagana-loka.git
   ```
2. **Firebase Setup**:
   - Create a project in the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android app with package name `com.yakshaganaloka.app`.
   - Download `google-services.json` and place it in the `app/` directory.
   - Enable Firestore, Authentication, and Storage.
3. **Maps API Key**:
   - Obtain a Google Maps API Key from [Google Cloud Console](https://console.cloud.google.com/).
   - Add it to your `local.properties` file: `MAPS_API_KEY=YOUR_KEY_HERE`.
4. **Build**:
   - Sync the project with Gradle files.
   - Run the app on an emulator or physical device.

---

## 📂 Project Structure
```
app/src/main/java/com/yakshaganaloka/app/
├── di/             # Hilt modules
├── models/         # Data classes
├── repository/     # Data sources (Firestore, Storage)
├── ui/
│   ├── components/ # Reusable UI elements
│   ├── screens/    # Full-screen composables
│   ├── theme/      # Typography, Color, and Shape definitions
│   └── viewmodels/ # State management logic
└── navigation/     # App routing logic
```

---

## 🤝 Contributing
Contributions are welcome! If you have suggestions for new features or find bugs, please feel free to open an issue or submit a pull request.

---

## 📜 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

Made with ❤️ for the Art of Yakshagana
