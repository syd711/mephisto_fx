package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.time.TimeListener;
import de.mephisto.radiofx.services.weather.WeatherInfo;
import de.mephisto.radiofx.services.weather.WeatherInfoListener;
import de.mephisto.radiofx.util.UIUtil;
import javafx.animation.FadeTransition;
import javafx.animation.FadeTransitionBuilder;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contains the date and weather info
 */
public class Header implements WeatherInfoListener, TimeListener {

  private static Font HEADER_LABEL_FONT = Font.font("Tahoma", FontPosture.REGULAR, 16);
  private static Font HEADER_LABEL_BOLD_FONT = Font.font("Tahoma", FontWeight.BOLD, 16);

  private static final int HEIGHT = 40;
  private static final int FONT_OFFSET = 26;

  private Text dateText;
  private Text timeText;
  private Text tempText;
  private GraphicsContext gc;

  public Header(BorderPane root) {
    HBox topRoot = new HBox();

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
    timeText = new Text(12, FONT_OFFSET, timeString);
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
    ImageView weatherImage = new ImageView(new Image(url, 32, 32, false, true));
    final Canvas canvas = new Canvas(32, 32);
    gc = canvas.getGraphicsContext2D();
    gc.drawImage(weatherImage.getImage(), 0, 0);
    iconBox.getChildren().add(canvas);

    HBox tempBox= new HBox(10);
    tempBox.setPadding(new Insets(11, 15, 11, 0));
    tempBox.setStyle("-fx-background-color: " + UIUtil.HEX_COLOR_DARK_2 + ";");
    topRoot.getChildren().add(tempBox);
    tempText = new Text(0, 0, defaultInfo.getTemp() + " °C");
    tempText.setFont(HEADER_LABEL_FONT);
    tempText.setFill(Color.WHITE);

    tempBox.getChildren().add(tempText);
//
    final FadeTransition fadeTransition = FadeTransitionBuilder.create()
        .duration(Duration.seconds(2))
        .node(topRoot)
        .fromValue(0)
        .toValue(1)
        .autoReverse(true)
        .build();

    fadeTransition.play();

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

    gc.clearRect(0, 0, UIUtil.WIDTH, UIUtil.HEIGHT);
    String url = info.getIconUrl();
    ImageView weatherImage = new ImageView(new Image(url, 32, 32, false, true));
    gc.drawImage(weatherImage.getImage(), UIUtil.WIDTH - 105, 4);
  }

  @Override
  public void timeChanged(Date date) {
    String dateString = new SimpleDateFormat("d. MMMMMMMMMMMM yyyy").format(date);
    dateText.setText(dateString);

    String timeString = new SimpleDateFormat("HH:mm").format(date);
    timeText.setText(timeString);
  }
}
