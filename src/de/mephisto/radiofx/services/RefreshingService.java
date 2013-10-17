package de.mephisto.radiofx.services;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Abstract superclass for services that regularly refresh their data to send them to the UI.
 */
public abstract class RefreshingService implements IService {
  private static final Logger LOG = LoggerFactory.getLogger(RefreshingService.class);

  private Collection<IServiceInfoListener> listeners = new ConcurrentLinkedQueue<IServiceInfoListener>();

  private long refreshInterval;
  private RefreshThread refreshThread;

  public RefreshingService(long intervalMillis) {
    this.refreshInterval = intervalMillis;
    this.refreshThread = new RefreshThread();
    this.refreshThread.start();
  }

  @Override
  public void forceRefresh() {
    getServiceData(true);
  }

  /**
   * Registers a new listener that fires once the controller service info changes.
   * @param listener
   */
  public void addServiceListener(IServiceInfoListener listener) {
    this.listeners.add(listener);
  }

  /**
   * Called once the server has some data change.
   * @param model
   */
  protected synchronized void notifyDataChange(final IServiceModel model) {
    for(final IServiceInfoListener listener : listeners) {
      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          if(listener.isChangeable()) {
            listener.serviceDataChanged(model);
          }
        }
      });
    }
  }

  /**
   * Internal refresh thread for triggering the data update.
   */
  class RefreshThread extends Thread {
    private boolean running = true;

    @Override
    public void run() {
      while (running) {
        try {
          Thread.sleep(refreshInterval);
          final List<IServiceModel> infoList = getServiceData(true);
          if(infoList == null) {
            return;
          }
          for (IServiceModel info : infoList) {
            notifyDataChange(info);
          }
        } catch (InterruptedException e) {
          LOG.error("Error in timer thread: " + e.getMessage());
        }
      }
    }
  }
}
