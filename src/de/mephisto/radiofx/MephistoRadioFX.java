package de.mephisto.radiofx;

import de.mephisto.radiofx.ui.UIState;
import de.mephisto.radiofx.ui.UIStateController;
import de.mephisto.radiofx.util.SystemUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MephistoRadioFX extends Application {
  private final static Logger LOG = LoggerFactory.getLogger(MephistoRadioFX.class);

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
    if (!SystemUtils.isWindows()) {
      primaryStage.initStyle(StageStyle.UNDECORATED);
    }
    primaryStage.show();

    UIStateController.getInstance().showSplashScreen();

    addStateListener(primaryStage);
    addDisposeListener(primaryStage);
  }

  public Stage getStage() {
    return stage;
  }


  private static void addStateListener(Stage primaryStage) {
    final UIState state = new UIState();

    primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.RIGHT) {
          Platform.runLater(new Runnable() {
            @Override
            public void run() {
              state.right();
            }
          });

        }
        if (keyEvent.getCode() == KeyCode.DOWN) {
          Platform.runLater(new Runnable() {
            @Override
            public void run() {
              state.push();
            }
          });
        }
        if (keyEvent.getCode() == KeyCode.UP) {
          Platform.runLater(new Runnable() {
            @Override
            public void run() {
              state.longPush();
            }
          });
        }
        if (keyEvent.getCode() == KeyCode.LEFT) {
          Platform.runLater(new Runnable() {
            @Override
            public void run() {
              state.left();
            }
          });
        }
      }
    });
  }

  private static void addDisposeListener(Stage primaryStage) {
    //ensures that the process is terminated on window dispose
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent windowEvent) {
        Platform.exit();
        System.exit(0);
      }
    });
  }
}