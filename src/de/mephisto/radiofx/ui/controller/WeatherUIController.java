package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.weather.WeatherInfo;
import de.mephisto.radiofx.ui.Pager;
import de.mephisto.radiofx.util.UIUtil;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * UIController for the weather infos.
 */
public class WeatherUIController extends PageableUIController {
  private static final Font WEATHER_HEADER_BOLD_FONT = Font.font("Tahoma", FontWeight.BOLD, 22);
  private static final Font WEATHER_DETAILS_FONT = Font.font("Tahoma", FontWeight.NORMAL, 14);
  private static final Font WEATHER_FORECAST_FONT = Font.font("Tahoma", FontWeight.NORMAL, 16);
  private static final Font WEATHER_TEMP_BOLD_FONT = Font.font("Tahoma", FontWeight.NORMAL, 60);

  private static final int IMAGE_SIZE = 128;

  private Text locationText;
  private Text tempText;

  private Text maxTempText;
  private Text minTempText;
  private Text descriptionText;
  private Text sunriseText;
  private Text sunsetText;

  private Canvas weatherIconCanvas;
  private boolean forecastMode = false;

  private HBox mainSection;
  private WeatherInfo activeModel;

  @Override
  public BorderPane init() {
    locationText = new Text(0, 0, "");
    locationText.setFont(WEATHER_HEADER_BOLD_FONT);
    locationText.setFill(UIUtil.COLOR_DARK_HEADER);

    BorderPane tabRoot = new BorderPane();

    VBox verticalRoot = new VBox(5);
    verticalRoot.setMinHeight(170);
    verticalRoot.setAlignment(Pos.TOP_CENTER);
    verticalRoot.setPadding(new Insets(5, 0, 5, 0));
    tabRoot.setCenter(verticalRoot);
    verticalRoot.setAlignment(Pos.CENTER);
    verticalRoot.getChildren().add(locationText);

    super.setPagingRoot(verticalRoot);

    mainSection = new HBox(15);
    mainSection.setAlignment(Pos.CENTER);
    mainSection.setPadding(new Insets(0, 0, 0, 10));
    mainSection.setMinHeight(125);
    verticalRoot.getChildren().add(mainSection);

    createDefaultInfo();

    //add page
    Pager pager = new Pager(tabRoot, ServiceRegistry.getWeatherService(), this);
    super.setPager(pager);
    super.setTabRoot(tabRoot);

    WeatherInfo currentWeatherInfo = ServiceRegistry.getWeatherService().getDefaultWeather();
    updatePage(currentWeatherInfo);

    UIUtil.fadeInComponent(tabRoot);
    return tabRoot;
  }

  /**
   * Creates the layout with the default weather info.
   */
  private void createDefaultInfo() {
    WeatherInfo currentWeatherInfo = ServiceRegistry.getWeatherService().getDefaultWeather();

    //image
    String url = currentWeatherInfo.getImageUrl();
    this.weatherIconCanvas = UIUtil.createImageCanvas(url, IMAGE_SIZE, IMAGE_SIZE);
    mainSection.getChildren().add(weatherIconCanvas);

    VBox centerVertical = new VBox(5);
    centerVertical.setAlignment(Pos.CENTER);

    tempText = new Text(0, 0, "");
    tempText.setFont(WEATHER_TEMP_BOLD_FONT);
    tempText.setFill(UIUtil.COLOR_DARK_HEADER);
    centerVertical.getChildren().add(tempText);
    mainSection.getChildren().add(centerVertical);

    descriptionText = new Text(0, 0, currentWeatherInfo.getDescription());
    descriptionText.setFont(WEATHER_DETAILS_FONT);
    descriptionText.setFill(UIUtil.COLOR_DARK_HEADER);
    centerVertical.getChildren().add(descriptionText);

    //detail info
    VBox temps = new VBox(5);
    temps.setMinWidth(128);
    temps.setPadding(new Insets(20, 20, 0, 15));

    minTempText = new Text(0, 0, "");
    minTempText.setFont(WEATHER_DETAILS_FONT);
    minTempText.setFill(UIUtil.COLOR_DARK_HEADER);
    temps.getChildren().add(minTempText);

    maxTempText = new Text(0, 0, "");
    maxTempText.setFont(WEATHER_DETAILS_FONT);
    maxTempText.setFill(UIUtil.COLOR_DARK_HEADER);
    temps.getChildren().add(maxTempText);

    sunriseText = new Text(0, 0, "");
    sunriseText.setFont(WEATHER_DETAILS_FONT);
    sunriseText.setFill(UIUtil.COLOR_DARK_HEADER);
    temps.getChildren().add(sunriseText);

    sunsetText = new Text(0, 0, "");
    sunsetText.setFont(WEATHER_DETAILS_FONT);
    sunsetText.setFill(UIUtil.COLOR_DARK_HEADER);
    temps.getChildren().add(sunsetText);

    mainSection.getChildren().add(temps);
  }

