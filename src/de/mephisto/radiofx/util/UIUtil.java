package de.mephisto.radiofx.util;

import de.mephisto.radiofx.MephistoRadioFX;
import de.mephisto.radiofx.resources.ResourceLoader;
import javafx.animation.FadeTransition;
import javafx.animation.FadeTransitionBuilder;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.configuration.Configuration;
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
 * Common UI helpers.
 */
public class UIUtil {
  private final static Logger LOG = LoggerFactory.getLogger(UIUtil.class);

  public static final Font FONT_NORMAL_12 = Font.font("Tahoma", FontWeight.NORMAL, 12);
  public static final Font FONT_BOLD_12 = Font.font("Tahoma", FontWeight.BOLD, 12);
  public static final Font FONT_NORMAL_14 = Font.font("Tahoma", FontWeight.NORMAL, 14);
  public static final Font FONT_BOLD_14 = Font.font("Tahoma", FontWeight.BOLD, 14);
  public static final Font FONT_NORMAL_16 = Font.font("Tahoma", FontWeight.NORMAL, 16);
  public static final Font FONT_BOLD_22 = Font.font("Tahoma", FontWeight.BOLD, 22);
  public static final Font FONT_NORMAL_60 = Font.font("Tahoma", FontWeight.NORMAL, 60);

  private final static File IMAGE_CACHE_DIR = new File("./image_cache/");
  private static Map<String, File> imageCache = new HashMap<String, File>();

  public static int WIDTH;
  public static int HEIGHT;
  public static int MIN_MAIN_HEIGHT;

  private static ExecutorService executor = Executors.newFixedThreadPool(3);


  public static String HEX_COLOR_DARK = "#333333";
  public static String HEX_COLOR_BACKGROUND = "#DACEB8";
  public static String HEX_COLOR_DARK_2 = "#464D4C";
  public static String HEX_COLOR_INACTIVE = "#C1B6A3";
  public static String HEX_COLOR_SEPARATOR = "#ABA18F";
  public static Color COLOR_DARK_HEADER = Color.valueOf(HEX_COLOR_DARK);

  static {
    final Configuration configuration = Config.getConfiguration("settings.properties");
    WIDTH = configuration.getInt("screen.width");
    HEIGHT = configuration.getInt("screen.height");

    MIN_MAIN_HEIGHT = HEIGHT - 82;

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

  public static Canvas createImageCanvas(String url, int width, int height) {
    ImageView img = new ImageView(new Image(url, width, height, false, true));
    final Canvas canvas = new Canvas(width, height);
    final GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.drawImage(img.getImage(), 0, 0);
    return canvas;
  }

  public static Canvas createLazyLoadingImageCanvas(final String id, final String url, final int width, final int height) {
    final Canvas canvas = new Canvas(width, height);
    executor.execute(new Runnable() {
      public void run() {
        String imageUrl = url;
        try {
          if (imageCache.containsKey(id)) {
            File image = imageCache.get(id);
            imageUrl = image.toURI().toURL().toString();
          }
          else {
            URL imgUrl = new URL(url);
            BufferedImage image = ImageIO.read(imgUrl);
            File target = new File(IMAGE_CACHE_DIR, id + ".png");
            ImageIO.write(image, "png", target);
            LOG.info("Written " + target.getAbsolutePath() + " to cache, URL: " + url);
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
      }
    });
    return canvas;
  }

  /**
   * Creates the initial state of the the UI.
   *
   * @param borderPane
   */
  public static void createScene(BorderPane borderPane) {
    Group root = new Group();
    root.getChildren().add(borderPane);
    Stage primaryStage = MephistoRadioFX.getInstance().getStage();
    if (primaryStage.getScene() == null) {
      Scene scene = new Scene(root, UIUtil.WIDTH, UIUtil.HEIGHT, Color.valueOf("#DACEB8"));
      scene.getStylesheets().add(ResourceLoader.getResource("theme.css"));
      primaryStage.setScene(scene);
    }
    else {
      primaryStage.getScene().setRoot(root);
    }
  }

  /**
   * Creates a fade out effect without playing it
   *
   * @param node
   * @return
   */
  public static FadeTransition createOutFader(Node node) {
    return FadeTransitionBuilder.create()
        .duration(Duration.millis(300))
        .node(node)
        .fromValue(1)
        .toValue(0)
        .autoReverse(false)
        .build();
  }

  /**
   * Hides a component via fade out.
   *
   * @param root
   */
  public static void fadeOutComponent(Node root) {
    final FadeTransition fadeTransition = FadeTransitionBuilder.create()
        .duration(Duration.millis(1))
        .node(root)
        .fromValue(0)
        .toValue(0.9)
        .autoReverse(false)
        .build();

    fadeTransition.play();
  }

  /**
   * Hides a component via fade int.
   *
   * @param root
   */
  public static void fadeInComponent(Node root) {
    final FadeTransition fadeTransition = FadeTransitionBuilder.create()
        .duration(Duration.millis(300))
        .node(root)
        .fromValue(0)
        .toValue(1)
        .autoReverse(false)
        .build();

    fadeTransition.play();
  }

}
