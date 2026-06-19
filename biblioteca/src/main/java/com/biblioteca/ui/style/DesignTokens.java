package com.biblioteca.ui.style;

import javafx.geometry.Insets;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public final class DesignTokens {

    private DesignTokens() {}

    // Background
    public static final Color BG_PRIMARY = Color.web("#0f172a");
    public static final Color BG_SURFACE = Color.web("#1e293b");
    public static final Color BG_SECONDARY = Color.web("#0f2740");
    public static final Color BG_CARD = Color.web("#112240");
    public static final Color BG_ELEVATED = Color.web("#1a365d");

    // Blue accents
    public static final Color BLUE_PRIMARY = Color.web("#1d4ed8");
    public static final Color BLUE_HOVER = Color.web("#2563eb");
    public static final Color BLUE_GLOW = Color.web("#3b82f6");
    public static final Color BLUE_LIGHT = Color.web("#60a5fa");
    public static final Color BLUE_SUBTLE = Color.web("#93c5fd");
    public static final Color BLUE_BG_SUBTLE = Color.rgb(29, 78, 216, 0.15);

    // Text
    public static final Color TEXT_PRIMARY = Color.WHITE;
    public static final Color TEXT_SECONDARY = Color.web("#cbd5e1");
    public static final Color TEXT_MUTED = Color.web("#94a3b8");

    // Accents
    public static final Color ACCENT_WARNING = Color.web("#fbbf24");
    public static final Color ACCENT_SUCCESS = Color.web("#22c55e");
    public static final Color ACCENT_ERROR = Color.web("#ef4444");
    public static final Color ACCENT_REMOVED = Color.web("#6b7280");

    // Semantic aliases
    public static final Color BORDER_FOCUS = BLUE_GLOW;
    public static final double DISABLED_OPACITY = 0.4;

    // Shadows
    public static final Color SHADOW = Color.rgb(0, 0, 0, 0.5);
    public static final Color SHADOW_BLUE = Color.rgb(29, 78, 216, 0.4);
    public static final Color SHADOW_CARD = Color.rgb(0, 0, 0, 0.4);

    // Radii
    public static final CornerRadii RADIUS_CARD = new CornerRadii(12);
    public static final CornerRadii RADIUS_PANEL = new CornerRadii(8);
    public static final CornerRadii RADIUS_BUTTON = new CornerRadii(6);
    public static final CornerRadii RADIUS_PILL = new CornerRadii(999);

    // Spacing
    public static final int SPACING_SMALL = 6;
    public static final int SPACING_BASE = 8;
    public static final int SPACING_MEDIUM = 12;
    public static final int SPACING_LARGE = 16;
    public static final int SPACING_XLARGE = 24;

    // Padding
    public static final Insets PADDING_CONTENT = new Insets(12);
    public static final Insets PADDING_PANEL = new Insets(16);
    public static final Insets PADDING_CARD = new Insets(10);

    // Font sizes
    public static final int FONT_XSMALL = 10;
    public static final int FONT_SMALL = 12;
    public static final int FONT_BASE = 13;
    public static final int FONT_MEDIUM = 14;
    public static final int FONT_LARGE = 15;
    public static final int FONT_XLARGE = 18;
    public static final int FONT_XXLARGE = 20;
    public static final int FONT_TITLE = 24;
    public static final int FONT_HERO = 28;

    // Card
    public static final int CARD_WIDTH = 150;
    public static final int CARD_HEIGHT = 230;
    public static final int CARD_HGAP = 12;
    public static final int CARD_VGAP = 12;

    // Layout
    public static final int FILTER_PANEL_WIDTH = 200;
    public static final int DETAIL_PANEL_WIDTH = 340;

    // Animation
    public static final int HOVER_SCALE_PCT = 108;
    public static final int HOVER_DURATION_MS = 200;
    public static final int FOCUS_DURATION_MS = 150;

    // Style helpers
    public static String bg(String color) {
        return "-fx-background-color: " + color + ";";
    }

    public static String bg(Color color) {
        return "-fx-background-color: " + toRgba(color) + ";";
    }

    public static String textFill(Color color) {
        return "-fx-text-fill: " + toRgba(color) + ";";
    }

    public static String textFill(String color) {
        return "-fx-text-fill: " + color + ";";
    }

    public static String roundedBg(Color color, double radius) {
        return bg(color) + " -fx-background-radius: " + radius + ";";
    }

    public static String border(Color color, double radius, int width) {
        return "-fx-border-color: " + toRgba(color) + "; -fx-border-radius: "
                + radius + "; -fx-border-width: " + width + ";";
    }

    public static String toRgba(Color c) {
        return String.format("rgba(%d,%d,%d,%.2f)",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255),
                c.getOpacity());
    }
}
