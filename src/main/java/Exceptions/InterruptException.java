package Exceptions;

import interpreter.RuntimeValue;
import parser.ast.ReturnStatement;

public class InterruptException extends Exception {
  public int interrupt_id;
  public ReturnStatement return_value;

  public InterruptException(int interrupt_id, String message) {
    super(message);
    this.interrupt_id = interrupt_id;
  }

  public InterruptException(int interrupt_id, String message, ReturnStatement return_value) {
    super(message);
    this.interrupt_id = interrupt_id;
    this.return_value = return_value;
  }
}
