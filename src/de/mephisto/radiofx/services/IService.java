package de.mephisto.radiofx.services;

import java.util.List;

/**
 * Interface to be implemented by all services.
 */
public interface IService {

  /**
   * Retrieves the service data for the refreshing.
   * @return
   */
  List<IServiceModel> getServiceData();

  /**
   * Adds a listener to the service.
   * @param listener
   */
  void addServiceListener(IServiceInfoListener listener);
}
