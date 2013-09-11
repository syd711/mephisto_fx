package de.mephisto.radiofx.ui;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * Implements methods a tab controller should react on.
 */
public interface ITabController {

  void prev();
  void next();
  void showDefault(BorderPane borderPane);
  Node getTabRoot();
}
