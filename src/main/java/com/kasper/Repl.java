package com.kasper;

import interpreter.Interpreter;
import parser.Parser;

import java.util.Scanner;


public class Repl {
  public static void main(String[] args) {
    System.out.println("\nRepl v1.0");
    Scanner sc = new Scanner(System.in);
    while (true) {
      try {
        System.out.print(">> ");
        String line = sc.nextLine();
        if (line.isEmpty() || line.equals("break")) {
          break;
        }
//        Parser parser = new Parser(line);
//        parser.printTokens();
//        parser.printAST();
        Interpreter interpreter = new Interpreter(line);
        interpreter.print_output();
      } catch (Exception err) {
        System.out.print(err.toString());
        System.out.println();
      }
    }
    sc.close();
  }
}
