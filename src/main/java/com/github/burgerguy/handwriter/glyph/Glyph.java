package com.github.burgerguy.handwriter.glyph;

import java.awt.image.BufferedImage;

public interface Glyph {
    BufferedImage getImage();
    char getCharacter();
}
