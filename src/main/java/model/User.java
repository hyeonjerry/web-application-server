package model;

public class User {

  private final String userId;
  private final String password;
  private final String name;
  private final String email;

  public User(final String userId, final String password, final String name, final String email) {
    this.userId = userId;
    this.password = password;
    this.name = name;
    this.email = email;
  }

  public String getUserId() {
    return this.userId;
  }

  public String getPassword() {
    return this.password;
  }

  public String getName() {
    return this.name;
  }

  public String getEmail() {
    return this.email;
  }

  @Override
  public String toString() {
    return "User [userId=" + this.userId + ", password=" + this.password + ", name=" + this.name
        + ", email="
        + this.email + "]";
  }
}
