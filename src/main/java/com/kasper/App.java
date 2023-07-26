package com.kasper;

import parser.Parser;
import parser.Program;
import tokeniser.*;

import java.util.Scanner;


public class App {
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
        Parser parser = new Parser(line);
        parser.printTokens();
        parser.printAST();
      } catch (Exception err) {
        System.out.print(err.toString());
//      err.printStackTrace();
        System.out.println();
      }
    }
    sc.close();
  }
}
