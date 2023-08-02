package parser.ast;

import parser.NodeType;

public class WhileStatement extends Statement {
  public Expression condition;
  public BlockBody body;

  public WhileStatement(Expression condition) {
    super(NodeType.WhileStatement);
    this.condition = condition;
    this.body = new BlockBody();
  }
}
