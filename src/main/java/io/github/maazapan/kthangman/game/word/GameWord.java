package io.github.maazapan.kthangman.game.word;

import io.github.maazapan.kthangman.utils.KatsuUtils;

import java.util.*;

public class GameWord {

    private String word;
    private String formattedWord;

    private int tips;

    private final List<String> tipsList;

    private final Map<String, List<String>> words = new HashMap<>();
    private final Map<Integer, String> usedTips = new HashMap<>();

    public GameWord() {
        this.tips = 3;
        this.tipsList = new ArrayList<>();
        words.put("HOUSE", Arrays.asList("A place where you live", "A building", "A building that has 4 walls"));
        words.put("CAT", Arrays.asList("A small animal", "A pet", "A cute animal"));
        words.put("MINECRAFT", Arrays.asList("A game", "A popular game", "A game that has a lot of players"));
        words.put("DOG", Arrays.asList("A pet", "A small animal", "A cute animal"));
        words.put("BANANA", Arrays.asList("A fruit", "A yellow fruit", "A fruit that has a lot of potassium"));
        words.put("APPLE", Arrays.asList("A fruit", "A red fruit", "A fruit that has a lot of vitamin C"));
        words.put("ORANGE", Arrays.asList("A fruit", "A orange fruit", "A fruit that has a lot of vitamin C"));
        words.put("TECHNOLOGY", Arrays.asList("A science", "A science of the future", "A science that can make you happy"));
        words.put("COMPUTER", Arrays.asList("A machine", "A machine that can do anything", "A machine that can make you happy"));
        words.put("MUSIC", Arrays.asList("A sound", "A sound that can make you happy", "A sound that can make you sad"));
    }

    public String getWord() {
        return word;
    }

    public int getTips() {
        return tips;
    }

    public String getFormattedWord() {
        return formattedWord;
    }

    public void setFormattedWord(String formattedWord) {
        this.formattedWord = formattedWord;
    }

    public void setTips(int tips) {
        this.tips = tips;
    }

    public int getRemainTips() {
        return tips;
    }

    public List<String> getTipsList() {
        return tipsList;
    }

    public Map<String, List<String>> getWords() {
        return words;
    }

    /**
     * Get a random tip from the tips list.
     *
     * @return Word tip
     */
    public String getRandomTip() {
        int random = 0;

        while (usedTips.containsKey(random)) {
            random = new Random().nextInt(tipsList.size());
        }

        usedTips.put(random, tipsList.get(random));
        tips--;

        return tipsList.get(random);
    }

    /**
     * Get a random word from the words list.
     */
    public void selectRandomWord() {
        int random = new Random().nextInt(words.keySet().size());

        // Set selected random word and tips.
        String selectedWord = words.keySet().toArray()[random].toString();
        tipsList.addAll(words.get(selectedWord));

        // Set word and formatted word.
        word = selectedWord;
        formattedWord = KatsuUtils.formatWord(selectedWord);
    }
}
