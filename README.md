# Project: Re-Lint-less Java Linter

## Contributors
Jett Strack, Charlie Kalousek, Ellen Tomlins

## Dependencies
- Java Version 11
- EXTERNAL LIBRARIES: ASM v9.2

## Build Instructions
Use Gradle build task to compile and run the default example test cases.
To run with your own directory use:
./gradlew run --args="<class-name|directory>"

Remember that the linter can only analyze compiled directories

**For example**
./gradlew run --args=build/classes/java/main/examples


