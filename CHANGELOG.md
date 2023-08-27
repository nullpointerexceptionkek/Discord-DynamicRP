## Change Log

### 1.0.1
- Fixed #28
- improved the update screen when launching callback

### 1.0.0
Major improvement:
- Recode the entire UI/UX with dynamic theme support
- Added animation
- If multiple instance is detected, the program will now show up the old instance instead of giving a warning, might not work on macOS and Linux

Minor improvement:
- Remove the need to restart the program when changing the theme and language

Major Bug fix:
- Fixed config manager deleting the wrong config
- Fixed auto switch screen is not updated correctly
- Fixed the application pop up on the wrong place

Minor Bug fix:
- Way to many to list

**Switched to gradle**

### 0.5.0 beta
New features:
- Added Auto Switch
  The auto switch allows automatic switching between different configs depending on which software is open on your computer,
  for example, you can display a rich preference when CAD software opens up and a different one when another software opens.
  This is a pre-release, and some exception handlers are not implemented 
  AutoSwitch also came with a new center interface; you can enable this via the settings.
  The left text will be your config; enter the process name to check on the right.
  Case matters in the field; for example, if you want to check the notepad process on windows, you should enter "Notepad.exe"
  It's not recommended to enter the process name with its ending instead of Notepad.exe. You should enter "Notepad," or in some cases, just the word "pad" is enough. a stricter name should be only used with a duplicate occurs


### v0.4.0 Zirconium
- Added config manager

### 0.3.0 Zinc
New Features:
- You can now customize the config directory with the user of the environment variables
- Added language manager. You can now select between Chinese and English
- You can now customize your timestamp in the config menu
- You can now change the update loop mode
- There will now be warnings if a callback fails to launch
- Added checks to prevent from even attempting to send a packet if the data is invalid
- You will now be given the option to select not to show again
- Added a shutdown hook and a warning message to display if an uncaught exception occurs

Bug fixes:
- Fixed sometimes that the callback thread acts weird
- Fixed some callback mode that does not work
- Fixed disable animation will cause crashes
- Fixed sometimes random null pointer exception is thrown

Linux support are not often tested and might result in the program being unstable
macOS support are not tested at all at this point

### 0.1.1 Sodium
**Major improvement:**
- Added System Tray
- Added animated preview
  **Major Bug fix:**
- fix sometimes sending dupe packet
  **Minor Bug fix:**
- fix sometimes the GUI not loaded correctly

**switched to maven for better dependency management**
### 0.0.0
- Initial created as [CDiscordRP](https://github.com/nullpointerexceptionkek/FxGui)
- Simple GUI for Rich Presence
- Able to set Rich Presence for one application in an array format.