package parser.ast;

import parser.NodeType;

public class Expression extends Statement {
  public Expression expression;

  public Expression(NodeType kind) {
    super(kind);
  }
}