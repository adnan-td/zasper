package gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Printer {
  private final static Gson gson = new GsonBuilder()
      .setPrettyPrinting()
      .disableHtmlEscaping()
      .create();

  public static void print(Object cl) {
    String json = gson.toJson(cl);
    System.out.println(json);
  }
}
