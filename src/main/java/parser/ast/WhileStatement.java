package parser.ast;

import parser.NodeType;

public class WhileStatement extends Statement {
  public BinaryExpression condition;
  public BlockBody body;

  public WhileStatement(BinaryExpression condition) {
    super(NodeType.WhileStatement);
    this.condition = condition;
    this.body = new BlockBody();
  }
}
