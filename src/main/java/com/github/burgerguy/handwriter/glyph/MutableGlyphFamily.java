package com.github.burgerguy.handwriter.glyph;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;

public class MutableGlyphFamily implements GlyphFamily {
    private final Char2ObjectMap<Glyph> charToGlyphMap;

    public MutableGlyphFamily(int expectedChars) {
        charToGlyphMap = new Char2ObjectOpenHashMap<>(expectedChars);
        charToGlyphMap.defaultReturnValue(SimpleGlyph.EMPTY);
    }

    public void put(char c, Glyph glyph) {
        charToGlyphMap.put(c, glyph);
    }

    @Override
    public Glyph getForCharacter(char c) {
        return charToGlyphMap.get(c);
    }
}
