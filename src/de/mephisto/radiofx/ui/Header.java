package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.time.TimeListener;
import de.mephisto.radiofx.services.weather.WeatherInfo;
import de.mephisto.radiofx.services.weather.WeatherInfoListener;
import de.mephisto.radiofx.util.UIUtil;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Contains the date and weather info
 */
public class Header extends Rectangle implements WeatherInfoListener, TimeListener {

  private static Font HEADER_LABEL_FONT = Font.font("Tahoma", FontPosture.REGULAR, 18);
  private static Font HEADER_LABEL_BOLD_FONT = Font.font("Tahoma", FontWeight.BOLD, 18);

  private static final int HEIGHT = 40;
  private static final int FONT_OFFSET = 26;

  private Text dateText;
  private Text timeText;
  private Text tempText;
  private GraphicsContext gc;

  public Header(Group root) {
    //date and time rect
    super(0, 0, UIUtil.WIDTH, HEIGHT);
    this.setFill(Color.valueOf("#333333"));

    Date date = ServiceRegistry.getTimeService().getTime();
    String dateString = new SimpleDateFormat("d. MMMMMMMMMMMM yyyy").format(date);
    dateText = new Text(80, FONT_OFFSET, dateString);
    dateText.setFont(HEADER_LABEL_FONT);
    dateText.setFill(Color.WHITE);

    String timeString = new SimpleDateFormat("HH:mm").format(date);
    timeText = new Text(15, FONT_OFFSET, timeString);
    timeText.setFont(HEADER_LABEL_BOLD_FONT);
    timeText.setFill(Color.WHITE);

    root.getChildren().add(this);
    root.getChildren().add(dateText);
    root.getChildren().add(timeText);

    //weather rect
    Rectangle weatherInfo = new Rectangle(UIUtil.WIDTH - 120, 0, UIUtil.WIDTH, HEIGHT);
    weatherInfo.setFill(Color.valueOf("#464D4C"));
    root.getChildren().add(weatherInfo);

    WeatherInfo defaultInfo = ServiceRegistry.getWeatherService().getDefaultWeatherInfo();
    tempText = new Text(UIUtil.WIDTH - 60, FONT_OFFSET, defaultInfo.getTemp() + " °C");
    tempText.setFont(HEADER_LABEL_FONT);
    tempText.setFill(Color.WHITE);
    root.getChildren().add(tempText);

    String url = defaultInfo.getIconUrl();
    ImageView weatherImage = new ImageView(new Image(url, 32, 32, false, true));
    final Canvas canvas = new Canvas(UIUtil.WIDTH, UIUtil.HEIGHT);
    gc = canvas.getGraphicsContext2D();
    gc.drawImage(weatherImage.getImage(), UIUtil.WIDTH - 105, 4);
    root.getChildren().add(canvas);

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
