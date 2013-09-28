package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.services.IService;
import de.mephisto.radiofx.services.IServiceInfoListener;
import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.ui.Pager;
import de.mephisto.radiofx.util.UIUtil;
import javafx.scene.Node;

/**
 * Provides the UI control for the paging.
 */
public abstract class PageableUIController extends UIController implements IServiceInfoListener {

  private Pager pager;
  private Node pagingRoot;

  private IService service;

  public PageableUIController(IService service) {
    super();
    this.service = service;
    this.service.addServiceListener(this);
  }

  protected void setPagingRoot(Node node) {
    this.pagingRoot = node;
  }

  protected void setPager(Pager pager) {
    this.pager = pager;
  }


  @Override
  public void serviceDataChanged(IServiceModel model) {
    if (pager.getActiveModel() != null && pager.getActiveModel().equals(model)) {
      updatePage(model);
    }
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
}
