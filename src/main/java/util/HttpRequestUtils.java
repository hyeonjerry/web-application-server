package util;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpRequestUtils {

  /**
   * @param queryString은 URL에서 ? 이후에 전달되는 field1=value1&field2=value2 형식임
   * @return
   */
  public static Map<String, String> parseQueryString(final String queryString) {
    return parseValues(queryString, "&");
  }

  /**
   * @param 쿠키 값은 name1=value1; name2=value2 형식임
   * @return
   */
  public static Map<String, String> parseCookies(final String cookies) {
    return parseValues(cookies, ";");
  }

  private static Map<String, String> parseValues(final String values, final String separator) {
    if (Strings.isNullOrEmpty(values)) {
      return Maps.newHashMap();
    }

    final String[] tokens = values.split(separator);
    return Arrays.stream(tokens).map(t -> getKeyValue(t, "=")).filter(Objects::nonNull)
        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
  }

  static Pair getKeyValue(final String keyValue, final String regex) {
    if (Strings.isNullOrEmpty(keyValue)) {
      return null;
    }

    final String[] tokens = keyValue.split(regex);
    if (tokens.length != 2) {
      return null;
    }

    return new Pair(tokens[0], tokens[1]);
  }

  public static Pair parseHeader(final String header) {
    return getKeyValue(header, ": ");
  }

  public static class Pair {

    String key;
    String value;

    Pair(final String key, final String value) {
      this.key = key.trim();
      this.value = value.trim();
    }

    public String getKey() {
      return this.key;
    }

    public String getValue() {
      return this.value;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
      result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Pair other = (Pair) obj;
      if (this.key == null) {
        if (other.key != null) {
          return false;
        }
      } else if (!this.key.equals(other.key)) {
        return false;
      }
      if (this.value == null) {
        if (other.value != null) {
          return false;
        }
      } else if (!this.value.equals(other.value)) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "Pair [key=" + this.key + ", value=" + this.value + "]";
    }
  }
}
