POCKET LEDGER - ANDROID APP (build the APK in 5 steps)
1. Install Android Studio (free): developer.android.com/studio
2. File > Open > choose this PocketLedger folder. Wait for "Gradle sync" to finish (needs internet once).
3. Menu: Build > Build Bundle(s) / APK(s) > Build APK(s).
4. Click "locate" in the popup. The file is app-debug.apk.
5. Send app-debug.apk to your phone and tap it to install (allow "Install unknown apps" when asked).
The app screens live in app/src/main/assets/index.html.

NO ANDROID STUDIO? BUILD ONLINE (free):
1. Create a free account at github.com and make a new repository.
2. Upload ALL files of this folder (including the hidden .github folder).
3. Open the Actions tab > "Build APK" > wait ~5 minutes until it turns green.
4. Open that run > Artifacts > download PocketLedger-APK > unzip to get app-debug.apk.
