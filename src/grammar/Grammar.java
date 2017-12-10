package grammar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 这是一个文法，
 * 一个文法包含（终结符，非终结符，起始符和文法方案（文法产生式））
 * 这个类提供了每个非终结符的first集和follow集，
 * 如果存在左递归，该类提供查询哪些符号产生左递归，
 * 该类还可判断该文法是否符合标准，是否存在左递归。
 * @see #update() 该类允许重新设置终结符、非终结符、文法方案（文法产生式）集合和起始符。但是必须调用update方法更新文法。
 */

public class Grammar {
    private char starter;
    private boolean isGrammar = true;
    private boolean isLeftRecursion = false;
    private HashSet<Character> leftRecursionListFirst = new HashSet<>();
    private HashSet<Character> leftRecursionListFollow = new HashSet<>();
    private HashSet<Character> terminator = new HashSet<>();
    private HashSet<Character> nonTerminator = new HashSet<>();
    private HashMap<Character, HashSet<String>> precept = new HashMap<>();
    private HashMap<Character, HashSet<Character>> firstList = new HashMap<>();
    private HashMap<Character, HashSet<Character>> followList = new HashMap<>();

    /**
     * @return 返回该文法的first集，若返回值为空则该文法不符合标准。
     */
    public HashMap<Character, HashSet<Character>> getFirstList() {
        @SuppressWarnings(value = "unchecked")
        HashMap<Character, HashSet<Character>> firstList = (HashMap<Character, HashSet<Character>>) this.firstList.clone();
        return firstList;
    }

    /**
     * @return 返回该文法的follow集，若返回值为空则该文法不符合标准。
     */
    public HashMap<Character, HashSet<Character>> getFollowList() {
        @SuppressWarnings(value = "unchecked")
        HashMap<Character, HashSet<Character>> followList = (HashMap<Character, HashSet<Character>>) this.followList.clone();
        return followList;
    }

    /**
     *
     * @return 返回该文法的终结符集合。
     */
    public HashSet<Character> getTerminator() {
        return terminator;
    }

    /**
     * 重新设置该文法的终结符集合。
     * @param terminator 新的终结符集合。
     */
    public void setTerminator(HashSet<Character> terminator) {
        this.terminator = terminator;
    }

    /**
     *
     * @return 返回该文法的非终极符集合。
     */
    public HashSet<Character> getNonTerminator() {
        return nonTerminator;
    }

    /**
     * 重新设置该文法的非终结符集合。
     * @param nonTerminator 新的非终结符集合。
     */
    public void setNonTerminator(HashSet<Character> nonTerminator) {
        this.nonTerminator = nonTerminator;
    }

    /**
     *
     * @return 返回该文法的文法方案（文法产生式）。
     */
    public HashMap<Character, HashSet<String>> getPrecept() {
        return precept;
    }

    /**
     * 重新设置该文法的文法方案（文法产生式）。
     * @param precept 新的文法方案（文法产生式）。
     */
    public void setPrecept(HashMap<Character, HashSet<String>> precept) {
        this.precept = precept;
    }

    /**
     *
     * @return 返回该文法的起始符。
     */
    public char getStarter() {
        return starter;
    }

    /**
     * 重新设置该文法的起始符。
     * @param starter 新的文法起始符。
     */
    public void setStarter(char starter) {
        this.starter = starter;
    }

    /**
     *
     * @return 返回一个布尔值，用于判断该文法是否符合标准。
     */
    public boolean isGrammar() {
        return isGrammar;
    }

    /**
     *
     * @return 返回一个布尔值，用于判断该文法是否存在左递归。
     */
    public boolean isLeftRecursion() {
        return isLeftRecursion;
    }

    /**
     * 构造方法，该方法内部已经调用update()方法更新。
     * @param terminator 文法终结符。
     * @param nonTerminator 文法非终结符。
     * @param starter 文法起始符。
     * @param precept 文法方案（文法产生式）。
     */
    public Grammar(HashSet<Character> terminator, HashSet<Character> nonTerminator, char starter, HashMap<Character, HashSet<String>> precept) {
        this.terminator = terminator;
        this.nonTerminator = nonTerminator;
        this.starter = starter;
        this.precept = precept;
    }

