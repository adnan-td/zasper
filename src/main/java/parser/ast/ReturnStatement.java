package parser.ast;

import parser.NodeType;

public class ReturnStatement extends Statement {
  public Expression argument;

  public ReturnStatement(Expression argument) {
    super(NodeType.ReturnStatement);
    this.argument = argument;
  }
}
