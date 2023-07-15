package tokeniser;

public class Parser {
  Token[] tokens;

  public Program getAST(String source) throws Exception {
    Program program = new Program();
    this.tokens = Tokeniser.tokenise(source);
    int i = 0;

    while (tokens[i].type != TokenType.EOF) {
      program.body.add(this.parse_statement());
      i++;
    }

    return program;
  }

  private Statement parse_statement() {
    return this.parse_expression();
  }

  private Expression parse_expression() {
    return new Expression(NodeType.BinaryExpression);
  }
}
