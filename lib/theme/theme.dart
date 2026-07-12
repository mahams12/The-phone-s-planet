import 'package:flutter/material.dart';

import 'colors.dart';

const String kFontInter = 'Inter';
const String kFontMono = 'JetBrainsMono';

ThemeData buildTppTheme() {
  final base = ThemeData(
    useMaterial3: true,
    brightness: Brightness.dark,
    fontFamily: kFontInter,
    scaffoldBackgroundColor: TppColors.bg,
    colorScheme: const ColorScheme.dark(
      primary: TppColors.accent,
      onPrimary: TppColors.accentOn,
      surface: TppColors.bgPanel,
      onSurface: TppColors.textPrimary,
      error: TppColors.danger,
    ),
  );

  return base.copyWith(
    textTheme: base.textTheme.apply(
      fontFamily: kFontInter,
      bodyColor: TppColors.textPrimary,
      displayColor: TppColors.textPrimary,
    ),
    appBarTheme: const AppBarTheme(
      backgroundColor: Colors.transparent,
      elevation: 0,
      foregroundColor: TppColors.textPrimary,
    ),
    inputDecorationTheme: InputDecorationTheme(
      filled: true,
      fillColor: TppColors.bgElevated,
      hintStyle: const TextStyle(color: TppColors.muted),
      labelStyle: const TextStyle(color: TppColors.textSecondary),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: TppColors.line),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: TppColors.line),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: TppColors.accent, width: 1.4),
      ),
      contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
    ),
    snackBarTheme: const SnackBarThemeData(
      backgroundColor: TppColors.bgElevated,
      contentTextStyle:
          TextStyle(fontFamily: kFontInter, color: TppColors.textPrimary),
      behavior: SnackBarBehavior.floating,
    ),
  );
}

TextStyle moneyStyle({
  double size = 14,
  FontWeight weight = FontWeight.w600,
  Color color = TppColors.textPrimary,
}) {
  return TextStyle(
    fontFamily: kFontMono,
    fontSize: size,
    fontWeight: weight,
    color: color,
  );
}
