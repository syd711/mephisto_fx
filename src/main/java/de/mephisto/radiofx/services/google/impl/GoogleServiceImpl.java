package de.mephisto.radiofx.services.google.impl;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.RefreshingService;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.services.google.MusicDictionary;
import de.mephisto.radiofx.ui.SplashScreen;
import de.mephisto.radiofx.util.Config;
import gmusic.api.impl.GoogleMusicAPI;
import gmusic.api.model.Playlist;
import gmusic.api.model.Playlists;
import gmusic.api.model.Song;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.text.Text;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Service that implements the access method to google music.
 */
public class GoogleServiceImpl extends RefreshingService implements IGoogleMusicService {
  private static final Logger LOG = LoggerFactory.getLogger(GoogleServiceImpl.class);
  private static final String CONFIG_NAME = "google.properties";
  private static final int REFRESH_INTERVAL = 10000;

  private GoogleMusicAPI api;

  public GoogleServiceImpl() {
    super(REFRESH_INTERVAL);
  }

  @Override
  public void initService(SplashScreen screen) {
  }

  @Override
  public void loadGoogleMusic(final Text loadingText) {
    Task task = new Task<Void>() {
      @Override public Void call() {
        Configuration config = Config.getConfiguration(CONFIG_NAME);
        api = new GoogleMusicAPI();
        try {
          updateLoadingText(loadingText, "Login to Google...");
          api.login(config.getString("google.login"), config.getString("google.password"));
        } catch (Exception e) {
          LOG.error("Error connecting to Google:" + e.getMessage());
          return null;
        }

        updateLoadingText(loadingText, "Loading Songs....");
        LOG.info("Loading all songs for " + this);

        try {
          Playlists lists = api.getAllPlaylists();

          updateLoadingText(loadingText, "Creating Albums....");
          for(Playlist list : lists.getPlaylists()) {
            de.mephisto.radiofx.services.google.Playlist p = playlistFor(list);
            MusicDictionary.getInstance().addPlaylist(p);
          }

          updateLoadingText(loadingText, "Creating Music Dictionary....");
          Collection<Song> songs = api.getAllSongs();
          for (Song song : songs) {
            de.mephisto.radiofx.services.google.Song mSong = songFor(song);
            MusicDictionary.getInstance().addSong(mSong);
          }
          LOG.info(this + " finished loading songs: " + songs.size() + " total");
          updateLoadingText(loadingText, "Creating UI....");
          notifyServiceLoaded();
        } catch (Exception e) {
          LOG.error("Failed to load Google songs: " + e.getMessage(), e);
          updateLoadingText(loadingText, "Failed to load Google Music: " + e.getMessage());
          notifyServiceLoaded();
        }
        return null;
      }
    };

    new Thread(task).start();
  }

  private void updateLoadingText(final Text text, final String msg) {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        text.setText(msg);
      }
    });
  }

  /**
   * Converts the google music playlist into the local model.
   *
   * @param list
   * @return
   */
  private de.mephisto.radiofx.services.google.Playlist playlistFor(Playlist list) {
    de.mephisto.radiofx.services.google.Playlist p = new de.mephisto.radiofx.services.google.Playlist(list.getTitle());
    for (Song song : list.getPlaylist()) {
      p.getSongs().add(songFor(song));
    }
    return p;
  }

  /**
   * Puts all music data from the google song api into the
   * local song model.
   *
   * @param song The song to convert.
   * @return The converted song.
   */
  private de.mephisto.radiofx.services.google.Song songFor(Song song) {
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

  @Override
  public List<IServiceModel> getServiceData(boolean forceRefresh) {
    return new ArrayList<IServiceModel>(getAlbums());
  }

  @Override
  public List<Album> getAlbums() {
    return MusicDictionary.getInstance().getAlbums();
  }
}
