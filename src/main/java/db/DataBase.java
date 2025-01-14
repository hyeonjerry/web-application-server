package db;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import model.User;

public class DataBase {

  private static final Map<String, User> users = Maps.newHashMap();

  public static void addUser(final User user) {
    users.put(user.getUserId(), user);
  }

  public static User findUserById(final String userId) {
    return users.get(userId);
  }

  public static Collection<User> findAll() {
    return users.values();
  }
}
