package de.mephisto.radiofx;

import de.mephisto.radiofx.ui.UIController;
import de.mephisto.radiofx.util.Config;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.configuration.Configuration;


public class MephistoRadioFX extends Application {
  private static MephistoRadioFX instance;
  private Stage stage;

  /**
   * And in the beginning there was main...
   *
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
  }

  public static MephistoRadioFX getInstance() {
    return instance;
  }

  @Override
  public void start(final Stage primaryStage) {
    instance = this;
    Configuration configuration = Config.getConfiguration("settings.properties");

    this.stage = primaryStage;
    primaryStage.show();
    primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.DOWN) {

        }
      }
    });

    //ensures that the process is terminated on window dispose
    primaryStage.setOnCloseRequest( new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent windowEvent) {
        Platform.exit();
        System.exit(0);
      }
    });

    UIController.getInstance().showCurrentStage();
  }

  public Stage getStage() {
    return stage;
  }
}