package de.mephisto.radiofx.resources.weather.small_white;

/**
 * Used for load images and stuff.
 */
public class WeatherSmallWhiteResourceLoader {
  public static String getResource(String s) {
    return WeatherSmallWhiteResourceLoader.class.getResource(s).toString();
  }
}
