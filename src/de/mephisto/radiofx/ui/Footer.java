package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.util.UIUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 */
public class Footer {
  private static Font TAB_FONT = Font.font("Tahoma", FontWeight.NORMAL, 16);
  private static final String STYLE_INACTIVE = "-fx-border-color: " + UIUtil.HEX_COLOR_SEPARATOR + " " + UIUtil.HEX_COLOR_SEPARATOR + " transparent "
      + UIUtil.HEX_COLOR_SEPARATOR + ";-fx-background-color: " + UIUtil.HEX_COLOR_INACTIVE + ";";
  private static final String STYLE_ACTIVE = "-fx-background-color: " + UIUtil.HEX_COLOR_BACKGROUND + ";-fx-border-color: transparent transparent transparent transparent";

  private static final int TOP_TAB_PADDING = 4;

  private HBox tab1;
  private HBox tab2;
  private HBox tab3;

  private int activeTab = 1;

  public Footer(BorderPane root) {
    HBox main = new HBox(0);
    main.setPadding(new Insets(10, 0, 5, 0));

    //radio tab
    tab1 = new HBox(0);
    tab1.setMinWidth(UIUtil.WIDTH/3);
    tab1.setAlignment(Pos.CENTER);
    tab1.setPadding(new Insets(TOP_TAB_PADDING, 8, 8, 8));

    main.getChildren().add(tab1);
    root.setBottom(main);

    Text text = new Text(0, 0, "Radio");
    text.setFont(TAB_FONT);
    text.setFill(UIUtil.COLOR_DARK_HEADER);
    tab1.getChildren().add(text);

    //weather tab
    tab2 = new HBox(0);
    tab2.setMinWidth(UIUtil.WIDTH / 3);
    tab2.setAlignment(Pos.CENTER);
    tab2.setPadding(new Insets(TOP_TAB_PADDING, 8, 8, 8));
    tab2.setStyle(STYLE_INACTIVE);

    text = new Text(0, 0, "Weather");
    text.setFont(TAB_FONT);
    text.setFill(UIUtil.COLOR_DARK_HEADER);
    tab2.getChildren().add(text);

    main.getChildren().add(tab2);

    //google tab
    tab3 = new HBox(0);
    tab3.setMinWidth(UIUtil.WIDTH / 3);
    tab3.setAlignment(Pos.CENTER);
    tab3.setPadding(new Insets(TOP_TAB_PADDING, 8, 8, 8));
    tab3.setStyle(STYLE_INACTIVE);

    text = new Text(0, 0, "Music");
    text.setFont(TAB_FONT);
    text.setFill(UIUtil.COLOR_DARK_HEADER);
    tab3.getChildren().add(text);

    main.getChildren().add(tab3);
  }

  public void switchTab() {
    activeTab+=1;
    if(activeTab > 3) {
      activeTab = 1;
    }

    switch (activeTab) {
      case 1: {
        tab1.setStyle(STYLE_ACTIVE);
        tab2.setStyle(STYLE_INACTIVE);
        tab3.setStyle(STYLE_INACTIVE);
        return;
      }
      case 2: {
        tab2.setStyle(STYLE_ACTIVE);
        tab1.setStyle(STYLE_INACTIVE);
        tab3.setStyle(STYLE_INACTIVE);
        return;
      }
      case 3: {
        tab3.setStyle(STYLE_ACTIVE);
        tab2.setStyle(STYLE_INACTIVE);
        tab1.setStyle(STYLE_INACTIVE);
        return;
      }
    }
  }
}
