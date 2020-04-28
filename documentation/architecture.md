# Architecture Description

## Structure

The software's structure follows the basic three 3-tier architecture type and its package structure is following:

[![Packages](packages.png)](https://github.com/shomarov/ohte-2020/blob/master/documentation/packages.png)

Package duplicatefinder.ui contains user interface implemented in JavaFX, duplicatefinder.domain contains the application logic and finally duplicatefinder.dao is responsible for reading from and writing data to the file system and database.

[![Architecture](architecture.png)](https://github.com/shomarov/ohte-2020/blob/master/documentation/architecture.png)

## User Interface

User interface consists of two views. First view (not yet implemented) is a login/register view, the second view is the main application view, which includes all the functions.

Each of those views are built as an independent Scene objects. The views are not viewabale simultaneously, as they are placed in the application's JavaFX Stage class one at a time.

UI is completely isolated from the application logic, it only calls appropriate methods of the MediaFileService and FilenameRenamer interfaces.

## Application Logic

At the moment the main parts of the application logic are classes:

### DirectoryInfo

Class that encapsulates information on the directory tree structure and the files they contain. Basically it is used to store the structure of the directory a user opens including subfolders. The UI then uses a DirectoryInfo object to populate the directory tree in the main view. Here is a demonstration of the process with the sequence diagram:

[![Sequence diagram](sequence_diagram.png)](https://github.com/shomarov/ohte-2020/blob/master/documentation/sequence_diagram.png)

### PhotoFileInfo

Class that encapsulates all essential information and metadata of a photo file. How it works:

1. The user scans a directory for photo files.
2. The PhotoFileService class then uses DirectoryDao to scan folder for files of type photo (provided DirectoryDao uses PhotoFileDao as MediaFileDao) and adds them to the DirectoryInfo object as a List.
3. DirectoryInfo now has all the fileinfos of the scanned folder
4. UI is able to show them with details

## Data Persistence

Data persistence layer is encapsulated to duplicatefinder.dao package. It uses a DAO factory pattern to some extent.

### _Interfaces_

#### _MediaFileDao_

Interface to be used for interaction with different types of media files (image, video, pdf, etc...).

#### _UserDao_

Interface to be user for interaction with user account login data.

### Classes

#### DirectoryDao

Class which does not use the DAO factory pattern. It is a stand-alone class that deals with directories and file system.

#### PhotoFileDao

Implements MediaFileDao, class offers methods used for interaction with files of image type.
