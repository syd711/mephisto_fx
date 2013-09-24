package de.mephisto.radiofx.services.mpd.impl;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.RefreshingService;
import de.mephisto.radiofx.services.mpd.StationInfo;
import de.mephisto.radiofx.util.Config;
import de.mephisto.radiofx.util.StreamInfoHelper;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class RadioServiceImpl extends RefreshingService {
  private final static Logger LOG = LoggerFactory.getLogger(RadioServiceImpl.class);

  private final static String CONFIG_NAME = "mpd.properties";
  private final static String SAVE_FILE = "conf/streams.properties";

  private final static String PROPERTY_HOST = "mpd.host";
  private final static String PROPERTY_PORT = "mpd.port";

  private final static int REFRESH_INTERVAL = 60000;

  private Configuration config;
  private MPDClient client;

  private List<IServiceModel> stations = new ArrayList<IServiceModel>();

  public RadioServiceImpl() {
    super(REFRESH_INTERVAL);

    this.config = Config.getConfiguration(CONFIG_NAME);
    String host = config.getString(PROPERTY_HOST);
    int port = config.getInt(PROPERTY_PORT, 6600);
//    client = new MPDClient(host, port);
//    client.connect();

    if (client != null && client.isLocalModeEnabled()) {
      client.executeLocalCommand("volume 99");
    }

    loadStations();
  }

  /**
   * Load stored streams
   */
  private void loadStations() {
    try {
      PropertiesConfiguration streamConfig = new PropertiesConfiguration(SAVE_FILE);

      Iterator<String> keys = streamConfig.getKeys();
      while (keys.hasNext()) {
        String key = keys.next();
        String url = streamConfig.getString(key);

        StationInfo info = new StationInfo();
        info.setUrl(url);
        stations.add(info);
      }
      refreshStations();
      LOG.info("Created MPD service with " + stations.size() + " stations.");
    } catch (ConfigurationException e) {
      LOG.error("Failed to load MPD configuration: " + e.getMessage(), e);
    }
  }

  private void refreshStations() {
    List<IServiceModel> clone = new ArrayList<IServiceModel>(stations);
    for (IServiceModel model : clone) {
      StationInfo info = (StationInfo) model;
//      StreamInfoHelper.loadInfo(info);
    }
  }

  @Override
  public List<IServiceModel> getServiceData() {
    refreshStations();
    return stations;
  }
}
