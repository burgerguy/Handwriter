package com.github.burgerguy.handwriter.glyph;

import com.github.burgerguy.handwriter.image.GlyphImage;
import com.github.burgerguy.handwriter.main.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public final class SimpleGlyph implements Glyph {

    public static final SimpleGlyph EMPTY = createEmptyGlyph();

    private static SimpleGlyph createEmptyGlyph() {
        BufferedImage image;
        try {
            image = ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("unknown-glyph.png")));
        } catch (IOException e) {
            image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        }
        return new SimpleGlyph(new GlyphImage(image, 20.0f, 1.0f), '\0');
    }

    private final GlyphImage image;
    private final char representedChar;

    private SimpleGlyph(GlyphImage image, char representedChar) {
        this.image = image;
        this.representedChar = representedChar;
    }

    @Override
    public GlyphImage getImage() {
        return image;
    }

    @Override
    public char getCharacter() {
        return representedChar;
    }

}
