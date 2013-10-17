package de.mephisto.radiofx.services.gpio;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import de.mephisto.radiofx.ui.SplashScreen;
import de.mephisto.radiofx.util.Config;
import de.mephisto.radiofx.util.SystemUtils;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Starts a C script that writes the rotary encoder values to a socket.
 */
public class RotaryEncoderService {
  private static final Logger LOG = LoggerFactory.getLogger(RotaryEncoderService.class);

  private ServerSocket myServerSocket;
  private int lastValue = 0;
  private Configuration configuration;

  private final static int LEFT = 0;
  private final static int RIGHT = 1;
  private final static int PUSH = 2;
  private final static int LONG_PUSH = 3;

  private long pushStart = 0;
  private static final long LONG_PUSH_WAIT_MILLIS = 400;

  private List<RotaryEncoderListener> listeners = new ArrayList<RotaryEncoderListener>();

  public RotaryEncoderService(SplashScreen screen) {
    if(SystemUtils.isWindows()) {
      return;
    }
    try {
      screen.setMessage("Creating Input Listeners", (screen.getProgress() + 0.1));
      configuration = Config.getConfiguration("gpio.properties");
      createSocketServer();
      connect();
      connectPushListener();
    }
    catch (IOException e) {
      LOG.error("Error creating rotary encoder service: " + e.getMessage(), e);
    }
  }

  private void connectPushListener() {
    final GpioController gpio = GpioFactory.getInstance();

    // provision gpio pin #02/Pin 13 as an input pin with its internal pull down resistor enabled
    final GpioPinDigitalInput pushButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);

    // create and register gpio pin listener
    pushButton.addListener(new GpioPinListenerDigital() {
      @Override
      public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        PinState state = event.getState();
        if (state == PinState.LOW) {
          long pushEnd = new Date().getTime();
          if(pushStart > 0 && (pushEnd-pushStart) > LONG_PUSH_WAIT_MILLIS) {
            updateListeners(LONG_PUSH);
          }
          else {
            updateListeners(PUSH);
          }
        }
        else {
          pushStart = new Date().getTime();
        }
      }
    });
  }

  /**
   * Adds a listener for the rotary encoder events.
   * @param listener
   */
  public void addListener(RotaryEncoderListener listener) {
    this.listeners.add(listener);
  }

  /**
   * Creates the socket server.
   * @throws IOException
   */
  private void createSocketServer() throws IOException {
    int port = configuration.getInt("rotary.encoder.socket");
    myServerSocket = new ServerSocket(port);
  }

  /**
   * Creates the socket server.
   * @throws IOException
   */
  private void connect() throws IOException {
    new Thread() {
      @Override
      public void run() {
        try {
          Socket client = myServerSocket.accept();
          read(client);
        } catch (IOException e) {
          LOG.error("Error connecting client: " + e.getMessage());
        }
      }
    }.start();

    //starts the rotary encoder socket client script.
    String cmd = configuration.getString("rotary.encoder.script");
    final ProcessBuilder sudo = new ProcessBuilder("sudo", cmd);
    sudo.start();
  }

  /**
   * Reads data from the socket.
   * @param client
   * @return
   * @throws IOException
   */
  private String read(Socket client) throws IOException {
    String response = null;
    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
      while (true) {
        char[] buffer = new char[200];
        int count = bufferedReader.read(buffer, 0, 200);
        response = new String(buffer, 0, count);
        int value = Integer.parseInt(response);
        if(value%4 == 0) {
          if(value > lastValue) {
            updateListeners(RIGHT);
          }
          else {
            updateListeners(LEFT);
          }
          lastValue = value;
        }
      }
    } catch (Exception e) {
      LOG.error("Error reading from client: " + e.getMessage());
      connect();
    }
    return response;
  }

  /**
   * Updates the UI
   * @param state
   */
  private void updateListeners(final int state) {
    switch (state) {
      case LEFT: {
        for(RotaryEncoderListener listener : listeners) {
          listener.previous();
        }
        break;
      }
      case RIGHT: {
        for(RotaryEncoderListener listener : listeners) {
          listener.next();
        }
        break;
      }
      case PUSH: {
        for(RotaryEncoderListener listener : listeners) {
          listener.push();
        }
        break;
      }
      case LONG_PUSH: {
        for(RotaryEncoderListener listener : listeners) {
          listener.longPush();
        }
        break;
      }
    }
  }
}