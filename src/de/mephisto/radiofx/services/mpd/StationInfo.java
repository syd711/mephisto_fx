package de.mephisto.radiofx.services.mpd;

import de.mephisto.radiofx.services.IServiceModel;

/**
 * Model for wrapping the radio station data.
 */
public class StationInfo implements IServiceModel {
  private String name;
  private String track;
  private String url;

  private boolean playable = true;
  private boolean infoAvailable = true;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTrack() {
    return track;
  }

  public void setTrack(String track) {
    this.track = track;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isPlayable() {
    return playable;
  }

  public void setPlayable(boolean playable) {
    this.playable = playable;
  }

  public boolean isInfoAvailable() {
    return infoAvailable;
  }

  public void setInfoAvailable(boolean infoAvailable) {
    this.infoAvailable = infoAvailable;
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null && obj instanceof StationInfo && ((StationInfo)obj).getUrl().equalsIgnoreCase(this.url);
  }
}
