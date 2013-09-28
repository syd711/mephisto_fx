package de.mephisto.radiofx.ui.controller;

import de.mephisto.radiofx.services.IService;
import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.mpd.StationInfo;
import de.mephisto.radiofx.ui.Pager;
import de.mephisto.radiofx.ui.UIStateController;
import de.mephisto.radiofx.util.UIUtil;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Controls the UI for the radio
 */
public class RadioUIController extends PageableUIController {
  private static final Font RADIO_STATION_FONT = Font.font("Tahoma", FontWeight.BOLD, 32);
  private static final Font RADIO_TRACK_FONT= Font.font("Tahoma", FontWeight.NORMAL, 18);
  private static final Font RADIO_URL_FONT= Font.font("Tahoma", FontWeight.NORMAL, 12);

  private Text stationText;
  private Text trackText;
  private Text urlText;

  public RadioUIController() {
    super(ServiceRegistry.getMpdService());
  }

  @Override
  public BorderPane init() {
    BorderPane tabRoot = new BorderPane();
    tabRoot.setMinHeight(UIUtil.MIN_MAIN_HEIGHT);

    VBox verticalRoot = new VBox(20);
    verticalRoot.setPadding(new Insets(20, 0, 0, 20));
    tabRoot.setCenter(verticalRoot);

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

    super.setPager(new Pager(tabRoot, ServiceRegistry.getMpdService().getServiceData()));
    super.setTabRoot(tabRoot);

    updatePage(ServiceRegistry.getMpdService().getServiceData().get(0));

    UIUtil.fadeInComponent(tabRoot);
    return tabRoot;
  }

  /**
   * Updates the fields with the given station info.
   * @param model
   */
  @Override
  public void updatePage(IServiceModel model) {
    StationInfo info = (StationInfo) model;
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

  @Override
  public IRotaryControllable push() {
    return this;
  }

  @Override
  public IRotaryControllable longPush() {
    return UIStateController.getInstance().getWeatherController();
  }
}
