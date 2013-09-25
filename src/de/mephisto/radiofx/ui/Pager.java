package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.services.IService;
import de.mephisto.radiofx.services.IServiceInfoListener;
import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.ui.controller.IFeatureController;
import de.mephisto.radiofx.util.UIUtil;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Paging for pageable elements of the UI
 */
public class Pager implements IServiceInfoListener {
  private final static int STROKE_WIDTH = 2;

  private HBox box;
  private IServiceModel activeModel;
  private IService service;
  private List<IServiceModel> models;
  private IFeatureController controller;
  private boolean bubbleMode;
  private boolean circle;
  private GraphicsContext gc;

  public Pager(BorderPane root, IService service, IFeatureController controller) {
    this(root, service, controller, true, true);
  }

  public Pager(BorderPane root, IService service, IFeatureController controller, boolean bubbleMode, boolean circle) {
    this.bubbleMode = bubbleMode;
    this.circle = circle;
    this.service = service;
    this.controller = controller;
    this.models = service.getServiceData();
    int margin = 20;
    if(models.size() > 10) {
      margin = 15;
    }
    if(models.size() > 20) {
      margin = 10;
    }
    this.activeModel = models.get(0);

    if(bubbleMode) {
      box = new HBox(margin);
      box.setAlignment(Pos.BASELINE_CENTER);
      for(IServiceModel model : models) {
        Circle selectorCircle = new Circle(6,6,6, UIUtil.COLOR_DARK_HEADER);
        selectorCircle.setStrokeWidth(STROKE_WIDTH);
        selectorCircle.setStroke(UIUtil.COLOR_DARK_HEADER);
        selectorCircle.setUserData(model);
        box.getChildren().add(selectorCircle);
      }
    }
    else {
      box = new HBox(0);
      box.setAlignment(Pos.CENTER);

      Canvas progress = new Canvas(450, 10);
      gc = progress.getGraphicsContext2D();
      gc.setFill(Paint.valueOf(UIUtil.HEX_COLOR_INACTIVE));
      gc.fillRoundRect(0, 0, 450, 10, 10, 10);
      gc.setFill(Paint.valueOf(UIUtil.HEX_COLOR_DARK));
      gc.fillOval(2,0,8, 8);

      box.getChildren().add(progress);
    }

    box.setMaxHeight(10);
    root.setBottom(box);
    updateActivity();

    service.addServiceListener(this);
  }

  /**
   * Next element
   */
  public IServiceModel next() {
    if(models.isEmpty())  {
      return null;
    }

    if(activeModel == null) {
      activeModel = models.get(0);
    }
    else {
      int index = models.indexOf(activeModel);
      index++;
      if(index < models.size()) {
        activeModel = models.get(index);
      }
      else {
        if(circle) {
          activeModel = models.get(0);
        }
        else {
          //ignore event
        }
      }
    }
    updateActivity();
    return activeModel;
  }

  /**
   * Prev element
   */
  public IServiceModel prev() {
    if(models.isEmpty())  {
      return null;
    }

    if(activeModel == null && circle) {
      activeModel = models.get(models.size()-1);
    }
    else {
      int index = models.indexOf(activeModel);
      index--;
      if(index >= 0) {
        activeModel = models.get(index);
      }
      else {
        if(circle) {
          activeModel = models.get(models.size()-1);
        }
        else {
          //ignore event
        }
      }
    }
    updateActivity();
    return activeModel;
  }

  /**
   * Updates the circle selection
   */
  private void updateActivity() {
    if(bubbleMode) {
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
    else{
      gc.clearRect(0, 0, 450, 10);
      gc.setFill(Paint.valueOf(UIUtil.HEX_COLOR_INACTIVE));
      gc.fillRoundRect(0, 0, 450, 10, 10, 10);
      gc.setFill(Paint.valueOf(UIUtil.HEX_COLOR_DARK));

      double pos = new Double(440)/models.size();
      pos = (pos*models.indexOf(activeModel))+2;
      gc.fillOval(pos,1,8, 8);
    }
  }

  @Override
  public void serviceDataChanged(IServiceModel model) {
    if(this.activeModel.equals(model)) {
      controller.updatePage(model);
    }
  }
}
