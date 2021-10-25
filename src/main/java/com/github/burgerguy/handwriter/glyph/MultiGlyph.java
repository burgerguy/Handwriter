package com.github.burgerguy.handwriter.glyph;

import java.awt.*;
import java.util.Random;

public class MultiGlyph implements Glyph {
    private final Random random;
    private final char representedChar;
    private final Image[] images;

    private final int[] recentIndices;
    private int recentArrayIdx = 0;

    public MultiGlyph(Image[] images, char representedChar, Random random, int recentMemorySize) {
        this.images = images;
        this.representedChar = representedChar;
        this.random = random;

        // don't use recent memory if this happens
        if (images.length - recentMemorySize <= 0) {
            this.recentIndices = null;
        } else {
            this.recentIndices = new int[recentMemorySize];
        }
    }

    @Override
    public Image getImage() {
        int generatedNum;
        do {
            generatedNum = random.nextInt(images.length);
            // retry if recently used
        } while (isRecentlyUsed(generatedNum));
        setRecentlyUsed(generatedNum);
        return images[generatedNum];
    }

    private boolean isRecentlyUsed(int index) {
        if (recentIndices == null) return false;

        for (int recentIndex : recentIndices) {
            if (index == recentIndex) return true;
        }
        return false;
    }

    private void setRecentlyUsed(int index) {
        if (recentIndices != null) {
            recentIndices[recentArrayIdx] = index;
            recentArrayIdx = (recentArrayIdx + 1) % recentIndices.length;
        }
    }

    @Override
    public char getCharacter() {
        return representedChar;
    }
}
