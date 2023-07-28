package parser;

import parser.ast.Statement;

import java.util.ArrayList;

public class Program extends Statement {
  public ArrayList<Statement> body = new ArrayList<>();

  public Program() {
    super(NodeType.Program);
  }
}

