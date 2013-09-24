package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.ui.controller.GoogleUIController;
import de.mephisto.radiofx.ui.controller.IFeatureController;
import de.mephisto.radiofx.ui.controller.RadioUIController;
import de.mephisto.radiofx.ui.controller.WeatherUIController;
import de.mephisto.radiofx.util.UIUtil;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * The global UI controller, receives the input from the state machine that
 * can be triggered via GPIO or via keyboard inputs.
 */
public class UIStateController {
  private static UIStateController instance = new UIStateController();
  private BorderPane borderPane;
  private Footer footer;

  private RadioUIController radioController = new RadioUIController();
  private WeatherUIController weatherController = new WeatherUIController();
  private GoogleUIController googleController = new GoogleUIController();

  private IFeatureController activeController = radioController;

  private int activeControllerId = 1;

  private UIStateController() {
    borderPane = new BorderPane();
    UIUtil.createScene(borderPane);

    new Header(borderPane);
    footer = new Footer(borderPane);
  }

  /**
   * Singleton getter
   * @return
   */
  public static UIStateController getInstance() {
    return instance;
  }

  public void showDefault() {
    activeController.showDefault(borderPane);
  }

  public void showNext() {
    activeController.next();
  }

  public void showPrevious() {
    activeController.prev();
  }

  public void push() {
    footer.switchTab();

    activeControllerId+=1;
    if(activeControllerId > 3) {
      activeControllerId = 1;
    }

    final Node center = activeController.getTabRoot();
    final FadeTransition outFader = UIUtil.createOutFader(center);
    outFader.onFinishedProperty().set(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        switch(activeControllerId) {
          case 1: {
            activeController = radioController;
            break;
          }
          case 2: {
            activeController = weatherController;
            break;
          }
          case 3: {
            activeController = googleController;
            break;
          }
        }
        activeController.showDefault(borderPane);
      }
    });
    outFader.play();

  }

  public void longPush() {
    activeController.push();
  }
}
