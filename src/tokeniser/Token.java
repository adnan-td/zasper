package tokeniser;

public class Token {
  String value;
  TokenType type;

  public Token(String value, TokenType type) {
    this.value = value;
    this.type = type;
  }

  @Override
  public String toString() {
    return String.format("Token: { value: \"%s\", type: \"%s\" }", value, type);
  }
}

enum TokenType {
  Number,
  Identifier,
  Equals,
  Let,
  OpenParanthesis, CloseParanthesis,
  BinaryOperator,
  EOF,
}
