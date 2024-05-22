package seng4430_softwarequalitytool.FogIndex;

public class SyllableCounter {

    private static int countSyllablesInWord(String word) {
        word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
        if(word.isEmpty()) return 0;

        int count = 0;
        boolean prevVowel = false;
        for (char c : word.toCharArray()) {
            if (isVowel(c)) {
                if (!prevVowel) {
                    count++;
                    prevVowel = true;
                }
            } else {
                prevVowel = false;
            }
        }

        if (word.endsWith("e")) count--;
        return Math.max(count, 1);
    }

    private static boolean isVowel(char c) {
        return "aeiou".indexOf(c) != -1;
    }

    public static int countWordsWithThreeOrMoreSyllables(String sentence) {
        int count = 0;
        for (String word : sentence.split("\\s+")) {
            if (countSyllablesInWord(word) >= 3) count++;
        }
        return count;
    }
}