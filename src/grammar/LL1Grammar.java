package grammar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * 这是一个LL1文法，继承自Grammar，
 * 这个类提供了每个产生式的select集 ，
 */
public class LL1Grammar extends Grammar {
    private boolean isLL1Grammar = true;
    private HashMap<Character, HashSet<Character>> selectIntersectionNotEmpty = new HashMap<>();
    private HashMap<Map.Entry<Character, HashSet<String>>, HashMap<String, HashSet<Character>>> selectList = new HashMap<>();

    public HashMap<Map.Entry<Character, HashSet<String>>, HashMap<String, HashSet<Character>>> getSelectList() {
        return selectList;
    }

    public HashMap<Character, HashSet<Character>> getSelectIntersectionNotEmpty() {
        return selectIntersectionNotEmpty;
    }

    public boolean isLL1Grammar() {
        return isLL1Grammar;
    }

    public LL1Grammar(HashSet<Character> terminator, HashSet<Character> nonTerminator, char starter, HashMap<Character, HashSet<String>> precept) {
        super(terminator, nonTerminator, starter, precept);
    }

    public LL1Grammar() {
    }

    @Override
    public void update() {
        super.update();
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
    }

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
                    if (firstCharacterFirstList.contains('ε')){
                        if (rightPreceptCopy.length() == 1){
                            rightPreceptSelect.remove('ε');
                            rightPreceptSelect.addAll(getFollowList().get(entry.getKey()));
                            break;
                        }
                        rightPreceptCopy = rightPreceptCopy.substring(1);
                    }else break;
                }
            }
            entrySelectList.put(rightPrecept, rightPreceptSelect);
        }

        return entrySelectList;
    }
}
