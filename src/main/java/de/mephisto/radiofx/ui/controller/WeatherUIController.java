package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.weather.WeatherInfo;
import de.mephisto.radiofx.ui.Footer;
import de.mephisto.radiofx.ui.Pager;
import de.mephisto.radiofx.ui.UIStateController;
import de.mephisto.radiofx.util.Colors;
import de.mephisto.radiofx.util.Fonts;
import de.mephisto.radiofx.util.PaneUtil;
import de.mephisto.radiofx.util.TransitionUtil;
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
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * UIController for the weather infos.
 */
public class WeatherUIController extends PageableUIController {
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

  public WeatherUIController() {
    super(ServiceRegistry.getWeatherService());
  }

  @Override
  public BorderPane init() {
    locationText = new Text(0, 0, "");
    locationText.setFont(Fonts.WEATHER_LOCATION_FONT);
    locationText.setFill(Colors.COLOR_DARK_HEADER);

    BorderPane tabRoot = new BorderPane();
    tabRoot.setMinHeight(PaneUtil.MIN_MAIN_HEIGHT);

    VBox verticalRoot = PaneUtil.createVBox(15, Pos.TOP_CENTER, null, new Insets(15, 0, 5, 0));
    tabRoot.setCenter(verticalRoot);
    verticalRoot.getChildren().add(locationText);

    super.setPagingRoot(verticalRoot);


    mainSection = PaneUtil.createHBox(15, Pos.TOP_CENTER, null, new Insets(0, 0, 0, 10));
    mainSection.setMinHeight(130);
    verticalRoot.getChildren().add(mainSection);

    createDefaultInfo();

    //add page
    Pager pager = new Pager(tabRoot, ServiceRegistry.getWeatherService().getServiceData(false));
    super.setPager(pager);

    WeatherInfo currentWeatherInfo = ServiceRegistry.getWeatherService().getDefaultWeather();
    updatePage(currentWeatherInfo);
    return tabRoot;
  }

  /**
   * Creates the layout with the default weather info.
   */
  private void createDefaultInfo() {
    WeatherInfo currentWeatherInfo = ServiceRegistry.getWeatherService().getDefaultWeather();
    if(currentWeatherInfo == null) {
      return;
    }

    //image
    String url = currentWeatherInfo.getImageUrl();
    this.weatherIconCanvas = TransitionUtil.createImageCanvas(url, IMAGE_SIZE, IMAGE_SIZE);
    mainSection.getChildren().add(weatherIconCanvas);

    VBox centerVertical = new VBox(30);
    centerVertical.setAlignment(Pos.CENTER);

    tempText = new Text(0, 0, "");
    tempText.setFont(Fonts.WEATHER_TEMP_FONT);
    tempText.setFill(Colors.COLOR_DARK_HEADER);
    centerVertical.getChildren().add(tempText);
    mainSection.getChildren().add(centerVertical);

    descriptionText = new Text(0, 0, currentWeatherInfo.getDescription());
    descriptionText.setFont(Fonts.WEATHER_DESCR_FONT);
    descriptionText.setFill(Colors.COLOR_DARK_HEADER);
    centerVertical.getChildren().add(descriptionText);

    //detail info
    VBox temps = new VBox(10);
    temps.setMinWidth(128);
    temps.setPadding(new Insets(20, 20, 0, 15));

    minTempText = PaneUtil.createText("", Fonts.FONT_NORMAL_20);
    temps.getChildren().add(minTempText);

    maxTempText = PaneUtil.createText("", Fonts.FONT_NORMAL_20);
    temps.getChildren().add(maxTempText);

    sunriseText = PaneUtil.createText("", Fonts.FONT_NORMAL_20);
    temps.getChildren().add(sunriseText);

    sunsetText = PaneUtil.createText("", Fonts.FONT_NORMAL_20);
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
      infoBox.setMinWidth(PaneUtil.WIDTH/5-30);
      infoBox.setAlignment(Pos.CENTER);

      Text dayText = new Text();
      dayText.setFill(Colors.COLOR_DARK_HEADER);
      dayText.setFont(Fonts.FONT_NORMAL_20);
      SimpleDateFormat format = new SimpleDateFormat("EEE");
      dayText.setText(format.format(info.getForecastDate()));
      infoBox.getChildren().add(dayText);

      final Canvas imageCanvas = TransitionUtil.createImageCanvas(info.getIconBlackUrl(), 64, 64);
      infoBox.getChildren().add(imageCanvas);

      Text tempText = new Text();
      tempText.setFill(Colors.COLOR_DARK_HEADER);
      tempText.setFont(Fonts.FONT_NORMAL_20);
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
      mainSection.getChildren().clear();
      createForecastInfo();
    }
  }

  @Override
  public int getFooterId() {
    return Footer.FOOTER_WEATHER;
  }

  @Override
  public IRotaryControllable push() {
    forecastMode = !forecastMode;
    final FadeTransition outFader = TransitionUtil.createOutFader(mainSection);
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
        TransitionUtil.fadeInComponent(mainSection);
      }
    });
    outFader.play();
    return this;
  }

  @Override
  public IRotaryControllable longPush() {
    return UIStateController.getInstance().getGoogleNaviController();
  }
}
