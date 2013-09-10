package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.MephistoRadioFX;
import de.mephisto.radiofx.util.UIUtil;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 */
public class UIController {
  private static UIController instance = new UIController();
  private WeatherController weatherController = new WeatherController();
  private BorderPane borderPane;

  private UIController() {
    Group root = new Group();
    borderPane = new BorderPane();
    root.getChildren().add(borderPane);
    Stage primaryStage = MephistoRadioFX.getInstance().getStage();
    if(primaryStage.getScene() == null) {
      Scene scene = new Scene(root, UIUtil.WIDTH, UIUtil.HEIGHT, Color.valueOf("#DACEB8"));
      primaryStage.setScene(scene);
    }
    else {
      primaryStage.getScene().setRoot(root);
    }
  }

  public static UIController getInstance() {
    return instance;
  }

  public void showCurrentStage() {
    UIUtil.showDefaultScene(borderPane);
  }

  public void showDefaultWeather() {
    weatherController.showDefaultWeather(borderPane);
  }

  public void showNextWeather() {
    weatherController.showNextWeather();
  }
}
