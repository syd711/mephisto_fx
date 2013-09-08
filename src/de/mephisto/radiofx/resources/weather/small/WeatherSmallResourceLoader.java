package de.mephisto.radiofx.resources.weather.small;

/**
 * Used for load images and stuff.
 */
public class WeatherSmallResourceLoader {
  public static String getResource(String s) {
    return WeatherSmallResourceLoader.class.getResource(s).toString();
  }
}
