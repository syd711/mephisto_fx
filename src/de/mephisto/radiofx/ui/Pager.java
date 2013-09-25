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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.List;

/**
 * Paging for pageable elements of the UI
 */
public class Pager implements IServiceInfoListener {
  private final static int STROKE_WIDTH = 2;

  private static int BUBBLE_RADIUS = 6;

  private HBox box;
  private IServiceModel activeModel;
  private List<IServiceModel> models;
  private IFeatureController controller;
  private boolean bubbleMode;
  private boolean circle;
  private GraphicsContext gc;
  private BorderPane root;

  public Pager(BorderPane root, IService service, IFeatureController controller) {
    this(root, service, controller, true, true);
  }

  public Pager(BorderPane root, IService service, IFeatureController controller, boolean bubbleMode, boolean circle) {
    this.bubbleMode = bubbleMode;
    this.circle = circle;
    this.root = root;
    this.controller = controller;
    this.models = service.getServiceData();
    this.activeModel = models.get(0);

    updateUI();

    service.addServiceListener(this);
  }

  private void updateUI() {
    if (bubbleMode) {
      int margin = 20;
      if (models.size() > 10) {
        margin = 15;
      }
      if (models.size() > 15) {
        margin = 8;
      }
      if (models.size() > 20) {
        margin = 3;
      }
      if (models.size() > 25) {
        margin = 2;
        BUBBLE_RADIUS = 4;
      }
      box = new HBox(margin);
      box.setAlignment(Pos.BASELINE_CENTER);
      for (IServiceModel model : models) {
        Circle selectorCircle = new Circle(BUBBLE_RADIUS, BUBBLE_RADIUS, BUBBLE_RADIUS, UIUtil.COLOR_DARK_HEADER);
        selectorCircle.setStrokeWidth(STROKE_WIDTH);
        selectorCircle.setStroke(UIUtil.COLOR_DARK_HEADER);
        selectorCircle.setUserData(model);
        box.getChildren().add(selectorCircle);
      }
    }
    else {
      BUBBLE_RADIUS = 6;
      box = new HBox(0);
      box.setAlignment(Pos.CENTER);

      Canvas progress = new Canvas(450, BUBBLE_RADIUS*2+2);
      gc = progress.getGraphicsContext2D();
      gc.setFill(Paint.valueOf(UIUtil.HEX_COLOR_INACTIVE));
      gc.fillRoundRect(0, 0, 450, BUBBLE_RADIUS*2+2, BUBBLE_RADIUS*2+2, BUBBLE_RADIUS*2+2);
      gc.setFill(Paint.valueOf(UIUtil.HEX_COLOR_DARK));
      gc.fillOval(2, 0, BUBBLE_RADIUS*2, BUBBLE_RADIUS*2);

      box.getChildren().add(progress);
    }

    box.setMaxHeight(10);
    root.setBottom(box);

    updateActivity();
  }

  /**
   * Updates the models the pager is working on.
   *
   * @param models
   */
  public void setModels(List<IServiceModel> models, IServiceModel activeModel) {
    this.models = models;
    this.activeModel = activeModel;

    updateUI();
  }

  /**
   * Toggles the display mode for paging.
   */
  public void toggleDisplayMode() {
    this.bubbleMode = !bubbleMode;
    updateUI();
  }

  /**
   * Next element
   */
  public IServiceModel next() {
    if (models.isEmpty()) {
      return null;
    }

    if (activeModel == null) {
      activeModel = models.get(0);
    }
    else {
      int index = models.indexOf(activeModel);
      index++;
      if (index < models.size()) {
        activeModel = models.get(index);
      }
      else {
        if (circle) {
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
    if (models.isEmpty()) {
      return null;
    }

    if (activeModel == null && circle) {
      activeModel = models.get(models.size() - 1);
    }
    else {
      int index = models.indexOf(activeModel);
      index--;
      if (index >= 0) {
        activeModel = models.get(index);
      }
      else {
        if (circle) {
          activeModel = models.get(models.size() - 1);
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
    if (bubbleMode) {
      final ObservableList<Node> children = this.box.getChildren();
      for (Node child : children) {
        Circle circle = (Circle) child;
        IServiceModel model = (IServiceModel) circle.getUserData();
        if (model.equals(activeModel)) {
          circle.setStrokeWidth(STROKE_WIDTH);
          circle.setStroke(UIUtil.COLOR_DARK_HEADER);
          circle.setFill(null);
        }
        else {
          circle.setFill(UIUtil.COLOR_DARK_HEADER);
        }
      }
    }
    else {
      gc.clearRect(0, 0, 450, BUBBLE_RADIUS*2+2);
      gc.setFill(Paint.valueOf(UIUtil.HEX_COLOR_INACTIVE));
      gc.fillRoundRect(0, 0, 450, BUBBLE_RADIUS*2+2, BUBBLE_RADIUS*2+2, BUBBLE_RADIUS*2+2);
      gc.setFill(Paint.valueOf(UIUtil.HEX_COLOR_DARK));

      double pos = new Double(440) / models.size();
      pos = (pos * models.indexOf(activeModel)) + 2;
      gc.fillOval(pos, 1, BUBBLE_RADIUS*2, BUBBLE_RADIUS*2);
    }
  }

  @Override
  public void serviceDataChanged(IServiceModel model) {
    if (this.activeModel.equals(model)) {
      controller.updatePage(model);
    }
  }

  /**
   * Returns the active selection
   * @return
   */
  public IServiceModel getActiveModel() {
    return activeModel;
  }
}