    /**
     * 无参构造方法，文法终结符、非终结符、文法方案（文法产生式）集合和起始符需要自己设置，
     * @see #update() 需要调用update方法更新文法。
     */
    public Grammar() {
    }

    /**
     * 用于更新文法的状态。
     * 更新内容：first集、follow集、该文法是否符合标准和该文法是否存在左递归。
     */
    public void update() {
        firstList.clear();
        followList.clear();

        HashSet<Character> visitedNonTerminatorList = new HashSet<>();
        for (char symbol : nonTerminator) {
            if (precept.get(symbol) == null || !isGrammar) {
                isGrammar = false;
                break;
            }
            HashSet<Character> symbolFirstList;
            HashSet<Character> symbolFollowList;
            visitedNonTerminatorList.add(symbol);
            if ((symbolFirstList = analysisFirstList(symbol, visitedNonTerminatorList)).size() == 0 && !leftRecursionListFirst.contains(symbol)) {
                isGrammar = false;
                break;
            }
            firstList.put(symbol, symbolFirstList);

            if ((symbolFollowList = analysisFollowList(symbol, visitedNonTerminatorList)).size() == 0 && !leftRecursionListFollow.contains(symbol)) {
                isGrammar = false;
                break;
            }
            followList.put(symbol, symbolFollowList);
        }

        if (!isGrammar) {
            firstList.clear();
            followList.clear();
        }
    }

    /**
     * 该方法采用递归算法计算一个非终结符的first集。在计算过程中可以判断该文法是否符合标准和该文法是否存在左递归。
     * @param symbol 需要计算first集的非终结符。
     * @param visitedNonTerminatorList 在计算first集过程中已经访问过的非终结符集合，主要用于避免出现左递归而引发的无限递归。
     * @return 返回该非终结符的first集。
     */
    private HashSet<Character> analysisFirstList(char symbol, HashSet<Character> visitedNonTerminatorList) {
        HashSet<Character> symbolFirstList = new HashSet<>();
        for (String preceptRight : precept.get(symbol)) {
            if (preceptRight.length() == 0)
                return new HashSet<>();

            String preceptRightCopy = preceptRight;
            while (preceptRightCopy.length() > 0) {
                if (terminator.contains(preceptRightCopy.charAt(0))) {
                    if ('ε' == preceptRightCopy.charAt(0) && preceptRightCopy.length() > 1)
                        return new HashSet<>();

                    symbolFirstList.add(preceptRightCopy.charAt(0));
                    break;
                } else if (nonTerminator.contains(preceptRightCopy.charAt(0))) {
                    HashSet<Character> rightSymbolFirstList;
                    if (firstList.containsKey(preceptRightCopy.charAt(0))) {
                        rightSymbolFirstList = firstList.get(preceptRightCopy.charAt(0));
                    } else if (!visitedNonTerminatorList.contains(preceptRightCopy.charAt(0))) {
                        visitedNonTerminatorList.add(preceptRightCopy.charAt(0));
                        rightSymbolFirstList = analysisFirstList(preceptRightCopy.charAt(0), visitedNonTerminatorList);
                        if (rightSymbolFirstList.size() == 0 && !leftRecursionListFirst.contains(preceptRightCopy.charAt(0))) {
                            isGrammar = false;
                            return new HashSet<>();
                        }
                        firstList.put(preceptRightCopy.charAt(0), rightSymbolFirstList);
                    } else {//firstList表中没有，但是访问过，所以存在左循环
                        isLeftRecursion = true;
                        leftRecursionListFirst.add(preceptRightCopy.charAt(0));
                        break;//左循环，抛弃
                    }

                    symbolFirstList.addAll(rightSymbolFirstList);
                    if (rightSymbolFirstList.contains('ε') && preceptRightCopy.length() > 1) {
                        preceptRightCopy = preceptRightCopy.substring(1);
                        symbolFirstList.remove('ε');
                    } else break;
                } else return new HashSet<>();
            }
        }

        return symbolFirstList;
    }

