package util;

import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtilsTest {

  private static final Logger logger = LoggerFactory.getLogger(IOUtilsTest.class);

  @Test
  public void readData() throws Exception {
    final String data = "abcd123";
    final StringReader sr = new StringReader(data);
    final BufferedReader br = new BufferedReader(sr);

    logger.debug("parse body : {}", IOUtils.readData(br, data.length()));
  }
}
