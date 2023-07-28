package parser.ast;

import parser.NodeType;

public class AssignmentExpression extends Expression {
  public final Expression assignedTo;
  public Expression value;

  public AssignmentExpression(Expression assignedTo, Expression value) {
    super(NodeType.AssignmentExpression);
    this.assignedTo = assignedTo;
    this.value = value;
  }
}
