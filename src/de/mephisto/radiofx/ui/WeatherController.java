package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.weather.WeatherInfo;
import de.mephisto.radiofx.util.UIUtil;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class WeatherController {
  private static Font WEATHER_HEADER_BOLD_FONT = Font.font("Tahoma", FontWeight.BOLD, 22);
  private static Font WEATHER_TEMP_BOLD_FONT = Font.font("Tahoma", FontWeight.NORMAL, 60);
  private Text locationText;

  private WeatherInfo currentWeatherInfo;

  public void showDefaultWeather(BorderPane root) {
    currentWeatherInfo = ServiceRegistry.getWeatherService().getDefaultWeatherInfo();

    locationText = new Text(0, 0, currentWeatherInfo.getCity() +  ", " + currentWeatherInfo.getCountry());
    locationText.setFont(WEATHER_HEADER_BOLD_FONT);
    locationText.setFill(UIUtil.COLOR_DARK_HEADER);

    VBox vbox = new VBox(5);
    vbox.getChildren().add(locationText);

    root.setCenter(vbox);


//    final double width = locationText.getBoundsInLocal().getWidth();
//    int xPos = (int)(UIUtil.WIDTH-width)/2;
//    locationText.setX(xPos);
//    root.getChildren().add(locationText);
//
//    String url = currentWeatherInfo.getImageUrl();
//    ImageView weatherImage = new ImageView(new Image(url, 128, 128, false, true));
//    final Canvas canvas = new Canvas(UIUtil.WIDTH, UIUtil.HEIGHT);
//    final GraphicsContext gc = canvas.getGraphicsContext2D();
//    gc.drawImage(weatherImage.getImage(), 20, 80);
//    root.getChildren().add(canvas);
//
//    Text tempText = new Text(170, 160, currentWeatherInfo.getTemp() + " °C");
//    tempText.setFont(WEATHER_TEMP_BOLD_FONT);
//    tempText.setFill(UIUtil.COLOR_DARK_HEADER);
//    root.getChildren().add(tempText);
  }

  public void showNextWeather() {
    final List<WeatherInfo> weatherInfoList = ServiceRegistry.getWeatherService().getWeatherInfoList();
    Iterator<WeatherInfo> weatherInfoIterator = weatherInfoList.iterator();
    while(weatherInfoIterator.hasNext()) {
      WeatherInfo info = weatherInfoIterator.next();
      if(info == currentWeatherInfo && weatherInfoIterator.hasNext()) {
        currentWeatherInfo = weatherInfoIterator.next();
        break;
      }
    }
    if(currentWeatherInfo == null) {
      currentWeatherInfo = weatherInfoList.get(0);
    }


  }
}
