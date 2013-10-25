package de.mephisto.radiofx.services.google;

import de.mephisto.radiofx.services.google.Album;

import java.util.Comparator;

/**
 * Supports different sort directions for Albums.
 */
public class AlbumComparator implements Comparator<Album> {

  public AlbumComparator() {
  }

  @Override
  public int compare(Album o1, Album o2) {
    return o1.getArtist().compareTo(o2.getArtist());
  }
}
