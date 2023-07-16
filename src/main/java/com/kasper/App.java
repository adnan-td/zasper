package com.kasper;

import parser.Parser;
import parser.Program;
import tokeniser.*;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("\nRepl v1.0");
        Scanner sc = new Scanner(System.in);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        while (true) {
            System.out.print(">> ");
            String line = sc.nextLine();
            if (line.isEmpty() || line.equals("break")) {
                break;
            }
            Parser parser = new Parser();
            Program pg = parser.getAST(line);
            String json = gson.toJson(pg);
            System.out.println(json);
        }
        sc.close();
    }
}
