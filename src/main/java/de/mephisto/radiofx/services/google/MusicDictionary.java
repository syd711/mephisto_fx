package de.mephisto.radiofx.services.google;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The collected music, gathered by all music providers.
 */
public class MusicDictionary {
  private final static Logger LOG = LoggerFactory.getLogger(MusicDictionary.class);

  private static MusicDictionary instance = new MusicDictionary();

  private Map<String, Song> songs = new HashMap<String, Song>();
  private Map<String, Album> albums = new HashMap<String, Album>();
  private Map<String, Playlist> playlists = new HashMap<String, Playlist>();

  private int idCounter = 0;

  /**
   * Returns the singleton instance of the dictionary.
   *
   * @return
   */
  public static MusicDictionary getInstance() {
    return instance;
  }

  /**
   * Returns a list of all albums.
   * @return
   */
  public List<Album> getAlbums() {
    List<Album> list = new ArrayList(albums.values());
    Collections.sort(list, new AlbumComparator());
    return list;
  }

  /**
   * Adds a song to the dictionary, updates all sub-dictionaries afterwards.
   *
   * @param song The song to add.
   */
  public void addSong(Song song) {
    createMID(song);
    songs.put(song.getId(), song);
    addToAlbum(song);
  }

  /**
   * Creates a unique id for a new mmodel.
   * @return
   */
  private void createMID(MModel model) {
    this.idCounter++;
    model.setMID(idCounter);
  }


  /**
   * Adds a playlist to the dictionary.
   * @param p
   */
  public void addPlaylist(Playlist p) {
    playlists.put(p.getName(), p);
  }

  /**
   * Creates an album for the song if it does not exist yet.
   *
   * @param song The song to add to the album.
   */
  public void addToAlbum(Song song) {
    if (!StringUtils.isEmpty(song.getAlbum())) {
      //create the regular dict entry and add song to album
      Album album = getAlbum(song);
      if(!album.containsSong(song)) {
        album.getSongs().add(song);
      }
    }
  }

  /**
   * Returns the album for given dict, creates a new one if it
   * does not exist yet.
   * @param song  The song to find the album for.
   * @return
   */
  private Album getAlbum(Song song) {
    Album album = null;
    if (!albums.containsKey(song.getAlbum())) {
      album = new Album(song.getArtist(), song.getAlbum());
      createMID(album);
      albums.put(song.getAlbum(), album);
    }
    else {
      album = albums.get(song.getAlbum());
    }

    if(!StringUtils.isEmpty(song.getAlbumArtUrl()) && StringUtils.isEmpty(album.getArtUrl())) {
      album.setArtUrl(song.getAlbumArtUrl());
    }
    if(StringUtils.isEmpty(album.getArtist()) && !StringUtils.isEmpty(song.getArtist())) {
      album.setArtist(song.getArtist());
    }
    if(StringUtils.isEmpty(album.getGenre()) && !StringUtils.isEmpty(song.getGenre())) {
      album.setGenre(song.getGenre());
    }

    if(song.getYear() > 0) {
      album.setYear(song.getYear());
    }
    return album;
  }
}
