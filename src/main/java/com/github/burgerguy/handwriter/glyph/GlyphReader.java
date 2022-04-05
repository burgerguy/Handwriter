package com.github.burgerguy.handwriter.glyph;

import com.github.burgerguy.handwriter.image.GlyphImage;

import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class GlyphReader {

    private final int columns;
    private final int rows;
    private final float gridLineSize;
    private final int alphaThreshold;
    private final float gridPaddingPercent;
    private final float minAlphaRatio;

    public GlyphReader(int columns, int rows, float gridLineSize, int alphaThreshold, float gridPaddingPercent, float minAlphaRatio) {
        this.columns = columns;
        this.rows = rows;
        this.gridLineSize = gridLineSize;
        this.alphaThreshold = alphaThreshold;
        this.gridPaddingPercent = gridPaddingPercent;
        this.minAlphaRatio = minAlphaRatio;
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
        representedChar = Character.toLowerCase(representedChar);
        List<GlyphImage> glyphListMain = new ArrayList<>();
        List<GlyphImage> glyphListUpper = Character.isAlphabetic(representedChar) ? new ArrayList<>() : null;
        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                int x = gridToImageX(row, originalImage.getWidth());
                int y = gridToImageY(col, originalImage.getHeight());
                int size = gridBoxSize(originalImage.getWidth());

                AtomicInteger currentHighestAlpha = new AtomicInteger(0);
                AtomicInteger currentTotalAlpha = new AtomicInteger(0);
                Rectangle newImageArea = new Rectangle((int) (size / 2.0), (int) (size / 2.0), 0, 0);
                ImageFilter grayscaleAlphaFilter = new RGBImageFilter() {
                    @Override
                    public int filterRGB(int x, int y, int rgb) {
                        int alpha = 255 - (int) (0.299 * (rgb >> 16 & 0xff) +
                                0.587 * (rgb >> 8 & 0xff) +
                                0.114 * (rgb & 0xff));

                        if (alpha >= alphaThreshold) {
                            newImageArea.add(x, y);
                        }

                        if (alpha < 0) alpha = 0;
                        if (alpha > 255) alpha = 255;
                        if (alpha > 0) {
                            currentTotalAlpha.getAndAdd(alpha);
                            currentHighestAlpha.getAndAccumulate(alpha, Math::max);
                        }
                        return rgb & 0x00FFFFFF | alpha << 24;
                    }
                };
                Image finalImage;
                try {
                    Image glyphImage = originalImage.getSubimage(x, y, size, size);
                    ImageProducer imageProducerGrayscale = new FilteredImageSource(glyphImage.getSource(), grayscaleAlphaFilter);
                    Image uncroppedFilteredImage = Toolkit.getDefaultToolkit().createImage(imageProducerGrayscale);
                    uncroppedFilteredImage.getWidth(null); // force load rawImage
                    if (newImageArea.width <= 0 || newImageArea.height <= 0) {
                        System.out.println("Image area is 0, stopping page... char: " + representedChar + " row: " + row + " col: " + col);
                        break;
                    } else {
                        int maxAlpha = size * size * 255;
                        int totalAlpha = currentTotalAlpha.get();
                        float alphaRatio = (float) totalAlpha / maxAlpha;
                        if (alphaRatio < minAlphaRatio) {
                            System.out.println("Image alpha ratio is " + alphaRatio + ", but min alpha ratio is " + minAlphaRatio + ", stopping page... char: " + representedChar + " row: " + row + " col: " + col);
                            break;
                        }
                    }
                    ImageProducer imageProducerCropped = new FilteredImageSource(uncroppedFilteredImage.getSource(), new CropImageFilter(newImageArea.x, newImageArea.y, newImageArea.width, newImageArea.height));
                    Image croppedFilteredImage = Toolkit.getDefaultToolkit().createImage(imageProducerCropped);
                    int highestAlpha = currentHighestAlpha.get();
                    if (highestAlpha < 255) {
                        float scale = 255.0f / highestAlpha;
                        ImageFilter alphaScaleFilter = new RGBImageFilter() {
                            @Override
                            public int filterRGB(int x, int y, int rgb) {
                                return rgb & 0x00FFFFFF | (int)((rgb >> 24) * scale) << 24;
                            }
                        };
                        ImageProducer imageProducerScaledAlpha = new FilteredImageSource(croppedFilteredImage.getSource(), alphaScaleFilter);
                        finalImage = Toolkit.getDefaultToolkit().createImage(imageProducerScaledAlpha);
                    } else {
                        finalImage = croppedFilteredImage;
                    }
                } catch (Exception e) {
                    System.out.println("Skipping rawImage for grid section row: " + row + " col: " + col);
                    continue;
                }

                // (int) (size * (2.0 / 3.0)) - newImageArea.y
                GlyphImage glyphImage = new GlyphImage(finalImage, 2.0f * size / 3.0f - newImageArea.y, finalImage.getHeight(null) * 44.0f / originalImage.getHeight());
                if (col >= 9 && glyphListUpper != null) {
                    glyphListUpper.add(glyphImage);
                } else {
                    glyphListMain.add(glyphImage);
                }
            }
        }

        glyphFamily.put(representedChar, new MultiGlyph(glyphListMain.toArray(new GlyphImage[0]), representedChar, random, 2));
        if (glyphListUpper != null) {
            char upperChar = Character.toUpperCase(representedChar);
            glyphFamily.put(upperChar, new MultiGlyph(glyphListUpper.toArray(new GlyphImage[0]), upperChar, random, 2));
        }
    }
}
