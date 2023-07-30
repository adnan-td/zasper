package parser.ast;

import parser.NodeType;

import java.util.ArrayList;
import java.util.List;

public class CallExpression extends Expression {
  public Identifier caller;
  public List<Expression> arguments = new ArrayList<>();

  public CallExpression(Identifier caller) {
    super(NodeType.CallExpression);
    this.caller = caller;
  }

  public void add_argument(Expression arg) {
    arguments.add(arg);
  }
}
