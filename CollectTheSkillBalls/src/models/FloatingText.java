package models;

import java.awt.*;

public class FloatingText {
    private String text;
    private Point position;
    private Color color;
    private int lifetime;
    private final int maxLifetime = 120;

    public FloatingText(Point position, String text, Color color) {
        this.position = position;
        this.text = text;
        this.color = color;
        this.lifetime = maxLifetime;
    }

    public void update() {
        lifetime--;
        position.y--;
    }

    public boolean isAlive() {
        return lifetime > 0;
    }

    public String getText() { return text; }
    public Point getPosition() { return position; }

    public Color getColor() {
        float opacity = Math.max(0, (float) lifetime / (float) maxLifetime);
        return new Color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, opacity);
    }
}