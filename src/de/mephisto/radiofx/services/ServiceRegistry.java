package de.mephisto.radiofx.services;

import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.services.google.impl.GoogleServiceImpl;
import de.mephisto.radiofx.services.mpd.impl.RadioServiceImpl;
import de.mephisto.radiofx.services.time.impl.EarthToolsTimeServiceImpl;
import de.mephisto.radiofx.services.weather.IWeatherService;
import de.mephisto.radiofx.services.weather.impl.YahooWeatherServiceImpl;

/**
 * Factory class for retrieving the service classes.
 */
public class ServiceRegistry {
  private static IWeatherService weatherService;
  private static RefreshingService timeService;
  private static RefreshingService mpdService;
  private static IGoogleMusicService googleService;

  public static void init() {
    weatherService = new YahooWeatherServiceImpl();
    timeService = new EarthToolsTimeServiceImpl();
    mpdService = new RadioServiceImpl();
    googleService = new GoogleServiceImpl();
  }

  /**
   * Returns the service for retrieving weather forecast.
   * @return
   */
  public static IWeatherService getWeatherService() {
    if (weatherService == null) {
      weatherService = new YahooWeatherServiceImpl();
    }
    return weatherService;
  }

  /**
   * Returns the service instance for the timer.
   * @return
   */
  public static RefreshingService getTimeService() {
    if(timeService == null) {
      timeService = new EarthToolsTimeServiceImpl();
    }
    return timeService;
  }

  /**
   * Returns the MPD service instance.
   * @return
   */
  public static RefreshingService getRadioService() {
    if(mpdService == null) {
      mpdService = new RadioServiceImpl();
    }
    return mpdService;
  }

  /**
   * Lazy loading of the google library.
   * @return
   */
  public static IGoogleMusicService getGoogleService() {
    if(googleService == null) {
      googleService = new GoogleServiceImpl();
    }
    return googleService;
  }
}
