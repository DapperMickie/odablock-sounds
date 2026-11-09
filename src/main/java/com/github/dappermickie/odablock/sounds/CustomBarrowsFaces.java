package com.github.dappermickie.odablock.sounds;

import com.github.dappermickie.odablock.OdablockConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.GameObject;
import net.runelite.api.Player;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.gameval.ObjectID;
import net.runelite.client.RuneLite;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

// Based on code/ideas from the VancedBarrows plugin
// (https://github.com/codyduong/VancedBarrows)
@Singleton
@Slf4j
public class CustomBarrowsFaces {
	public enum BarrowsBrothers {
		AHRIM("Ahrim"),
		DHAROK("Dharok"),
		GUTHAN("Guthan"),
		KARIL("Karil"),
		TORAG("Torag"),
		VERAC("Verac");

		private final String name;

		BarrowsBrothers(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private static final int CRYPT_REGION_ID = 14231;
	private static final int CRYPT_OBJECT_SCAN_RADIUS = 20;

	private static final int IMAGE_SIZE_RESIZABLE = 256;
	private static final int IMAGE_SIZE_FIXED = 192;
	private static final float ALPHA_PEAK_RESIZABLE = 0.65f;
	private static final float ALPHA_FADE_RESIZABLE = 0.325f;
	private static final float ALPHA_PEAK_FIXED = 0.5f;
	private static final float ALPHA_FADE_FIXED = 0.25f;

	@Inject
	private Client client;

	@Inject
	private OdablockConfig config;

	@Inject
	private CustomBarrowsFacesOverlay overlay;

	private volatile List<BufferedImage> randomFaceImages = Collections.emptyList();
	private volatile BufferedImage dharokFaceImage;
	private Widget hiddenFaceWidget;
	private int animationTick = -1;

	public void reloadImages() {
		randomFaceImages = Collections.emptyList();
		dharokFaceImage = null;

		File odafacesDir = new File(RuneLite.RUNELITE_DIR, "odablock-sounds" + File.separator + "odafaces");
		if (!odafacesDir.exists() || !odafacesDir.isDirectory()) {
			return;
		}

		// Load torvseta_face.png
		List<BufferedImage> loadedImages = new ArrayList<>();
		File torvsetaFile = new File(odafacesDir, "torvesta_face.png");
		if (!torvsetaFile.exists()) {
			torvsetaFile = new File(odafacesDir, "torvseta_face.png");
		}
		if (torvsetaFile.exists()) {
			try {
				dharokFaceImage = ImageIO.read(torvsetaFile);
			} catch (IOException e) {
				log.error("Failed to load torvseta_face.png", e);
			}
		}

		// Load oda_face_x.png and custom_face_x.png
		File[] imageFiles = odafacesDir.listFiles((dir, name) -> {
			String lowerName = name.toLowerCase(Locale.ROOT);
			return lowerName.startsWith("oda_face_") || lowerName.startsWith("custom_face_");
		});

		if (imageFiles != null) {
			for (File imageFile : imageFiles) {
				try {
					BufferedImage img = ImageIO.read(imageFile);
					if (img != null) {
						loadedImages.add(img);
					}
				} catch (IOException e) {
					log.error("Failed to load image: {}", imageFile.getName(), e);
				}
			}
		}
		randomFaceImages = Collections.unmodifiableList(loadedImages);
	}

	public void onGameTick() {
		Widget faceWidget = client.getWidget(24, 1); // Barrows faces widget
		if (faceWidget == null) {
			animationTick = -1;
			overlay.setVisible(false);
			return;
		}

		// If a jumpscare occurs (widget is not hidden) and we haven't started animating
		if (!faceWidget.isHidden() && animationTick == -1) {
			BarrowsBrothers brother = getBarrowsBrotherForCurrentLocation();
			if (brother != null && isCustomFaceEnabledFor(brother)) {
				List<BufferedImage> images = randomFaceImages;
				BufferedImage imgToRender = null;
				if (brother == BarrowsBrothers.DHAROK) {
					imgToRender = dharokFaceImage;
				} else if (!images.isEmpty()) {
					int idx = ThreadLocalRandom.current().nextInt(images.size());
					imgToRender = images.get(idx);
				}

				if (imgToRender != null) {
					faceWidget.setHidden(true);
					hiddenFaceWidget = faceWidget;
					final boolean isFixedMode = !client.isResized();
					final int imageSize = isFixedMode ? IMAGE_SIZE_FIXED : IMAGE_SIZE_RESIZABLE;

					overlay.setImage(imgToRender);
					overlay.setOverlayLocation(getRandomOnScreenLocation(imageSize, imageSize));
					overlay.setSize(imageSize, imageSize);
					overlay.setAlpha(0.0f);
					overlay.setVisible(true);

					animationTick = 0;
				}
			}
		}

		if (animationTick >= 0) {
			// Make sure we keep hiding the vanilla widget
			faceWidget.setHidden(true);

			final boolean isFixedMode = !client.isResized();
			float alpha;
			switch (animationTick) {
				case 0:
					alpha = isFixedMode ? ALPHA_FADE_FIXED : ALPHA_FADE_RESIZABLE;
					break;
				case 1:
				case 2:
					alpha = isFixedMode ? ALPHA_PEAK_FIXED : ALPHA_PEAK_RESIZABLE;
					break;
				case 3:
					alpha = isFixedMode ? ALPHA_FADE_FIXED : ALPHA_FADE_RESIZABLE;
					break;
				case 4:
					alpha = 0.0f;
					break;
				default:
					overlay.setVisible(false);
					animationTick = -1;
					return;
			}
			overlay.setAlpha(alpha);
			animationTick++;
		}
	}

	public void resetAnimation() {
		overlay.setVisible(false);
		if (animationTick >= 0 && hiddenFaceWidget != null
			&& hiddenFaceWidget == client.getWidget(24, 1)) {
			hiddenFaceWidget.setHidden(false);
		}
		hiddenFaceWidget = null;
		animationTick = -1;
	}

	public void shutDown() {
		resetAnimation();
		randomFaceImages = Collections.emptyList();
		dharokFaceImage = null;
	}

	private boolean isCustomFaceEnabledFor(BarrowsBrothers brother) {
		switch (brother) {
			case AHRIM:
				return config.customBarrowsAhrim();
			case DHAROK:
				return config.customBarrowsDharok();
			case GUTHAN:
				return config.customBarrowsGuthan();
			case KARIL:
				return config.customBarrowsKaril();
			case TORAG:
				return config.customBarrowsTorag();
			case VERAC:
				return config.customBarrowsVerac();
			default:
				return false;
		}
	}

	private BarrowsBrothers getBarrowsBrotherForCurrentLocation() {
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null) {
			return null;
		}

		WorldPoint location = localPlayer.getWorldLocation();
		int regionId = location.getRegionID();

		if (regionId == CRYPT_REGION_ID) {
			if (hasNearbyObject(ObjectID.BARROWS_STAIRS_AHRIM) || hasNearbyObject(ObjectID.BARROW_AHRIM_SARCOPHAGUS)) {
				return BarrowsBrothers.AHRIM;
			}
			if (hasNearbyObject(ObjectID.BARROWS_STAIRS_DHAROK)
					|| hasNearbyObject(ObjectID.BARROW_DHAROK_SARCOPHAGUS)) {
				return BarrowsBrothers.DHAROK;
			}
			if (hasNearbyObject(ObjectID.BARROWS_STAIRS_GUTHAN)
					|| hasNearbyObject(ObjectID.BARROW_GUTHAN_SARCOPHAGUS)) {
				return BarrowsBrothers.GUTHAN;
			}
			if (hasNearbyObject(ObjectID.BARROWS_STAIRS_KARIL) || hasNearbyObject(ObjectID.BARROW_KARIL_SARCOPHAGUS)) {
				return BarrowsBrothers.KARIL;
			}
			if (hasNearbyObject(ObjectID.BARROWS_STAIRS_TORAG) || hasNearbyObject(ObjectID.BARROW_TORAG_SARCOPHAGUS)) {
				return BarrowsBrothers.TORAG;
			}
			if (hasNearbyObject(ObjectID.BARROWS_STAIRS_VERAC) || hasNearbyObject(ObjectID.BARROW_VERAC_SARCOPHAGUS)) {
				return BarrowsBrothers.VERAC;
			}

			// Tunnels brother
			int tunnelBrotherVal = client.getVarbitValue(457);
			switch (tunnelBrotherVal) {
				case 0:
					return BarrowsBrothers.AHRIM;
				case 1:
					return BarrowsBrothers.DHAROK;
				case 2:
					return BarrowsBrothers.GUTHAN;
				case 3:
					return BarrowsBrothers.KARIL;
				case 4:
					return BarrowsBrothers.TORAG;
				case 5:
					return BarrowsBrothers.VERAC;
			}
		}

		return null;
	}

