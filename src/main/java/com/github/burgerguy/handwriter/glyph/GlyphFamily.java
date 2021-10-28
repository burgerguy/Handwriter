package com.github.burgerguy.handwriter.glyph;

public interface GlyphFamily {
    Glyph getForCharacter(char c);

    // TODO: these don't belong here, probably in Page or something
    float getSpaceSize();
    float getTabSize();
}
