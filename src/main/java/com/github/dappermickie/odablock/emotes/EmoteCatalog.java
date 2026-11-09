package com.github.dappermickie.odablock.emotes;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.RuneLite;
import okhttp3.*;

@Singleton
public class EmoteCatalog
{
 private static final String BASE = "https://raw.githubusercontent.com/DapperMickie/odablock-sounds/emotes/";
 private static final Path CACHE = RuneLite.RUNELITE_DIR.toPath().resolve("odablock-emotes");
 @Inject private OkHttpClient http;
 private static class Manifest { int version; Emote[] emotes; }
 private String installed;
 synchronized void reset() { installed = null; }

 synchronized Emote[] refresh() throws IOException
 {
  Files.createDirectories(CACHE);
  String json;
  try { json = new String(download("emotes.json"), StandardCharsets.UTF_8); }
  catch (IOException failure)
  {
   if (!Files.exists(CACHE.resolve("emotes.json"))) throw failure;
   json = Files.readString(CACHE.resolve("emotes.json"));
  }
  if (json.equals(installed)) return null;
  Manifest manifest = new Gson().fromJson(json, Manifest.class);
  if (manifest == null || manifest.version < 1 || manifest.emotes == null || manifest.emotes.length == 0)
   throw new IOException("Invalid emote manifest");
  Path directory = CACHE.resolve(Integer.toString(manifest.version));
  Files.createDirectories(directory);
  for (Emote emote : manifest.emotes)
  {
   if (emote == null || emote.getTrigger() == null || emote.getTrigger().isEmpty() || emote.getAltTriggers() == null)
    throw new IOException("Invalid emote trigger");
   for (String file : emote.files())
   {
    if (file == null || !file.matches("[a-zA-Z0-9_-]+\\.png")) throw new IOException("Invalid emote filename");
    Path target = directory.resolve(file);
    if (!Files.exists(target))
    {
     byte[] bytes = download(file);
     if (javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(bytes)) == null) throw new IOException("Invalid image");
     Path temporary = Files.createTempFile(directory, "emote", ".tmp");
     try { Files.write(temporary, bytes); Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING); }
     finally { Files.deleteIfExists(temporary); }
    }
   }
   emote.prepare(directory);
  }
  Files.writeString(CACHE.resolve("emotes.json"), json);
  installed = json;
  return manifest.emotes;
 }
 private byte[] download(String file) throws IOException
 {
  try (Response response = http.newCall(new Request.Builder().url(BASE + file).build()).execute())
  {
   if (!response.isSuccessful() || response.body() == null) throw new IOException("Emote download failed: " + file);
   return response.body().bytes();
  }
 }
}
