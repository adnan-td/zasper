package parser.ast;

import parser.NodeType;

import java.util.ArrayList;
import java.util.List;

public class BlockBody extends Statement {
  public List<Statement> body = new ArrayList<>();

  public BlockBody() {
    super(NodeType.BlockStatement);
  }
}
