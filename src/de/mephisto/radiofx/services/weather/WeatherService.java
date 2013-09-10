package de.mephisto.radiofx.services.weather;

import java.util.List;

/**
 * The common interface for weather services.
 */
public interface WeatherService {
  public static final String IMG_SUNNY = "weather-sun.png";
  public static final String IMG_SUNNY_CLOUDY_1 = "weather-cloud-sun.png";
  public static final String IMG_SUNNY_CLOUDY_2 = "weather-clouds.png";
  public static final String IMG_CLOUDY = "weather-cloud.png";
  public static final String IMG_SNOW = "weather-snow.png";
  public static final String IMG_SNOW_RAINY = "weather-snow.png";
  public static final String IMG_SUNNY_RAINY = "weather-rain.png";
  public static final String IMG_RAINY = "weather-rain.png";
  public static final String IMG_STORMY = "weather-thunder.png";


  /**
   * Returns the weather information of the stored locations.
   * @return
   */
  List<WeatherInfo> getWeatherInfoList();

  /**
   * Registers a new listener that fires once the weather info changes.
   * @param listener
   */
  void addWeatherListener(WeatherInfoListener listener);

  /**
   * Returns the default weather info
   * @return
   */
  WeatherInfo getDefaultWeatherInfo();
}
