package io.github.maazapan.kthangman.game.discover;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
                this.indexDiscover.add(i);
                this.charDiscover.add(letter);

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

        return finalFormat.toString().replaceAll("", " ").trim();
    }

    /**
     * Discover a random letter in the formatted word.
     *
     * @param word       Word to discover
     * @param formatWord format of the word
     * @return Formatted word with a random letter discovered
     */
    public String discoverRandom(String word, String formatWord) {
        List<Integer> spaces = new ArrayList<>();

        String formatted = formatWord.replaceAll(" ", "");
        String formattedWord = word.replace(" ", "");

        StringBuilder finalFormat = new StringBuilder(formatted);
        char selectedChar = ' ';
        int index = 0;

        while (indexDiscover.contains(index)) {
            index = new Random().nextInt(formattedWord.length());
        }

        for (int i = 0; i < formattedWord.length(); i++) {
            if (word.charAt(i) == ' ') {
                spaces.add(i);
            }

            if (index == i) {
                this.indexDiscover.add(i);
                this.charDiscover.add(selectedChar);

                selectedChar = formattedWord.charAt(i);
            }
        }

        finalFormat.deleteCharAt(index);
        finalFormat.insert(index, String.valueOf(selectedChar));

        for (Integer space : spaces) {
            finalFormat.insert(space, " ");
        }

        return finalFormat.toString().trim();
    }

    public List<Character> getCharDiscover() {
        return charDiscover;
    }
}
