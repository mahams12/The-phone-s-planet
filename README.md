# TPP — The Phone's Planet

Personal Android inventory & sales ledger app. Built with Jetpack Compose and Firebase Firestore.

## Firebase project

- **Project:** The phone's planet (`the-phone-s-planet`)
- **Package:** `com.company.planet`

## Setup

1. Open the project in **Android Studio**
2. In [Firebase Console](https://console.firebase.google.com/project/the-phone-s-planet), create **Firestore Database** if you haven't yet
3. Sync Gradle and run on your phone (API 26+)

`google-services.json` is already in `app/`.

### Firestore rules (personal use)

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /phones/{phoneId} {
      allow read, write: if true;
    }
  }
}
```

Deploy from project root after `firebase login`:

```bash
firebase deploy --only firestore
```

Or paste the rules in Firebase Console → Firestore → Rules.

## Run

```bash
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Features

- Single-page app — opens straight to your inventory
- TPP logo branding
- Phone inventory with sales & profit tracking
- Real-time Firestore sync
- Responsive on all phone sizes
