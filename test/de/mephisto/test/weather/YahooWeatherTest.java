package de.mephisto.test.weather;

import de.mephisto.radiofx.services.weather.impl.YahooWeatherServiceImpl;
import org.junit.Test;

/**
 *
 */
public class YahooWeatherTest {

  @Test
  public void testWeather() {
    YahooWeatherServiceImpl service = new YahooWeatherServiceImpl();
    assert (!service.getWeatherInfoList().isEmpty());
  }

  @Test
  public void testWeatherCodes() {
    YahooWeatherServiceImpl service = new YahooWeatherServiceImpl();
    for (int i = 0; i < 47; i++) {
      service.convertTypeCodeImage(i);
    }
  }
}
