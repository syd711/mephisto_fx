package de.mephisto.radiofx.services.mpd.impl;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.RefreshingService;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.mpd.IMpdService;
import de.mephisto.radiofx.services.mpd.StationInfo;
import de.mephisto.radiofx.ui.SplashScreen;
import de.mephisto.radiofx.util.Config;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class MpdServiceImpl extends RefreshingService implements IMpdService {
  private final static Logger LOG = LoggerFactory.getLogger(MpdServiceImpl.class);

  private final static String CONFIG_NAME = "mpd.properties";
  private final static String CONFIG_FILE = "conf/streams.properties";

  private final static String PROPERTY_HOST = "mpd.host";
  private final static String PROPERTY_PORT = "mpd.port";

  private final static String PROPERTY_ACTIVE_STATION = "active.station";

  private final static int REFRESH_INTERVAL = 3000;

  private MPDClient client;

  private StationInfo activeStation;
  private PropertiesConfiguration streamConfig;

  private List<IServiceModel> stations = new ArrayList<IServiceModel>();

  public MpdServiceImpl() {
    super(REFRESH_INTERVAL);
  }

  /**
   * Load stored streams
   */
  @Override
  public void initService(SplashScreen screen) {
    screen.setMessage("Connecting to MPD Server", (screen.getProgress() + 0.15));
    Configuration config = Config.getConfiguration(CONFIG_NAME);
    String host = config.getString(PROPERTY_HOST);
    int port = config.getInt(PROPERTY_PORT, 6600);
    client = new MPDClient(host, port);
    client.connect();

    screen.setMessage("Loading Radio Stations", (screen.getProgress() + 0.1));
    if (client.isLocalModeEnabled()) {
      client.executeLocalCommand("volume 99");
    }

    try {
      streamConfig = new PropertiesConfiguration(CONFIG_FILE);

      Iterator<String> keys = streamConfig.getKeys();
      while (keys.hasNext()) {
        String key = keys.next();
        if (!key.contains(".url")) {
          continue;
        }

        String id = key.substring(0, key.indexOf("."));
        String url = streamConfig.getString(key);
        StationInfo info = new StationInfo(Integer.parseInt(id));
        info.setUrl(url);
        String nameKey = id + ".name";
        if (streamConfig.containsKey(nameKey)) {
          info.setName(streamConfig.getString(nameKey));
        }

        stations.add(info);
      }

      if(streamConfig.containsKey(PROPERTY_ACTIVE_STATION)) {
        int id = Integer.parseInt(streamConfig.getString(PROPERTY_ACTIVE_STATION));
        for (IServiceModel model : stations) {
          StationInfo info = (StationInfo) model;
          if(info.getId() == id) {
            info.setActive(true);
            break;
          }
        }
      }

    } catch (Exception e) {
      LOG.error("Error initializing MPD service: " + e.getMessage(), e);
    }


    LOG.info("Created MPD service with " + stations.size() + " stations.");
  }

  /**
   * Updates each station name and current track.
   */
  private void refreshStations() {
    List<IServiceModel> clone = new ArrayList<IServiceModel>(stations);
    for (IServiceModel model : clone) {
      StationInfo info = (StationInfo) model;
      if (info.isActive()) {
        PlaylistInfo current = client.playlistInfo();
        boolean modifiedName = info.applyPlaylistInfo(current);
        if (modifiedName) {
          saveStations();
        }
      }
    }
  }

  /**
   * Saves the stations with updates stations names.
   */
  private void saveStations() {
    try {
      streamConfig.clear();
      for (IServiceModel model : stations) {
        StationInfo info = (StationInfo) model;
        streamConfig.addProperty(info.getId() + ".url", info.getUrl());

        if (!StringUtils.isEmpty(info.getName())) {
          streamConfig.addProperty(info.getId() + ".name", info.getName());
        }
      }

      if(activeStation != null) {
        streamConfig.addProperty(PROPERTY_ACTIVE_STATION, activeStation.getId());
      }
      streamConfig.save(new File(CONFIG_FILE));
      LOG.info("Saved updated streams.properties");
    } catch (ConfigurationException e) {
      LOG.error("Failed to store streams: " + e.getMessage());
    }
  }

  @Override
  public List<IServiceModel> getServiceData() {
    refreshStations();
    return stations;
  }

  @Override
  public void playStation(StationInfo info) {
    if (activeStation != null) {
      activeStation.setActive(false);
    }
    activeStation = info;
    activeStation.setTrack("");
    info.setActive(true);
    saveStations();
    String url = info.getUrl();
    client.play(url);
  }

  @Override
  public void playAlbum(Album album) {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
