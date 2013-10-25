package de.mephisto.radiofx.services.mpd;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.mpd.impl.PlaylistInfo;
import org.apache.commons.lang.StringUtils;

/**
 * Model for wrapping the radio station data.
 */
public class StationInfo implements IServiceModel {
  private String name;
  private String track;
  private String url;
  private int id;

  private boolean active;

  public StationInfo(int id) {
    this.id = id;
  }

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

  @Override
  public boolean equals(Object obj) {
    return obj != null && obj instanceof StationInfo && ((StationInfo)obj).getUrl().equalsIgnoreCase(this.url);
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isActive() {
    return active;
  }

  /**
   * Applies the info that has been read via telnet to the model.
   * @param current
   */
  public boolean applyPlaylistInfo(PlaylistInfo current) {
    boolean modifiedName = false;
    if(StringUtils.isEmpty(name) || !name.equals(current.getName())) {
      if(!StringUtils.isEmpty(current.getName()) && !current.getName().equals(this.name)) {
        this.name = current.getName();
        modifiedName = true;
      }
    }

    if(!StringUtils.isEmpty(current.getTitle())) {
      this.track = current.getTitle();
    }
    return modifiedName;
  }

  @Override
  public String toString() {
    if(!StringUtils.isEmpty(name)) {
      return "Station '" + name + "'";
    }
    return "Station '" + url + "'";
  }

  public int getId() {
    return id;
  }
}
