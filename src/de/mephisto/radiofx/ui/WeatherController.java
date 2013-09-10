package de.mephisto.radiofx.ui;

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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 */
public class WeatherController {
  private static final Font WEATHER_HEADER_BOLD_FONT = Font.font("Tahoma", FontWeight.BOLD, 22);
  private static final Font WEATHER_DETAILS_FONT = Font.font("Tahoma", FontWeight.NORMAL, 16);
  private static final Font WEATHER_TEMP_BOLD_FONT = Font.font("Tahoma", FontWeight.NORMAL, 60);

  private static final Color FONT_COLOR = UIUtil.COLOR_DARK_HEADER;

  private static final int IMAGE_SIZE = 128;

  private Text locationText;
  private Text tempText;

  private Text maxTempText;
  private Text minTempText;
  private Text descriptionText;


  private VBox verticalRoot;
  private Canvas weatherIconCanvas;

  private Pager pager;

  public void showDefaultWeather(BorderPane root) {
    WeatherInfo currentWeatherInfo = ServiceRegistry.getWeatherService().getDefaultWeatherInfo();

    locationText = new Text(0, 0, currentWeatherInfo.getCity() +  ", " + currentWeatherInfo.getCountry());
    locationText.setFont(WEATHER_HEADER_BOLD_FONT);
    locationText.setFill(FONT_COLOR);

    BorderPane mainRoot = new BorderPane();
    root.setCenter(mainRoot);

    verticalRoot = new VBox(5);
    verticalRoot.setMinHeight(170);
    verticalRoot.setAlignment(Pos.TOP_CENTER);
    verticalRoot.setPadding(new Insets(5, 0, 5, 0));
    mainRoot.setCenter(verticalRoot);
    verticalRoot.setAlignment(Pos.CENTER);
    verticalRoot.getChildren().add(locationText);

    HBox hBox = new HBox(15);
    hBox.setAlignment(Pos.CENTER);
    hBox.setPadding(new Insets(0, 0, 0, 10));
    verticalRoot.getChildren().add(hBox);

    //image
    String url = currentWeatherInfo.getImageUrl();
    this.weatherIconCanvas = UIUtil.createImageCanvas(url, IMAGE_SIZE, IMAGE_SIZE);
    hBox.getChildren().add(weatherIconCanvas);

    //temp
    tempText = new Text(0, 0, currentWeatherInfo.getTemp() + " °C");
    tempText.setFont(WEATHER_TEMP_BOLD_FONT);
    tempText.setFill(FONT_COLOR);
    hBox.getChildren().add(tempText);

    //detail info
    VBox temps = new VBox(5);
    temps.setMinWidth(128);
    temps.setPadding(new Insets(28, 20, 0, 15));

    maxTempText = new Text(0, 0, "Max " + currentWeatherInfo.getHighTemp() + " °C");
    maxTempText.setFont(WEATHER_DETAILS_FONT);
    maxTempText.setFill(FONT_COLOR);
    temps.getChildren().add(maxTempText);

    minTempText = new Text(0, 0, "Min " + currentWeatherInfo.getLowTemp() + " °C");
    minTempText.setFont(WEATHER_DETAILS_FONT);
    minTempText.setFill(FONT_COLOR);
    temps.getChildren().add(minTempText);

    descriptionText = new Text(0, 0, currentWeatherInfo.getDescription());
    descriptionText.setFont(WEATHER_DETAILS_FONT);
    descriptionText.setFill(FONT_COLOR);
    temps.getChildren().add(descriptionText);

    hBox.getChildren().add(temps);

    //add page
    pager = new Pager(mainRoot, ServiceRegistry.getWeatherService().getWeatherInfoList());
  }

  /**
   * Slides to the next weather info
   */
  public void showNextWeather() {
    WeatherInfo info = (WeatherInfo) pager.next();
    UIUtil.fadeOutComponent(verticalRoot);
    updateWeatherComponents(info);
    UIUtil.fadeInComponent(verticalRoot);
  }

  /**
   * Assigns all values of the current weather info to the correspondig
   * components.
   */
  private void updateWeatherComponents(WeatherInfo info) {
    //location
    locationText.setText(info.getCity() +  ", " + info.getCountry());

    //icon
    weatherIconCanvas.getGraphicsContext2D().clearRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
    String url = info.getImageUrl();
    ImageView weatherImage = new ImageView(new Image(url, IMAGE_SIZE, IMAGE_SIZE, false, true));
    weatherIconCanvas.getGraphicsContext2D().drawImage(weatherImage.getImage(), 0, 0);

    //temps
    tempText.setText(info.getTemp() + " °C");
    maxTempText.setText(info.getHighTemp()+ " °C");
    minTempText.setText(info.getLowTemp() + " °C");

    //description
    descriptionText.setText(info.getDescription());
  }
}
