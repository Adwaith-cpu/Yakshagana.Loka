# Android App Troubleshooting Guide (Startup Crashes)

If your app is opening and closing immediately, follow these steps to identify and fix the issue.

## 1. Using Logcat to Find the Error
Logcat is your most powerful tool in Android Studio.
1. Connect your device or start the emulator.
2. At the bottom of Android Studio, click on **Logcat**.
3. In the search bar, type `FATAL` or `MainActivity`.
4. Look for a stack trace (red text). It will usually say `Caused by: ...` followed by a line number in your code.
5. **Click the link** in the log to jump to the exact line causing the crash.

## 2. Common Startup Crash Causes

### A. Missing Firebase Configuration
**Symptoms:** `IllegalStateException: Default FirebaseApp is not initialized` or app crashes when calling `FirebaseAuth.getInstance()`.
**Fix:** 
- Add `google-services.json` to the `app/` folder.
- In `app/build.gradle.kts`, ensure `id("com.google.gms.google-services")` is active.

### B. SplashScreen Initialization
**Symptoms:** `java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity.`
**Fix:** Ensure `installSplashScreen()` is called **before** `super.onCreate(savedInstanceState)` in `MainActivity.kt`.

### C. Missing Internet Permission
**Symptoms:** App crashes when attempting to fetch data from Firestore or login.
**Fix:** Ensure `<uses-permission android:name="android.permission.INTERNET" />` is in `AndroidManifest.xml`.

### D. Google Maps API Key
**Symptoms:** Map is blank or app crashes if API key is missing/invalid.
**Fix:** Add your key to `AndroidManifest.xml` under `<meta-data android:name="com.google.android.geo.API_KEY" ... />`.

## 3. Emulator & Device Issues
- **Old Emulator:** If using an old emulator, it might not support the latest Google Play Services required for Firebase/Maps. Use an emulator with "Google Play Store" icon.
- **Hardware Acceleration:** Ensure your computer's BIOS has Virtualization enabled (VT-x/AMD-V) for smooth emulator performance.

## 4. Pro-Tips for Production
- **Wrap Entry Points:** Always wrap `setContent` or heavy initializations in `try-catch` with logging during development.
- **Check Network:** Always check if the internet is available before making network calls.
- **Null Safety:** Use Kotlin's null safety (`?.`, `?:`) when dealing with external SDKs like Firebase.
