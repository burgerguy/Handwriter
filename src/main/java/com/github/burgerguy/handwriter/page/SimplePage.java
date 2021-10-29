package com.github.burgerguy.handwriter.page;

import java.awt.image.BufferedImage;

public class SimplePage implements Page {

    private final BufferedImage backgroundImage;
    private final float leftMargin;
    private final float rightMargin;
    private final float topMargin;
    private final float bottomMargin;
    private final float lineHeight;
    private final boolean wordWrap;
    private final float randomOffset;

    private final int pointerXStart;
    private final int pointerYStart;
    private int pointerX;
    private int pointerY;

    public SimplePage(BufferedImage backgroundImage, float leftMargin, float rightMargin, float topMargin, float bottomMargin, float lineHeight, boolean wordWrap, float randomOffset) {
        this.backgroundImage = backgroundImage;
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
        this.lineHeight = lineHeight;
        this.wordWrap = wordWrap;
        this.randomOffset = randomOffset;

        pointerXStart = (int) (getWidth() * leftMargin);
        pointerYStart = (int) (getHeight() * topMargin);
        pointerX = pointerXStart;
        pointerY = pointerYStart;
    }

    @Override
    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    @Override
    public float getLeftMargin() {
        return leftMargin;
    }

    @Override
    public float getRightMargin() {
        return rightMargin;
    }

    @Override
    public float getTopMargin() {
        return topMargin;
    }

    @Override
    public float getBottomMargin() {
        return bottomMargin;
    }

    @Override
    public float getLineHeight() {
        return lineHeight;
    }

    @Override
    public boolean wordWrap() {
        return wordWrap;
    }

    @Override
    public int getWidth() {
        return backgroundImage.getWidth();
    }

    @Override
    public int getHeight() {
        return backgroundImage.getHeight();
    }

    @Override
    public float getPointerX() {
        return pointerX;
    }

    @Override
    public float getPointerY() {
        return pointerY;
    }

    @Override
    public void addToPointer(float x) {
        pointerX += x;
    }

    @Override
    public void nextLine() {
        pointerX = pointerXStart;
        pointerY += getHeight() * lineHeight;
    }

    @Override
    public void resetPointer() {
        pointerX = pointerXStart;
        pointerY = pointerYStart;
    }

    @Override
    public float randomOffsetMax() {
        return randomOffset;
    }

}
