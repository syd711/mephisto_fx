package de.mephisto.radiofx.services.mpd.impl;

import de.mephisto.radiofx.services.mpd.IStationListener;
import de.mephisto.radiofx.services.mpd.RadioService;
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
public class RadioServiceImpl implements RadioService {
  private final static Logger LOG = LoggerFactory.getLogger(RadioServiceImpl.class);

  private final static String CONFIG_NAME = "mpd.properties";
  private final static String SAVE_FILE = "conf/streams.properties";

  private final static String PROPERTY_HOST = "mpd.host";
  private final static String PROPERTY_PORT = "mpd.port";

  private final static int REFRESH_INTERVAL = 3000;

  private Configuration config;
  private MPDClient client;

  private List<StationInfo> stations = new ArrayList<StationInfo>();
  private List<IStationListener> listeners = new ArrayList<IStationListener>();

  private MPDStatusListener statusListenerThread;

  public RadioServiceImpl() {
    this.config = Config.getConfiguration(CONFIG_NAME);
    String host = config.getString(PROPERTY_HOST);
    int port = config.getInt(PROPERTY_PORT, 6600);
//    client = new MPDClient(host, port);
//    client.connect();

    //listens on the playlist status
    statusListenerThread = new MPDStatusListener();
    statusListenerThread.start();

    if(client != null && client.isLocalModeEnabled()) {
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

        StreamInfoHelper.loadInfo(info);
      }
      LOG.info("Created MPD service with " + stations.size() + " stations.");
    } catch (ConfigurationException e) {
      LOG.error("Failed to load MPD configuration: " + e.getMessage(), e);
    }
  }

  @Override
  public List<StationInfo> getStations() {
    return stations;
  }

  @Override
  public void addStationListener(IStationListener listener) {
    this.listeners.add(listener);
  }

  /**
   * Reads the MPD server status to update the UI.
   */
  class MPDStatusListener extends Thread {
    private boolean running = true;

    @Override
    public void run() {
      while(running) {
        try {
          Thread.sleep(3000);
          for(IStationListener listener : listeners) {

          }
        } catch (InterruptedException e) {
          LOG.error("Error refresh MPD thread: " + e.getMessage());
        }
      }
    }
  }
}
