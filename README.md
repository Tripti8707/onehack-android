# OneHack-Android
The official OneHack Android app. Originally developed for MHacks 6 at The University of Michigan, Ann Arbor.

# Building
The project has been built with Android Studio, and consequently uses the Gradle build system.
If you have used Gradle before, great! If you haven't, you might want to take a look at some guides online.

To build the project for debug, you should only need to import it into Android studio. You may also build it from the command line, using:

`$ ./gradlew clean assembleDebug`

If you plan on building this project for release, you'll need to add a `signingConfig` block to app/build.gradle with your keys. This will depend on how you plan on signing it.

**You may need to clean your project and sync Gradle before your first run.**

`Build > Clean project`

# Credits
Special thanks to the developers & maintainers of the following:
- [OkHttp](http://square.github.io/okhttp/)
- [Retrofit](http://square.github.io/retrofit/)
- [MaterialDrawer](http://mikepenz.github.io/MaterialDrawer/)
- [FloatingActionButton](https://github.com/futuresimple/android-floating-action-button)
