package parser.ast;

import parser.NodeType;

public class BreakStatement extends Statement {
  public BreakStatement() {
    super(NodeType.BreakStatement);
  }
}
