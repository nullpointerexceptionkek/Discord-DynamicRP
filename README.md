# **CDiscordRP**

CDiscordRP is a Discord Rich Presence Manager that allows you to display your current game in your Discord profile.
This program is written in Java 8 and kotlin 1.7.21 and uses the Discord IPC, JavaFX, and AnimatesFX libraries.


> I build this during my free time, so I can't guarantee that I will update this project frequently.

## Features

- Save multiple Rich Presence config
- Auto launch on start up
- Allowing different method of setting the Rich Presence
    - Callback: Sends Rich Presence data every x seconds according to your config
    - Auto Switch: this will automatically switch between the Rich Presence you have set, based on the application you
      are running
- Easy to use JavaFX GUI
- No need for installation, just run the .jar file
- Configs are automatically saved
- System tray support
- Implementation in all 3 operating systems (Windows, Macos, Linux), tested on Windows 10 and 11, Ubuntu 20.04
- More themes than you ever imagined

## Support

> | Operating System | Auto Launch On Start up         | Rich Presence  | Auto Switch |
>|------------------|---------------------------------|----------------|-------------|
> | Windows          | Supported/(tested on 10 and 11) | Supported      | Supported   |
> | Macos            | Not implemented                 | Not tested     | Not tested  |
> | Linux            | Not implemented                 | Supported      | Supported   |

macOS implementation should be the same as Linux, I haven't given a chance to test it on, but it should work.

## How to use

![img.png](DirecotryManager.png)

1. on your first start, you will be asked to select a directory where the program will store its data.
   This directory will be used to store the config Json file

> This data is stored in the environment variable "CDRPCDir"

![img.png](MainScreen.png)

2. Now after you selected a directory, you will see the main screen.
   the Application ID field is the field where you will enter your application ID.

> Discord Developer Portal: https://discord.com/developers/applications, you can create a new application and copy the
> application ID from there.

- Double-clicking an item on the list will open the edit screen, in this field you can edit the detail
  about the text you want to display.
  <br>
  You can start the callback by pressing the "Launch Callback" button.

![img.png](EditScreen.png)

- Delay: the delay between each callback in milliseconds, this is ignored if you only have one item in the list.
  Large image/Small Image: this can either be a image key on discord, or a direct link to an image.
  First Line/Second Line: the text that will be displayed on the first line and second line in the rich presence.

**Any field that is null or empty will be ignored**

> **ANY TEXT YOU ENTER HERE WILL BE DISPLAYED ON DISCORD, SO BE CAREFUL WHAT YOU ENTER, YOU MIGHT GET BANNED FOR
INAPPROPRIATE STUFF**
**I AM NOT RESPONSIBLE FOR ANYTHING YOU DO WITH THIS PROGRAM**

There is a lot more to explore!!! Have fun!

## Uninstalling

- This program is not installed on your computer, for uninstallation, you can just simply detele the .jar file

> Note that this will not delete your config folder, if you want a compelete uninstallation, you need to delete the
> config folder manually.

- Config folder location can be found in the environment variable "CDRPCDir"

> You can delete the enviroment variable on Windows by running the following command in cmd:
> ``` set CDRPCDir ""```

## Learn

What to learn how it works?
Check out the [LEARN.md](LEARN.md) file

## How to build

This project is made using Gradle on Intellij IDEA, so you can just clone the project and open it in Intellij IDEA.
For running with Gradle, simply run

```
gradlew run
```

and you can build using

```
gradle build
```

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
