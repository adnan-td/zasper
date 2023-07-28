package interpreter;

public class RuntimeValue<t> {
  ValueType type;
  t value;

  public RuntimeValue(ValueType type, t value) {
    this.type = type;
    this.value = value;
  }
}

class NullVal extends RuntimeValue<Integer> {
  public NullVal() {
    super(ValueType.Null, null);
  }
}

class IntVal extends RuntimeValue<Integer> {
  public IntVal(int value) {
    super(ValueType.Integer, value);
  }
}

class DoubleVal extends RuntimeValue<Double> {
  public DoubleVal(double value) {
    super(ValueType.Double, value);
  }
}

class BoolVal extends RuntimeValue<Boolean> {
  public BoolVal(boolean value) {
    super(ValueType.Boolean, value);
  }
}