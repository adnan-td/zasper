package parser.ast;

import parser.NodeType;

public class IfStatement extends Statement {
  public IfStatement alternate;
  public Expression test;
  public BlockBody consequent;

  public IfStatement(Expression test) {
    super(NodeType.IfStatement);
    this.test = test;
    this.consequent = new BlockBody();
  }

  public void set_alternate(IfStatement alternate) {
    this.alternate = alternate;
  }

  public void add_consequent(Statement st) {
    this.consequent.body.add(st);
  }
}
