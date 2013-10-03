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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
public class UIUtil {
  private final static Logger LOG = LoggerFactory.getLogger(UIUtil.class);

  public static final Font FONT_NORMAL_12 = Font.font("Tahoma", FontWeight.NORMAL, 12);
  public static final Font FONT_NORMAL_10 = Font.font("Tahoma", FontWeight.NORMAL, 10);
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
            imageUrl = imageUrl.replaceAll("s130", "s100"); //scale to used size
            URL imgUrl = new URL(imageUrl);
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
      Scene scene = new Scene(root, UIUtil.WIDTH, UIUtil.HEIGHT, Color.valueOf(HEX_COLOR_BACKGROUND));
      scene.getStylesheets().add(ResourceLoader.getResource("theme.css"));
    }
    else {
      primaryStage.getScene().setRoot(root);
    }
  }



  public static SplashScreen createSplashScene() {
    Group root = new Group();
    VBox vbox = new VBox(20);
    vbox.setAlignment(Pos.CENTER);
    root.getChildren().add(vbox);
    Canvas logoCanvas = createImageCanvas(ResourceLoader.getResource("logo.png"), WIDTH, 150);
    vbox.getChildren().add(logoCanvas);

    double y = WIDTH-50;
    ProgressBar loadingBar = new ProgressBar();
    loadingBar.setMinWidth(WIDTH - 100);
    loadingBar.setStyle("-fx-accent: " + HEX_COLOR_DARK_2 + ";");
    loadingBar.setLayoutY(y);
    vbox.getChildren().add(loadingBar);

    Text loadingMsg = new Text("Initializing...");
    loadingMsg.setFont(FONT_NORMAL_14);
    vbox.getChildren().add(loadingMsg);

    try {
      HBox ipBox = new HBox();
      ipBox.setPadding(new Insets(17, 5, 5, 0));
      ipBox.setAlignment(Pos.BASELINE_RIGHT);
      Text ip = new Text("IP: " + Inet4Address.getLocalHost().getHostAddress());
      ip.setFont(FONT_NORMAL_10);
      ipBox.getChildren().add(ip);
      vbox.getChildren().add(ipBox);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }


    Stage primaryStage = MephistoRadioFX.getInstance().getStage();
    Scene scene = new Scene(root, UIUtil.WIDTH, UIUtil.HEIGHT, Color.valueOf(HEX_COLOR_BACKGROUND));
    primaryStage.setScene(scene);

    SplashScreen sp = new SplashScreen(vbox, loadingBar, loadingMsg);
    return sp;
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
  public static FadeTransition createInFader(Node node) {
    return FadeTransitionBuilder.create()
        .duration(Duration.millis(300))
        .node(node)
        .fromValue(0)
        .toValue(1)
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

  public static void addStateListener(Stage primaryStage) {
    final UIState state = new UIState();

    primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.RIGHT) {
          Platform.runLater(new Runnable() {
            @Override
            public void run() {
              state.right();
            }
          });

        }
        if (keyEvent.getCode() == KeyCode.DOWN) {
          Platform.runLater(new Runnable() {
            @Override public void run() {
              state.push();
            }
          });
        }
        if (keyEvent.getCode() == KeyCode.UP) {
          Platform.runLater(new Runnable() {
            @Override public void run() {
              state.longPush();
            }
          });
        }
        if (keyEvent.getCode() == KeyCode.LEFT) {
          Platform.runLater(new Runnable() {
            @Override public void run() {
              state.left();
            }
          });
        }
      }
    });
  }

  public static void addDisposeListener(Stage primaryStage) {
    //ensures that the process is terminated on window dispose
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent windowEvent) {
        Platform.exit();
        System.exit(0);
      }
    });
  }
}
