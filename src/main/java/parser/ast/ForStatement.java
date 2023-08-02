package parser.ast;

import parser.NodeType;

public class ForStatement extends Statement {
  public Expression test;
  public Statement update;
  public Statement init;
  public BlockBody body;

  public ForStatement(Statement init, Expression test, Statement update) {
    super(NodeType.ForStatement);
    this.test = test;
    this.update = update;
    this.init = init;
    this.body = new BlockBody();
  }
}

