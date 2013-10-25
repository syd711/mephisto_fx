package de.mephisto.radiofx.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for accessing the different config files.
 */
public class Config {
  private final static Logger LOG = LoggerFactory.getLogger(Config.class);

  public final static String CONFIG_FOLDER = "conf/";

  public static Configuration getConfiguration(String name) {
    try {
      Configuration config = new PropertiesConfiguration(getConfigFile(name));
      return config;
    } catch (Throwable e) {
      LOG.error("Error loading " + name + " config: " + e.getMessage(), e);
    }
    return null;
  }

  private static String getConfigFile(String config) {
    return CONFIG_FOLDER + config;
  }
}
