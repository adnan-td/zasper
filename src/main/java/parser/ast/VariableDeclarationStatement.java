package parser.ast;

import interpreter.RuntimeValue;
import interpreter.ValueType;
import parser.NodeType;

public class VariableDeclarationStatement extends Statement {
  public String identifier;
  public Expression value;

  public VariableDeclarationStatement(String identifier, Expression value) {
    super(NodeType.VariableDeclarationStatement);
    this.identifier = identifier;
    this.value = value;
  }

  public VariableDeclarationStatement(String identifier) {
    super(NodeType.VariableDeclarationStatement);
    this.identifier = identifier;
    this.value = null;
  }
}



