package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.*;

class LR0Analysis {
    private int projectIndex = 0;
    private boolean isLR0 = true;
    private char starter;
    private String[][] table;
    private HashMap<String, Integer> precept = new HashMap<>();
    private ArrayList<Character> terminator = new ArrayList<>();
    private ArrayList<Character> nonTerminator = new ArrayList<>();
    private HashMap<Integer, HashMap<String, Integer>> closure = new HashMap<>();
    private HashMap<Integer, HashMap<Character, Integer>> closureRelationship = new HashMap<>();
    private HashMap<Integer, Boolean> conflict = new HashMap<>();
    private HashMap<Integer, Boolean> moveToProject = new HashMap<>();
    private HashMap<Integer, Boolean> attributionProject = new HashMap<>();
    private HashMap<Integer, Boolean> pending = new HashMap<>();
    private int acc = -1;

    LR0Analysis(String nonTerminator, String terminator, String starter, String precept) {
        for (char c : nonTerminator.trim().toCharArray())
            this.nonTerminator.add(c);
        for (char c : terminator.trim().toCharArray())
            this.terminator.add(c);
        this.starter = starter.trim().charAt(0);
        int i = 0;
        for (String string : Arrays.asList(precept.trim().split("\n"))) {
            this.precept.put(string.trim(), i++);
        }
    }

    boolean isLR0() {
        return isLR0;
    }

    private void init(TableView<ObservableList<StringProperty>> tableView) {
        tableView.getColumns().add(Controller.createColumn(2, "序号"));
        tableView.getColumns().add(Controller.createColumn(1, "ACTION"));
        tableView.getColumns().add(Controller.createColumn(0, "GOTO"));
    }

    private boolean closure(int index) {
        HashMap<String, Integer> projectSet = closure.get(index);
        HashMap<Character, HashMap<String, Integer>> symbolProject = new HashMap<>();
        boolean attribution = false;
        boolean moveInto = false;
        for (Map.Entry entry : projectSet.entrySet()) {
            HashMap<String, Integer> ij = new HashMap<>();
            String derivation = (String) entry.getKey();
            Integer i = (Integer) entry.getValue();
            if (i < derivation.length()) {
                ij.put(derivation, i + 1);
                if (derivation.length() > i + 1 && nonTerminator.contains(derivation.charAt(i + 1))) {
                    HashSet<Character> sign = new HashSet<>();
                    sign.add(derivation.charAt(i + 1));
                    for (Map.Entry<String, Integer> p : precept.entrySet()) {
                        if (sign.contains(p.getKey().trim().charAt(0))) {
                            ij.put(p.getKey().trim(), 3);
                            sign.add(p.getKey().trim().charAt(3));
                        }
                    }
                }
                if (terminator.contains(derivation.charAt(i))) {
                    moveInto = true;
                    moveToProject.put(index, true);
                } else if (nonTerminator.contains(derivation.charAt(i))) {
                    pending.put(index, true);
                }
                if (symbolProject.containsKey(derivation.charAt(i))) {
                    symbolProject.get(derivation.charAt(i)).putAll(ij);
                } else symbolProject.put(derivation.charAt(i), ij);
            } else {
                attribution = true;
                attributionProject.put(index, true);
                if (derivation.trim().charAt(0) == starter)
                    acc = index;
            }
        }

        HashMap<Character, Integer> hashMap = new HashMap<>();
        int flag = 0;
        for (Map.Entry<Character, HashMap<String, Integer>> entry : symbolProject.entrySet()) {
            flag = 1;
            int num = isHaveClosure(entry.getValue());
            if (num < 0) {
                closure.put(++projectIndex, entry.getValue());
                hashMap.put(entry.getKey(), projectIndex);
                int i = projectIndex;
                if (closure(i))
                    conflict.put(i, true);
            } else {
                hashMap.put(entry.getKey(), num);
            }
        }
        if (flag != 0)
            closureRelationship.put(index, hashMap);
        else closureRelationship.put(index, new HashMap<>());

        return moveInto && attribution;
    }

    private int isHaveClosure(HashMap<String, Integer> hashMap) {
        for (Map.Entry<Integer, HashMap<String, Integer>> entry : closure.entrySet()) {
            if (compareMapByEntrySet(entry.getValue(), hashMap))
                return entry.getKey();
        }
        return -1;
    }

