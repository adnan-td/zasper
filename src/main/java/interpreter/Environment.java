package interpreter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Environment {
  private final Environment parent;
  private final Map<String, RuntimeValue<?>> variables;
  private final HashSet<String> constants;

  public Environment() {
    this(null);
  }

  public Environment(Environment parent_ENV) {
    this.parent = parent_ENV;
    this.variables = new HashMap<>();
    this.constants = new HashSet<>();
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
    Environment env = resolve(var_name);
    if (constants.contains(var_name)) {
      throw new Exception("Cannot assign keywords. Assigning " + var_name);
    }
    env.variables.put(var_name, value);
    return value;
  }

  public RuntimeValue<?> lookup_var(String var_name) throws Exception {
    Environment env = resolve(var_name);
    return env.variables.get(var_name);
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

  public Environment create_child_environment() {
    return new Environment(this);
  }
}
