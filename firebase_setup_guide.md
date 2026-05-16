# Complete Firebase Configuration Guide

Follow these steps to resolve the "Firebase not configured" error and get your backend running.

## 1. Create & Register your Firebase Project
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Click **Add Project** and give it a name (e.g., `YakshaganaLoka`).
3. Once the project is ready, click the **Android icon** to add an app.
4. **Register App**:
   - **Android package name**: `com.yakshaganaloka.app` (This MUST match exactly).
   - **App nickname**: `Yakshagana Loka`.
   - **Debug signing certificate SHA-1**: (Optional for now, but required for Google Sign-In later).
5. Click **Register App**.

## 2. Download and Place `google-services.json`
1. Download the `google-services.json` file provided by Firebase.
2. In Android Studio, change the project view from "Android" to **"Project"** (dropdown in the top left).
3. Navigate to `yakshagana-loka/app/`.
4. **Paste** the `google-services.json` file inside the `app/` folder.

## 3. Enable Authentication & Firestore
1. In the Firebase Console sidebar, go to **Build > Authentication**.
2. Click **Get Started**, then select **Email/Password** and enable it.
3. Go to **Build > Firestore Database** and click **Create database**.
4. Start in **Test Mode** (for development) and select a location near you.

## 4. Finalize Android Studio Setup
1. Open `app/build.gradle.kts`.
2. Find the `plugins` block and **uncomment** the line: `id("com.google.gms.google-services")`.
3. Click **"Sync Now"** in the top bar.

---

## Troubleshooting Common Errors

### "No matching client found for package name"
**Cause:** The package name in `google-services.json` does not match the `applicationId` in `app/build.gradle.kts`.
**Fix:** Ensure both are `com.yakshaganaloka.app`. If you change it, you must download a new `google-services.json`.

### "Default FirebaseApp is not initialized"
**Cause:** The `google-services` plugin is not applied or the JSON file is missing.
**Fix:** Apply the plugin in `app/build.gradle.kts` and ensure the file is in the `app/` folder.

### "SHA-1 missing"
**Cause:** Required for Google Sign-In or Dynamic Links.
**Fix:** In Android Studio, open the **Gradle** tab on the right, navigate to `app > Tasks > android > signingReport`. Copy the SHA-1 value and add it to your Project Settings in Firebase.
