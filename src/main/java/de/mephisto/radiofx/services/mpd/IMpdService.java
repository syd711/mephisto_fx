package de.mephisto.radiofx.services.mpd;

import de.mephisto.radiofx.services.IService;
import de.mephisto.radiofx.services.google.Album;

import java.util.List;

/**
 * The interface to be implemented by the MPD service.
 */
public interface IMpdService extends IService {
  /**
   * Playback of the given radio station
   * @param info
   */
  void playStation(StationInfo info);

  /**
   * Playback of the given album.
   * @param album
   */
  void playAlbum(Album album);

  /**
   * Returns true if the mpd service is running in radio mode.
   * @return
   */
  boolean isRadioMode();
}
