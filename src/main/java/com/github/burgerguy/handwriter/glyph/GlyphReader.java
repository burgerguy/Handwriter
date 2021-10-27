package com.github.burgerguy.handwriter.glyph;

import com.github.burgerguy.handwriter.image.AnchoredImage;

import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GlyphReader {

    private final int columns;
    private final int rows;
    private final float gridLineSize;
    private final int cropThreshold;
    private final float gridPaddingPercent;
    private final int alphaAdd;

    public GlyphReader(int columns, int rows, float gridLineSize, int cropThreshold, float gridPaddingPercent, int alphaAdd) {
        this.columns = columns;
        this.rows = rows;
        this.gridLineSize = gridLineSize;
        this.cropThreshold = cropThreshold;
        this.gridPaddingPercent = gridPaddingPercent;
        this.alphaAdd = alphaAdd;
    }

    // gridLineSize / original * new = new grid line width
    // we then need to half that to get the correct offset

    private int gridToImageX(int row, int imageWidth) {
        return (int) (imageWidth * (row + 1.0 + gridPaddingPercent) * 3 / 34 + gridLineSize / 2.0 / 612.0 * imageWidth);
    }

    private int gridToImageY(int column, int imageHeight) {
        return (int) (imageHeight * (column + 1.0 + gridPaddingPercent) * 3 / 44 + gridLineSize / 2.0 / 792.0 * imageHeight);
    }

    private int gridBoxSize(int imageWidth) {
        return (int) (imageWidth * (1.0 - gridPaddingPercent * 2.0) * 3 / 34 - 6.0 / 612.0 * imageWidth);
    }

    // 3x3 normal notebook lines height per grid box

    public void readPageGlyphs(BufferedImage originalImage, char representedChar, Random random, MutableGlyphFamily glyphFamily) {
        boolean includesCaps = Character.isAlphabetic(representedChar);
        representedChar = Character.toLowerCase(representedChar);

        List<AnchoredImage> glyphListMain = new ArrayList<>();
        List<AnchoredImage> glyphListUpper = includesCaps ? new ArrayList<>() : null;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int x = gridToImageX(row, originalImage.getWidth());
                int y = gridToImageY(col, originalImage.getHeight());
                int size = gridBoxSize(originalImage.getWidth());

                Rectangle newImageArea = new Rectangle((int) (size / 2.0), (int) (size / 2.0), 0, 0);
                ImageFilter grayscaleAlphaFilter = new RGBImageFilter() {
                    @Override
                    public int filterRGB(int x, int y, int rgb) {
                        int gray = 255 - (int) (0.299 * (rgb >> 16 & 0xff) +
                                0.587 * (rgb >> 8 & 0xff) +
                                0.114 * (rgb & 0xff));

                        if (gray >= cropThreshold) {
                            newImageArea.add(x, y);
                        }

                        gray += alphaAdd;

                        if (gray < 0) gray = 0;
                        if (gray > 255) gray = 255;
                        return rgb & (0x00FFFFFF | gray << 24);
                    }
                };
                Image croppedFilteredImage;
                try {
                    Image glyphImage = originalImage.getSubimage(x, y, size, size);
                    ImageProducer imageProducerGrayscale = new FilteredImageSource(glyphImage.getSource(), grayscaleAlphaFilter);
                    Image uncroppedFilteredImage = Toolkit.getDefaultToolkit().createImage(imageProducerGrayscale);
                    uncroppedFilteredImage.getWidth(null); // force load image
                    ImageProducer imageProducerCropped = new FilteredImageSource(uncroppedFilteredImage.getSource(), new CropImageFilter(newImageArea.x, newImageArea.y, newImageArea.width, newImageArea.height));
                    croppedFilteredImage = Toolkit.getDefaultToolkit().createImage(imageProducerCropped);
                } catch (Exception e) {
                    System.out.println("Skipping image for grid section row: " + row + " col: " + col);
                    continue;
                }

                AnchoredImage anchoredImage = new AnchoredImage(croppedFilteredImage, (int) (size / (2.0 / 3.0)) + newImageArea.y);
                if (row >= 10 && glyphListUpper != null) {
                    glyphListUpper.add(anchoredImage);
                } else {
                    glyphListMain.add(anchoredImage);
                }
            }
        }

        glyphFamily.put(representedChar, new MultiGlyph(glyphListMain.toArray(new AnchoredImage[0]), representedChar, random, 2));
        if (glyphListUpper != null) {
            char upperChar = Character.toUpperCase(representedChar);
            glyphFamily.put(upperChar, new MultiGlyph(glyphListUpper.toArray(new AnchoredImage[0]), upperChar, random, 2));
        }
    }
}
