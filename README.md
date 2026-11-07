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

## Customizing Your Sounds

### The `custom` Folder

The easiest way to add your own sounds is through the **`custom`** folder. This folder is located at:

```
C:\Users\<your username>\.runelite\odablock-sounds\custom\
```

This folder is **never overwritten** during plugin updates — your custom sounds are always safe.

### 1. Add your sound files

Drop any `.wav` files into the `custom` folder. That's it!

> **Important:** Files must be actual `.wav` format — just renaming a `.mp3` to `.wav` won't work. Use a converter if needed.

### 2. Name your files

Use `_` (underscores) or `-` (dashes) to separate words in your file names. The plugin will automatically turn them into clean labels in the UI.

| File Name | Displays As |
|---|---|
| `my_death_sound.wav` | My Death Sound |
| `epic-ags-spec.wav` | Epic AGS Spec |
| `BigHit.wav` | Big Hit |

### 3. Assign sounds via the Override Picker

Once your files are in the `custom` folder, open the **Sound Overrides** panel in the plugin sidebar. Your custom sounds will appear as options for **any** sound action — just check the ones you want to use.

### Troubleshooting

- **Sound doesn't play?** Make sure the event plays the default sound first. If it doesn't, the issue is in our plugin or your _in-game_ settings (e.g., collection log notifications must be enabled in-game).
- **File not showing in the picker?** Confirm it's a valid `.wav` file and is inside the `custom` folder. Restart the client if needed.
- **Want to reset everything?** Delete the `odablock-sounds` folder and reload your client. All defaults will re-download and a fresh `custom` folder will be created.

### Music player tracks

Enable **Odablock Warriors** and/or **Terry** under **Music & Emotes** to add separate
entries at the bottom of the in-game music player. Use **Play** or **Stop** on each
entry. Selecting a normal game track stops the plugin song; all original tracks,
including **7th Realm**, remain available. Disabling a track removes its entry.

Terry - Odablock X @ACRAZEOFFICIAL uses `terry/Terry.wav` on the `sounds-v2` branch.
Both songs use the existing sound downloader; Terry will be available once that file is uploaded.
Both songs use the plugin's announcement volume.

### Legacy Custom Folders

If you previously used the per-sound-directory `custom` subfolders (e.g., `odablock-sounds/death/custom/`), those still work. However, we recommend moving your files into the single root `custom` folder for simplicity — it's easier to manage and will never be deleted.
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
