package de.mephisto.radiofx.services.weather.impl;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import de.mephisto.radiofx.resources.weather.big.WeatherBigResourceLoader;
import de.mephisto.radiofx.resources.weather.small_black.WeatherSmallBlackResourceLoader;
import de.mephisto.radiofx.resources.weather.small_white.WeatherSmallWhiteResourceLoader;
import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.RefreshingService;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.weather.IWeatherService;
import de.mephisto.radiofx.services.weather.WeatherInfo;
import de.mephisto.radiofx.ui.SplashScreen;
import de.mephisto.radiofx.util.Config;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 0	tornado
 * 1	tropical storm
 * 2	hurricane
 * 3	severe thunderstorms
 * 4	thunderstorms
 * 5	mixed rain and snow
 * 6	mixed rain and sleet
 * 7	mixed snow and sleet
 * 8	freezing drizzle
 * 9	drizzle
 * 10	freezing rain
 * 11	showers
 * 12	showers
 * 13	snow flurries
 * 14	light snow showers
 * 15	blowing snow
 * 16	snow
 * 17	hail
 * 18	sleet
 * 19	dust
 * 20	foggy
 * 21	haze
 * 22	smoky
 * 23	blustery
 * 24	windy
 * 25	cold
 * 26	cloudy
 * 27	mostly cloudy (night)
 * 28	mostly cloudy (day)
 * 29	partly cloudy (night)
 * 30	partly cloudy (day)
 * 31	clear (night)
 * 32	sunny
 * 33	fair (night)
 * 34	fair (day)
 * 35	mixed rain and hail
 * 36	hot
 * 37	isolated thunderstorms
 * 38	scattered thunderstorms
 * 39	scattered thunderstorms
 * 40	scattered showers
 * 41	heavy snow
 * 42	scattered snow showers
 * 43	heavy snow
 * 44	partly cloudy
 * 45	thundershowers
 * 46	snow showers
 * 47	isolated thundershowers
 * 3200	not available
 */
public class YahooWeatherServiceImpl extends RefreshingService implements IWeatherService {
  private static final Logger LOG = LoggerFactory.getLogger(YahooWeatherServiceImpl.class);

  public static final String IMG_SUNNY = "weather-sun.png";
  public static final String IMG_SUNNY_CLOUDY_1 = "weather-cloud-sun.png";
  public static final String IMG_SUNNY_CLOUDY_2 = "weather-clouds.png";
  public static final String IMG_CLOUDY = "weather-cloud.png";
  public static final String IMG_SNOW = "weather-snow.png";
  public static final String IMG_SNOW_RAINY = "weather-snow.png";
  public static final String IMG_SUNNY_RAINY = "weather-rain.png";
  public static final String IMG_RAINY = "weather-rain.png";


  public static final String IMG_STORMY = "weather-thunder.png";
  private static final int REFRESH_INTERVAL = 60000;

  public YahooWeatherServiceImpl() {
    super(REFRESH_INTERVAL);
  }

  /**
   * Returns a list of all configured weather locations.
   *
   * @return
   */
  @Override
  public List<IServiceModel> getServiceData() {
    List<IServiceModel> infoList = new ArrayList<IServiceModel>();
    final Configuration configuration = Config.getConfiguration("weather.properties");
    int count = 0;
    while (true) {
      count++;
      String url = configuration.getString(String.valueOf(count));
      if (!StringUtils.isEmpty(url)) {
        WeatherInfo info = getWeather(url);
        if(info != null) {
          info.setDefaultLocation(count == 1);
          infoList.add(info);
        }
      }
      else {
        break;
      }
    }

    return infoList;
  }

  @Override
  public void initService(SplashScreen splashScreen) {
    //no update necessary for weather
  }

  /**
   * Returns the info token of the weather today.
   *
   * @param url
   * @return
   */
  private WeatherInfo getWeather(String url) {
    SyndFeed feed = getFeed(url);
    if(feed != null) {
      WeatherInfo info = getWeatherInfo(feed);
      info.setId(url);
      return info;
    }
    return null;
  }

  /**
   * Returns the feed for the given URL.
   *
   * @param url
   * @return
   * @throws IOException
   * @throws FeedException
   */
  private SyndFeed getFeed(String url) {
    try {
      URL feedSource = new URL(url);
      SyndFeedInput input = new SyndFeedInput();
      SyndFeed feed = input.build(new XmlReader(feedSource));
//      LOG.info("Requested weather info " + url);
      return feed;
    } catch (Exception e) {
      LOG.error("Error reading weather RSS stream: " + e.getMessage(), e);
    }
    return null;
  }

  /**
   * Returns a weather info about the current weather.
   *
   * @param feed
   * @return
   */
  private WeatherInfo getWeatherInfo(SyndFeed feed) {
    WeatherInfo info = new WeatherInfo();
    final List<Element> currentInfo = (List) feed.getForeignMarkup();
    for (Element element : currentInfo) {
      String name = element.getName();
      if (name.equalsIgnoreCase("location")) {
        info.setCity(element.getAttribute("city").getValue());
        info.setCountry(element.getAttribute("country").getValue());
        break;
      }
    }

    //root elements
    final List<Element> channelElements = (List<Element>) feed.getForeignMarkup();
    for(Element element : channelElements) {
      //wind
      if(element.getName().equalsIgnoreCase("wind")) {
        info.setWind(Double.parseDouble(element.getAttributeValue("speed")));
      }
      //sunrise and sunset
      else if(element.getName().equalsIgnoreCase("astronomy")) {
        try {
          SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.US);
          info.setSunrise(format.parse(element.getAttributeValue("sunrise")));
          info.setSunset(format.parse(element.getAttributeValue("sunset")));
        }
        catch (ParseException e) {
          LOG.error("Error retrieving sunrise time for " + element.getAttributeValue("sunrise")+ ": " + e.getMessage());
        }
      }
    }


