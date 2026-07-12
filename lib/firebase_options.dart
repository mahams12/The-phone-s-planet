// File generated for Firebase project the-phone-s-planet.
// Android uses the existing google-services.json (com.company.planet).
// For iOS: add an iOS app in Firebase Console with bundle id com.company.planet,
// download GoogleService-Info.plist into ios/Runner/, then update iosAppId below.

import 'package:firebase_core/firebase_core.dart' show FirebaseOptions;
import 'package:flutter/foundation.dart'
    show defaultTargetPlatform, kIsWeb, TargetPlatform;

class DefaultFirebaseOptions {
  static FirebaseOptions get currentPlatform {
    if (kIsWeb) {
      throw UnsupportedError('Web is not configured for TPP.');
    }
    switch (defaultTargetPlatform) {
      case TargetPlatform.android:
        return android;
      case TargetPlatform.iOS:
        return ios;
      default:
        throw UnsupportedError(
          'DefaultFirebaseOptions are not supported for this platform.',
        );
    }
  }

  static const FirebaseOptions android = FirebaseOptions(
    apiKey: 'AIzaSyCKXkJ_SzwgxIQnp8qzaJCNK6aKMW9lxno',
    appId: '1:480148514109:android:a065247787bdcf6a4e5154',
    messagingSenderId: '480148514109',
    projectId: 'the-phone-s-planet',
    storageBucket: 'the-phone-s-planet.firebasestorage.app',
  );

  static const FirebaseOptions ios = FirebaseOptions(
    apiKey: 'AIzaSyABQ4VKLUEsQkt60meEgPUbo5stoc4AtgE',
    appId: '1:480148514109:ios:a920366227a7ae9e4e5154',
    messagingSenderId: '480148514109',
    projectId: 'the-phone-s-planet',
    storageBucket: 'the-phone-s-planet.firebasestorage.app',
    iosBundleId: 'com.ark.tpp',
  );
}
