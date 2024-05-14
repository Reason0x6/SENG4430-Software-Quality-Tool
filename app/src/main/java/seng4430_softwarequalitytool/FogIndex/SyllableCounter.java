package seng4430_softwarequalitytool.FogIndex;

public class SyllableCounter {

    // Function to count the number of syllables in a word
    private static int countSyllablesInWord(String word) {
        // Remove non-alphabetic characters and convert to lowercase
        word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();

        // If word is empty, return 0
        if(word.isEmpty())
            return 0;

        // Count the number of syllables using a simple heuristic
        int count = 0;
        boolean prevVowel = false;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (isVowel(c)) {
                if (!prevVowel) {
                    count++;
                    prevVowel = true;
                }
            } else {
                prevVowel = false;
            }
        }

        // Adjust the count for certain cases
        if (word.endsWith("e")) {
            count--;
        }
        if (count == 0) {
            count = 1; // At least one syllable for any word
        }

        return count;
    }

    // Function to check if a character is a vowel
    private static boolean isVowel(char c) {
        return "aeiou".indexOf(c) != -1;
    }

    // Function to count the number of words with three or more syllables in a sentence
    public static int countWordsWithThreeOrMoreSyllables(String sentence) {
        String[] words = sentence.split("\\s+");
        int count = 0;
        for (String word : words) {
            if (countSyllablesInWord(word) >= 3) {
                count++;
            }
        }
        return count;
    }


}

