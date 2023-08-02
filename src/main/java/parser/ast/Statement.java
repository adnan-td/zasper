package parser.ast;

import com.google.gson.Gson;
import parser.NodeType;
import tokeniser.Location;

public class Statement {
  public NodeType kind;
  public Location loc;

  public Statement(NodeType kind) {
    this.kind = kind;
    this.loc = new Location();
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}