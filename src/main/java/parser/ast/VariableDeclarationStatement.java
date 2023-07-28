package parser.ast;

import interpreter.ValueType;
import parser.NodeType;
import tokeniser.TokenType;

public class VariableDeclarationStatement extends Statement {
  public String identifier;
  public Expression value;
  public ValueType type;

  public VariableDeclarationStatement(ValueType type, String identifier, Expression value) {
    super(NodeType.VariableDeclarationStatement);
    this.type = type;
    this.identifier = identifier;
    this.value = value;
  }

  public VariableDeclarationStatement(ValueType type, String identifier) {
    super(NodeType.VariableDeclarationStatement);
    this.type = type;
    this.identifier = identifier;
    this.value = null;
  }

  public VariableDeclarationStatement(TokenType type, String identifier, Expression value) {
    super(NodeType.VariableDeclarationStatement);
    switch (type) {
      case DoubleDeclaration:
        this.type = ValueType.Double;
      case IntegerDeclaration:
        this.type = ValueType.Integer;
      default:
        this.type = ValueType.Integer;
    }
    this.identifier = identifier;
    this.value = value;
  }

  public VariableDeclarationStatement(TokenType type, String identifier) {
    super(NodeType.VariableDeclarationStatement);
    switch (type) {
      case DoubleDeclaration:
        this.type = ValueType.Double;
      case IntegerDeclaration:
        this.type = ValueType.Integer;
      default:
        this.type = ValueType.Integer;
    }
    this.identifier = identifier;
    this.value = null;
  }
}
