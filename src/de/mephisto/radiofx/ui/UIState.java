package de.mephisto.radiofx.ui;

/**
 * Translates the user input events into UI updates.
 */
public class UIState {

  public void left() {
    UIController.getInstance().showPrevious();
  }

  public void right() {
    UIController.getInstance().showNext();
  }

  public void enter() {
    UIController.getInstance().toggleState();
  }
}
