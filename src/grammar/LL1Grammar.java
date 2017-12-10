package grammar;

import java.util.*;

/**
 * 这是一个LL1文法，继承自Grammar，
 * 该类提供了每个产生式的select集 ，
 * 该类还可分析符合该文法的句子，并给出分析过程。
 */
public class LL1Grammar extends Grammar {
    private boolean isLL1Grammar = true;
    private HashMap<Character, HashMap<Character, String>> analysisTable = new HashMap<>();
    private HashMap<Character, HashSet<Character>> selectIntersectionNotEmpty = new HashMap<>();
    private HashMap<Map.Entry<Character, HashSet<String>>, HashMap<String, HashSet<Character>>> selectList = new HashMap<>();

    /**
     * @return 返回该文法所有产生式的select集。
     */
    public HashMap<Map.Entry<Character, HashSet<String>>, HashMap<String, HashSet<Character>>> getSelectList() {
        return selectList;
    }

    /**
     * @return 返回相同左部产生式交集不为空的非终结符。
     */
    public HashMap<Character, HashSet<Character>> getSelectIntersectionNotEmpty() {
        return selectIntersectionNotEmpty;
    }

    /**
     * @return 返回一个布尔值，用于判断该文法是否为LL1文法。
     */
    public boolean isLL1Grammar() {
        return isLL1Grammar;
    }

    /**
     * 构造方法，该方法内部并未调用update()方法更新。
     *
     * @param terminator    文法终结符。
     * @param nonTerminator 文法非终结符。
     * @param starter       文法起始符。
     * @param precept       文法方案（文法产生式）。
     * @see #update() 请调用update方法更新。
     * 如果存在左递归，则该文法不是LL1文法。
     */
    public LL1Grammar(HashSet<Character> terminator, HashSet<Character> nonTerminator, char starter, HashMap<Character, HashSet<String>> precept) {
        super(terminator, nonTerminator, starter, precept);
        if (isLeftRecursion()) {
            isLL1Grammar = false;
        }
    }

    /**
     * 无参构造方法，文法终结符、非终结符、文法方案（文法产生式）集合和起始符需要自己设置，
     *
     * @see #update() 需要调用update方法更新文法。
     */
    public LL1Grammar() {
    }

    /**
     * 重写了Grammar的update方法，内部调用Grammar的update方法。
     * 更新内容：select集、是否为LL1文法和分析表。
     */
    @Override
    public void update() {
        super.update();
        selectList.clear();
        selectIntersectionNotEmpty.clear();
        isLL1Grammar = isLeftRecursion();

        if (isGrammar()) {
            for (Map.Entry<Character, HashSet<String>> entry : getPrecept().entrySet()) {
                HashMap<String, HashSet<Character>> entrySelectList = analysisSelectList(entry);
                selectList.put(entry, entrySelectList);

                Iterator<Map.Entry<String, HashSet<Character>>> iterator = entrySelectList.entrySet().iterator();
                HashSet<Character> selectIntersection = new HashSet<>(iterator.next().getValue());
                while (iterator.hasNext())
                    selectIntersection.retainAll(iterator.next().getValue());
                if (selectIntersection.size() != 0) {
                    isLL1Grammar = false;
                    selectIntersectionNotEmpty.put(entry.getKey(), selectIntersection);
                }
            }
        } else isLL1Grammar = false;

        if (isLL1Grammar)
            analysisTable();
    }

    /**
     * 该方法计算一个非终结符所有产生式的select集。
     *
     * @param entry 一个非终结符的所有产生式。
     * @return 返回一个非终结符的所有产生式的select集。
     */
    private HashMap<String, HashSet<Character>> analysisSelectList(Map.Entry<Character, HashSet<String>> entry) {
        HashMap<String, HashSet<Character>> entrySelectList = new HashMap<>();
        for (String rightPrecept : entry.getValue()) {
            HashSet<Character> rightPreceptSelect = new HashSet<>();
            String rightPreceptCopy = rightPrecept;
            while (rightPreceptCopy.length() > 0) {
                char firstCharacter = rightPreceptCopy.charAt(0);
                if (getTerminator().contains(firstCharacter)) {
                    if (firstCharacter == 'ε') {
                        rightPreceptSelect.addAll(getFollowList().get(entry.getKey()));
                        break;
                    } else {
                        rightPreceptSelect.add(firstCharacter);
                        break;
                    }
                } else {
                    HashSet<Character> firstCharacterFirstList = getFirstList().get(firstCharacter);
                    rightPreceptSelect.addAll(firstCharacterFirstList);
                    if (firstCharacterFirstList.contains('ε')) {
                        if (rightPreceptCopy.length() == 1) {
                            rightPreceptSelect.remove('ε');
                            rightPreceptSelect.addAll(getFollowList().get(entry.getKey()));
                            break;
                        }
                        rightPreceptCopy = rightPreceptCopy.substring(1);
                    } else break;
                }
            }
            entrySelectList.put(rightPrecept, rightPreceptSelect);
        }

        return entrySelectList;
    }

    /**
     * 给出分析符合该文法的句子的分析过程。
     */
    public ArrayList<String[]> analyzeSentences(String sentence) {
        ArrayList<String[]> analysisProcess = new ArrayList<>();
        if (isLL1Grammar) {
            // TODO: 2017/12/10 17:22 analyzeSentences
        }
        return analysisProcess;
    }

    /**
     * 构建该文法的分析表。
     */
    private void analysisTable(){
        // TODO: 2017/12/10 17:32 analysisTable
    }

    /**
     * 该方法消除左递归。
     */
    public void eliminateLeftRecursion() {
        if (isGrammar()) {
            // TODO: 2017/12/10 16:59 eliminateLeftRecursion
        }
    }
}
