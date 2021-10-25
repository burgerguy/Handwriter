package com.github.burgerguy.handwriter.glyph;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GlyphReader {

    // 792 * line / (44 / 3)
    // 14 lines total, so 13 columns
    public static final int COLUMNS = 13;

    // 612 * line / (34 / 3)
    // 11 lines total, so 10 rows
    public static final int ROWS = 10;

    // 6.0 / original * new = new grid line width
    // we then need to half that to get the correct offset

    private static int gridToImageX(int row, int imageWidth) {
        return ((imageWidth * row * 3) / 34) + (int) ((3.0 / 612.0) * imageWidth);
    }

    private static int gridToImageY(int column, int imageHeight) {
        return ((imageHeight * column * 3) / 44) + (int) ((3.0 / 792.0) * imageHeight);
    }

    // 3x3 normal notebook lines height per grid box

    public static MultiGlyph readPageGlyphs(BufferedImage originalImage, char representedChar, Random random) {
        List<BufferedImage> glyphList = new ArrayList<>();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                glyphList.add(cropAndGrayscaleGlyph(originalImage, row, col));
            }
        }
        return new MultiGlyph(glyphList.toArray(new BufferedImage[0]), representedChar, random, 2);
    }

    private static BufferedImage cropAndGrayscaleGlyph(BufferedImage bufferedImage, int row, int column) {
        int x = gridToImageX(row, bufferedImage)
        return null;
    }
}
