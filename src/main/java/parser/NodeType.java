package parser;

public enum NodeType {
  // Statements
  ProgramStatement,
  VariableDeclarationStatement,
  WhileStatement,
  ForStatement,
  BlockStatement,
  FunctionDeclaration,
  ReturnStatement,
  BreakStatement,
  ContinueStatment,
  IfStatement,

  //  Expressions
  IntegerLiteral,
  DoubleLiteral,
  BooleanLiteral,
  StringLiteral,
  NullLiteral,
  Identifier,
  BinaryExpression,
  AssignmentExpression,
  CallExpression,
}
