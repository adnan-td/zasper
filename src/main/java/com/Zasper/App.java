package com.Zasper;

import gson.Printer;
import interpreter.Interpreter;
import parser.Parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class App {
  public static void main(String[] args) {
    try {
      System.out.println("\nZasper v1.0");
      String source = readFileAsString("src\\main\\java\\com\\Zasper\\input.zas");
      Parser parser = new Parser(source);
      parser.printTokens();
      parser.printAST();
      Interpreter interpreter = new Interpreter(source);
      Printer.print(interpreter.evaluate());
    } catch (Exception err) {
      System.out.print(err.toString());
      System.out.println();
      err.printStackTrace();
      System.out.println();
    }
  }

  public static String readFileAsString(String filePath) throws IOException {
    StringBuilder content = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        content.append(line).append("\n");
      }
    }
    return content.toString();
  }
}
