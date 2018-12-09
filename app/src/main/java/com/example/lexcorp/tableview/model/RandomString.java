package com.example.lexcorp.tableview.model;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class RandomString {

    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String lower = upper.toLowerCase(Locale.ROOT);

    private static final String alphanum = upper + lower;

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    @SuppressWarnings("WeakerAccess")
    public RandomString(int length, Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    @SuppressWarnings("WeakerAccess")
    public RandomString(int length, Random random) {
        this(length, random, alphanum);
    }

    public RandomString(int length) {
        this(length, new SecureRandom());
    }

    @SuppressWarnings("unused")
    public RandomString() {
        this(21);
    }

}
