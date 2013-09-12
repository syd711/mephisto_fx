package de.mephisto.radiofx;

import de.mephisto.radiofx.ui.UIStateController;
import de.mephisto.radiofx.ui.UIState;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


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
    this.stage = primaryStage;
    primaryStage.show();

    final UIState state = new UIState();

    primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.RIGHT) {
          Platform.runLater(new Runnable() {
            @Override public void run() {
              state.right();
            }
          });

        }
        if (keyEvent.getCode() == KeyCode.DOWN) {
          Platform.runLater(new Runnable() {
            @Override public void run() {
              state.enter();
            }
          });
        }
        if (keyEvent.getCode() == KeyCode.LEFT) {
          Platform.runLater(new Runnable() {
            @Override public void run() {
              state.left();
            }
          });
        }
      }
    });

    //ensures that the process is terminated on window dispose
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent windowEvent) {
        Platform.exit();
        System.exit(0);
      }
    });

    UIStateController.getInstance().showDefault();
  }

  public Stage getStage() {
    return stage;
  }
}