    private static boolean compareMapByEntrySet(Map<String, Integer> map1, Map<String, Integer> map2) {

        if (map1.size() != map2.size()) {
            return false;
        }

        Integer tmp1;
        Integer tmp2;
        boolean b = false;

        for (Map.Entry<String, Integer> entry : map1.entrySet()) {
            if (map2.containsKey(entry.getKey())) {
                tmp1 = entry.getValue();
                tmp2 = map2.get(entry.getKey());

                if (null != tmp1 && null != tmp2) {   //都不为null
                    if (tmp1.equals(tmp2)) {
                        b = true;
                    } else {
                        b = false;
                        break;
                    }
                } else if (null == tmp1 && null == tmp2) {  //都为null
                    b = true;
                } else {
                    b = false;
                    break;
                }
            } else {
                b = false;
                break;
            }
        }


        return b;
    }

    private String analysisTable(TableView<ObservableList<StringProperty>> tableView) {
        String text = "该文法属于LR0文法";
        HashMap<String, Integer> i0 = new HashMap<>();
        HashSet<Character> symbol = new HashSet<>();
        symbol.add(starter);
        for (Map.Entry<String, Integer> p : precept.entrySet()) {
            if (symbol.contains(p.getKey().trim().charAt(0))) {
                i0.put(p.getKey().trim(), 3);
                symbol.add(p.getKey().trim().charAt(3));
            }
        }
        closure.put(0, i0);
        closure(0);

        ArrayList<Character> symbolAll = new ArrayList<>(terminator);
        symbolAll.add('#');
        for (int i = 0; i < symbolAll.size(); i++) {
            tableView.getColumns().get(1).getColumns().add(Controller.createColumn(i + 3, symbolAll.get(i).toString()));
        }
        symbolAll.addAll(nonTerminator);
        symbolAll.remove((Character) starter);
        for (int i = terminator.size() + 1; i < symbolAll.size(); i++) {
            tableView.getColumns().get(2).getColumns().add(Controller.createColumn(i + 3, symbolAll.get(i).toString()));
        }
        table = new String[projectIndex + 1][symbolAll.size()];

        for (Map.Entry<Integer, HashMap<Character, Integer>> entry : closureRelationship.entrySet()) {
            for (Map.Entry<Character, Integer> e : entry.getValue().entrySet()) {
                if (!conflict.containsKey(entry.getKey())) {
                    String element = "";
                    if (moveToProject.containsKey(entry.getKey()) && terminator.contains(e.getKey())) {
                        element = "S" + e.getValue();
                    } else if (pending.containsKey(entry.getKey()) && nonTerminator.contains(e.getKey())) {
                        element = e.getValue().toString();
                    }
                    table[entry.getKey()][symbolAll.indexOf(e.getKey())] = element;
                } else {
                    text = "存在移近归约冲突";
                    isLR0 = false;
                }
            }
            if (attributionProject.containsKey(entry.getKey())) {
                if (entry.getKey() == acc) {
                    table[entry.getKey()][symbolAll.indexOf('#')] = "acc";
                } else if (closure.get(entry.getKey()).size() == 1) {
                    String element = "R" + precept.get(closure.get(entry.getKey()).entrySet().iterator().next().getKey());
                    for (char e : terminator)
                        table[entry.getKey()][symbolAll.indexOf(e)] = element;
                } else {
                    text = "存在归约归约冲突";
                    isLR0 = false;
                }
            }
        }

        return text;
    }

    String analysis(TableView<ObservableList<StringProperty>> tableView) {
        init(tableView);
        StringBuilder text = new StringBuilder();
        for (Map.Entry<String, Integer> entry : precept.entrySet()) {
            text .append("[").append(entry.getValue()).append("] ").append(entry.getKey()).append("\n");
        }
        text.append(analysisTable(tableView)).append("\n");

        for (int i = 0; i < table.length; i++) {
            ObservableList<StringProperty> data = FXCollections.observableArrayList();
            data.add(new SimpleStringProperty(""));
            data.add(new SimpleStringProperty(""));
            data.add(new SimpleStringProperty("" + i));
            for (String value : table[i]) {
                data.add(new SimpleStringProperty(value));
            }
            tableView.getItems().add(data);
        }

        return text.toString();
    }

    void analyzeSentences(String sentence, TableView<ObservableList<StringProperty>> tableView){
        // TODO: 2017/12/8 analyzeSentences
    }
}
