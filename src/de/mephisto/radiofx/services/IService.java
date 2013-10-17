package de.mephisto.radiofx.services;

import de.mephisto.radiofx.ui.SplashScreen;

import java.util.List;

/**
 * Interface to be implemented by all services.
 */
public interface IService {

  /**
   * Retrieves the service data for the refreshing.
   * @return
   */
  List<IServiceModel> getServiceData(boolean forceRefresh);

  /**
   * Forces the service to refresh the data without waiting for the refresh interval.
   */
  void forceRefresh();

  /**
   * Adds a listener to the service.
   * @param listener
   */
  void addServiceListener(IServiceInfoListener listener);

  /**
   * Initializes the service.
   */
  void initService(SplashScreen splashScreen);
}
