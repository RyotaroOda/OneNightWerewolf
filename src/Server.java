import java.io.*;
import java.net.*;
import java.util.*;

class ConnectionClosedException extends IOException {
  public ConnectionClosedException() {
    super();
  }

  public ConnectionClosedException(String message) {
    super(message);
  }
}

public class Server extends Thread {
  public static final int PORT = 8080;
  public static final long SURV_INTERVAL = 1_000;
  public static final long SILENT_TOLERANCE = 25_000;
  private static final int CONNECTION_TIMEOUT = 4_000;
  private int totalClients = 0;
  private boolean isClosing = false;
  private ArrayList<ServerSocket> serverSocketList = new ArrayList<>();
  private ArrayList<Socket> socketList = new ArrayList<>();
  private ArrayList<PrintWriter> writerList = new ArrayList<>();
  private ArrayList<BufferedReader> readerList = new ArrayList<>();
  private ArrayList<Queue<String>> readBufferList = new ArrayList<>();
  private ArrayList<Long> lastAliveTimeList = new ArrayList<>();

  Server() {
    super();
    this.start();
  }

  @Override
  public void run() {
    while (!isClosing) {
      if (totalClients > 0) {
        break;
      }
      try {
        Thread.sleep(SURV_INTERVAL);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    while (!isClosing) {
      for (int i = 0; i < totalClients; i++) {
        try {
          updateReadBuffer(i);
        } catch (IOException e) {
          e.printStackTrace();
        }
        if (isClosing) {
          break;
        }
        try {
          Thread.sleep(SURV_INTERVAL / totalClients);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * 接続しているクライアントの合計数を返します。
   * 
   * @return 接続しているクライアントの合計数
   */
  public int getTotalClients() {
    return totalClients;
  }

  /**
   * 指定されたクライアントからServer.SILENT_TOLERANCEフィールドで<br>
   * 指定された時間よりも長く受信していないかどうかを返します。
   * 
   * @param n クライアントの番号（開始番号は0）
   * @return 指定されたクライアントからServer.SILENT_TOLERANCEフィールドで指定された時間よりも長く受信していない場合はtrue
   */
  private boolean isConnectionSilent(int n) {
    return System.currentTimeMillis() - lastAliveTimeList.get(n) > SILENT_TOLERANCE;
  }

  /**
   * 指定されたクライアントに文字列を送信します。
   * 
   * @param n   クライアントの番号（開始番号は0）
   * @param str 送信する文字列
   * @throws ConnectionClosedException 指定されたクライアントとの接続が閉じている場合
   */
  public void write(int n, String str) throws ConnectionClosedException {
    if (isConnectionSilent(n)) {
      throw new ConnectionClosedException("Connection with client " + n + " closed");
    }
    writerList.get(n).println(";;APPLICATION;;" + str);
  }

  /**
   * すべてのクライアントに同じ文字列を送信します。
   * 
   * @param str 送信する文字列
   * @throws ConnectionClosedException 接続が閉じているクライアントがある場合
   */
  public void writeAll(String str) throws ConnectionClosedException {
    for (int i = 0; i < totalClients; i++) {
      write(i, str);
    }
  }

  /**
   * 指定されたクライアントからの受信バッファに文字列を読み込みます。
   * 
   * @param n クライアントの番号（開始番号は0）
   * @throws ConnectionClosedException 指定されたクライアントとの接続が閉じている場合
   * @throws IOException               I/Oエラーが起きた場合
   */
  private synchronized void updateReadBuffer(int n) throws ConnectionClosedException, IOException {
    if (isConnectionSilent(n)) {
      throw new ConnectionClosedException("Connection with client " + n + " closed");
    }

    BufferedReader reader = readerList.get(n);

    if (!reader.ready()) {
      return;
    }

    Queue<String> queue = readBufferList.get(n);

    while (reader.ready()) {
      String str = reader.readLine();
      if (str.startsWith(";;APPLICATION;;")) {
        queue.add(str.substring(15));
      } else if (str.startsWith(";;SOCKET;;")) {
        switch (str.substring(10)) {
          case "ALIVE?":
            writerList.get(n).println(";;SOCKET;;ALIVE");
            break;
          case "CLOSE":
            lastAliveTimeList.set(n, 0L);
            throw new ConnectionClosedException("Connection with client " + n + " closed");
        }
      }
    }

    lastAliveTimeList.set(n, System.currentTimeMillis());
  }

  /**
   * 指定されたクライアントからの受信バッファから一つ取り出します。
   * 
   * @param n クライアントの番号（開始番号は0）
   * @return 読んだ文字列
   * @throws NoSuchElementException 指定されたクライアントからの受信バッファが空の場合
   */
  public String read(int n) throws NoSuchElementException {
    return readBufferList.get(n).remove();
  }

  /**
   * 指定されたクライアントからの受信バッファから一つ取り出します。<br>
   * 受信バッファが空の場合は，空でなくなるまで待機します。
   * 
   * @param n      クライアントの番号（開始番号は0）
   * @param millis 受信バッファが空の場合に待機する際の単位時間
   * @return 読んだ文字列
   * @throws ConnectionClosedException 指定されたクライアントとの接続が閉じている場合
   * @throws IOException               I/Oエラーが起きた場合
   */
  public String blockingRead(int n, long millis) throws ConnectionClosedException, IOException {
    while (true) {
      if (isReadReady(n)) {
        try {
          return read(n);
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
   * 指定されたクライアントの受信バッファが空でないかどうかを返します。
   * 
   * @param n クライアントの番号（開始番号は0）
   * @return 指定されたクライアントの受信バッファが空でなければtrue
   * @throws IOException I/Oエラーが起きた場合
   */
  public boolean isReadReady(int n) throws ConnectionClosedException, IOException {
    if (readBufferList.get(n).isEmpty()) {
      updateReadBuffer(n);
    }
    return !readBufferList.get(n).isEmpty();
  }

  private synchronized boolean registerSocket(ServerSocket serverSocket, Socket socket) {
    PrintWriter pw = null;
    BufferedReader br = null;
    try {
      pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
      br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    serverSocketList.add(serverSocket);
    socketList.add(socket);
    writerList.add(pw);
    readerList.add(br);
    readBufferList.add(new LinkedList<String>());
    lastAliveTimeList.add(System.currentTimeMillis());
    totalClients++;
    return true;
  }

  /**
   * クライアントからの接続を待ち受けます。<br>
   * タイムアウト時間はServer.CONNECTION_TIMEOUTフィールドで指定されます。
   * 
   * @return 接続に成功した場合はtrue
   * @throws IOException I/Oエラーが起きた場合
   */
  public boolean waitConnection() throws IOException {
    int initialClients = totalClients;
    ServerSocket defaultSocket = new ServerSocket(PORT);
    defaultSocket.setSoTimeout(CONNECTION_TIMEOUT);

    try {
      Socket tmpSocket = defaultSocket.accept();
      System.out.println(
          "Temporary Connected with " + tmpSocket.getInetAddress().getHostAddress() + ":" + tmpSocket.getPort());
      PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(tmpSocket.getOutputStream())),
          true);
      ServerSocket newServerSocket = new ServerSocket(PORT + 1 + totalClients);
      out.println(newServerSocket.getLocalPort());
      Socket socket = newServerSocket.accept();
      System.out.println("Connected with " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
      if (!registerSocket(newServerSocket, socket)) {
        System.err.println("Getting output or input stream of connection with "
            + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " failed");
        socket.close();
        newServerSocket.close();
      }
      System.out.println("Closing temporary socket...");
      tmpSocket.close();
    } catch (SocketTimeoutException e) {
    }

    for (int i = 0; i < totalClients; i++) {
      updateReadBuffer(i);
    }

    defaultSocket.close();

    return totalClients > initialClients;
  }

  /**
   * 最も新しく参加したクライアントとの接続を閉じます。
   * getTotalClients()の戻り値は1小さくなります。
   * 接続しているクライアントが存在しない場合は何も行いません。
   */
  public synchronized void disconnectLatest() {
    if (getTotalClients() < 1) {
      return;
    }

    int idx = getTotalClients() - 1;

    totalClients--;

    lastAliveTimeList.remove(idx);

    writerList.get(idx).close();
    writerList.remove(idx);

    readBufferList.remove(idx);

    try {
      readerList.get(idx).close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    readerList.remove(idx);

    System.out.println("Closing connection with " + socketList.get(idx).getInetAddress().getHostAddress() + ":"
        + socketList.get(idx).getPort());

    try {
      socketList.get(idx).close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    socketList.remove(idx);

    try {
      serverSocketList.get(idx).close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    serverSocketList.remove(idx);
  }

  /**
   * すべてのソケット接続を閉じます。
   */
  public void closeAll() {
    isClosing = true;

    for (int i = 0; i < totalClients; i++) {
      writerList.get(i).println(";;SOCKET;;CLOSE");
    }

    System.out.println("Closing sockets...");
    for (int i = 0; i < totalClients; i++) {
      try {
        socketList.get(i).close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        serverSocketList.get(i).close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void writeAll(TagType ready) {
  }
}