  /**
   * Creates the layout components for the forecast.
   */
  private void createForecastInfo() {
    final List<WeatherInfo> forecast = activeModel.getForecast();
    for(WeatherInfo info : forecast) {
      VBox infoBox = new VBox(15);
      infoBox.setPadding(new Insets(0,10,0,10));
      infoBox.setAlignment(Pos.CENTER);

      Text dayText = new Text();
      dayText.setFill(UIUtil.COLOR_DARK_HEADER);
      dayText.setFont(WEATHER_FORECAST_FONT);
      SimpleDateFormat format = new SimpleDateFormat("EEE");
      dayText.setText(format.format(info.getForecastDate()));
      infoBox.getChildren().add(dayText);

      final Canvas imageCanvas = UIUtil.createImageCanvas(info.getIconBlackUrl(), 32, 32);
      infoBox.getChildren().add(imageCanvas);

      Text tempText = new Text();
      tempText.setFill(UIUtil.COLOR_DARK_HEADER);
      tempText.setFont(WEATHER_DETAILS_FONT);
      tempText.setText(info.getLowTemp() + "-" + info.getHighTemp() + " °C");
      infoBox.getChildren().add(tempText);

      mainSection.getChildren().add(infoBox);
    }
  }

  /**
   * Assigns all values of the current weather info to the corresponding
   * components.
   */
  @Override
  public void updatePage(IServiceModel model) {
    this.activeModel = (WeatherInfo) model;

    //location
    locationText.setText(activeModel.getCity() +  ", " + activeModel.getCountry());

    if(!forecastMode) {

      //icon
      weatherIconCanvas.getGraphicsContext2D().clearRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
      String url = activeModel.getImageUrl();
      ImageView weatherImage = new ImageView(new Image(url, IMAGE_SIZE, IMAGE_SIZE, false, true));
      weatherIconCanvas.getGraphicsContext2D().drawImage(weatherImage.getImage(), 0, 0);

      //temps
      tempText.setText(activeModel.getTemp() + " °C");
      maxTempText.setText("Max: " + activeModel.getHighTemp()+ " °C");
      minTempText.setText("Min: " + activeModel.getLowTemp() + " °C");

      //description
      descriptionText.setText(activeModel.getDescription());

      SimpleDateFormat format = new SimpleDateFormat("HH:mm");
      sunsetText.setText("Sunset: " + format.format(activeModel.getSunset()));
      sunriseText.setText("Sunrise: " + format.format(activeModel.getSunrise()));
    }
    else {
      //no refresh for forecast
    }
  }

  @Override
  public void push() {
    forecastMode = !forecastMode;

    final FadeTransition outFader = UIUtil.createOutFader(mainSection);
    outFader.onFinishedProperty().set(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        mainSection.getChildren().clear();
        if(forecastMode) {
          createForecastInfo();
        }
        else {
          createDefaultInfo();
        }
        updatePage(activeModel);
        UIUtil.fadeInComponent(mainSection);
      }
    });
    outFader.play();
  }
}
