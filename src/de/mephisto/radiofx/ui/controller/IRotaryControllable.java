package de.mephisto.radiofx.ui.controller;

/**
 * Implements methods a tab controller should react on.
 */
public interface IRotaryControllable {
  void prev();
  void next();
  IRotaryControllable push();
  IRotaryControllable longPush();
}
