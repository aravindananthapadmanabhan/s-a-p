# s-a-p
this is the siddhi android app. this will be used to create an android app which will help keep things organized. 
Divided into 4 Major Activities
1. Update
1. Audit
1. Search
1. Properties

## Build & run (from this repository)

I added a minimal Android project skeleton so you can work from Codespaces without Android Studio.

Requirements:
- Java JDK 11 or later installed in the environment.
- Gradle (or ability to create the Gradle wrapper). You can either use a system Gradle or generate the wrapper locally.

Quick steps:

1. Generate the Gradle wrapper locally (if you don't have it checked in):

```bash
# from repository root
gradle wrapper --gradle-version 8.4
```

2. Build the debug APK using the wrapper (preferred) or system gradle:

```bash
# use wrapper if present
./gradlew :app:assembleDebug
# or with system gradle
gradle :app:assembleDebug
```

3. Install to a connected device (requires adb):

```bash
./gradlew :app:installDebug
```

Notes:
- I added build files, an `app` module, a minimal `MainActivity`, manifest, and basic resource files.
- The Gradle wrapper JAR/binaries were intentionally not added. If you prefer, I can add the wrapper files so `./gradlew` works out-of-the-box.
- If you'd like to develop fully in Codespaces without Android Studio, consider using the Android SDK and adb in the container or use remote preview tools.

## Downloading the APK

After CI runs successfully the debug APK will be available in two places:

- Actions artifact: go to the repository page → Actions → select the run for "Android CI" → Artifacts → download `app-debug`.
- Release asset: the workflow also creates a short-lived release per CI run and uploads `app-debug.apk` as an asset — go to the repository page → Releases and find the latest CI build release.

Both provide the same debug APK which you can install on a device for testing. See above for installation instructions.


## Linting

This repository includes ktlint and detekt configurations on the `chore/add-linters` branch. To run them locally:

```bash
./gradlew ktlintCheck
./gradlew detekt
```

To auto-format Kotlin files with ktlint:

```bash
./gradlew ktlintFormat
```

These tools are provided on a separate branch and are not merged into `master` unless you decide to do so.


Divided into 4 Major Activities
1. Update
1. Audit
1. Search
1. Properties

## Object
- An object here represents any type of a thing. think about it as a physical object like a book or a abstract thing like a song , a png file in the computer .
- A container is a special type of abstract object which can contain other object 
- Every object may  hold other objects
- an object which is not within other object can be called as container.
- a container has to have a physical dimension and also a phyical address if it has physical things. else it can be virtual.
