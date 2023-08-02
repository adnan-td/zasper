package parser.ast;

import parser.NodeType;

public class BooleanLiteral extends Expression {
  public boolean value;

  public BooleanLiteral(boolean value) {
    super(NodeType.BooleanLiteral);
    this.value = value;
  }
}
