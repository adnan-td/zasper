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

  //  Expressions
  IntegerLiteral,
  DoubleLiteral,
  Identifier,
  BinaryExpression,
  AssignmentExpression,
  CallExpression,
}
