package de.mephisto.radiofx;

import de.mephisto.radiofx.ui.UIStateController;
import de.mephisto.radiofx.util.UIUtil;
import javafx.application.Application;
import javafx.stage.Stage;


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

    UIStateController.getInstance().showSplashScreen();

    UIUtil.addStateListener(primaryStage);
    UIUtil.addDisposeListener(primaryStage);
  }

  public Stage getStage() {
    return stage;
  }
}