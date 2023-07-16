package com.kasper;

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
            if (line.isEmpty() || line == "break") {
                break;
            }
            Token[] tokens = Tokeniser.tokenise(line);
            for (Token t : tokens) {
                String json = gson.toJson(t);
                System.out.println(json);
            }
        }

        sc.close();
    }
}
