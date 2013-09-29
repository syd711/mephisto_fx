package de.mephisto.radiofx.ui.controller;

/**
 * Implements methods a tab controller should react on.
 */
public interface IRotaryControllable {
  IRotaryControllable prev();
  IRotaryControllable next();
  IRotaryControllable push();
  IRotaryControllable longPush();
}