	private boolean hasNearbyObject(int objectId) {
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null) {
			return false;
		}

		LocalPoint localPoint = localPlayer.getLocalLocation();
		if (localPoint == null) {
			return false;
		}

		Scene scene = client.getScene();
		Tile[][][] tiles = scene.getTiles();
		int plane = client.getPlane();
		int baseX = localPoint.getSceneX();
		int baseY = localPoint.getSceneY();

		for (int dx = -CRYPT_OBJECT_SCAN_RADIUS; dx <= CRYPT_OBJECT_SCAN_RADIUS; dx++) {
			for (int dy = -CRYPT_OBJECT_SCAN_RADIUS; dy <= CRYPT_OBJECT_SCAN_RADIUS; dy++) {
				int sceneX = baseX + dx;
				int sceneY = baseY + dy;

				if (sceneX < 0 || sceneY < 0 || sceneX >= Constants.SCENE_SIZE || sceneY >= Constants.SCENE_SIZE) {
					continue;
				}

				Tile tile = tiles[plane][sceneX][sceneY];
				if (tile == null) {
					continue;
				}

				for (GameObject gameObject : tile.getGameObjects()) {
					if (gameObject != null && gameObject.getId() == objectId) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private Point getRandomOnScreenLocation(int imageWidth, int imageHeight) {
		int canvasWidth = client.getCanvasWidth();
		int canvasHeight = client.getCanvasHeight();

		int maxX = canvasWidth - imageWidth;
		int maxY = canvasHeight - imageHeight;

		int x = ThreadLocalRandom.current().nextInt(0, Math.max(1, maxX));
		int y = ThreadLocalRandom.current().nextInt(0, Math.max(1, maxY));

		return new Point(x, y);
	}
}
