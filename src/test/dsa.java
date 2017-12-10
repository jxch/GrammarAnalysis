package test;

import grammar.Grammar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class dsa {
    public static void main(String[] args) {
        /*
         * S->bA
         * A->ε
         */
        Character[] ter = {'a', 'b','ε'};
        Character[] nonTer = {'S', 'A'};
        String[] p1 = {"bA"};
        String[] p2 = {"ε"};
        HashSet<Character> terminator = new HashSet<>(Arrays.asList(ter));
        HashSet<Character> nonTerminator = new HashSet<>(Arrays.asList(nonTer));
        HashMap<Character, HashSet<String>> precept = new HashMap<>();
        char starter = 'S';

        HashSet<String> r1 = new HashSet<>(Arrays.asList(p1));
        HashSet<String> r2 = new HashSet<>(Arrays.asList(p2));

        precept.put(starter, r1);
        precept.put('A', r2);

        Grammar grammar = new Grammar(terminator, nonTerminator, starter, precept);
        System.out.print(grammar.getFirstList().toString() + grammar.isGrammar() + grammar.getFollowList().toString());
    }
}
