package de.mephisto.radiofx.services;

import de.mephisto.radiofx.services.time.TimeService;
import de.mephisto.radiofx.services.time.impl.EarthToolsTimeServiceImpl;
import de.mephisto.radiofx.services.weather.WeatherService;
import de.mephisto.radiofx.services.weather.impl.YahooWeatherServiceImpl;

/**
 * Factory class for retrieving the service classes.
 */
public class ServiceRegistry {
  private static WeatherService weatherService;
  private static TimeService timeService;

  /**
   * Returns the service for retrieving weather forecast.
   * @return
   */
  public static WeatherService getWeatherService() {
    if (weatherService == null) {
      weatherService = new YahooWeatherServiceImpl();
    }
    return weatherService;
  }

  public static TimeService getTimeService() {
    if(timeService == null) {
      timeService = new EarthToolsTimeServiceImpl();
    }
    return timeService;
  }

}
