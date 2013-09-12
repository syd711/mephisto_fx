package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.services.IService;
import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.util.UIUtil;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.Iterator;
import java.util.List;

/**
 * Paging for pageable elements of the UI
 */
public class Pager {
  private final static int STROKE_WIDTH = 2;

  private HBox box;
  private IServiceModel activeModel;
  private IService service;

  public Pager(BorderPane root, IService service) {
    this.service = service;
    List<IServiceModel> models = service.getServiceData();
    int margin = 20;
    if(models.size() > 10) {
      margin = 15;
    }
    if(models.size() > 20) {
      margin = 10;
    }
    box = new HBox(margin);
    box.setAlignment(Pos.BASELINE_CENTER);
    this.activeModel = models.get(0);
    for(IServiceModel model : models) {
      Circle circle = new Circle(6,6,6, UIUtil.COLOR_DARK_HEADER);
      circle.setStrokeWidth(STROKE_WIDTH);
      circle.setStroke(UIUtil.COLOR_DARK_HEADER);
      circle.setUserData(model);
      box.getChildren().add(circle);
    }
    updateActivity();
    root.setBottom(box);
  }

  /**
   * Next element
   */
  public IServiceModel next() {
    List<IServiceModel> models = service.getServiceData();
    Iterator<IServiceModel> iterator = (Iterator<IServiceModel>) models.iterator();
    IServiceModel current = activeModel;
    activeModel = null;
    while(iterator.hasNext()) {
      IServiceModel info = iterator.next();
      if(info.equals(current) && iterator.hasNext()) {
        activeModel = iterator.next();
        break;
      }
    }
    if(activeModel == null) {
      activeModel = models.get(0);
    }
    updateActivity();
    return activeModel;
  }

  /**
   * Prev element
   */
  public IServiceModel prev() {
    List<IServiceModel> models = service.getServiceData();
    Iterator<IServiceModel> iterator = (Iterator<IServiceModel>) models.iterator();
    IServiceModel current = activeModel;
    activeModel = null;
    while(iterator.hasNext()) {
      IServiceModel info = iterator.next();
      if(info.equals(current) && activeModel != null) {
        break;
      }
      activeModel = info;
    }
    if(activeModel == null) {
      activeModel = models.get(models.size()-1);
    }
    updateActivity();
    return activeModel;
  }

  /**
   * Updates the circle selection
   */
  private void updateActivity() {
    final ObservableList<Node> children = this.box.getChildren();
    for(Node child : children) {
      Circle circle = (Circle) child;
      IServiceModel model = (IServiceModel) circle.getUserData();
      if(model.equals(activeModel)) {
        circle.setStrokeWidth(STROKE_WIDTH);
        circle.setStroke(UIUtil.COLOR_DARK_HEADER);
        circle.setFill(null);
      }
      else {
        circle.setFill(UIUtil.COLOR_DARK_HEADER);
      }
    }
  }
}
