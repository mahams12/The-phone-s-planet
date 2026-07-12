import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';

import 'firebase_options.dart';
import 'screens/home_screen.dart';
import 'state/phone_controller.dart';
import 'theme/colors.dart';
import 'theme/theme.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);
  SystemChrome.setSystemUIOverlayStyle(
    const SystemUiOverlayStyle(
      statusBarColor: Colors.transparent,
      statusBarIconBrightness: Brightness.light,
      systemNavigationBarColor: TppColors.bg,
      systemNavigationBarIconBrightness: Brightness.light,
    ),
  );
  runApp(const TppApp());
}

class TppApp extends StatelessWidget {
  const TppApp({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) => PhoneController(),
      child: MaterialApp(
        title: 'TPP — The Phone\'s Planet',
        debugShowCheckedModeBanner: false,
        theme: buildTppTheme(),
        home: const HomeScreen(),
      ),
    );
  }
}
