package parser.ast;

import interpreter.ValueType;
import parser.NodeType;
import parser.ParseTokenType;
import tokeniser.TokenType;

import java.util.ArrayList;
import java.util.List;

public class FunctionDeclaration extends Statement {
  public Identifier id;
  public List<Identifier> parameters = new ArrayList<>();
  public BlockBody body = new BlockBody();
  public ValueType returnType;

  public FunctionDeclaration(Identifier id) {
    super(NodeType.FunctionDeclaration);
    this.id = id;
  }

  public void add_argument(Identifier arg) throws Exception {
    parameters.add(arg);
  }

  public void set_return_type(TokenType type) throws Exception {
    this.returnType = ParseTokenType.toRuntimeValueType(type);
  }
}
