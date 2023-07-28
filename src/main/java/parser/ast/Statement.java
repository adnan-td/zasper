package parser.ast;

import com.google.gson.Gson;
import parser.NodeType;

public class Statement {
  public NodeType kind;

  public Statement(NodeType kind) {
    this.kind = kind;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}
