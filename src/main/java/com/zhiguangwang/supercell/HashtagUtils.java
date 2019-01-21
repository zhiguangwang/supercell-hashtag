package com.zhiguangwang.supercell;

import java.util.Objects;

public class HashtagUtils {

    // Base-14 encoding
    private static final String ALPHABET = "0289PYLQGRJCUV";
    private static final int BASE = ALPHABET.length();

    private static final int HI_BITS = 8;
    private static final int HI_MODULO = 1 << HI_BITS;
    private static final int HI_MIN_VALUE = 1;
    private static final int HI_MAX_VALUE = HI_MODULO - 1;

    public static long[] getHiLoFromHashtag(String hashtag) {
        long num = getLongFromHashtag(hashtag);
        long hi = num % HI_MODULO;
        long lo = (num - hi) >> HI_BITS;
        return new long[]{hi, lo};
    }

    public static String getHashtagFromHiLo(long hi, long lo) {
        if (hi < HI_MIN_VALUE || hi > HI_MAX_VALUE) {
            throw new IllegalArgumentException("Invalid hi: " + hi);
        }
        long num = (lo << HI_BITS) + hi;
        return getHashtagFromLong(num);
    }

    public static long getLongFromHashtag(String hashtag) {
        hashtag = normalizeHashtag(hashtag);
        return decode(hashtag);
    }

    public static String getHashtagFromLong(long num) {
        return encode(num);
    }

    private static String normalizeHashtag(String hashtag) {
        Objects.requireNonNull(hashtag, "hashtag");
        return hashtag
            .trim()
            .replace("#", "")
            .toUpperCase();
    }

    private static long decode(String s) {
        long num = 0;
        for (int i = 0; i < s.length(); i++) {
            int index = ALPHABET.indexOf(s.codePointAt(i));
            if (index == -1) {
                throw new IllegalArgumentException("Invalid char: " + s.charAt(i));
            } else {
                num *= BASE;
                num += index;
            }
        }
        return num;
    }

    private static String encode(long num) {
        var sb = new StringBuilder(16);
        do {
            var index = (int) (num % BASE);
            num /= BASE;
            sb.append(ALPHABET.charAt(index));
        } while (num > 0);
        sb.reverse();
        return sb.toString();
    }
}
