package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.weather.WeatherInfo;
import de.mephisto.radiofx.util.UIUtil;
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

/**
 * UIController for the weather infos.
 */
public class WeatherController extends PageableUIController {
  private static final Font WEATHER_HEADER_BOLD_FONT = Font.font("Tahoma", FontWeight.BOLD, 22);
  private static final Font WEATHER_DETAILS_FONT = Font.font("Tahoma", FontWeight.NORMAL, 14);
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

    HBox hBox = new HBox(15);
    hBox.setAlignment(Pos.CENTER);
    hBox.setPadding(new Insets(0, 0, 0, 10));
    verticalRoot.getChildren().add(hBox);

    //image
    WeatherInfo currentWeatherInfo = ServiceRegistry.getWeatherService().getDefaultWeather();
    String url = currentWeatherInfo.getImageUrl();
    this.weatherIconCanvas = UIUtil.createImageCanvas(url, IMAGE_SIZE, IMAGE_SIZE);
    hBox.getChildren().add(weatherIconCanvas);

    //temps
    VBox mainTemp = new VBox(5);
    mainTemp.setAlignment(Pos.CENTER);
    tempText = new Text(0, 0, "");
    tempText.setFont(WEATHER_TEMP_BOLD_FONT);
    tempText.setFill(UIUtil.COLOR_DARK_HEADER);
    mainTemp.getChildren().add(tempText);
    hBox.getChildren().add(mainTemp);

    descriptionText = new Text(0, 0, currentWeatherInfo.getDescription());
    descriptionText.setFont(WEATHER_DETAILS_FONT);
    descriptionText.setFill(UIUtil.COLOR_DARK_HEADER);
    mainTemp.getChildren().add(descriptionText);

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

    hBox.getChildren().add(temps);

    //add page
    Pager pager = new Pager(tabRoot, ServiceRegistry.getWeatherService());
    super.setPager(pager);
    super.setTabRoot(tabRoot);

    updatePage(currentWeatherInfo);

    UIUtil.fadeInComponent(tabRoot);
    return tabRoot;
  }

  /**
   * Assigns all values of the current weather info to the corresponding
   * components.
   */
  @Override
  public void updatePage(IServiceModel model) {
    WeatherInfo info = (WeatherInfo) model;
    //location
    locationText.setText(info.getCity() +  ", " + info.getCountry());

    //icon
    weatherIconCanvas.getGraphicsContext2D().clearRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
    String url = info.getImageUrl();
    ImageView weatherImage = new ImageView(new Image(url, IMAGE_SIZE, IMAGE_SIZE, false, true));
    weatherIconCanvas.getGraphicsContext2D().drawImage(weatherImage.getImage(), 0, 0);

    //temps
    tempText.setText(info.getTemp() + " °C");
    maxTempText.setText("Max: " + info.getHighTemp()+ " °C");
    minTempText.setText("Min: " + info.getLowTemp() + " °C");

    //description
    descriptionText.setText(info.getDescription());

    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    sunsetText.setText("Sunset: " + format.format(info.getSunset()));
    sunriseText.setText("Sunrise: " + format.format(info.getSunrise()));
  }
}
