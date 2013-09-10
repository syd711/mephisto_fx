package de.mephisto.radiofx.ui;

import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.mpd.StationInfo;
import de.mephisto.radiofx.util.UIUtil;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Controls the UI for the radio
 */
public class RadioController implements ITabController {
  private static final Font RADIO_STATION_FONT = Font.font("Tahoma", FontWeight.BOLD, 32);
  private static final Font RADIO_TRACK_FONT= Font.font("Tahoma", FontWeight.NORMAL, 18);
  private static final Font RADIO_URL_FONT= Font.font("Tahoma", FontWeight.NORMAL, 12);

  private Text stationText;
  private Text trackText;
  private Text urlText;
  private VBox verticalRoot;

  private Pager pager;

  public void showDefault(BorderPane root) {
    BorderPane radioRoot = new BorderPane();
    radioRoot.setMinHeight(UIUtil.HEIGHT - 88);
    root.setCenter(radioRoot);

    verticalRoot = new VBox(20);
    verticalRoot.setPadding(new Insets(20, 0, 0, 20));
    radioRoot.setCenter(verticalRoot);

    stationText = new Text(0, 0, "");
    stationText.setFont(RADIO_STATION_FONT);
    stationText.setFill(UIUtil.COLOR_DARK_HEADER);
    verticalRoot.getChildren().add(stationText);

    trackText = new Text(0, 0, "");
    trackText.setFont(RADIO_TRACK_FONT);
    trackText.setFill(UIUtil.COLOR_DARK_HEADER);
    verticalRoot.getChildren().add(trackText);

    urlText = new Text(0, 0, "");
    urlText.setFont(RADIO_URL_FONT);
    urlText.setFill(UIUtil.COLOR_DARK_HEADER);

    verticalRoot.getChildren().add(urlText);

    final List<StationInfo> stations = ServiceRegistry.getRadioService().getStations();
    pager = new Pager(radioRoot, stations);

    updateRadioComponents(stations.get(0));
  }


  /**
   * Slides to the next weather info
   */
  public void next() {
    StationInfo info = (StationInfo) pager.next();
    UIUtil.fadeOutComponent(verticalRoot);
    updateRadioComponents(info);
    UIUtil.fadeInComponent(verticalRoot);
  }

  /**
   * Slides to the previous weather info
   */
  public void prev() {
    StationInfo info = (StationInfo) pager.prev();
    UIUtil.fadeOutComponent(verticalRoot);
    updateRadioComponents(info);
    UIUtil.fadeInComponent(verticalRoot);
  }

  /**
   * Updates the fields with the given station info.
   * @param info
   */
  private void updateRadioComponents(StationInfo info) {
    stationText.setText(formatValue(info.getName(), 22));
    trackText.setText(formatValue(info.getTrack(), 50));
    urlText.setText(formatValue(info.getUrl(), 70));
  }

  /**
   * Formats the string before output.
   * @param value
   * @param length
   * @return
   */
  private String formatValue(String value, int length)  {
    if(value != null && value.length() > length) {
      int lastWhitespace = value.lastIndexOf(" ");
      if(lastWhitespace < length) {
        length = lastWhitespace;
      }
      value = value.substring(0, length) + "...";
    }
    return value;
  }
}
