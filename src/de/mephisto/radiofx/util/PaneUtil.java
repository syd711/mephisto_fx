package de.mephisto.radiofx.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.commons.configuration.Configuration;

/**
 * Helper class for creating panes and nodes
 */
public class PaneUtil {

  public static double WIDTH;
  public static double HEIGHT;
  public static double MIN_MAIN_HEIGHT;

  static {
    final Configuration configuration = Config.getConfiguration("settings.properties");
    WIDTH = configuration.getInt("screen.width");
    HEIGHT = configuration.getInt("screen.height");

    MIN_MAIN_HEIGHT = HEIGHT - 100;
  }

  public static HBox createHBox(int margin, Pos alignment, String style, Insets padding) {
    HBox box = new HBox(margin);
    if(style != null) {
      box.setStyle(style);
    }

    if(alignment != null) {
      box.setAlignment(alignment);
    }
    box.setPadding(padding);
    return box;
  }

  public static VBox createVBox(int margin, Pos alignment, String style, Insets padding) {
    VBox box = new VBox(margin);
    box.setStyle(style);
    if(alignment != null) {
      box.setAlignment(alignment);
    }
    box.setPadding(padding);
    return box;
  }

  public static HBox createHBox(int margin, String style, Insets padding) {
    return createHBox(margin, null, style, padding);
  }

  public static Text createText(String value, Font font) {
    Text text = new Text(0, 0, value);
    text.setFont(font);
    text.setFill(Colors.COLOR_DARK_HEADER);
    return text;
  }
}
