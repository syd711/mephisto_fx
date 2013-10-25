package de.mephisto.radiofx.resources;

/**
 * Used for load images and stuff.
 */
public class ResourceLoader {
  public static String getResource(String s) {
    return ResourceLoader.class.getResource(s).toString();
  }
}
