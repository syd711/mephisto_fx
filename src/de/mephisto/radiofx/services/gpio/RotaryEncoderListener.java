package de.mephisto.radiofx.services.gpio;

/**
 * Listener to react on rotary encoder actions.
 */
public interface RotaryEncoderListener {
  void previous();
  void next();
  void push();
  void longPush();
}
