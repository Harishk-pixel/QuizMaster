package com.quizmaster.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Design system tokens including colors, typography, and layout measurements.
 */
public final class UIConstants {

    private UIConstants() {}

    // --- Color Palette ---
    public static final Color BG_DARK = new Color(0x0B, 0x10, 0x20);             // #0B1020
    public static final Color SURFACE_SECONDARY = new Color(0x12, 0x1A, 0x2B);    // #121A2B
    public static final Color SURFACE_CARD = new Color(0x17, 0x22, 0x38);         // #172238
    public static final Color SURFACE_CARD_HOVER = new Color(0x1E, 0x2C, 0x48);
    public static final Color SURFACE_ELEVATED = new Color(0x22, 0x31, 0x50);

    public static final Color ACCENT_PRIMARY = new Color(0x7C, 0x5C, 0xFC);       // #7C5CFC
    public static final Color ACCENT_PRIMARY_HOVER = new Color(0x6D, 0x4B, 0xED);
    public static final Color ACCENT_PRIMARY_LIGHT = new Color(0x7C, 0x5C, 0xFC, 40);

    public static final Color ACCENT_SECONDARY = new Color(0x35, 0xB6, 0xFF);     // #35B6FF
    public static final Color ACCENT_SECONDARY_HOVER = new Color(0x23, 0xA2, 0xEB);
    public static final Color ACCENT_SECONDARY_LIGHT = new Color(0x35, 0xB6, 0xFF, 40);

    public static final Color TEXT_MAIN = new Color(0xF4, 0xF7, 0xFC);            // #F4F7FC
    public static final Color TEXT_MUTED = new Color(0x9A, 0xA8, 0xBD);           // #9AA8BD
    public static final Color TEXT_DIM = new Color(0x62, 0x73, 0x8D);

    public static final Color SUCCESS = new Color(0x35, 0xC9, 0x8A);              // #35C98A
    public static final Color SUCCESS_BG = new Color(0x35, 0xC9, 0x8A, 35);

    public static final Color ERROR = new Color(0xFF, 0x64, 0x7C);                // #FF647C
    public static final Color ERROR_BG = new Color(0xFF, 0x64, 0x7C, 35);

    public static final Color WARNING = new Color(0xFF, 0xB0, 0x39);              // #FFB039
    public static final Color WARNING_BG = new Color(0xFF, 0xB0, 0x39, 35);

    public static final Color BORDER_SUBTLE = new Color(0x23, 0x32, 0x4E);
    public static final Color BORDER_ACTIVE = new Color(0x7C, 0x5C, 0xFC);

    // --- Corner Radii ---
    public static final int RADIUS_SM = 8;
    public static final int RADIUS_MD = 12;
    public static final int RADIUS_LG = 16;
    public static final int RADIUS_XL = 20;
    public static final int RADIUS_PILL = 24;

    // --- Typography ---
    private static final String FONT_FAMILY = resolveFontFamily();

    private static String resolveFontFamily() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Set<String> fonts = new HashSet<>(Arrays.asList(ge.getAvailableFontFamilyNames()));
        if (fonts.contains("Segoe UI")) return "Segoe UI";
        if (fonts.contains("Inter")) return "Inter";
        if (fonts.contains("San Francisco")) return "San Francisco";
        if (fonts.contains("Helvetica Neue")) return "Helvetica Neue";
        if (fonts.contains("Arial")) return "Arial";
        return Font.SANS_SERIF;
    }

    public static Font getFont(int style, float size) {
        return new Font(FONT_FAMILY, style, (int) size).deriveFont(size);
    }

    public static final Font FONT_HERO = getFont(Font.BOLD, 32f);
    public static final Font FONT_H1 = getFont(Font.BOLD, 24f);
    public static final Font FONT_H2 = getFont(Font.BOLD, 20f);
    public static final Font FONT_H3 = getFont(Font.BOLD, 16f);
    public static final Font FONT_SUBTITLE = getFont(Font.PLAIN, 14f);
    public static final Font FONT_BODY = getFont(Font.PLAIN, 14f);
    public static final Font FONT_BODY_BOLD = getFont(Font.BOLD, 14f);
    public static final Font FONT_BODY_MEDIUM = getFont(Font.PLAIN, 15f);
    public static final Font FONT_BUTTON = getFont(Font.BOLD, 14f);
    public static final Font FONT_CAPTION = getFont(Font.PLAIN, 12f);
    public static final Font FONT_BADGE = getFont(Font.BOLD, 11f);
    public static final Font FONT_LOGO = getFont(Font.BOLD, 22f);
}
