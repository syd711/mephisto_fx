package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.util.UIUtil;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 *
 */
public class UIController {
  private static UIController instance = new UIController();
  private BorderPane borderPane;
  private Footer footer;

  private WeatherController weatherController = new WeatherController();
  private RadioController radioController = new RadioController();

  private ITabController activeController = radioController;

  private UIController() {
    borderPane = new BorderPane();
    UIUtil.createScene(borderPane);

    new Header(borderPane);
    footer = new Footer(borderPane);
  }

  /**
   * Singleton getter
   * @return
   */
  public static UIController getInstance() {
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

  public void toggleState() {
    footer.toggleFooter();

    final Node center = activeController.getTabRoot();
    final FadeTransition outFader = UIUtil.createOutFader(center);
    outFader.onFinishedProperty().set(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        if(activeController == radioController) {
          activeController = weatherController;
        }
        else {
          activeController = radioController;
        }
        activeController.showDefault(borderPane);
      }
    });
    outFader.play();

  }
}
