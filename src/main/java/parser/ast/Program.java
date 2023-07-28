package parser.ast;

import parser.NodeType;

import java.util.ArrayList;

public class Program extends Statement {
  public ArrayList<Statement> body = new ArrayList<>();

  public Program() {
    super(NodeType.ProgramStatement);
  }
}

