package parser.ast;

import parser.NodeType;

public class AssignmentExpression extends Expression {
  public final Expression assignedTo;
  public Expression value;
  public String operator;

  public AssignmentExpression(Expression assignedTo, Expression value, String operator) {
    super(NodeType.AssignmentExpression);
    this.assignedTo = assignedTo;
    this.value = value;
    this.operator = operator;
  }
}
