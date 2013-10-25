package de.mephisto.radiofx.util;

/**
 *
 */
public class SystemUtils {

  /**
   * Returns true if VM is running on windows.
   * @return
   */
  public static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("windows");
  }
}
