package tokeniser;

public class Token {
  public String value;
  public TokenType type;

  public Token(String value, TokenType type) {
    this.value = value;
    this.type = type;
  }

  @Override
  public String toString() {
    return String.format("Token: { value: \"%s\", type: \"%s\" }", value, type);
  }
}
