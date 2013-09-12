package de.mephisto.radiofx.services.weather;

import de.mephisto.radiofx.services.IService;

/**
 * Common weather service methods.
 */
public interface IWeatherService extends IService {

  /**
   * Returns the default weather.
   * @return
   */
  WeatherInfo getDefaultWeather();
}
