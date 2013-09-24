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
  private Map<String, Genre> genres = new HashMap<String, Genre>();
  private Map<String, Album> albums = new HashMap<String, Album>();
  private Map<String, Playlist> playlists = new HashMap<String, Playlist>();
  private Map<Integer, MModel> globalDict = new HashMap<Integer, MModel>();

  private int idCounter = 0;

  /**
   * Returns the singleton instance of the dictionary.
   *
   * @return
   */
  public static MusicDictionary getInstance() {
    return instance;
  }

  public Collection<Song> getSongs() {
    return songs.values();
  }

  public Song getSong(int mid) {
    return (Song)globalDict.get(mid);
  }

  public void reset() {
    instance = new MusicDictionary();
    LOG.info("Music dictionary has been cleared.");
  }


  /**
   * Looks up the collection for the given id, might be an album or a genre, ...
   * @param id
   * @return
   */
  public Playlist getPlaylist(int id) {
    Playlist col = (Playlist)globalDict.get(id);
    return col;
  }

  /**
   * Returns a list of all albums.
   * @return
   */
  public List<Album> getAlbums() {
    List<Album> list = new ArrayList(albums.values());
    Collections.sort(list, new SongCollectionComparator());
    return list;
  }

  public Album getAlbum(int id) {
    return (Album)globalDict.get(id);
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
    addToGenre(song);
    LOG.debug("Add song " + song);
  }

  /**
   * Creates a unique id for a new mmodel.
   * @return
   */
  private void createMID(MModel model) {
    this.idCounter++;
    model.setMID(idCounter);
    globalDict.put(idCounter, model);
  }

  /**
   * Adds a playlist to the dictionary.
   * @param p
   */
  public void addPlaylist(Playlist p) {
    playlists.put(p.getName(), p);
  }

  /**
   * Adds the song to its genre or creates a new one.
   *
   * @param song
   */
  private void addToGenre(Song song) {
    if (!StringUtils.isEmpty(song.getGenre())) {
      Genre genre = null;
      if (!genres.containsKey(song.getGenre())) {
        genre = new Genre(song.getGenre());
        createMID(genre);
        genres.put(song.getAlbum(), genre);
      }
      else {
        genre = genres.get(song.getAlbum());
      }
      genre.getSongs().add(song);
    }
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



  public List<Playlist> getAlbumsOfArtist(int mid) {
    List<Playlist> lists = new ArrayList<Playlist>();
    Album album = getAlbum(mid);
    Iterator<Album> it = albums.values().iterator();
    while(it.hasNext()) {
      Album a = it.next();
      String artist = a.getArtist();
      if(!StringUtils.isEmpty(artist) && artist.equalsIgnoreCase(album.getArtist())) {
        lists.add(a);
      }
    }
    return lists;
  }

  public List<Playlist> getAlbumsOfGenre(int mid) {
    List<Playlist> lists = new ArrayList<Playlist>();
    Album album = getAlbum(mid);
    Iterator<Album> it = albums.values().iterator();
    while(it.hasNext()) {
      Album a = it.next();
      String genre = a.getGenre();
      if(!StringUtils.isEmpty(genre) && genre.equalsIgnoreCase(album.getGenre())) {
        lists.add(a);
      }
    }
    return lists;
  }

  /**
   * Searches for albums and songs.
   * @param term
   * @return
   */
  public List<Playlist> search(String term) {
    List<Playlist> lists = new ArrayList<Playlist>();
    List<Album> albums = getAlbums();
    for(Album playlist : albums) {
      if(playlist.getName().toLowerCase().contains(term.toLowerCase())) {
        lists.add(playlist);
        continue;
      }
      if(playlist.getArtist().toLowerCase().contains(term.toLowerCase())) {
        lists.add(playlist);
        continue;
      }
      for(Song song : playlist.getSongs()) {
        if(song.getName().toLowerCase().contains(term.toLowerCase())) {
          lists.add(playlist);
          continue;
        }
      }
    }
    return lists;
  }
}
