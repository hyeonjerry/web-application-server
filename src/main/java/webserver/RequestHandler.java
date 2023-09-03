package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;
import util.IOUtils;

public class RequestHandler extends Thread {

  private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
  private static final String PREFIX_PATH = "./webapp";

  private final Socket connection;

  public RequestHandler(final Socket connectionSocket) {
    this.connection = connectionSocket;
  }

  private static String[] readAndTokenize(final BufferedReader br) throws IOException {
    final String line = br.readLine();
    return line.split(" ");
  }

  @Override
  public void run() {
    log.debug("New Client Connect! Connected IP : {}, Port : {}", this.connection.getInetAddress(),
        this.connection.getPort());

    try (
        final InputStream in = this.connection.getInputStream();
        final OutputStream out = this.connection.getOutputStream();
        final InputStreamReader reader = new InputStreamReader(in);
        final BufferedReader br = new BufferedReader(reader)
    ) {
      // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
      final String[] tokens = readAndTokenize(br);
      final String httpMethod = tokens[0];
      final String resourcePath = tokens[1];

      String header;
      final Map<String, Pair> headers = new HashMap<>();
      while (!(header = br.readLine()).isEmpty()) {
        final Pair pair = HttpRequestUtils.parseHeader(header);
        headers.put(pair.getKey(), pair);
      }

      if (httpMethod.equalsIgnoreCase("post")) {
        final Pair pair = headers.get("Content-Length");
        final int contentLength = Integer.parseInt(pair.getValue());
        final String body = IOUtils.readData(br, contentLength);
        final Map<String, String> values = HttpRequestUtils.parseQueryString(body);
        final User user = new User(
            values.get("userId"),
            values.get("password"),
            values.get("name"),
            values.get("email")
        );
        System.out.println(user);
      }

      final DataOutputStream dos = new DataOutputStream(out);
      final byte[] body = Files.readAllBytes(new File(PREFIX_PATH + resourcePath).toPath());
      response200Header(dos, body.length);
      responseBody(dos, body);
    } catch (final IOException e) {
      log.error(e.getMessage());
    }
  }

  private void response200Header(final DataOutputStream dos, final int lengthOfBodyContent) {
    try {
      dos.writeBytes("HTTP/1.1 200 OK \r\n");
      dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
      dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
      dos.writeBytes("\r\n");
    } catch (final IOException e) {
      log.error(e.getMessage());
    }
  }

  private void responseBody(final DataOutputStream dos, final byte[] body) {
    try {
      dos.write(body, 0, body.length);
      dos.flush();
    } catch (final IOException e) {
      log.error(e.getMessage());
    }
  }
}
