package io.github.maazapan.kthangman.game.discover;

import io.github.maazapan.kthangman.utils.KatsuUtils;

import java.util.*;

public class DiscoverLetter {

    private final List<Integer> indexDiscover = new ArrayList<>();
    private final List<Character> charDiscover = new ArrayList<>();

    /**
     * Discover a letter in the word, with a letter
     * and format a new word with the letter discovered
     *
     * @param word       Word to discover
     * @param formatWord Word format
     * @param letter     Letter to discover
     * @return Formatted word with the letter discovered
     */
    public String discover(String word, String formatWord, char letter) {
        List<Integer> spaces = new ArrayList<>();
        List<Integer> editedIndex = new ArrayList<>();

        String formatted = formatWord.replaceAll(" ", "");
        String formattedWord = word.replace(" ", "");

        StringBuilder finalFormat = new StringBuilder(formatted);

        for (int i = 0; i < formattedWord.length(); i++) {
            if (word.charAt(i) == ' ') {
                spaces.add(i);
            }

            if (formattedWord.charAt(i) == letter) {
                indexDiscover.add(i);
                editedIndex.add(i);
            }
        }
        for (Integer index : editedIndex) {
            finalFormat.deleteCharAt(index);
            finalFormat.insert(index, "" + letter);
        }

        for (Integer space : spaces) {
            finalFormat.insert(space, " ");
        }
        this.charDiscover.add(letter);

        return finalFormat.toString().replace("", " ").trim();
    }

    public String discoverRandomLetter(String word, String formatWord) {
        List<Integer> spaces = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == ' ') {
                spaces.add(i);
            }
        }

        String splitWord = word.replace(" ", "");
        String[] formattedWord = formatWord.split(" ");
        int index = 0;

        while (indexDiscover.contains(index)) {
            index = new Random().nextInt(splitWord.length());
        }

        StringBuilder finalFormat = new StringBuilder();
        String selectedChar = "" + splitWord.charAt(index);

        for (int i = 0; i < formattedWord.length; i++) {
            if (i == index) {
                finalFormat.append(selectedChar).append(" ");
                continue;
            }
            finalFormat.append(formattedWord[i]).append(" ");
        }

        for (Integer space : spaces) {
            finalFormat.insert(space * 2, " ");
        }

        this.charDiscover.add(splitWord.charAt(index));
        this.indexDiscover.add(index);

        return finalFormat.toString().trim();
    }

    public List<Character> getCharDiscover() {
        return charDiscover;
    }

    public List<Integer> getIndexDiscover() {
        return indexDiscover;
    }
}
