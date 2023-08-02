package NativeCode;

import interpreter.RuntimeValue;

import java.util.List;

public class Functions {

  public static void print(List<RuntimeValue<?>> args) {
    for (RuntimeValue<?> rt : args) {
      System.out.print(rt.value);
    }
    System.out.println();
  }

  public static void execute(String id, List<RuntimeValue<?>> args) throws Exception {
    switch (id) {
      case "print":
        print(args);
        break;
      default:
        throw new Exception("Native function not found");
    }
  }
}
