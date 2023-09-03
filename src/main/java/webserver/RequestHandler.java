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
import java.util.Objects;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {

  private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
  private static final String PREFIX_PATH = "./webapp";

  private final Socket connection;

  public RequestHandler(final Socket connectionSocket) {
    this.connection = connectionSocket;
  }

  @Override
  public void run() {
    log.debug("New Client Connect! Connected IP : {}, Port : {}", this.connection.getInetAddress(),
        this.connection.getPort());

    try (
        final BufferedReader br = bufferingInputStream(this.connection.getInputStream());
        final OutputStream out = this.connection.getOutputStream()
    ) {
      // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
      final String line = br.readLine();
      final RequestInfo requestInfo = extractRequestInfo(line);
      final Map<String, String> headers = extractHeaders(br);

      final DataOutputStream dos = new DataOutputStream(out);

      if (requestInfo.isPostMethod()) {
        final int contentLength = Integer.parseInt(headers.get("Content-Length"));
        final Map<String, String> body = HttpRequestUtils.parseQueryString(
            IOUtils.readData(br, contentLength)
        );

        final User user = new User(
            body.get("userId"),
            body.get("password"),
            body.get("name"),
            body.get("email")
        );
        System.out.println(user);

        response302Header(dos, "/index.html");
      } else {
        final byte[] body = Files.readAllBytes(
            new File(PREFIX_PATH + requestInfo.getUrl()).toPath()
        );

        response200Header(dos, body.length);
        responseBody(dos, body);
      }
    } catch (final IOException e) {
      log.error(e.getMessage());
    }
  }

  private BufferedReader bufferingInputStream(final InputStream in) {
    final InputStreamReader reader = new InputStreamReader(in);
    return new BufferedReader(reader);
  }

  private RequestInfo extractRequestInfo(final String request) {
    final String[] tokens = request.split(" ");
    return new RequestInfo(tokens);
  }

  private Map<String, String> extractHeaders(final BufferedReader br) throws IOException {
    String line;
    final Map<String, String> headers = new HashMap<>();
    while (!(line = br.readLine()).isEmpty()) {
      final String[] header = line.split(": ");
      headers.put(header[0].trim(), header[1].trim());
    }
    return headers;
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

  private void response302Header(final DataOutputStream dos, final String location) {
    try {
      dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
      dos.writeBytes("Location: " + location + "\r\n");
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

  private static class RequestInfo {

    private final String method;
    private final String url;
    private final String httpVersion;

    public RequestInfo(final String... tokens) {
      this.method = tokens[0];
      this.url = tokens[1];
      this.httpVersion = tokens[2];
    }

    public String getUrl() {
      return this.url;
    }

    public String getMethod() {
      return this.method;
    }

    public String getHttpVersion() {
      return this.httpVersion;
    }

    public boolean isPostMethod() {
      return this.method.equalsIgnoreCase("post");
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      final RequestInfo that = (RequestInfo) o;
      return Objects.equals(this.url, that.url) && Objects.equals(this.method, that.method)
          && Objects.equals(this.httpVersion, that.httpVersion);
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.url, this.method, this.httpVersion);
    }

    @Override
    public String toString() {
      return "RequestInfo{" +
          "method='" + this.method + '\'' +
          ", url='" + this.url + '\'' +
          ", httpVersion='" + this.httpVersion + '\'' +
          '}';
    }
  }
}
