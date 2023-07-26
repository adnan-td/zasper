package com.kasper;

import parser.Parser;
import parser.Program;
import tokeniser.*;

import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App {
    public static void main(String[] args) {
        System.out.println("\nRepl v1.0");
        Scanner sc = new Scanner(System.in);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        while (true) {
            try {
                System.out.print(">> ");
                String line = sc.nextLine();
                if (line.isEmpty() || line.equals("break")) {
                    break;
                }
                Parser parser = new Parser();
                Program pg = parser.getAST(line);
                String json = gson.toJson(pg);
                System.out.println("*** AST ***");
                System.out.println(json);
            } catch (Exception err) {
                System.out.print(err.toString());
//                err.printStackTrace();
                System.out.println();
            }
        }
        sc.close();
    }
}
