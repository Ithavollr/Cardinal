package org.evlis.cardinal.helpers;

import org.evlis.cardinal.GlobalVars;

import java.util.List;
import java.util.Random;

public class Cryptography {
    // https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function
    public static int fnv1aHash(String input) {
        final int FNV_PRIME = 0x01000193; // 16777619
        final int FNV_OFFSET_BASIS = 0x811c9dc5; // 2166136261

        int hash = FNV_OFFSET_BASIS;
        for (char c : input.toCharArray()) {
            hash ^= c;
            hash *= FNV_PRIME;
        }
        return hash;
    }
    // Creates a unique mnemonic phrase to use as a key for each player's home dimension.
    public static String generatePlayerKey(String input) {
        int hash = fnv1aHash(input);
        Random rand = new Random(hash);

        // Load the wordlist from GlobalVars
        List<String> wordlist = GlobalVars.WORDLIST;
        int maxIndex = wordlist.size();
        StringBuilder phrase = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            int roll = rand.nextInt(maxIndex);
            phrase.append(wordlist.get(roll));
        }
        return phrase.toString();
    }
}
