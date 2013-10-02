package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.util.UIUtil;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.List;

/**
 * Paging for pageable elements of the UI
 */
public class Pager {
  private final static int PAGER_WIDTH = 410;
  private final static int STROKE_WIDTH = 2;

  private int bubbleRadius= 6;
  private HBox box;
  private IServiceModel activeModel;
  private List<IServiceModel> models;
  private boolean bubbleMode;
  private boolean circle;
  private GraphicsContext gc;
  private Pane root;
  private HBox bubbleBox;

  public Pager(Pane root, List<IServiceModel> models) {
    this(root, models, true, true);
  }

  public Pager(Pane root, List<IServiceModel> models, boolean bubbleMode, boolean circle) {
    this.bubbleMode = bubbleMode;
    this.circle = circle;
    this.root = root;
    this.models = models;

    if(!models.isEmpty()) {
      this.activeModel = models.get(0);
      updateUI();
    }
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
        bubbleRadius = 4;
      }
      box = new HBox(0);
      box.setAlignment(Pos.BASELINE_LEFT);
      bubbleBox = new HBox(margin);
      bubbleBox.setMinWidth(UIUtil.WIDTH);
      bubbleBox.setAlignment(Pos.CENTER);
      for (IServiceModel model : models) {
        Circle selectorCircle = new Circle(bubbleRadius, bubbleRadius, bubbleRadius, UIUtil.COLOR_DARK_HEADER);
        selectorCircle.setStrokeWidth(STROKE_WIDTH);
        selectorCircle.setStroke(UIUtil.COLOR_DARK_HEADER);
        selectorCircle.setUserData(model);
        bubbleBox.getChildren().add(selectorCircle);
      }
      box.getChildren().add(bubbleBox);
    }
    else {
      bubbleRadius = 6;
      box = new HBox(0);
      box.setAlignment(Pos.BASELINE_LEFT);
      box.setPadding(new Insets(0,0,0,30));

      Canvas progress = new Canvas(PAGER_WIDTH, bubbleRadius*2+2);
      gc = progress.getGraphicsContext2D();
      gc.setFill(Paint.valueOf(UIUtil.HEX_COLOR_INACTIVE));
      gc.fillRoundRect(0, 0, PAGER_WIDTH, bubbleRadius*2+2, bubbleRadius*2+2, bubbleRadius*2+2);
      gc.setFill(Paint.valueOf(UIUtil.HEX_COLOR_DARK));
      gc.fillOval(2, 0, bubbleRadius*2, bubbleRadius*2);

      box.getChildren().add(progress);
    }

    if(root instanceof BorderPane) {
      ((BorderPane)root).setBottom(box);
    }
    else {
      root.getChildren().add(box);
    }

    updateActivity();
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
      final ObservableList<Node> children = this.bubbleBox.getChildren();
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
      gc.clearRect(0, 0, PAGER_WIDTH, bubbleRadius*2+2);
      gc.setFill(Paint.valueOf(UIUtil.HEX_COLOR_INACTIVE));
      gc.fillRoundRect(0, 0, PAGER_WIDTH, bubbleRadius*2+2, bubbleRadius*2+2, bubbleRadius*2+2);
      gc.setFill(Paint.valueOf(UIUtil.HEX_COLOR_DARK));

      double pos = new Double(PAGER_WIDTH-bubbleRadius*2) / models.size();
      pos = (pos * models.indexOf(activeModel)) + 2;
      gc.fillOval(pos, 1, bubbleRadius*2, bubbleRadius*2);
    }
  }

  /**
   * Returns the active selection
   * @return
   */
  public IServiceModel getActiveModel() {
    return activeModel;
  }

  public void setModels(List<IServiceModel> models, IServiceModel activeModel) {
    this.models = models;
    this.activeModel = activeModel;
    updateUI();
  }

  /**
   * Returns true if the page is at the first position.
   * @return
   */
  public boolean isAtStart() {
    return models.indexOf(activeModel) == 0;
  }

  /**
   * Returns true if the pager has the last element selected.
   * @return
   */
  public boolean isAtEnd() {
    return models.indexOf(activeModel) == (models.size()-1);
  }

  public void toggleMode() {
    this.bubbleMode = !bubbleMode;
    updateUI();
  }
}