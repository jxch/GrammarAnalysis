package test;

import grammar.Grammar;
import grammar.LL1Grammar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class dsa {
    public static void main(String[] args) {
        /*
         * S→aA
         * S→d
         * A→bAS
         * A→ε
         */
        Character[] ter = {'a', 'b', 'ε','d'};
        Character[] nonTer = {'S', 'A'};
        String[] p1 = {"aA","d"};
        String[] p2 = {"ε","bAS"};
        HashSet<Character> terminator = new HashSet<>(Arrays.asList(ter));
        HashSet<Character> nonTerminator = new HashSet<>(Arrays.asList(nonTer));
        HashMap<Character, HashSet<String>> precept = new HashMap<>();
        char starter = 'S';

        HashSet<String> r1 = new HashSet<>(Arrays.asList(p1));
        HashSet<String> r2 = new HashSet<>(Arrays.asList(p2));

        precept.put(starter, r1);
        precept.put('A', r2);

        LL1Grammar grammar = new LL1Grammar(terminator, nonTerminator, starter, precept);
        grammar.update();
        System.out.println(grammar.getFirstList().toString() + grammar.isGrammar() + grammar.getFollowList().toString());
        System.out.println(grammar.isLL1Grammar());

        System.out.println(grammar.getSelectList().toString());
    }
}
