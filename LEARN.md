## DynamicRP

> There is java documentation inside the source code, you can read it to understand the code better.

Concepts:

1. Use the Discord IPC to connect to Discord and send Rich Presence data.
2. Storing configs in a Json file.
3. Using JavaFX to create a GUI.

Libraries used:
> Note: These libraries might have dependencies that are not listed here.

1. Discord IPC - send Rich Presence data to Discord
2. JavaFX - create a GUI
3. AnimatesFX - create animations
4. Gson - convert Java objects to Json and vice versa, aka storing configs in a Json file.

## How does it work?

I will explain this according to the feature listed in the README.md file.

### Save multiple Rich Presence config

How does this program finds the config folder?

- It finds the config folder by looking at the environment variable "DynamicRP".
- If the environment variable is not set, it will ask you to select a directory where the program will store its data.
- This directory will be used to store the config Json file
  You can see the detail of the implementation in
  the [DirectoryManager.kt](src/main/kotlin/lee/aspect/dev/dynamicrp/manager/DirectoryManager.kt)

The function writeDirectoryEnvironmentVar() is the implementation to write the environment variable in different OS.

How does this program store the config?

- There is a few important files
    1. Settings.json - this file stores the config for the program.
    2. Switch.json - config for the auto switch feature.
    3. default_UpdateScript.json

The settings.json is the file that stores the primary config for the program, such as the languages.
The rich presence config is stored in the following name format: yourconfig_UpdateScript.json, this file includes the
detail of the rich presence, Ex: application ID.
The Switch.json stores an array that includes the config file and application to check for.

You can see the detail of implementation in\
[ConfigManager.kt](src/main/kotlin/lee/aspect/dev/dynamicrp/manager/ConfigManager.kt) - manage configs\
[Adapter and Loader](src/main/java/lee/aspect/dev/dynamicrp/json/loader) - saves the config

### Auto launch on start up

How does this program auto launch on start up?

- This program creates a .bat file in the default windows start up folder to auto launch the program on start up.

I create a video on the implementation of this feature: [here](https://www.youtube.com/watch?v=juQjEHAI_Uw&t=468s)\
You can see the detail of the implementation
in [StartLaunch.java](src/main/java/lee/aspect/dev/dynamicrp/util/system/StartLaunch.java)

### GUI

This application uses pretty much what a typical JavaFX application uses.
It uses both FXML and plain code to create the GUI.

- FXML is used to create static GUI, such as the settings page.
- Code is used to create dynamic GUI that changes depending on the amount of data, such as the config manager.

### Application tray

There isn't really much to say about this, uses Java awt to create a tray.
implementation is
in [ApplicationTray.java](src/main/java/lee/aspect/dev/dynamicrp/application/core/ApplicationTray.java)

