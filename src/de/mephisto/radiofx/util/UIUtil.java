package de.mephisto.radiofx.util;

import de.mephisto.radiofx.ui.Footer;
import de.mephisto.radiofx.ui.Header;
import javafx.animation.FadeTransition;
import javafx.animation.FadeTransitionBuilder;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.apache.commons.configuration.Configuration;

/**
 * Common UI helpers.
 */
public class UIUtil {
  public static int WIDTH;
  public static int HEIGHT;


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
  }

  public static Canvas createImageCanvas(String url, int width, int height) {
    ImageView weatherImage = new ImageView(new Image(url, width, height, false, true));
    final Canvas canvas = new Canvas(width, height);
    final GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.drawImage(weatherImage.getImage(), 0, 0);
    return canvas;
  }

  /**
   * Hides a component via fade out.
   * @param root
   */
  public static void fadeOutComponent(Pane root) {
    final FadeTransition fadeTransition = FadeTransitionBuilder.create()
        .duration(Duration.seconds(1))
        .node(root)
        .fromValue(0)
        .toValue(1)
        .autoReverse(true)
        .build();

    fadeTransition.play();
  }

  /**
   * Hides a component via fade int.
   * @param root
   */
  public static void fadeInComponent(Pane root) {
    final FadeTransition fadeTransition = FadeTransitionBuilder.create()
        .duration(Duration.seconds(1))
        .node(root)
        .fromValue(0)
        .toValue(1)
        .autoReverse(true)
        .build();

    fadeTransition.play();
  }

}
