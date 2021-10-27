package com.github.burgerguy.handwriter.glyph;

import com.github.burgerguy.handwriter.image.AnchoredImage;
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
        return new SimpleGlyph(new AnchoredImage(image, 20), '\0');
    }

    private final AnchoredImage image;
    private final char representedChar;

    private SimpleGlyph(AnchoredImage image, char representedChar) {
        this.image = image;
        this.representedChar = representedChar;
    }

    @Override
    public AnchoredImage getImage() {
        return image;
    }

    @Override
    public char getCharacter() {
        return representedChar;
    }
}
