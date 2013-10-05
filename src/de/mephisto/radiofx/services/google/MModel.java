package de.mephisto.radiofx.services.google;

import de.mephisto.radiofx.services.IServiceModel;

/**
 * Superclass for all model instances.
 */
public class MModel implements IServiceModel {
  //the unique mephisto id
  private int mId;
  private boolean active = false;

  public int getMID() {
    return mId;
  }

  public void setMID(int mId) {
    this.mId = mId;
  }

  @Override
  public void setActive(boolean b) {
    active = b;
  }

  @Override
  public boolean isActive() {
    return active;
  }
}
