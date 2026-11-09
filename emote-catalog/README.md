# Emote catalog

This directory supplies the root of the independent `emotes` branch.
The plugin checks emotes.json at startup and every five minutes.
Add a PNG and an entry with trigger, file, optional overheadFile, and altTriggers.
The oda prefix is added automatically to word triggers and aliases.
Increment version whenever changing images. Upload images before publishing the manifest.
Clients retain the previous working mapping if an update fails, and cache downloads under RuneLite/odablock-emotes.
