package parser.ast;

import parser.NodeType;

public class StringLiteral extends Expression {
  public String value;

  public StringLiteral(String value) {
    super(NodeType.StringLiteral);
    this.value = value;
  }
}
