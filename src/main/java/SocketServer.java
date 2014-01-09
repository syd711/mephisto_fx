import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class SocketServer {
  public static void main(String[] args) throws Exception {
    final ServerSocket myServerSocket = new ServerSocket(2106);
    new Thread() {
      @Override
      public void run() {
        try {
          Socket client = myServerSocket.accept();
          read(client);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }.start();
  }

  /**
   * Reads data from the socket.
   *
   * @param client
   * @return
   * @throws IOException
   */
  private static String read(Socket client) throws IOException {
    String response = null;
    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
      while (true) {
        char[] buffer = new char[200];
        int count = bufferedReader.read(buffer, 0, 200);
        response = new String(buffer, 0, count);
        String[] split = response.split("#");
        for(String action : split) {
          int value = Integer.parseInt(action);
          System.out.println("Rotary Value: " + value);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return response;
  }
}
