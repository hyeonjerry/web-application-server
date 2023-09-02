package util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Map;
import org.junit.Test;
import util.HttpRequestUtils.Pair;

public class HttpRequestUtilsTest {

  @Test
  public void parseQueryString() {
    String queryString = "userId=javajigi";
    Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
    assertThat(parameters.get("userId"), is("javajigi"));
    assertThat(parameters.get("password"), is(nullValue()));

    queryString = "userId=javajigi&password=password2";
    parameters = HttpRequestUtils.parseQueryString(queryString);
    assertThat(parameters.get("userId"), is("javajigi"));
    assertThat(parameters.get("password"), is("password2"));
  }

  @Test
  public void parseQueryString_null() {
    Map<String, String> parameters = HttpRequestUtils.parseQueryString(null);
    assertThat(parameters.isEmpty(), is(true));

    parameters = HttpRequestUtils.parseQueryString("");
    assertThat(parameters.isEmpty(), is(true));

    parameters = HttpRequestUtils.parseQueryString(" ");
    assertThat(parameters.isEmpty(), is(true));
  }

  @Test
  public void parseQueryString_invalid() {
    final String queryString = "userId=javajigi&password";
    final Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
    assertThat(parameters.get("userId"), is("javajigi"));
    assertThat(parameters.get("password"), is(nullValue()));
  }

  @Test
  public void parseCookies() {
    final String cookies = "logined=true; JSessionId=1234";
    final Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies);
    assertThat(parameters.get("logined"), is("true"));
    assertThat(parameters.get("JSessionId"), is("1234"));
    assertThat(parameters.get("session"), is(nullValue()));
  }

  @Test
  public void getKeyValue() throws Exception {
    final Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");
    assertThat(pair, is(new Pair("userId", "javajigi")));
  }

  @Test
  public void getKeyValue_invalid() throws Exception {
    final Pair pair = HttpRequestUtils.getKeyValue("userId", "=");
    assertThat(pair, is(nullValue()));
  }

  @Test
  public void parseHeader() throws Exception {
    final String header = "Content-Length: 59";
    final Pair pair = HttpRequestUtils.parseHeader(header);
    assertThat(pair, is(new Pair("Content-Length", "59")));
  }
}
