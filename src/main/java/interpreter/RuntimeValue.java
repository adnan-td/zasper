package interpreter;

import parser.ast.BlockBody;
import parser.ast.Identifier;

import java.util.List;

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

class FunctionRuntime {
  public Environment env;
  public BlockBody body;
  public List<Identifier> parameters;
  public ValueType returnType;

  public FunctionRuntime(Environment env, BlockBody body, List<Identifier> parameters, ValueType returnType) throws Exception {
    this.env = env;
    this.body = body;
    this.parameters = parameters;
    this.returnType = returnType;
    declare_parameters();
  }

  private void declare_parameters() throws Exception {
    for (Identifier identifier : parameters) {
      env.declare_var(identifier.symbol, new RuntimeValue<>(identifier.type, null));
    }
  }

  public Environment assign_arguments(List<RuntimeValue<?>> arguments) throws Exception {
    Environment subEnv = env.create_child_environment();
    if (arguments.size() != parameters.size()) {
      throw new Exception(String.format("Expected %d arguments, got %d arguments", parameters.size(), arguments.size()));
    }
    for (int i = 0; i < arguments.size(); i++) {
      subEnv.assign_var(parameters.get(i).symbol, arguments.get(i));
    }
    return subEnv;
  }
}