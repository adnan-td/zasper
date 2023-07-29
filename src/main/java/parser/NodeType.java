package parser;

public enum NodeType {
  // Statements
  ProgramStatement,
  VariableDeclarationStatement,
  WhileStatement,
  ForStatement,
  BlockStatement,

  //  Expressions
  IntegerLiteral,
  DoubleLiteral,
  Identifier,
  BinaryExpression,
  AssignmentExpression,
}
