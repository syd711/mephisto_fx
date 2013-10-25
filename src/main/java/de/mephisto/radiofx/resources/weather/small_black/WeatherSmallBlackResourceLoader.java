package de.mephisto.radiofx.resources.weather.small_black;

/**
 * Used for load images and stuff.
 */
public class WeatherSmallBlackResourceLoader {
  public static String getResource(String s) {
    return WeatherSmallBlackResourceLoader.class.getResource(s).toString();
  }
}
