package de.mephisto.test.time;

import de.mephisto.radiofx.services.time.impl.EarthToolsTimeServiceImpl;
import de.mephisto.radiofx.services.weather.impl.YahooWeatherServiceImpl;
import org.junit.Test;

/**
 *
 */
public class EarthToolsTimeTest {

  @Test
  public void testTime() throws InterruptedException {
    EarthToolsTimeServiceImpl service = new EarthToolsTimeServiceImpl();
    System.out.println("Time " + service.getTime());
    Thread.sleep(2000);
    System.out.println("Time " + service.getTime());
  }

}
