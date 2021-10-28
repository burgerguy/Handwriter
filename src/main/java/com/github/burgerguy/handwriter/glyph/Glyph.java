package com.github.burgerguy.handwriter.glyph;

import com.github.burgerguy.handwriter.image.GlyphImage;

public interface Glyph {
    GlyphImage getImage();
    char getCharacter();
}
