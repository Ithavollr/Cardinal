package org.evlis.cardinal.helpers;

import org.evlis.cardinal.GlobalVars;

import java.util.List;
import java.util.Random;

public class Cryptography {
    // https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function
    private static int fnv1aHash(String input) {
        final int FNV_PRIME = 0x01000193; // 16777619
        final int FNV_OFFSET_BASIS = 0x811c9dc5; // 2166136261

        int hash = FNV_OFFSET_BASIS;
        for (char c : input.toCharArray()) {
            hash ^= c;
            hash *= FNV_PRIME;
        }
        return hash;
    }
    // Use an Xorshift PPRNG so we control the implementation and ensure it doesn't change
    private static int xorShift32(int seed, int bound) {
        int x = seed;
        x ^= (x << 13);
        x ^= (x >>> 17);
        x ^= (x << 15);
        int result = x & Integer.MAX_VALUE; // mask to ensure non-negative
        return result % bound; // make sure bound > 0 or there will be trouble
    }
    // Creates a unique mnemonic phrase to use as a key for each player's home dimension.
    public static String generatePlayerKey(String input) {
        int hash = fnv1aHash(input);
        // Load the wordlist from GlobalVars
        List<String> wordlist = GlobalVars.WORDLIST;
        int maxIndex = wordlist.size();
        StringBuilder phrase = new StringBuilder();
        int roll = hash;
        for (int i = 0; i < 3; i++) {
            roll = xorShift32(roll, maxIndex);
            phrase.append(wordlist.get(roll));
        }
        return phrase.toString();
    }
}
