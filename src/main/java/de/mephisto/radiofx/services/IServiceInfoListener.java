package de.mephisto.radiofx.services;

/**
 * Interface to be implemented by service to notify data changes to the UI.
 */
public interface IServiceInfoListener {

  void serviceDataChanged(IServiceModel model);

  /**
   * May return false when the UI is currently updating it's state otherwise
   * Ensure that the update lock is released afterwards!
   * @return
   */
  boolean isChangeable();
}
