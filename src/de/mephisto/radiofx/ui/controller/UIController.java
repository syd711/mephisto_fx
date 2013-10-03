package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.util.UIUtil;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * Common methods for the services.
 */
public abstract class UIController implements IRotaryControllable {

  private BorderPane tabRoot;

  public UIController() {
    init();
  }

  /**
   * Invoked on tab change, applies the new root panel
   * to the border layout and fades the component in.
   * @param root
   */
  public void display(BorderPane root) {
    tabRoot.setOpacity(0);
    root.setCenter(tabRoot);
    onDisplay();
    UIUtil.fadeInComponent(tabRoot);
  }

  public Node getTabRoot() {
    return tabRoot;
  }

  /**
   * Sets the root for the tab, including possible paging components.
   * @param root
   */
  public void setTabRoot(BorderPane root) {
    this.tabRoot = root;
  }

  /**
   * Can be overwritten to init the UI that depends on a previous selection.
   */
  public void onDisplay() {
    //not used here
  }

  /**
   * Calls once the controller changes, allows to dispose some other components first.
   */
  public void onDispose() {
    //not used here
  }

  /**
   * Creates the default UI for the controller.
   * @return
   */
  public abstract BorderPane init();

  /**
   * Method to be implemented by the controller, shows the UI for the given model.
   * @param model
   */
  public abstract void updatePage(IServiceModel model);
}
