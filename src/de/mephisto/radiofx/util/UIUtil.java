package de.mephisto.radiofx.util;

import de.mephisto.radiofx.MephistoRadioFX;
import de.mephisto.radiofx.ui.Footer;
import de.mephisto.radiofx.ui.Header;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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
   * Creates the initial state of the the UI.
   * @param borderPane
   */
  public static void createScene(BorderPane borderPane) {
    Group root = new Group();
    root.getChildren().add(borderPane);
    Stage primaryStage = MephistoRadioFX.getInstance().getStage();
    if(primaryStage.getScene() == null) {
      Scene scene = new Scene(root, UIUtil.WIDTH, UIUtil.HEIGHT, Color.valueOf("#DACEB8"));
      primaryStage.setScene(scene);
    }
    else {
      primaryStage.getScene().setRoot(root);
    }
  }

  /**
   * Creates a fade out effect without playing it
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
