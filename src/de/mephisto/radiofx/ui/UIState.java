package de.mephisto.radiofx.ui;

/**
 * Translates the user input events into UI updates.
 */
public class UIState {

  public void left() {
    UIStateController.getInstance().showPrevious();
  }

  public void right() {
    UIStateController.getInstance().showNext();
  }

  public void enter() {
    UIStateController.getInstance().toggleState();
  }
}
