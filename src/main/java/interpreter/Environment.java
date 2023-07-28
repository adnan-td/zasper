package interpreter;

import java.util.HashMap;
import java.util.Map;

public class Environment {
  private final Environment parent;
  private final Map<String, RuntimeValue<?>> variables;

  public Environment() {
    this(null);
  }

  public Environment(Environment parent_ENV) {
    this.parent = parent_ENV;
    this.variables = new HashMap<>();
  }

  public RuntimeValue<?> declare_var(String var_name, RuntimeValue<?> value) {
    if (variables.containsKey(var_name)) {
      throw new RuntimeException("Cannot declare variable " + var_name + ". As it already is defined.");
    }

    variables.put(var_name, value);
    return value;
  }

  public RuntimeValue<?> assign_var(String var_name, RuntimeValue<?> value) {
    Environment env = resolve(var_name);
    env.variables.put(var_name, value);
    return value;
  }

  public RuntimeValue<?> lookup_var(String var_name) {
    Environment env = resolve(var_name);
    return env.variables.get(var_name);
  }

  public Environment resolve(String var_name) {
    if (variables.containsKey(var_name)) {
      return this;
    }

    if (parent == null) {
      throw new RuntimeException("Cannot resolve '" + var_name + "' as it does not exist.");
    }

    return parent.resolve(var_name);
  }

  public Environment create_child_environment() {
    return new Environment(this);
  }
}
