package parser.ast;

import interpreter.ValueType;
import parser.NodeType;
import parser.ParseTokenType;
import tokeniser.TokenType;

public class VariableDeclarationStatement extends Statement {
  public String identifier;
  public Expression value;
  public ValueType type;
  public VariableDeclarationStatement lateralDeclaration = null;

  public VariableDeclarationStatement(TokenType type, String identifier, Expression value) throws Exception {
    super(NodeType.VariableDeclarationStatement);
    this.type = ParseTokenType.toRuntimeValueType(type);
    this.identifier = identifier;
    this.value = value;
  }

  public VariableDeclarationStatement(TokenType type, String identifier) throws Exception {
    this(type, identifier, null);
  }
}
