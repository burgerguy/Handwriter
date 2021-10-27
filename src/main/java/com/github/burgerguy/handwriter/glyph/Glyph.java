package com.github.burgerguy.handwriter.glyph;

import com.github.burgerguy.handwriter.image.AnchoredImage;

public interface Glyph {
    AnchoredImage getImage();
    char getCharacter();
}
