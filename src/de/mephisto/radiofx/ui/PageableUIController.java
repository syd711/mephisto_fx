package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.util.UIUtil;
import javafx.scene.Node;

/**
 * Provides the UI control for the paging.
 */
public abstract class PageableUIController extends AbstractUIController {

  private Pager pager;
  private Node pagingRoot;

  protected void setPagingRoot(Node node) {
    this.pagingRoot = node;
  }

  protected void setPager(Pager pager) {
    this.pager = pager;
  }

  /**
   * Slides to the next weather info
   */
  public void next() {
    IServiceModel info = pager.next();
    UIUtil.fadeOutComponent(pagingRoot);
    updatePage(info);
    UIUtil.fadeInComponent(pagingRoot);
  }

  /**
   * Slides to the previous weather info
   */
  public void prev() {
    IServiceModel info = pager.prev();
    UIUtil.fadeOutComponent(pagingRoot);
    updatePage(info);
    UIUtil.fadeInComponent(pagingRoot);
  }

  /**
   * Method to be implemented by the controller, shows the UI for the given model.
   * @param model
   */
  public abstract void updatePage(IServiceModel model);
}
