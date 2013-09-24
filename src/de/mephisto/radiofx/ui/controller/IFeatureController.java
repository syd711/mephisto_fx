package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.services.IServiceModel;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * Implements methods a tab controller should react on.
 */
public interface IFeatureController {

  BorderPane init();
  void prev();
  void next();
  void push();
  void showDefault(BorderPane borderPane);
  Node getTabRoot();

  /**
   * Method to be implemented by the controller, shows the UI for the given model.
   * @param model
   */
  void updatePage(IServiceModel model);
}
