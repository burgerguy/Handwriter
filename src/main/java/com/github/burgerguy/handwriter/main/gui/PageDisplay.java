package com.github.burgerguy.handwriter.main.gui;

import com.github.burgerguy.handwriter.glyph.Glyph;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;

import javax.swing.text.Style;
import java.awt.*;
import java.util.function.Supplier;

public class PageDisplay extends Component {

    private final Char2ObjectMap<Glyph> glyphFamily;

    public PageDisplay(Supplier<String> textSupplier,Char2ObjectMap<Glyph> glyphFamily) {
        this.glyphFamily = glyphFamily;
    }
}
