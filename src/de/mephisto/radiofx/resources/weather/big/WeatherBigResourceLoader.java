package de.mephisto.radiofx.resources.weather.big;

/**
 * Used for load images and stuff.
 */
public class WeatherBigResourceLoader {
  public static String getResource(String s) {
    return WeatherBigResourceLoader.class.getResource(s).toString();
  }
}
