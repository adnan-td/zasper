package interpreter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Environment {
  private final Environment parent;
  private final Map<String, RuntimeValue<?>> variables;
  private final HashSet<String> constants;

  public Environment(Environment parent_ENV) throws Exception {
    this.parent = parent_ENV;
    this.variables = new HashMap<>();
    this.constants = new HashSet<>();
    if (parent_ENV == null) {
      declare_default_global_var(this);
    }
  }

  public RuntimeValue<?> declare_var(String var_name, RuntimeValue<?> value) throws Exception {
    if (variables.containsKey(var_name)) {
      throw new Exception("Cannot declare variable " + var_name + ". As it already is defined.");
    }

    variables.put(var_name, value);
    return value;
  }

  public void declare_constant_var(String var_name, RuntimeValue<?> value) throws Exception {
    constants.add(var_name);
    declare_var(var_name, value);
  }

  public RuntimeValue<?> assign_var(String var_name, RuntimeValue<?> value) throws Exception {
    return assign_var(var_name, value, "=");
  }

  public RuntimeValue<?> assign_var(String var_name, RuntimeValue<?> value, String operator) throws Exception {
    Environment env = resolve(var_name);
    if (constants.contains(var_name)) {
      throw new Exception("Cannot assign keywords. Assigning " + var_name);
    }
    RuntimeValue<?> assignedTo = env.variables.get(var_name);
    if (assignedTo.type == value.type) {
      if (assignedTo.value == null) {
        env.variables.put(var_name, value);
        return value;
      } else if (assignedTo.type == ValueType.Integer) {
        int leftVal = (Integer) assignedTo.value;
        int rightVal = (Integer) value.value;
        switch (operator) {
          case "+=":
            value = new RuntimeValue<>(assignedTo.type, leftVal + rightVal);
            break;
          case "-=":
            value = new RuntimeValue<>(assignedTo.type, leftVal - rightVal);
            break;
          case "*=":
            value = new RuntimeValue<>(assignedTo.type, leftVal * rightVal);
            break;
          case "/=":
            value = new RuntimeValue<>(assignedTo.type, leftVal / rightVal);
            break;
          case "%=":
            value = new RuntimeValue<>(assignedTo.type, leftVal % rightVal);
            break;
        }
        env.variables.put(var_name, value);
        return value;
      } else if (assignedTo.type == ValueType.Double) {
        double leftVal = (Double) assignedTo.value;
        double rightVal = (Double) value.value;
        switch (operator) {
          case "+=":
            value = new RuntimeValue<>(assignedTo.type, leftVal + rightVal);
            break;
          case "-=":
            value = new RuntimeValue<>(assignedTo.type, leftVal - rightVal);
            break;
          case "*=":
            value = new RuntimeValue<>(assignedTo.type, leftVal * rightVal);
            break;
          case "/=":
            value = new RuntimeValue<>(assignedTo.type, leftVal / rightVal);
            break;
          case "%=":
            value = new RuntimeValue<>(assignedTo.type, leftVal % rightVal);
            break;
        }
        env.variables.put(var_name, value);
        return value;
      } else {
        throw new Exception("Assignment Operation failed");
      }

    } else if (value.type == ValueType.Null) {
      RuntimeValue<?> newVal = new RuntimeValue<>(assignedTo.type, null);
      env.variables.put(var_name, newVal);
      return newVal;
    }
    throw new Exception(String.format("Cannot assign %s to %s", value.type, env.variables.get(var_name).type));
  }

  public RuntimeValue<?> lookup_var(String var_name) throws Exception {
    Environment env = resolve(var_name);
    return env.variables.get(var_name);
  }

  public RuntimeValue<?> remove_var(String var_name) throws Exception {
    Environment env = resolve(var_name);
    return env.variables.remove(var_name);
  }

  public Environment resolve(String var_name) throws Exception {
    if (variables.containsKey(var_name)) {
      return this;
    }
    if (parent == null) {
      throw new Exception("Cannot resolve '" + var_name + "' as it does not exist.");
    }
    return parent.resolve(var_name);
  }

  public Environment create_child_environment() throws Exception {
    return new Environment(this);
  }

  private void declare_default_global_var(Environment env) throws Exception {
    env.declare_constant_var("x", new IntVal(100));
    env.declare_constant_var("true", new BoolVal(true));
    env.declare_constant_var("false", new BoolVal(false));
    env.declare_constant_var("null", new NullVal());
  }
}
