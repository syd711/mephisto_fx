package de.mephisto.radiofx.services;

/**
 * Interface to be implemented by service to notify data changes to the UI.
 */
public interface IServiceInfoListener {

  void serviceDataChanged(IServiceModel model);
}
