package de.mephisto.radiofx.services.mpd;

import de.mephisto.radiofx.services.IService;
import de.mephisto.radiofx.services.google.Album;

import java.util.List;

/**
 * The interface to be implemented by the MPD service.
 */
public interface IMpdService extends IService {
  void playStation(StationInfo info);
  void playAlbum(Album album);

  boolean isRadioMode();
}
