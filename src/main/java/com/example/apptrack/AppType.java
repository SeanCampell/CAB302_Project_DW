package com.example.apptrack;

public enum AppType {

    Game('g'),
    Productive('p'),
    Internet('i'),
    Entertainment('e');

    /*
     * This particular char value to its possessor
     */
    private final char symbol;

    /*
     * When an object of this enumerated type is
     * constructed, the constructor stores the particular
     * value associated with the app type in a private field
     */

    AppType(char value) { symbol = value; }

    /*
     * Returns the (subjective) value of this app type to the
     * app who possesses it.
     */

    public int getValue() { return symbol; }
}
