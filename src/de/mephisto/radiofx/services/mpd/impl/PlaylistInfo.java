package de.mephisto.radiofx.services.mpd.impl;

import org.apache.commons.lang.StringUtils;

/**
 * Encapsulates the result of the MPD playlistinfo command
 */
public class PlaylistInfo {
  private String title;
  private String name;

  public PlaylistInfo(String output) {
    if(!StringUtils.isEmpty(output)) {
      if(output.contains("Title") && output.contains("Name")) {
        title = output.substring(output.indexOf("Title")+6, output.indexOf("Name")).trim();
      }

      if(output.contains("Name") && output.contains("Pos")) {
        name = output.substring(output.indexOf("Name")+6, output.indexOf("Pos")).trim();
      }
    }
  }

  public String getTitle() {
    return title;
  }

  public String getName() {
    return name;
  }
}
