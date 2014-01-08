package de.mephisto.radiofx.services.mpd.impl;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.RefreshingService;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.google.Song;
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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class MpdServiceImpl extends RefreshingService implements IMpdService {
  private final static Logger LOG = LoggerFactory.getLogger(MpdServiceImpl.class);

  private final static int MODE_RADIO = 0;
  private final static int MODE_MUSIC = 1;



  private final static String CONFIG_NAME = "mpd.properties";
  private final static String CONFIG_FILE = "conf/streams.properties";

  private final static String PROPERTY_HOST = "mpd.host";
  private final static String PROPERTY_PORT = "mpd.port";

  private final static String PROPERTY_ACTIVE_STATION = "active.station";

  private final static int REFRESH_INTERVAL = 2000;

  private MPDClient client;

  private StationInfo activeStation;
  private Album activeAlbum;

  private List<IServiceModel> stations = new ArrayList<IServiceModel>();

  private long playbackTime = 0l;
  private int mode = MODE_RADIO;

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
    LOG.info("Connecting to " + host + ":" + port);
    client = new MPDClient(host, port);
    client.connect();

    screen.setMessage("Loading Radio Stations", (screen.getProgress() + 0.1));
    if (client.isLocalModeEnabled()) {
      client.executeLocalCommand("volume 95");
    }

    try {
      PropertiesConfiguration streamConfig = new PropertiesConfiguration(CONFIG_FILE);
      this.stations = loadStations(streamConfig);

      if(streamConfig.containsKey(PROPERTY_ACTIVE_STATION)) {
        int id = Integer.parseInt(streamConfig.getString(PROPERTY_ACTIVE_STATION));
        for (IServiceModel model : stations) {
          StationInfo info = (StationInfo) model;
          if(info.getId() == id) {
            playStation(info);
            if(!StringUtils.isEmpty(info.getName())) {
              screen.setStatusInfo("Playing \"" + info.getName() + "\"");
            }
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
   * Loads the station infos from a properties file and returns the representing model
   * @return
   * @throws ConfigurationException
   * @param streamConfig
   */
  private List<IServiceModel> loadStations(PropertiesConfiguration streamConfig) throws ConfigurationException {
    List<IServiceModel> stationInfoList = new ArrayList<IServiceModel>();

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
      else {
        info.setName(url);
      }

      stationInfoList.add(info);
    }
    return stationInfoList;
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
        if(current != null) {
          boolean modifiedName = info.applyPlaylistInfo(current);
          if (modifiedName) {
            saveStations();
          }
        }
      }
    }
  }

  /**
   * Resolves the active MPD playback and applies it to the album.
   */
  private void refreshAlbum() {
    long currentDuration = new Date().getTime()-playbackTime;
    Song activeSong = activeAlbum.getActiveSong();
    if(playbackTime > 0 && activeSong != null && currentDuration > activeSong.getDurationMillis()) {
      Song song = activeAlbum.nextSong();
      activeSong.setActive(false);
      if(song != null) {
        LOG.info("Playing next song: " + song);
        playAlbum(activeAlbum);
      }
      else {
        LOG.info("Reached on of playback for " + activeAlbum);
      }
    }
  }

  /**
   * Saves the stations with updates stations names.
   */
  private synchronized void saveStations() {
    try {
      PropertiesConfiguration streamConfig = new PropertiesConfiguration(CONFIG_FILE);
      List<IServiceModel> stations = loadStations(streamConfig);
      for (IServiceModel model : stations) {
        StationInfo info = (StationInfo) model;
        streamConfig.setProperty(info.getId() + ".url", info.getUrl());

        if (!StringUtils.isEmpty(info.getName())) {
          streamConfig.setProperty(info.getId() + ".name", info.getName());
        }
      }

      if(activeStation != null) {
        streamConfig.setProperty(PROPERTY_ACTIVE_STATION, activeStation.getId());
      }
      streamConfig.save(new File(CONFIG_FILE));
      LOG.info("Saved updated streams.properties");
    } catch (ConfigurationException e) {
      LOG.error("Failed to store streams: " + e.getMessage());
    }
  }

  @Override
  public List<IServiceModel> getServiceData(boolean forceRefresh) {
    //this is a little bit tricky here since we use this service by to UI controller
    //this call maybe a delegation of another service controller to fix the thread refresh.
    if(mode == MODE_RADIO) {
      refreshStations();
      return stations;
    }
    else {
      refreshAlbum();
      return new ArrayList<IServiceModel>(activeAlbum.getSongs());
    }
  }

  /**
   * Switches the service to different modes.
   * @param mode
   */
  private void setMode(int mode) {
    this.mode = mode;
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

    //switch at this point to avoid too early refresh with wrong station.
    setMode(MODE_RADIO);
    String url = info.getUrl();
    client.play(url);
  }

  @Override
  public void playAlbum(Album album) {
    if(activeAlbum != null) {
      activeAlbum.setActive(false);
    }
    activeAlbum = album;
    activeAlbum.setActive(true);
    Song song = activeAlbum.getActiveSong();
    song.setActive(true);

    //switch at this point to avoid too early refresh with empty active song.
    setMode(MODE_MUSIC);
    try {
      final String playbackUrl = song.getPlaybackUrl();
      playbackTime = client.play(playbackUrl);
    } catch (Exception e) {
      LOG.error("Error executing playback of '" + song.getName() + "': " + e.getMessage(), e);
    }
  }

  @Override
  public boolean isRadioMode() {
    return mode == MODE_RADIO;
  }
}
