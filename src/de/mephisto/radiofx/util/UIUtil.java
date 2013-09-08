package de.mephisto.radiofx.util;

import de.mephisto.radiofx.MephistoRadioFX;
import de.mephisto.radiofx.ui.Header;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.commons.configuration.Configuration;

/**
 * Common UI helpers.
 */
public class UIUtil {
  public static int WIDTH;
  public static int HEIGHT;


  static {
    final Configuration configuration = Config.getConfiguration("settings.properties");
    WIDTH = configuration.getInt("screen.width");
    HEIGHT = configuration.getInt("screen.height");
  }

  /**
   * Creates the default root panel.
   * @return
   */
  public static Scene showDefaultScene() {
    Stage primaryStage = MephistoRadioFX.getInstance().getStage();
    Group root = new Group();
    if(primaryStage.getScene() == null) {
      Scene scene = new Scene(root, UIUtil.WIDTH, UIUtil.HEIGHT, Color.valueOf("#DACEB8"));
      primaryStage.setScene(scene);
    }
    else {
      primaryStage.getScene().setRoot(root);
    }

    Header header = new Header(root);

    return primaryStage.getScene();
  }
}
