package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.ui.Pager;
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
  @Override
  public void next() {
    IServiceModel info = pager.next();
    UIUtil.fadeOutComponent(pagingRoot);
    updatePage(info);
    UIUtil.fadeInComponent(pagingRoot);
  }

  /**
   * Slides to the previous weather info
   */
  @Override
  public void prev() {
    IServiceModel info = pager.prev();
    UIUtil.fadeOutComponent(pagingRoot);
    updatePage(info);
    UIUtil.fadeInComponent(pagingRoot);
  }


  /**
   * Invoked for the push event
   */
  @Override
  public void push() {
    //maybe implemented by subclasses
  }

}
