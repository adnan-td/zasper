package interpreter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Environment {
  private final Environment parent;
  private final Map<String, RuntimeValue<?>> variables;
  private final Map<String, FunctionRuntime> functions;
  private final HashSet<String> constants;

  public Environment(Environment parent_ENV) throws Exception {
    this.parent = parent_ENV;
    this.variables = new HashMap<>();
    this.functions = new HashMap<>();
    this.constants = new HashSet<>();
    if (parent_ENV == null) {
      declare_native_functions();
    }
  }

  public RuntimeValue<?> declare_var(String var_name, RuntimeValue<?> value) throws Exception {
    if (variables.containsKey(var_name)) {
      throw new Exception("Cannot declare variable " + var_name + " as it already exists");
    }

    variables.put(var_name, value);
    return value;
  }

  public void declare_constant_var(String var_name, RuntimeValue<?> value) throws Exception {
    constants.add(var_name);
    declare_var(var_name, value);
  }

  public void declare_function(String function_name, FunctionRuntime value) throws Exception {
    if (functions.containsKey(function_name)) {
      throw new Exception("Cannot declare function " + function_name + " as it already exists");
    }
    constants.add(function_name);
    functions.put(function_name, value);
  }

  public void assign_var(String var_name, RuntimeValue<?> value) throws Exception {
    assign_var(var_name, value, "=");
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
      } else if (assignedTo.type == ValueType.Boolean) {
        if (!operator.equals("=")) throw new Exception(String.format("Cannot perform '%s' on booleans", operator));
        this.variables.put(var_name, value);
        return value;
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

  public FunctionRuntime lookup_function(String function_name) throws Exception {
    Environment env = resolve_function(function_name);
    return env.functions.get(function_name);
  }

  public RuntimeValue<?> remove_var(String var_name) throws Exception {
    Environment env = resolve(var_name);
    return env.variables.remove(var_name);
  }

  public Environment resolve(String var_name) throws Exception {
    if (variables.containsKey(var_name)) {
      return this;
    }
    if (functions.containsKey(var_name)) {
      throw new Exception(String.format("function '%s' already exists", var_name));
    }
    if (parent == null) {
      throw new Exception("Cannot resolve '" + var_name + "' as it does not exist");
    }
    return parent.resolve(var_name);
  }

  public Environment resolve_function(String function_name) throws Exception {
    if (variables.containsKey(function_name)) {
      throw new Exception(String.format("variable '%s' already exists", function_name));
    }
    if (functions.containsKey(function_name)) {
      return this;
    }
    if (parent == null) {
      throw new Exception("Cannot resolve '" + function_name + "' as it does not exist");
    }
    return parent.resolve_function(function_name);
  }

  public Environment create_child_environment() throws Exception {
    return new Environment(this);
  }

  private void declare_native_functions() throws Exception {
    add_native_function("print");
  }

  private void add_native_function(String name) throws Exception {
    constants.add(name);
    functions.put(name, new FunctionRuntime(name));
  }
}
