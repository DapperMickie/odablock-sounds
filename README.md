# Odablock Plugin [![Plugin Installs](https://img.shields.io/endpoint?url=https://api.runelite.net/pluginhub/shields/installs/plugin/odablock)](https://runelite.net/plugin-hub/DapperMickie) [![Plugin Rank](https://img.shields.io/endpoint?url=https://api.runelite.net/pluginhub/shields/rank/plugin/odablock)](https://runelite.net/plugin-hub/show/odablock)

##### A plugin for [RuneLite](https://runelite.net/)

Odablock announces when you complete an achievement!

Huge thanks to [Odablock](https://kick.com/odablock) for providing custom recorded audio for this plugin!

Some `actions` might have multiple sounds, whenever there are multiple sounds, the sound being played will be chosen at random.
___
## General Troubleshooting
BEFORE TRYING ANYTHING ELSE, ENABLE THIS IN THE **RUNESCAPE** SETTINGS

![image](https://user-images.githubusercontent.com/62370532/208992085-e2c07494-d8bb-489e-b7f3-ed538175acbc.png)

Whenever this does not resolve your issue, please feel free to look in the [Issues](https://github.com/DapperMickie/odablock-sounds/issues) section of this GitHub page to see if anyone else had this issue.
___

## Customizing your sounds

### Warning

Because we have a system in place that automatically updates the sounds for this plugin, it is highly recommended to have a backup folder of your custom sounds. The system __will always__ override all the sounds whenever a sound update comes out. This means that after each sound update, you will have to replace all your custom sounds again.

### 1. Locate your `.runelite` folder

On Windows this is likely to be here: `C:\Users\<your username>\.runelite`

If you aren't sure, it's the same place that stores your `settings.properties`

Within this `.runelite` folder, there should be a `odablock-sounds` folder, which is where the sound files are downloaded to

### 2. Prepare your sound files

Make sure your files are all `.wav` format (just changing the extension won't work, actually convert them)

Make sure the file name __exactly__ matches the name of the existing file (in `odablock-sounds` folder) you want to replace

### 3. Understand how the files are handled

If you replace an existing file in `odablock-sounds` using exactly the same file name, your sound will be loaded instead

If you place a new file with an unexpected file name in `odablock-sounds`, it will be deleted

If you place a new folder inside `odablock-sounds` that is unexpected, this should be left as is, so can be used to store multiple sounds that you may want to swap in at a future date

If you want to revert to a default sound file, simply delete the relevant file in `odablock-sounds` and the default file will be re-downloaded when the plugin next starts

### 4. If it fails to play your sound

Remove your sound and make sure it plays the default sound for that event - if not, there is something misconfigured in your plugin _or in-game_ settings. For example, the collection log event can only be captured if your _in-game_ notifications for collection log slots are turned on

Check that your file is actually a valid `.wav` and not just a renamed `.mp3` or similar

Check that the file is still there in the `odablock-sounds` folder, if you accidentally used an incorrect file name, it won't have been loaded, and will have been deleted

### 5. Resetting all sounds

You can reset all the sounds by deleting the `odablock-sounds` folder and then reloading your client.
___

## Other information

### Currently implemented sounds include

You can find all the sound files [here](https://github.com/DapperMickie/odablock-sounds/tree/sounds-v2) and all the code [here](https://github.com/DapperMickie/odablock-sounds/tree/master/src/main/java/com/github/dappermickie/odablock/sounds).

### Systems

We have implemented a few systems to support all of these features. 

#### Sound system

First and foremost we have implemented a sound system that consists of a [sound engine](https://github.com/DapperMickie/odablock-sounds/blob/master/src/main/java/com/github/dappermickie/odablock/SoundEngine.java) and a [sound file manager](https://github.com/DapperMickie/odablock-sounds/blob/master/src/main/java/com/github/dappermickie/odablock/SoundFileManager.java) to play all the sounds.

Sounds are downloaded to the local file system instead of being 'baked in' to the plugin build, allowing for further
expansion in the future while also 'supporting' user-swapped sounds for pre-existing events/actions (please refer to the warning section of `Customising your sounds`).

#### Snowball system

The snowball system consists of a [snowball user manager](https://github.com/DapperMickie/odablock-sounds/blob/master/src/main/java/com/github/dappermickie/odablock/SnowballUserManager.java) this manager downloads/updates the list of users that are allowed to snowball people (and have the sound play). We have chosen to not make this list editable on your end.

#### Player kill system

Because the OSRS team adds new player kill lines from time to time, we've chosen to add a system to update the possible player kill lines without having to push a new plugin. This system uses the [Player kill line manager](https://github.com/DapperMickie/odablock-sounds/blob/master/src/main/java/com/github/dappermickie/odablock/PlayerKillLineManager.java). This manager downloads/updates a [list of possible kill lines](https://github.com/DapperMickie/odablock-sounds/blob/playerkillpatterns/pklines.txt). This system is then used in the `Killing player` sound to determine whether or not you killed someone.


### Planned / Work In Progress expansions

- none

### Potential future expansions

- none at this moment

### Known Issues

PulseAudio on Linux can just refuse to accept the audio formats used despite claiming to accept them.
