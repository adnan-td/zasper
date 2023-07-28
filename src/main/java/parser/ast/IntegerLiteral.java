package parser.ast;

import parser.NodeType;

public class IntegerLiteral extends Expression {
  public int value;

  public IntegerLiteral(int value) {
    super(NodeType.IntegerLiteral);
    this.value = value;
  }

}
