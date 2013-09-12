package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.util.UIUtil;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * Common methods for the services.
 */
public abstract class AbstractUIController implements IFeatureController{

  private BorderPane tabRoot;

  @Override
  public void showDefault(BorderPane root) {
    root.setCenter(tabRoot);
    UIUtil.fadeInComponent(tabRoot);
  }

  @Override
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
}
