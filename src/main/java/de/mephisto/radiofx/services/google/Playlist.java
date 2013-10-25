package de.mephisto.radiofx.services.google;

import org.apache.commons.lang.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A collection of songs, identified by a name.
 */
public class Playlist extends MModel {
  private List<Song> songs = new ArrayList<Song>();

  private String name;
  private String artUrl;
  private int playlistSize = -1;

  public Playlist(String name) {
    this.name = name;
  }

  public String getArtUrl() {
    return artUrl;
  }

  public void setSize(int size) {
    playlistSize = size;
  }

  public String getName() {
    return name;
  }

  public List<Song> getSongs() {
    Collections.sort(songs);
    return songs;
  }

  public int getSize() {
    if(!songs.isEmpty())
      return songs.size();
    return playlistSize;
  }

  @Override
  public String toString() {
    return "Playlist '" + getName() + "', tracks: " + getSize();
  }

  public void setArtUrl(String artUrl) {
    this.artUrl = artUrl;
  }

  public String getDuration() {
    long durationMillis = 0;
    for(Song songs : getSongs()) {
      durationMillis+= songs.getDurationMillis();
    }
    if(durationMillis > 0) {
      durationMillis-=3600000;
      return DateFormatUtils.format(durationMillis, "HH:mm:ss");
    }
    return "";
  }

  /**
   * Checks if the given song is already part of the playlist.
   * @param compare
   * @return
   */
  public boolean containsSong(Song compare) {
    for(Song song : songs) {
      if(song.getName().toLowerCase().equalsIgnoreCase(compare.getName().toLowerCase())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the next song or null if no next song is available.
   * @return
   */
  public Song nextSong() {
    Iterator<Song> it = getSongs().iterator();
    Song activeSong = getActiveSong();
    if(activeSong == null) {
      Song song  = it.next();
      song.setActive(true);
      return song;
    }

    while(it.hasNext()) {
      Song next = it.next();
      if(next.equals(activeSong) && it.hasNext()) {
        activeSong.setActive(false);
        activeSong = it.next();
        activeSong.setActive(true);
        return activeSong;
      }
    }
    return null;
  }

  public Song getActiveSong() {
    for(Song song : getSongs()) {
      if(song.isActive()) {
        return song;
      }
    }
    return null;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Playlist && ((Playlist)obj).getMID() == this.getMID();
  }
}
