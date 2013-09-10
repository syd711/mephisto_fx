package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.util.UIUtil;
import javafx.animation.FillTransitionBuilder;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 */
public class Footer {
  private static Font TAB_FONT = Font.font("Tahoma", FontWeight.NORMAL, 18);

  private HBox tab1;
  private HBox tab2;

  private boolean tab1Active;

  public Footer(BorderPane root) {
    HBox main = new HBox(0);
    main.setPadding(new Insets(14, 0, 5, 0));

    tab1 = new HBox(0);
    tab1.setMinWidth(UIUtil.WIDTH/2);
    tab1.setAlignment(Pos.CENTER);
    tab1.setPadding(new Insets(6, 8, 8, 8));
    tab1.setStyle("-fx-border-color: " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + " transparent " +
        "transparent;-fx-background-color: " + UIUtil.HEX_COLOR_INACTIVE + ";");

    main.getChildren().add(tab1);
    root.setBottom(main);

    Text text = new Text(0, 0, "Radio");
    text.setFont(TAB_FONT);
    text.setFill(UIUtil.COLOR_DARK_HEADER);
    tab1.getChildren().add(text);

    tab2 = new HBox(0);
    tab2.setMinWidth(UIUtil.WIDTH / 2);
    tab2.setAlignment(Pos.CENTER);
    tab2.setPadding(new Insets(6, 8, 8, 8));

    text = new Text(0, 0, "Weather");
    text.setFont(TAB_FONT);
    text.setFill(UIUtil.COLOR_DARK_HEADER);
    tab2.getChildren().add(text);

    main.getChildren().add(tab2);
  }

  public void toggleFooter() {
    tab1Active = !tab1Active;
    if(tab1Active) {
      tab1.setStyle("-fx-background-color: " + UIUtil.HEX_COLOR_BACKGROUND + ";");

      tab1.setStyle("-fx-background-color: " + UIUtil.HEX_COLOR_BACKGROUND + ";-fx-border-color: transparent transparent transparent transparent");
      tab2.setStyle("-fx-border-color: " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + " transparent "
          + UIUtil.HEX_COLOR_SEPARATOR + ";-fx-background-color: " + UIUtil.HEX_COLOR_INACTIVE + ";");
    }
    else {
      tab2.setStyle("-fx-background-color: " + UIUtil.HEX_COLOR_BACKGROUND + ";");

      tab2.setStyle("-fx-background-color: " + UIUtil.HEX_COLOR_BACKGROUND + ";-fx-border-color: transparent transparent transparent transparent");
      tab1.setStyle("-fx-border-color: " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + " transparent "
          + UIUtil.HEX_COLOR_SEPARATOR + ";-fx-background-color: " + UIUtil.HEX_COLOR_INACTIVE + ";");
    }
  }
}
