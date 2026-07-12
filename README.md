# TPP — The Phone's Planet

Personal phone inventory & sales ledger. Built with **Flutter** + Firebase Firestore.
Runs on **Android** and **iOS** from one codebase.

## Firebase

- **Project:** The phone's planet (`the-phone-s-planet`)
- **Package / bundle:** `com.company.planet`
- **Collection:** `phones` (same schema as before — existing data works)

### Android

`android/app/google-services.json` is already in place.

### iOS

1. Firebase Console → add an **iOS** app with bundle id `com.company.planet`
2. Download `GoogleService-Info.plist` into `ios/Runner/`
3. Update `iosAppId` in `lib/firebase_options.dart` to the new iOS app id

## Setup

```bash
flutter pub get
flutter run
```

Open in Android Studio / VS Code / Xcode as needed. Requires Flutter 3.16+.

## Features

- Dashboard — inventory counts, profit split (Asif / Shozab), brand breakdown
- Add / Edit phone — live profit preview, ±500 amount steppers
- Inventory — search, brand & stock filters, preview sheet

## Firestore rules (personal use)

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

```bash
firebase deploy --only firestore
```
