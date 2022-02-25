package com.github.burgerguy.handwriter.page;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.awt.image.BufferedImage;
import java.util.Random;

public class RandomBackgroundPageProvider implements PageProvider {

    private final Int2ObjectMap<Page> pageNoToPageMap;

    private final Random random;
    private final BufferedImage[] frontBackgroundImages;
    private final BufferedImage[] backBackgroundImages;
    private final float leftMargin;
    private final float rightMargin;
    private final float topMargin;
    private final float bottomMargin;
    private final float lineHeight;
    private final WordWrapMode wordWrapMode;
    private final float randomOffset;

    // TODO: add memory for repeats
    public RandomBackgroundPageProvider(int expectedPages, BufferedImage[] frontBackgroundImages, BufferedImage[] backBackgroundImages, float leftMargin, float rightMargin, float topMargin, float bottomMargin, float lineHeight, WordWrapMode wordWrapMode, float randomOffset, Random random) {
        this.frontBackgroundImages = frontBackgroundImages;
        this.backBackgroundImages = backBackgroundImages;
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
        this.lineHeight = lineHeight;
        this.wordWrapMode = wordWrapMode;
        this.random = random;
        this.randomOffset = randomOffset;

        pageNoToPageMap = new Int2ObjectOpenHashMap<>(expectedPages);
    }

    @Override
    public Page getPage(int pageNo) {
        return pageNoToPageMap.computeIfAbsent(pageNo, i -> {
            BufferedImage backgroundImage;
            if (pageNo % 2 == 0) {
                backgroundImage = frontBackgroundImages[random.nextInt(frontBackgroundImages.length)];
            } else {
                backgroundImage = backBackgroundImages[random.nextInt(backBackgroundImages.length)];
            }

            return new SimplePage(backgroundImage, leftMargin, rightMargin, topMargin, bottomMargin, lineHeight, wordWrapMode, randomOffset);
        });
    }
}
