App Store Listing — TPP: The Phone's Planet
Copy-paste ready content for App Store Connect. Every field below is within Apple's character limits.


App Name (max 30 chars)
TPP — The Phone's Planet


Subtitle (max 30 chars)
Phone stock & profit tracker


Alternative (exactly 30 chars):
Phone inventory & sales ledger


Promotional Text (max 170 chars)
Track every phone you buy and sell — stock, prices, profit splits and budget. A clean, fast ledger made for phone resellers. No ads, no spreadsheets.


Description (max 4000 chars)
TPP — The Phone's Planet is a clean, fast inventory and sales ledger built for people who buy and sell phones. Ditch the notebook and the spreadsheet: log a device in seconds and always know exactly where your money is.

TRACK EVERY PHONE
• Record brand, model, storage, colour and condition details
• Save IMEI numbers and PTA approval status
• Log battery health for used devices
• Mark phones as in stock or sold, with purchase and sale dates

KNOW YOUR PROFIT
• Enter purchase and sale prices and see profit calculated instantly
• Live profit preview while you type — before you even save
• Track commissions and partner profit splits automatically
• Clear warnings when a deal would result in a loss

DASHBOARD AT A GLANCE
• Inventory counts: total, in stock and sold
• Total money and total profit, updated in real time
• Brand breakdown so you know what sells

BUDGET TRACKING
• Set your buying budget and track withdrawals
• Always know how much you can spend on the next deal

FAST AND FOCUSED
• Beautiful dark interface designed for quick daily use
• Powerful search plus brand and stock filters
• Quick amount steppers for fast price entry
• Cloud sync keeps your ledger safe if you change devices

PRIVATE BY DESIGN
• No account or sign-up required
• No ads, no analytics trackers, no clutter
• Only the business data you enter is stored

Whether you flip a few phones a month or run a busy shop counter, TPP keeps your stock, sales and profit organised — all from your pocket.


Keywords (max 100 chars, comma-separated) — 93 characters
phone,inventory,reseller,ledger,stock,profit,imei,mobile shop,sales tracker,budget,pta,resale


URLs
Site is live on Cloudflare Workers at the URLs below.

Marketing URL: https://legal.thephonesplanet.workers.dev/
Support URL: https://legal.thephonesplanet.workers.dev/#support
Privacy Policy URL: https://legal.thephonesplanet.workers.dev/privacy
Terms of Use (EULA): https://legal.thephonesplanet.workers.dev/terms


Other listing fields
Category: Business (secondary: Utilities)
Age Rating: 4+ (no objectionable content in the questionnaire)
Copyright: © 2026 The Phone's Planet
Price: Free
App Privacy (nutrition labels): In the App Privacy questionnaire select "Data is collected", then User Content (the inventory/ledger data stored in Firestore), used only for App Functionality, not linked to the user's identity, and not used for tracking. Nothing else is collected — no analytics, no ads, no identifiers used for tracking.


Everything Else Needed Before Submission (checklist)

Accounts & identifiers
- Apple Developer Program membership ($99/year) on your friend's account
- Register the bundle ID com.company.planet in the developer portal — strongly consider changing it to something unique like com.thephonesplanet.app before first release (it can never be changed after publishing). If you change it, also add the new iOS app in Firebase.
- Create the app record in App Store Connect (name, primary language, SKU)

Firebase (from the README — still pending)
- Firebase Console → add an iOS app with the final bundle ID
- Download GoogleService-Info.plist into ios/Runner/
- Update iosAppId in lib/firebase_options.dart
- Tighten Firestore rules — current rules allow anyone to read/write (allow read, write: if true). Fine for personal use, but for a public App Store app anyone could read/wipe your ledger. At minimum, add Firebase Anonymous Auth and scope data per user.

Build & assets
- App icon 1024×1024 (already configured via flutter_launcher_icons — just make sure assets/icon/app_icon.png is final, no alpha for iOS)
- Screenshots: required for 6.9" iPhone (1320×2868) — one set covers all sizes now. Take them from the Simulator (iPhone 16 Pro Max): Dashboard, Inventory, Add Phone, Preview sheet, Budget. 3–10 images.
- Set the display name in ios/Runner/Info.plist (CFBundleDisplayName) — keep it short for the home screen, e.g. TPP
- Bump version in pubspec.yaml if needed (currently 1.0.0+1)
- Release build: flutter build ipa then upload via Xcode Organizer or Transporter (needs a Mac with Xcode + signing certificates)

App Store Connect submission form
- Promotional text, description, keywords, subtitle (above)
- Marketing / Support / Privacy Policy URLs (above, after Cloudflare deploy)
- App Privacy questionnaire (guidance above)
- Age rating questionnaire
- App Review contact info (name, phone, email)
- Sign-in info for review: select "Sign-in not required" (the app has no login)
- Export compliance: the app only uses standard HTTPS encryption → answer "Yes, uses encryption" + "exempt" (standard/HTTPS exemption)
- Notes for the reviewer (optional but helpful): explain it's an inventory ledger for phone resellers; no account needed

Website
- Website is live in its own repo (tpp-website) on Cloudflare Workers
- Support email set to mehihamza1@gmail.com on all pages (done)
- Add the real App Store link to the download button in index.html once the app is live
