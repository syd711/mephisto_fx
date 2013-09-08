package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.util.UIUtil;

/**
 *
 */
public class UIController {
  private static UIController instance = new UIController();

  public static UIController getInstance() {
    return instance;
  }

  public void showCurrentStage() {
    UIUtil.showDefaultScene();
  }
}
