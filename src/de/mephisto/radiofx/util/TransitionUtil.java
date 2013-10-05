package de.mephisto.radiofx.util;

import de.mephisto.radiofx.MephistoRadioFX;
import de.mephisto.radiofx.resources.ResourceLoader;
import de.mephisto.radiofx.ui.SplashScreen;
import de.mephisto.radiofx.ui.UIState;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Common UI helpers.
 */
public class TransitionUtil {
  private final static Logger LOG = LoggerFactory.getLogger(TransitionUtil.class);

  public static int WIDTH;
  public static int HEIGHT;
  public static int MIN_MAIN_HEIGHT;

  static {
    final Configuration configuration = Config.getConfiguration("settings.properties");
    WIDTH = configuration.getInt("screen.width");
    HEIGHT = configuration.getInt("screen.height");

    MIN_MAIN_HEIGHT = HEIGHT - 82;
  }

  public static void moveNode(Node node, int from, int to, boolean reverse, long delay, EventHandler<ActionEvent> e, boolean moveX) {
    if(reverse) {
      int tmp = to;
      to = from;
      from = tmp;
    }
    TranslateTransition build = TranslateTransitionBuilder.create()
        .duration(Duration.millis(delay))
        .node(node)
        .fromX(from)
        .toX(to)
        .autoReverse(false)
        .build();
    if(!moveX) {
      build = TranslateTransitionBuilder.create()
          .duration(Duration.millis(delay))
          .node(node)
          .fromY(from)
          .toY(to)
          .autoReverse(false)
          .build();
    }
    if(e != null) {
      build.setOnFinished(e);
    }
    build.play();
  }

  /**
   * Moves the given node from x to y.
   * @param node
   * @param from
   * @param to
   * @param reverse
   * @param delay
   */
  public static void moveNodeX(Node node, int from, int to, boolean reverse, long delay) {
    moveNode(node, from, to, reverse, delay, null, true);
  }

  public static void moveNodeY(Node node, int from, int to, boolean reverse, long delay) {
    moveNode(node, from, to, reverse, delay, null, false);
  }

  /**
   * Creates an image canvas with the given widht and height.
   * @param url
   * @param width
   * @param height
   * @return
   */
  public static Canvas createImageCanvas(String url, int width, int height) {
    ImageView img = new ImageView(new Image(url, width, height, false, true));
    final Canvas canvas = new Canvas(width, height);
    final GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.drawImage(img.getImage(), 0, 0);
    return canvas;
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
   * Creates a fade out effect without playing it
   *
   * @param node
   * @return
   */
  public static FadeTransition createBlink(Node node) {
    return FadeTransitionBuilder.create()
        .duration(Duration.millis(400))
        .node(node)
        .fromValue(0.1)
        .cycleCount(Timeline.INDEFINITE)
        .toValue(1)
        .autoReverse(true)
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
