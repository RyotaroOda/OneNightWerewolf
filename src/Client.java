import java.io.*;
import java.net.*;
import java.util.*;

public class Client extends Thread {
  private boolean isClosing = false;
  private Socket socket;
  private PrintWriter writer;
  private BufferedReader reader;
  private Queue<String> readBuffer = new LinkedList<>();
  private long lastAliveTime;

  Client() {
    super();
    this.start();
  }

  @Override
  public void run() {
    while (!isClosing) {
      if (lastAliveTime > 0) {
        break;
      }
      try {
        Thread.sleep(Server.SURV_INTERVAL);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    while (!isClosing) {
      try {
        updateReadBuffer();
      } catch (IOException e) {
        e.printStackTrace();
      }
      writer.println(";;SOCKET;;ALIVE?");
      try {
        Thread.sleep(Server.SURV_INTERVAL);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * サーバからServer.SILENT_TOLERANCEフィールドで<br>
   * 指定された時間よりも長く受信していないかどうかを返します。
   * 
   * @return サーバからServer.SILENT_TOLERANCEフィールドで指定された時間よりも長く受信していない場合はtrue
   */
  private boolean isConnectionSilent() {
    return System.currentTimeMillis() - lastAliveTime > Server.SILENT_TOLERANCE;
  }

  /**
   * サーバに文字列を送信します。
   * 
   * @param str 送信する文字列
   * @throws ConnectionClosedException
   */
  public void write(String str) throws ConnectionClosedException {
    if (isConnectionSilent()) {
      throw new ConnectionClosedException("Connection with server closed");
    }
    writer.println(";;APPLICATION;;" + str);
  }

  /**
   * 受信バッファに文字列を読み込みます。
   * 
   * @throws ConnectionClosedException サーバとの接続が閉じている場合
   * @throws IOException               I/Oエラーが起きた場合
   */
  private synchronized void updateReadBuffer() throws ConnectionClosedException, IOException {
    if (isConnectionSilent()) {
      throw new ConnectionClosedException("Connection with server closed");
    }

    if (!reader.ready()) {
      return;
    }

    while (reader.ready()) {
      String str = reader.readLine();
      if (str.startsWith(";;APPLICATION;;")) {
        readBuffer.add(str.substring(15));
      } else if (str.startsWith(";;SOCKET;;")) {
        switch (str.substring(10)) {
          case "ALIVE":
            break;
          case "CLOSE":
            lastAliveTime = 0L;
            throw new ConnectionClosedException("Connection with server closed");
        }
      }
    }

    lastAliveTime = System.currentTimeMillis();
  }

  /**
   * 受信バッファから一つ取り出します。
   * 
   * @return 読んだ文字列
   * @throws NoSuchElementException 受信バッファが空の場合
   */
  public String read() throws NoSuchElementException {
    return readBuffer.remove();
  }

  /**
   * 受信バッファから一つ取り出します。<br>
   * 受信バッファが空の場合は，空でなくなるまで待機します。
   * 
   * @param millis 受信バッファが空の場合に待機する際の単位時間
   * @return 読んだ文字列
   * @throws ConnectionClosedException サーバとの接続が閉じている場合
   * @throws IOException               I/Oエラーが起きた場合
   */
  public String blockingRead(long millis) throws ConnectionClosedException, IOException {
    while (true) {
      if (isReadReady()) {
        try {
          return read();
        } catch (NoSuchElementException e) {
          e.printStackTrace();
        }
      }
      try {
        Thread.sleep(millis);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 受信バッファが空でないかどうかを返します。
   * 
   * @return 受信バッファが空でなければtrue
   * @throws IOException I/Oエラーが起きた場合
   */
  public boolean isReadReady() throws ConnectionClosedException, IOException {
    if (readBuffer.isEmpty()) {
      updateReadBuffer();
    }
    return !readBuffer.isEmpty();
  }

  /**
   * サーバに接続します。
   * 
   * @return 接続に成功した場合はtrue
   * @throws IOException I/Oエラーが起きた場合
   */
  public synchronized boolean connectServer() throws IOException {
    InetAddress addr = InetAddress.getByName("localhost");//REVIEW:
    Socket tmpSocket = new Socket(addr, Server.PORT);
    System.out
        .println("Temporary Connected with " + tmpSocket.getInetAddress().getHostAddress() + ":" + tmpSocket.getPort());
    BufferedReader in = new BufferedReader(new InputStreamReader(tmpSocket.getInputStream()));
    int p = Integer.parseInt(in.readLine());

    System.out.println("Closing temporary socket...");
    tmpSocket.close();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    socket = new Socket(addr, p);
    System.out.println("Connected with " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    lastAliveTime = System.currentTimeMillis();

    return true;
  }

  /**
   * ソケット接続を閉じます。
   */
  public void close() {
    isClosing = true;

    if (writer != null) {
      writer.println(";;SOCKET;;CLOSE");
    }

    System.out.println("Closing socket...");

    if (socket != null) {
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}