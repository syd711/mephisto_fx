package de.mephisto.radiofx.services;

import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.services.google.impl.GoogleServiceImpl;
import de.mephisto.radiofx.services.gpio.RotaryEncoderService;
import de.mephisto.radiofx.services.mpd.IMpdService;
import de.mephisto.radiofx.services.mpd.impl.MpdServiceImpl;
import de.mephisto.radiofx.services.time.impl.EarthToolsTimeServiceImpl;
import de.mephisto.radiofx.services.weather.IWeatherService;
import de.mephisto.radiofx.services.weather.impl.YahooWeatherServiceImpl;
import de.mephisto.radiofx.ui.SplashScreen;
import de.mephisto.radiofx.ui.UIStateController;

/**
 * Factory class for retrieving the service classes.
 */
public class ServiceRegistry {
  private static IWeatherService weatherService;
  private static RefreshingService timeService;
  private static IMpdService mpdService;
  private static IGoogleMusicService googleService;
  private static RotaryEncoderService rotaryEncoderService;

  public static void init(final SplashScreen splashScene) {
    new Thread() {
      @Override
      public void run() {
        mpdService = new MpdServiceImpl();
        mpdService.initService(splashScene);

        googleService = new GoogleServiceImpl();
        googleService.initService(splashScene);

        weatherService = new YahooWeatherServiceImpl();
        weatherService.initService(splashScene);

        timeService = new EarthToolsTimeServiceImpl();
        timeService.initService(splashScene);

        rotaryEncoderService = new RotaryEncoderService(splashScene);

        UIStateController.getInstance().createControllers(splashScene);

        splashScene.setMessage("Wait for it...", 1.0);

        splashScene.dispose();
      }
    }.start();
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
  public static IMpdService getMpdService() {
    if(mpdService == null) {
      mpdService = new MpdServiceImpl();
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

  /**
   * Lazy loading of the rotaryEncoderService.
   * @return
   */
  public static RotaryEncoderService getRotaryEncoderService() {
    return rotaryEncoderService;
  }
}
