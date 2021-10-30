package com.github.burgerguy.handwriter.glyph;

import java.util.Collection;

public interface GlyphFamily {
    Glyph getForCharacter(char c);
    Collection<Glyph> getAllGlyphs();
    float getSpaceSize();
}
