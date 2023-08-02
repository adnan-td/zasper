package parser.ast;

import interpreter.ValueType;
import parser.NodeType;

public class NullLiteral extends Expression {
  public Object value = null;
  public ValueType type;

  public NullLiteral() {
    super(NodeType.NullLiteral);
  }
}
