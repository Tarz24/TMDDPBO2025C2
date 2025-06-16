package models;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Ball {
    private int x, y, value, speed, direction;
    private boolean isActive = true;
    private Image image;

    private static Image meteoriteImage, asteroidImage, satelliteImage, starImage, ufoImage, plutoImage;

    static {
        try {
            meteoriteImage = ImageIO.read(new File("resources/images/meteorite.png"));
            asteroidImage = ImageIO.read(new File("resources/images/asteroid.png"));
            satelliteImage = ImageIO.read(new File("resources/images/satellite.png"));
            starImage = ImageIO.read(new File("resources/images/star.png"));
            ufoImage = ImageIO.read(new File("resources/images/ufo.png"));
            plutoImage = ImageIO.read(new File("resources/images/pluto.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final int BALL_SIZE = 33;
    public static final int DEFAULT_SPEED = 2;

    public Ball(int x, int y, int value, int direction) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.direction = direction;
        this.image = assignImageByValue(value);
        this.speed = (value == -50) ? DEFAULT_SPEED * 2 : DEFAULT_SPEED;
    }

    private Image assignImageByValue(int v) {
        if (v == 250) return ufoImage;
        if (v == -50) return plutoImage;
        if (v <= 30) return meteoriteImage;
        if (v <= 60) return asteroidImage;
        if (v <= 90) return satelliteImage;
        if (v <= 120) return starImage;
        return meteoriteImage;
    }

    public void update() {
        if (isActive) {
            x += speed * direction;
        }
    }

    public boolean isOutOfBounds(int screenWidth) {
        return x < -BALL_SIZE || x > screenWidth;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, BALL_SIZE, BALL_SIZE);
    }

    public Point getCenter() {
        return new Point(x + BALL_SIZE / 2, y + BALL_SIZE / 2);
    }

    public Image getImage() { return image; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getValue() { return value; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}