package parser;

public enum NodeType {
  // Statements
  ProgramStatement,
  VariableDeclarationStatement,

  //  Expressions
  NumericLiteral,
  Identifier,
  BinaryExpression,
  AssignmentExpression,
}
