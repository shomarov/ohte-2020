# DuplicatePhotoFinderAndRenamer

## Description

With this software users are able to scan for duplicate photos, batch rename photos, view and remove photo's metadata.

_created for the Helsinki University Ohjelmistotekniikka 2020 course_

## Documentation

How to use

[Software Requirements](https://github.com/shomarov/ohte-2020/blob/master/documentation/requirements.md)

Architecture Description

Test Documentation

[Timekeeping](https://github.com/shomarov/ohte-2020/blob/master/documentation/timekeeping.md)

## Releases

[Week 3](https://github.com/shomarov/ohte-2020/releases/tag/v0.3)

## Command-Line Operations

**_Run all commands from the project root folder_**

### Running

Run:

    mvn compile exec:java -Dexec.mainClass=duplicatefinder.Main

### Generating executable jar file

Command:

    mvn package

generates executable jar-file to folder target

### Running executable jar file

Run:

    java -jar target/DuplicatePhotoFinderAndRenamer-1.0-SNAPSHOT.jar

### Testing

Tests are run with command:

    mvn test

Test coverage report is created with command:

    mvn jacoco:report

You can view the report by opening target/site/jacoco/index.html using your favorite browser
