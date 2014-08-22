Android Auth Client SDK
=======================

The Auth SDK requires API level 10 or greater.

Building the SDK
----------------

You can build this project directly from the command line using Gradle or in Android Studio.

The library depends on the following libraries:

 * Google Android Application Compatibility (com.android.support:appcompat)
 * Google GSON - should be in the Maven Central repository
 * Google Http Client Jackson2 - should be in Maven Central repository
 * Google OAuth Client - should be in Maven Central repository

To load this project in Android Studio, you will need to select "Import Project" and select the `build.gradle` file in
the project's base directory.

To build the project from the command line, run the command `./gradlew clean assemble`.  If you have a device connected
to your computer then you can also run the unit test suite with the command `./gradlew connectedCheck`.
