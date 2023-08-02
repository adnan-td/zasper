package Exceptions;

import tokeniser.Location;

public class ParsingException extends Exception {
  public ErrorTypes errorType;
  public Location location;
  public String source;
  public String message;

  public ParsingException(ErrorTypes errorType, Location location, String source) {
    this.errorType = errorType;
    this.location = location;
    this.source = source;
    this.message = null;
  }

  public ParsingException(ErrorTypes errorType, String message, Location location, String source) {
    this.errorType = errorType;
    this.location = location;
    this.source = source;
    this.message = message;
  }

  public String getErrorMessage() {
    switch (this.errorType) {
      case InvalidCondition:
        return "Invalid condition. Condition must return boolean";
      case InvalidAssignmentToBoolean:
        return "Expected boolean or null";
      case InvalidReturnType:
        return "Invalid or missing function return type";
      case InvalidVariableDeclaration:
        return "Invalid variable declaration";
      case InvalidVariableDeclarationOrInitialisation:
        return "Invalid variable declaration or initialisation";
      case VariableNotFound:
        return "Variable not found";
      case ImproperIndentation:
        return "Improper Indentation";
      case StringNotClosed:
        return "String not closed";
      default:
        return null;
    }
  }

  @Override
  public String toString() {
    int startIndex = location.start;
    int endIndex = location.end;
    int lineNumber = source.substring(0, startIndex).split("\n").length;
    int lineStart = source.lastIndexOf('\n', startIndex) + 1;
    int lineEnd = source.indexOf('\n', endIndex);

    if (lineEnd < 0) {
      lineEnd = source.length();
    }

    String errorLine = source.substring(lineStart, lineEnd);
    StringBuilder errorString = new StringBuilder();
    errorString.append("Error occured at line ").append(lineNumber).append(":\n");
    errorString.append(errorLine).append("\n");

    for (int i = 0; i < startIndex - lineStart; i++) {
      errorString.append(" ");
    }
    errorString.append("^");
    errorString.append("\n");
    if (getErrorMessage() == null && message != null) {
      errorString.append(message).append("\n");
    } else {
      errorString.append(getErrorMessage()).append("\n");
    }
    return errorString.toString();
  }
}
