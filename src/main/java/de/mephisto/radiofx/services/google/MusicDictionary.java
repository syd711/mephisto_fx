package de.mephisto.radiofx.services.google;

import de.mephisto.radiofx.util.Config;
import gmusic.api.impl.GoogleMusicAPI;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The collected music, gathered by all music providers.
 */
public class MusicDictionary {
  private final static Logger LOG = LoggerFactory.getLogger(MusicDictionary.class);
  private static final String CONFIG_NAME = "google.properties";

  private static MusicDictionary instance = null;

  private Map<String, Song> songs = new HashMap<String, Song>();
  private Map<String, Album> albums = new HashMap<String, Album>();
  private Map<String, Playlist> playlists = new HashMap<String, Playlist>();

  private GoogleMusicAPI api;
  private int idCounter = 0;

  /**
   * Returns the singleton instance of the dictionary.
   *
   * @return
   */
  public static MusicDictionary getInstance() {
    if (instance == null) {
      instance = new MusicDictionary();
      instance.api = new GoogleMusicAPI();
    }
    return instance;
  }


  public void createDictionary() throws Exception {
    Configuration config = Config.getConfiguration(CONFIG_NAME);
    api.login(config.getString("google.login"), config.getString("google.password"));

    Collection<gmusic.api.model.Song> songs = api.getAllSongs();
    LOG.info(this + " finished loading songs: " + songs.size() + " total");
    for (gmusic.api.model.Song song : songs) {
      de.mephisto.radiofx.services.google.Song fxSong = songFor(song);
      addSong(fxSong);
    }
  }

  /**
   * Returns a list of all albums.
   *
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
   *
   * @return
   */
  private void createMID(MModel model) {
    this.idCounter++;
    model.setMID(idCounter);
  }


  /**
   * Adds a playlist to the dictionary.
   *
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
      if (!album.containsSong(song)) {
        album.getSongs().add(song);
      }
    }
  }

  /**
   * Returns the album for given dict, creates a new one if it
   * does not exist yet.
   *
   * @param song The song to find the album for.
   * @return
   */
  private Album getAlbum(Song song) {
    Album album = null;
    if (!albums.containsKey(song.getAlbum())) {
      album = new Album(song.getArtist(), song.getAlbum());
      createMID(album);
      albums.put(song.getAlbum(), album);
    } else {
      album = albums.get(song.getAlbum());
    }

    if (!StringUtils.isEmpty(song.getAlbumArtUrl()) && StringUtils.isEmpty(album.getArtUrl())) {
      album.setArtUrl(song.getAlbumArtUrl());
    }
    if (StringUtils.isEmpty(album.getArtist()) && !StringUtils.isEmpty(song.getArtist())) {
      album.setArtist(song.getArtist());
    }
    if (StringUtils.isEmpty(album.getGenre()) && !StringUtils.isEmpty(song.getGenre())) {
      album.setGenre(song.getGenre());
    }

    if (song.getYear() > 0) {
      album.setYear(song.getYear());
    }
    return album;
  }


  /**
   * Puts all music data from the google song api into the
   * local song model.
   *
   * @param song The song to convert.
   * @return The converted song.
   */
  private de.mephisto.radiofx.services.google.Song songFor(gmusic.api.model.Song song) {
    de.mephisto.radiofx.services.google.Song mSong = new de.mephisto.radiofx.services.google.Song(api);
    mSong.setOriginalModel(song);

    mSong.setId(song.getId());
    mSong.setName(song.getName());

    if (!StringUtils.isEmpty(song.getAlbumArtUrl())) {
      mSong.setAlbumArtUrl("http:" + song.getAlbumArtUrl());
    }

    mSong.setAlbum(song.getAlbum());
    mSong.setArtist(song.getAlbumArtist());
    if (StringUtils.isEmpty(song.getAlbumArtist()) && !StringUtils.isEmpty(song.getArtist())) {
      mSong.setArtist(song.getArtist());
    }
    mSong.setComposer(song.getComposer());
    mSong.setTrack(song.getTrack());
    mSong.setDurationMillis(song.getDurationMillis());
    mSong.setYear(song.getYear());
    mSong.setGenre(song.getGenre());
    return mSong;
  }
}
