package com.sm.query.delayedresolution;

/**
 * Created on 4/25/16.
 */
public interface StringModifier {
    String modify(String original);

    static StringModifier LOWER = new StringModifier() {
        @Override
        public String modify(String original) {
            return original.toLowerCase();
        }
    };

    static StringModifier UPPER = new StringModifier() {
        @Override
        public String modify(String original) {
            return original.toUpperCase();
        }
    };

    class Substring implements StringModifier {
        private final int beginning;

        public Substring(int beginning) {
            this.beginning = beginning;
        }

        @Override
        public String modify(String original) {
            return original.substring(beginning);
        }
    }
}
