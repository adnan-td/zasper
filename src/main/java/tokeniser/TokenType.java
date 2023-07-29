package tokeniser;

public enum TokenType {
  Identifier,
  Equals,
  AssignmentOperator,
  Let,
  OpenParenthesis, CloseParenthesis,
  BinaryOperator,
  EOF,
  IntegerDeclaration,
  StringDeclaration,
  DoubleDeclaration,
  BooleanDeclaration,
  Double,
  Integer,
  String,
  EOL,
  Dot,
  Comma,
  Colon,
  WhileStatement,
  ForStatement,

  Indentation,
  Dedentation,
}
