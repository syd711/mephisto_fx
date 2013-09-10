package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.time.TimeListener;
import de.mephisto.radiofx.services.weather.WeatherInfo;
import de.mephisto.radiofx.services.weather.WeatherInfoListener;
import de.mephisto.radiofx.util.UIUtil;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Contains the date and weather info
 */
public class Header implements WeatherInfoListener, TimeListener {

  private static Font HEADER_LABEL_FONT = Font.font("Tahoma", FontPosture.REGULAR, 16);
  private static Font HEADER_LABEL_BOLD_FONT = Font.font("Tahoma", FontWeight.BOLD, 16);


  private Text dateText;
  private Text timeText;
  private Text tempText;
  private Canvas weatherIconCanvas;
  private HBox topRoot;

  public Header(BorderPane root) {
    topRoot = new HBox();

    HBox hbox = new HBox(15);
    hbox.setPadding(new Insets(11, 142, 11, 15));
    hbox.setStyle("-fx-background-color: " + UIUtil.HEX_COLOR_DARK + ";");
    root.setTop(topRoot);
    topRoot.getChildren().add(hbox);

    Date date = ServiceRegistry.getTimeService().getTime();
    String dateString = new SimpleDateFormat("d. MMMMMMMMMMMM yyyy").format(date);
    dateText = new Text(0, 0, dateString);
    dateText.setFont(HEADER_LABEL_FONT);
    dateText.setFill(Color.WHITE);

    String timeString = new SimpleDateFormat("HH:mm").format(date);
    timeText = new Text(0, 0, timeString);
    timeText.setFont(HEADER_LABEL_BOLD_FONT);
    timeText.setFill(Color.WHITE);

    hbox.getChildren().add(timeText);
    hbox.getChildren().add(dateText);

    HBox iconBox = new HBox(12);
    iconBox.setPadding(new Insets(5, 15, 0, 15));
    iconBox.setStyle("-fx-background-color: " + UIUtil.HEX_COLOR_DARK_2 + ";");
    topRoot.getChildren().add(iconBox);

    WeatherInfo defaultInfo = ServiceRegistry.getWeatherService().getDefaultWeatherInfo();
    String url = defaultInfo.getIconUrl();
    weatherIconCanvas = UIUtil.createImageCanvas(url, 32, 32);
    iconBox.getChildren().add(weatherIconCanvas);

    HBox tempBox= new HBox(10);
    tempBox.setPadding(new Insets(11, 15, 11, 0));
    tempBox.setStyle("-fx-background-color: " + UIUtil.HEX_COLOR_DARK_2 + ";");
    topRoot.getChildren().add(tempBox);
    tempText = new Text(0, 0, defaultInfo.getTemp() + " °C");
    tempText.setFont(HEADER_LABEL_FONT);
    tempText.setFill(Color.WHITE);

    tempBox.getChildren().add(tempText);
    UIUtil.fadeInComponent(topRoot);

    ServiceRegistry.getWeatherService().addWeatherListener(this);
    ServiceRegistry.getTimeService().addTimeListener(this);
  }

  @Override
  public void weatherChanged(WeatherInfo info) {
    if (!info.isDefaultLocation()) {
      return;
    }
    String temp = info.getTemp();
    tempText.setText(temp + " °C");

    weatherIconCanvas.getGraphicsContext2D().clearRect(0, 0, UIUtil.WIDTH, UIUtil.HEIGHT);
    String url = info.getIconUrl();
    ImageView weatherImage = new ImageView(new Image(url, 32, 32, false, true));
    weatherIconCanvas.getGraphicsContext2D().drawImage(weatherImage.getImage(), 0, 0);
  }

  @Override
  public void timeChanged(Date date) {
    String dateString = new SimpleDateFormat("d. MMMMMMMMMMMM yyyy", Locale.US).format(date);
    dateText.setText(dateString);

    String timeString = new SimpleDateFormat("HH:mm").format(date);
    timeText.setText(timeString);

  }
}
