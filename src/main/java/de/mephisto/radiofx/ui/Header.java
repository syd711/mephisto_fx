package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.resources.ResourceLoader;
import de.mephisto.radiofx.services.IServiceInfoListener;
import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.time.DateTimeInfo;
import de.mephisto.radiofx.services.weather.WeatherInfo;
import de.mephisto.radiofx.util.Colors;
import de.mephisto.radiofx.util.Fonts;
import de.mephisto.radiofx.util.PaneUtil;
import de.mephisto.radiofx.util.TransitionUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Contains the date and weather info
 */
public class Header implements IServiceInfoListener {
  private Text dateText;
  private Text timeText;
  private Text tempText;
  private Canvas weatherIconCanvas;
  private HBox topRoot;

  public Header(BorderPane root) {
    topRoot = new HBox();

    HBox hbox = PaneUtil.createHBox(12, Pos.CENTER_LEFT, "-fx-background-color: " + Colors.HEX_COLOR_DARK + ";", new Insets(11, 0, 11, 15));
    hbox.setMinWidth(PaneUtil.WIDTH-135);
    root.setTop(topRoot);
    topRoot.getChildren().add(hbox);

    final List<IServiceModel> serviceData = ServiceRegistry.getTimeService().getServiceData(false);
    final DateTimeInfo dateTimeInfo = (DateTimeInfo) serviceData.get(0);
    Date date = dateTimeInfo.getDate();
    String dateString = new SimpleDateFormat("d. MMMMMMMMMMMM yyyy", Locale.US).format(date);
    dateText = new Text(0, 0, dateString);
    dateText.setFont(Fonts.FONT_NORMAL_20);
    dateText.setFill(Color.WHITE);

    String timeString = new SimpleDateFormat("HH:mm").format(date);
    timeText = new Text(0, 0, timeString);
    timeText.setFont(Fonts.FONT_NORMAL_20);
    timeText.setFill(Color.WHITE);

    final Canvas imageCanvas = TransitionUtil.createImageCanvas(ResourceLoader.getResource("time.png"), 20, 20);
    hbox.getChildren().add(imageCanvas);
    hbox.getChildren().add(timeText);
    hbox.getChildren().add(dateText);

    HBox iconBox = new HBox(12);
    iconBox.setPadding(new Insets(7, 0, 0, 15));
    iconBox.setStyle("-fx-background-color: " + Colors.HEX_COLOR_DARK_2 + ";");
    topRoot.getChildren().add(iconBox);


    WeatherInfo defaultInfo = ServiceRegistry.getWeatherService().getDefaultWeather();
    String url = defaultInfo.getIconWhiteUrl();
    weatherIconCanvas = TransitionUtil.createImageCanvas(url, 32, 32);
    iconBox.getChildren().add(weatherIconCanvas);


    HBox tempBox= PaneUtil.createHBox(10, Pos.CENTER, "-fx-background-color: " + Colors.HEX_COLOR_DARK_2 + ";", new Insets(11, 15, 11, 11));
    tempBox.setMinWidth(90);
    topRoot.getChildren().add(tempBox);
    tempText = new Text(0, 0, defaultInfo.getTemp() + " °C");
    tempText.setFont(Fonts.FONT_NORMAL_20);
    tempText.setFill(Color.WHITE);

    tempBox.getChildren().add(tempText);
    TransitionUtil.fadeInComponent(topRoot);

    ServiceRegistry.getWeatherService().addServiceListener(this);
    ServiceRegistry.getTimeService().addServiceListener(this);
  }


  @Override
  public void serviceDataChanged(IServiceModel model) {
    //weather update
    if(model instanceof WeatherInfo) {
      WeatherInfo info = (WeatherInfo) model;
      if (!info.isDefaultLocation()) {
        return;
      }
      String temp = info.getTemp();
      tempText.setText(temp + " °C");

      weatherIconCanvas.getGraphicsContext2D().clearRect(0, 0, PaneUtil.WIDTH, PaneUtil.HEIGHT);
      String url = info.getIconWhiteUrl();
      ImageView weatherImage = new ImageView(new Image(url, 32, 32, false, true));
      weatherIconCanvas.getGraphicsContext2D().drawImage(weatherImage.getImage(), 0, 0);
    }
    //time update
    else if(model instanceof DateTimeInfo) {
      DateTimeInfo info = (DateTimeInfo) model;
      Date date = info.getDate();
      String dateString = new SimpleDateFormat("d. MMMMMMMMMMMM yyyy", Locale.US).format(date);
      dateText.setText(dateString);

      String timeString = new SimpleDateFormat("HH:mm").format(date);
      timeText.setText(timeString);
    }
  }

  @Override
  public boolean isChangeable() {
    return true;
  }


}
