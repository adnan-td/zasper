package parser;

import interpreter.ValueType;
import tokeniser.TokenType;

public class ParseTokenType {

  public static ValueType toRuntimeValueType(TokenType tokenType) {
    switch (tokenType) {
      case DoubleDeclaration:
        return ValueType.Double;
      case IntegerDeclaration:
        return ValueType.Integer;
      case StringDeclaration:
        return ValueType.String;
      case BooleanDeclaration:
        return ValueType.Boolean;
      case Null:
        return ValueType.Null;
      default:
        return null;
//        throw new Exception("Cannot parse token type");
    }
  }
}
