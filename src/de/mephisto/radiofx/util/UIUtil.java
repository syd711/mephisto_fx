package de.mephisto.radiofx.util;

import de.mephisto.radiofx.ui.Header;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.apache.commons.configuration.Configuration;

/**
 * Common UI helpers.
 */
public class UIUtil {
  public static int WIDTH;
  public static int HEIGHT;

  public static String HEX_COLOR_DARK = "#333333";
  public static String HEX_COLOR_DARK_2 = "#464D4C";
  public static Color COLOR_DARK_HEADER = Color.valueOf(HEX_COLOR_DARK);

  static {
    final Configuration configuration = Config.getConfiguration("settings.properties");
    WIDTH = configuration.getInt("screen.width");
    HEIGHT = configuration.getInt("screen.height");
  }

  /**
   * Creates the default root panel.
   * @return
   */
  public static void showDefaultScene(BorderPane root) {
    new Header(root);
  }
}
