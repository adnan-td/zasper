package parser.ast;

import interpreter.ValueType;
import parser.NodeType;
import parser.ParseTokenType;
import tokeniser.TokenType;

public class Identifier extends Expression {
  public String symbol;
  public ValueType type;

  public Identifier(String symbol, TokenType tokenType) throws Exception {
    super(NodeType.Identifier);
    this.symbol = symbol;
    this.type = ParseTokenType.toRuntimeValueType(tokenType);
  }

  public Identifier(String symbol) {
    super(NodeType.Identifier);
    this.symbol = symbol;
  }
}