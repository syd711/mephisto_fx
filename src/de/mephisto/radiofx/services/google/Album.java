package de.mephisto.radiofx.services.google;

import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.mpd.IMpdService;

/**
 * The model that represents an album.
 */
public class Album extends Playlist {
  private String artist;
  private String genre;
  private int year;

  public Album(String artist, String name) {
    super(name);
    this.artist = artist;
  }

  public String getArtist() {
    return artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  @Override
  public String toString() {
    return "Album '" + getName() + "' by '" + artist + "', tracks: " + getSize();
  }

  public String getCoverId() {
    return (getArtist() + "-" + getName()).replaceAll(" ", "_")
        .replaceAll("\\?", "")
        .replaceAll("!", "")
        .replaceAll(":", "-")
        .replaceAll("ö", "oe")
        .replaceAll("ä", "ae")
        .replaceAll("ü", "ue")
        .replaceAll("\\.", "")
        .replaceAll("ß", "ss")
        .replaceAll("\\\\", "-")
        .replaceAll("'", "")
        .replaceAll("/", "-");
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public void play() {
    IMpdService service = ServiceRegistry.getMpdService();
    service.playAlbum(this);
  }
}
