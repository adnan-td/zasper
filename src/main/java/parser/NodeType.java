package parser;

public enum NodeType {
  // Statements
  ProgramStatement,
  VariableDeclarationStatement,

  //  Expressions
  IntegerLiteral,
  DoubleLiteral,
  Identifier,
  BinaryExpression,
  AssignmentExpression,
}
