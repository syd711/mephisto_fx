package de.mephisto.radiofx.services.weather;

/**
 * Event listener for weather info changes.
 */
public interface WeatherInfoListener {

  /**
   * Fired when one of the weather information sources has changed.
   * @param info
   */
  void weatherChanged(WeatherInfo info);
}
