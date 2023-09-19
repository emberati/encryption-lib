package com.emb.crypto;

import java.util.*;
import java.util.stream.Collectors;

public class LetterFrequencyEncoder implements Encoder<String> {
    private final Map<Character, Character> dict = new HashMap<>();
    private final Map<Character, Integer> letterMap = new HashMap<>();
    private final Map<String, Integer> bigramMap = new HashMap<>();
    private final Map<String, Integer> trigramMap = new HashMap<>();

    @Deprecated
    @Override
    public String encode(String data) {
        return null;
    }

    @Override
    public String decode(String data) {
        var words = data.split("[,.\s:\\-\"\n]", -1);
        var decoded = new StringBuilder();

        for (var word : words) analyze(word);
        fillDict();

        for (int i = 0; i < data.length(); i++)
            if (dict.containsKey(data.charAt(i)))
                decoded.append(dict.get(data.charAt(i)));
            else decoded.append(data.charAt(i));

        return decoded.toString();
    }

    /**
     * Method for analyzing split words from initial text
     * @param word what to analyze
     */
    public void analyze(String word) {
        for (char letter : word.toCharArray()) {
            letterMap.merge(letter, 1, Integer::sum);
        }

        for (int i = 0; i < word.length() - 1; i++) {
            var bigram = word.substring(i, i + 2);
            bigramMap.merge(bigram, 1, Integer::sum);
        }

        for (int i = 0; i < word.length() - 2; i++) {
            String trigram = word.substring(i, i + 3);
            trigramMap.merge(trigram, 1, Integer::sum);
        }
    }

    private void fillDict() {
        dict.put('щ', 'в');    // первое слово текста - либо В, либо С
        dict.put('ц', 'о');    // по частоте
        // цъц - о_о - либо обо, либо оно, но есть удвоение ъъ - это нн (ии, оо, сс), значит ъ - это н
        dict.put('ъ', 'н');
        // ъэжэ - н___ - возможно, ними, так как есть удвоение ээ - то "э", вероятно, "и"
        dict.put('э', 'и');    // тогда ъэжэ - ними, значит ж - это м
        dict.put('ж', 'м');
        // ъдж - это либо ним, либо нем (нём), либо нам
        // если бы "д" было "и" или "а", то в шифротексте был бы такой предлог, но его нет => д - это е
        dict.put('д', 'е');
        // йщцэжэ (_воими) + щйд (в_е) => "й" - это "с"
        dict.put('й', 'с');
        // Предлог ъм (н_) - это на, не, ни или но (поскольку буквы о,и,е уже расшифрованы, то м - это а)
        dict.put('м', 'а');
        // цйцпдъъц (осо_енно) - п - это б
        dict.put('п', 'б');
        // ъмещмъэд (на_вание) - е - это з
        dict.put('е', 'з');
        // щсдждъм (в_емена) - с - это р
        dict.put('с', 'р');
        // И далее в таком духе
        dict.put('и', 'к');
        dict.put('ь', 'л');
        dict.put('ю', 'т');
        dict.put('к', 'й');
        dict.put('з', 'п');
        dict.put('ы', 'у');
        dict.put('л', 'ж');
        dict.put('я', 'д');
        dict.put('б', 'ы');
        dict.put('ф', 'г');
        dict.put('о', 'х');
        dict.put('н', 'я');
        dict.put('ч', 'ь');
        dict.put('ш', 'ц');
        dict.put('г', 'э');
        dict.put('у', 'ш');
        dict.put('а', 'ч');
        dict.put('в', 'щ');
        dict.put('т', 'ю');
        dict.put('х', 'ъ');
        dict.put('р', 'ф');
    }

    private <T> LinkedHashSet<Map.Entry<T, Integer>> getFrequencies(Map<T, Integer> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Map.Entry<String, Integer>> getBigramFrequencies() {
        return getFrequencies(bigramMap);
    }

    public Set<Map.Entry<String, Integer>> getTriramFrequencies() {
        return getFrequencies(trigramMap);
    }

    public Set<Map.Entry<String, Integer>> getBigramDoubles() {
        return getBigramFrequencies()
                .stream()
                .filter(entry -> {
                    var bigram = entry.getKey();
                    return bigram.charAt(0) == bigram.charAt(1);
                })
                .collect(Collectors.toSet());
    }

    public void flush() {
        dict.clear();
        letterMap.clear();
        bigramMap.clear();
        trigramMap.clear();
    }
}
