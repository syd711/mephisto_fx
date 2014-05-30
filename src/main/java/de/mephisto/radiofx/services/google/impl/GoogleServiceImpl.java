package de.mephisto.radiofx.services.google.impl;

import de.mephisto.radiofx.services.IServiceModel;
import de.mephisto.radiofx.services.RefreshingService;
import de.mephisto.radiofx.services.ServiceRegistry;
import de.mephisto.radiofx.services.google.Album;
import de.mephisto.radiofx.services.google.IGoogleMusicService;
import de.mephisto.radiofx.services.google.MusicDictionary;
import de.mephisto.radiofx.ui.SplashScreen;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that implements the access method to google music.
 */
public class GoogleServiceImpl extends RefreshingService implements IGoogleMusicService {
  private static final Logger LOG = LoggerFactory.getLogger(GoogleServiceImpl.class);

  private static final int REFRESH_INTERVAL = 10000;

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
        try {
          ServiceRegistry.getMpdService().stop();
          updateLoadingText(loadingText, "Loading Google Music...");
          MusicDictionary.getInstance().createDictionary();
          notifyServiceLoaded();
        } catch (Exception e) {
          LOG.error("Failed to load Google songs: " + e.getMessage(), e);
          updateLoadingText(loadingText, "Failed to load Google Music: " + e.getMessage());
          notifyServiceLoaded();
        }
        finally {
          ServiceRegistry.getMpdService().start();
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


  @Override
  public List<IServiceModel> getServiceData(boolean forceRefresh) {
    return new ArrayList<IServiceModel>(getAlbums());
  }

  @Override
  public List<Album> getAlbums() {
    return MusicDictionary.getInstance().getAlbums();
  }
}
