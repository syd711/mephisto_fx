package de.mephisto.radiofx.services.google;

import de.mephisto.radiofx.services.IServiceStateListener;
import de.mephisto.radiofx.services.IService;
import javafx.scene.text.Text;

import java.util.List;

/**
 * The interface to be implemented by the google service.
 */
public interface IGoogleMusicService extends IService {
  List<Album> getAlbums();
  void loadGoogleMusic(Text loadingText);
}