    /**
     * 该方法采用递归算法计算一个非终结符的follow集。在计算过程中可以判断该文法是否符合标准和该文法是否存在左递归。
     * @param symbol 需要计算follow集的非终结符。
     * @param visitedNonTerminatorList 在计算follow集过程中已经访问过的非终结符集合，主要用于避免出现左递归而引发的无限递归。
     * @return 返回该非终结符的follow集。
     */
    private HashSet<Character> analysisFollowList(char symbol, HashSet<Character> visitedNonTerminatorList) {
        HashSet<Character> symbolFollowList = new HashSet<>();
        if (symbol == starter)
            symbolFollowList.add('#');

        for (Map.Entry<Character, HashSet<String>> entry : precept.entrySet()) {
            if (entry.getValue().size() == 0 || !nonTerminator.contains(entry.getKey()))
                return new HashSet<>();

            for (String preceptRight : entry.getValue()) {
                if (preceptRight.length() == 0)
                    return new HashSet<>();

                int index = -1;
                while ((index = preceptRight.indexOf(symbol, index + 1)) >= 0) {
                    String preceptRightCopy = preceptRight.substring(index);
                    if (preceptRightCopy.length() == 1) {
                        HashSet<Character> rightSymbolFollowList;
                        if (followList.containsKey(entry.getKey())) {
                            rightSymbolFollowList = followList.get(entry.getKey());
                        } else if (!visitedNonTerminatorList.contains(entry.getKey())) {
                            visitedNonTerminatorList.add(entry.getKey());
                            rightSymbolFollowList = analysisFollowList(entry.getKey(), visitedNonTerminatorList);
                            if (rightSymbolFollowList.size() == 0 && !leftRecursionListFollow.contains(entry.getKey())) {
                                isGrammar = false;
                                return new HashSet<>();
                            }
                            followList.put(entry.getKey(), rightSymbolFollowList);
                        } else {
                            isLeftRecursion = true;
                            leftRecursionListFollow.add(entry.getKey());
                            continue;
                        }
                        symbolFollowList.addAll(rightSymbolFollowList);
                    } else if (terminator.contains(preceptRightCopy.charAt(1))) {
                        if (preceptRightCopy.charAt(1) == 'ε')
                            return new HashSet<>();

                        symbolFollowList.add(preceptRightCopy.charAt(1));
                    } else if (nonTerminator.contains(preceptRightCopy.charAt(1))) {
                        HashSet<Character> rightSymbolFollowList;
                        HashSet<Character> rightSymbolFirstList;
                        if (firstList.containsKey(preceptRightCopy.charAt(1))) {
                            rightSymbolFirstList = firstList.get(preceptRightCopy.charAt(1));
                        } else {
                            HashSet<Character> visitedNonTerminatorListFirst = new HashSet<>();
                            visitedNonTerminatorListFirst.add(preceptRightCopy.charAt(1));
                            rightSymbolFirstList = analysisFirstList(preceptRightCopy.charAt(1), visitedNonTerminatorListFirst);
                            if (rightSymbolFirstList.size() == 0 && !leftRecursionListFirst.contains(preceptRightCopy.charAt(1))) {
                                isGrammar = false;
                                return new HashSet<>();
                            }
                            firstList.put(preceptRightCopy.charAt(1), rightSymbolFirstList);
                        }
                        rightSymbolFollowList = rightSymbolFirstList;
                        if (rightSymbolFirstList.contains('ε')) {
                            rightSymbolFollowList.remove('ε');
                            if (preceptRightCopy.length() == 2) {
                                if (followList.containsKey(entry.getKey())) {
                                    rightSymbolFollowList.addAll(followList.get(entry.getKey()));
                                } else if (!visitedNonTerminatorList.contains(entry.getKey())) {
                                    visitedNonTerminatorList.add(entry.getKey());
                                    HashSet<Character> leftSymbolFollowList = analysisFollowList(entry.getKey(), visitedNonTerminatorList);
                                    if (leftSymbolFollowList.size() == 0 && !leftRecursionListFollow.contains(entry.getKey())) {
                                        isGrammar = false;
                                        return new HashSet<>();
                                    }
                                    followList.put(entry.getKey(), leftSymbolFollowList);
                                    rightSymbolFollowList.addAll(leftSymbolFollowList);
                                } else {
                                    isLeftRecursion = true;
                                    leftRecursionListFollow.add(entry.getKey());
                                    continue;
                                }
                            }
                        }
                        symbolFollowList.addAll(rightSymbolFollowList);
                    } else return new HashSet<>();
                }
            }
        }

        return symbolFollowList;
    }
}