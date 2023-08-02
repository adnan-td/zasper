package tokeniser;

public class Token {
  public String value;
  public TokenType type;
  public Location location;

  public Token(String value, TokenType type, int start, int end) {
    this.value = value;
    this.type = type;
    this.location = new Location(start, end);
  }

  @Override
  public String toString() {
    return String.format("Token: { value: \"%s\", type: %s }", value, type);
//    return String.format("Token: { value: \"%s\", type: %s }\n%s", value, type, location);
  }
}
