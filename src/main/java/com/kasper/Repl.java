package com.kasper;

import gson.Printer;
import interpreter.Interpreter;
import parser.Parser;

import java.util.Scanner;


public class Repl {
  public static void main(String[] args) {
    try {
      Interpreter interpreter = new Interpreter();
      Scanner sc = new Scanner(System.in);
      System.out.println("\nRepl v1.0");
      while (true) {
        try {

          System.out.print(">> ");
          String line = sc.nextLine();
          if (line.isEmpty() || line.equals("break")) {
            break;
          }
          Parser parser = new Parser(line);
          parser.printTokens();
          parser.printAST();
          Printer.print(interpreter.interpret(line));
        } catch (Exception er) {
          System.out.print(er.toString());
          System.out.println();
          er.printStackTrace();
          System.out.println();
        }
      }
      sc.close();
    } catch (Exception err) {
      System.out.print(err.toString());
      System.out.println();
    }
  }
}
