package de.mephisto.radiofx.util;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Caching of music cover images
 */
public class ImageCache {
  private final static Logger LOG = LoggerFactory.getLogger(ImageCache.class);

  private static final File IMAGE_CACHE_DIR = new File("./image_cache/");
  private static Map<String, File> imageCache = new HashMap<String, File>();

  static {
    final File[] files = IMAGE_CACHE_DIR.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".png");
      }
    });
    for (File file : files) {
      String id = file.getName().substring(0, file.getName().length() - 4);
      imageCache.put(id, file);
    }
  }


  public static Canvas createLazyLoadingImageCanvas(final String id, final String url, final int width, final int height) {
    final Canvas canvas = new Canvas(width, height);
    String imageUrl = url;
    try {
      if (imageCache.containsKey(id)) {
        File image = imageCache.get(id);
        imageUrl = image.toURI().toURL().toString();
      }
      else {
        imageUrl = imageUrl.replaceAll("s130", "s" + width); //scale to used size
        LOG.info("Caching " + imageUrl);
        URL imgUrl = new URL(imageUrl);
        BufferedImage image = ImageIO.read(imgUrl);
        File target = new File(IMAGE_CACHE_DIR, id + ".png");
        ImageIO.write(image, "png", target);
        LOG.info("Written " + target.getAbsolutePath() + " to cache, URL: " + imageUrl);
        imageCache.put(id, target);
        imageUrl = target.toURI().toURL().toString();

      }
    } catch (IOException e) {
      LOG.error("Error storing image to cache: " + e.getMessage());
    }

    ImageView img = new ImageView(new Image(imageUrl, width, height, false, true));
    final GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.drawImage(img.getImage(), 1, 1);
    gc.rect(0, 0, width, height);
    return canvas;
  }
}
