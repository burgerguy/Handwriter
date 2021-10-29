package com.github.burgerguy.handwriter.glyph;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class MutableGlyphFamily implements GlyphFamily {
    private final Char2ObjectMap<Glyph> charToGlyphMap;
    private final Random random;

    public MutableGlyphFamily(int expectedChars, Random random) {
        charToGlyphMap = new Char2ObjectOpenHashMap<>(expectedChars);
        charToGlyphMap.defaultReturnValue(SimpleGlyph.EMPTY);
        this.random = random;
    }

    public void put(char c, Glyph glyph) {
        charToGlyphMap.put(c, glyph);
    }

    @Override
    public Glyph getForCharacter(char c) {
        return charToGlyphMap.get(c);
    }

    @Override
    public float getSpaceSize() {
        // TODO: don't base it on page size
//        int index = random.nextInt(charToGlyphMap.size());
//
//        Iterator<Glyph> iterator = charToGlyphMap.values().iterator();
//
//        for( int i = 0; i < index-1; i++ ) {
//            iterator.next();
//        }
//
//        return iterator.next().getImage().rawImage().getWidth(null);
        return .012f;
    }

    @Override
    public float getTabSize() {
        return getSpaceSize() * 4.0f;
    }

    @Override
    public Collection<Glyph> getAllGlyphs() {
        return charToGlyphMap.values();
    }
}
