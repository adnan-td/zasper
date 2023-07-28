package com.kasper;

import interpreter.Interpreter;
import parser.Parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class App {
  public static void main(String[] args) {
    try {
      System.out.println("\nKasper v1.0");
      String source = readFileAsString("src\\main\\java\\com\\kasper\\input.kas");
      Interpreter interpreter = new Interpreter(source);
      interpreter.print_output();
    } catch (Exception err) {
      System.out.print(err.toString());
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
