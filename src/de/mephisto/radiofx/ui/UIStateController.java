package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.ui.controller.*;
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
  private GoogleUINaviController googleNaviController = new GoogleUINaviController();
  private GoogleUIPlayerController googlePlayerController = new GoogleUIPlayerController();

  //radio controller is default
  private UIController activeController = radioController;

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
    activeController.display(borderPane);
  }

  public void showNext() {
    activeController.next();
  }

  public void showPrevious() {
    activeController.prev();
  }

  public void push() {
    activeController = (UIController) activeController.push();
  }

  public void longPush() {
    footer.switchTab();
    UIController newController = (UIController) activeController.longPush();
    if(!newController.equals(activeController)) {
      activeController = newController;
      final Node center = activeController.getTabRoot();
      final FadeTransition outFader = UIUtil.createOutFader(center);
      outFader.onFinishedProperty().set(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
          activeController.display(borderPane);
        }
      });
      outFader.play();
    }
  }

  public WeatherUIController getWeatherController() {
    return weatherController;
  }

  public GoogleUINaviController getGoogleNaviController() {
    return googleNaviController;
  }

  public GoogleUIPlayerController getGooglePlayerController() {
    return googlePlayerController;
  }

  public RadioUIController getRadioController() {
    return radioController;
  }

  public void display(UIController controller) {
    controller.display(borderPane);
  }
}
