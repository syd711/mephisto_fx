package de.mephisto.radiofx.services.weather;

import de.mephisto.radiofx.services.IServiceModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Weather info token
 */
public class WeatherInfo implements IServiceModel {
  private List<WeatherInfo> forecast = new ArrayList<WeatherInfo>();

  private String city;
  private String country;

  private boolean defaultLocation;

  private String lowTemp;
  private String temp;
  private String highTemp;

  private String imageUrl;
  private String iconWhiteUrl;
  private String iconBlackUrl;

  private Date forecastDate;
  private Date localTime;

  private String description;

  private String id;

  private String latitude;
  private String longitude;

  private Date sunrise;
  private Date sunset;

  private double wind;

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getLowTemp() {
    return lowTemp;
  }

  public void setLowTemp(String lowTemp) {
    this.lowTemp = lowTemp;
  }

  public String getHighTemp() {
    return highTemp;
  }

  public void setHighTemp(String highTemp) {
    this.highTemp = highTemp;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Date getForecastDate() {
    return forecastDate;
  }

  public void setForecastDate(Date forecastDate) {
    this.forecastDate = forecastDate;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<WeatherInfo> getForecast() {
    return forecast;
  }

  public void setForecast(List<WeatherInfo> forecast) {
    this.forecast = forecast;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public Date getLocalTime() {
    return localTime;
  }

  public void setLocalTime(Date localTime) {
    this.localTime = localTime;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTemp() {
    return temp;
  }

  public void setTemp(String temp) {
    this.temp = temp;
  }

  public String getIconWhiteUrl() {
    return iconWhiteUrl;
  }

  public void setIconWhiteUrl(String iconWhiteUrl) {
    this.iconWhiteUrl = iconWhiteUrl;
  }

  public boolean isDefaultLocation() {
    return defaultLocation;
  }

  public void setDefaultLocation(boolean defaultLocation) {
    this.defaultLocation = defaultLocation;
  }


  public double getWind() {
    return wind;
  }

  public void setWind(double wind) {
    this.wind = wind;
  }

  public Date getSunset() {
    return sunset;
  }

  public void setSunset(Date sunset) {
    this.sunset = sunset;
  }

  public Date getSunrise() {
    return sunrise;
  }

  public void setSunrise(Date sunrise) {
    this.sunrise = sunrise;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null || !(obj instanceof WeatherInfo) ) {
      return false;
    }

    return this.getCity().equals(((WeatherInfo)obj).getCity());
  }

  public String getIconBlackUrl() {
    return iconBlackUrl;
  }

  public void setIconBlackUrl(String iconBlackUrl) {
    this.iconBlackUrl = iconBlackUrl;
  }
}