    final List<SyndEntry> items = (List) feed.getEntries();
    for (SyndEntry entry : items) {
      final List<Element> forecastInfo = (List) entry.getForeignMarkup();
      boolean todayApplied = false;
      for (Element element : forecastInfo) {
        //get forecast
        if (element.getName().equalsIgnoreCase("forecast")) {
          WeatherInfo forecast = new WeatherInfo();
          String high = element.getAttributeValue("high");
          String low = element.getAttributeValue("low");
          String description = element.getAttributeValue("text");
          int code = Integer.parseInt(element.getAttribute("code").getValue());
          String date = element.getAttribute("date").getValue();
          String imageUrl = convertTypeCodeImage(code);

          //e.g. Fri, 06 Sep 2013 11:49 am CEST
          Date formatted = null;
          try {
            formatted = new SimpleDateFormat("dd MMM yyyy", Locale.US).parse(date);
          } catch (ParseException e) {
            LOG.error("Error parsing date '" + date + ": " + e.getMessage());
          }

          forecast.setDescription(description);
          forecast.setForecastDate(formatted);
          forecast.setHighTemp(high);
          forecast.setLowTemp(low);
          forecast.setImageUrl(WeatherBigResourceLoader.getResource(imageUrl));
          forecast.setIconWhiteUrl(WeatherSmallWhiteResourceLoader.getResource(imageUrl));
          forecast.setIconBlackUrl(WeatherSmallBlackResourceLoader.getResource(imageUrl));

          info.getForecast().add(forecast);
        }
        //get location
        else if (element.getName().equalsIgnoreCase("lat")) {
          info.setLatitude(element.getText());
        }
        else if (element.getName().equalsIgnoreCase("long")) {
          info.setLatitude(element.getText());
        }
        //get local time
        else if (element.getName().equalsIgnoreCase("condition")) {
          String date = element.getAttributeValue("date");
          try {
            Date formatted = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.US).parse(date);
            info.setLocalTime(formatted);
            info.setTemp(element.getAttributeValue("temp"));
            info.setDescription(element.getAttributeValue("text"));

            int code = Integer.parseInt(element.getAttribute("code").getValue());
            String imageUrl = convertTypeCodeImage(code);
            LOG.info("Weather code for " + info.getCity() + " is " + code);
            info.setImageUrl(WeatherBigResourceLoader.getResource(imageUrl));
            info.setIconWhiteUrl(WeatherSmallWhiteResourceLoader.getResource(imageUrl));
          } catch (ParseException e) {
            LOG.error("Error retrieving local time for " + date + ": " + e.getMessage());
          }
        }
      }
    }

    //apply data of first forecast
    WeatherInfo todayForecast = info.getForecast().get(0);
    info.setForecastDate(todayForecast.getForecastDate());
    info.setHighTemp(todayForecast.getHighTemp());
    info.setLowTemp(todayForecast.getLowTemp());
    return info;
  }

  private String convertTypeCodeImage(int code) {
    String img = IMG_SUNNY_CLOUDY_1;
    if (code < 5 || (code >= 37 && code <= 39) || code == 45) {
      img = IMG_STORMY;
    }
    else if ((code >= 5 && code < 9) || code == 17 || code == 18 || code == 13 || code == 14 || code == 46 || code == 47) {
      img = IMG_SNOW_RAINY;
    }
    else if ((code >= 15 && code <= 16) || (code >= 40 && code <= 43)) {
      img = IMG_SNOW;
    }
    else if(code >= 9 && code < 13) {
      img = IMG_RAINY;
    }
    else if (code == 36 || (code >= 32 && code <= 34) || code == 31) {
      img = IMG_SUNNY;
    }
    else if (code == 35) {
      img = IMG_RAINY;
    }
    else if ((code >= 20 && code <= 25) || code == 19) {
      img = IMG_CLOUDY;
    }
    else if ((code >= 9 && code < 13)) {
      img = IMG_SUNNY_RAINY;
    }
    else if ((code >= 29 && code <= 30)) {
      img = IMG_SUNNY_CLOUDY_1;
    }
    else if ((code >= 26 && code <= 28) || code == 44) {
      img = IMG_SUNNY_CLOUDY_2;
    }
    else {
      LOG.warn("Unmapped weather code: " + code);
    }
    return img;
  }

  @Override
  public WeatherInfo getDefaultWeather() {
    final List<IServiceModel> serviceData = ServiceRegistry.getWeatherService().getServiceData();
    for(IServiceModel model : serviceData) {
      WeatherInfo info = (WeatherInfo) model;
      if(info.isDefaultLocation()) {
        return info;
      }
    }
    return null;
  }
}
