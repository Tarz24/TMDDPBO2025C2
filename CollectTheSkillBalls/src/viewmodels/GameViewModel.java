package viewmodels;

import models.Ball;
import models.FloatingText;
import models.PlayerResult;
import repositories.DatabaseRepository;
import services.SoundService;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Observable;

@SuppressWarnings("deprecation")
public class GameViewModel extends Observable {

    public static final int GAME_WIDTH = 1920;
    public static final int GAME_HEIGHT = 1080;
    public static final int PLAYER_AREA_TOP_Y = 440;
    public static final int PLAYER_AREA_BOTTOM_Y = 590;
    public static final Rectangle ROCKET_AREA = new Rectangle(75, PLAYER_AREA_TOP_Y, 256, 256);
    public static final int MAX_PLUTO_HITS = 30;

    private PlayerResult currentPlayer;
    private Point playerPosition;
    private List<Ball> balls;
    private List<FloatingText> floatingTexts;
    private Ball hookedBall = null;
    private boolean isGameOver = false;

    private int gameTimeSeconds = 0;
    private int plutoHitCount = 0;
    private long lastTimeCheck;

    private boolean movingUp, movingDown, movingLeft, movingRight;
    private final int playerSpeed = 5;
    private final int reelSpeed = 15;

    private Random random;
    private SoundService soundService;
    private DatabaseRepository dbRepository;

    public GameViewModel(PlayerResult player) {
        this.currentPlayer = player;
        this.playerPosition = new Point(GAME_WIDTH / 2, (PLAYER_AREA_TOP_Y + PLAYER_AREA_BOTTOM_Y) / 2);
        this.balls = new ArrayList<>();
        this.floatingTexts = new ArrayList<>();
        this.random = new Random();
        this.lastTimeCheck = System.currentTimeMillis();
        this.dbRepository = DatabaseRepository.getInstance();
        this.soundService = SoundService.getInstance();

        initializeSound();
    }

    private void initializeSound() {
        soundService.loadSound("catch", "resources/sounds/catch.wav");
        soundService.loadSound("music", "resources/sounds/music.wav");
        soundService.loopSound("music");
    }

    public void updateGame() {
        if (isGameOver) return;

        movePlayer();
        updateHookedBall();
        updateFreeBalls();
        spawnNewBall();
        updateFloatingTexts();
        updateGameTime();

        setChanged();
        notifyObservers();

        if (plutoHitCount >= MAX_PLUTO_HITS) {
            isGameOver = true;
            soundService.stopSound("music");
            setChanged();
            notifyObservers("GAME_OVER");
        }
    }

    public void handleMouseClick(Point clickPoint) {
        for (int i = balls.size() - 1; i >= 0; i--) {
            Ball ball = balls.get(i);
            if (ball.isActive() && ball.getBounds().contains(clickPoint)) {
                attemptToFish(ball);
                break;
            }
        }
    }

    public void setPlayerMovement(boolean up, boolean down, boolean left, boolean right) {
        this.movingUp = up;
        this.movingDown = down;
        this.movingLeft = left;
        this.movingRight = right;
    }

    public void endGame() {
        isGameOver = true;
        soundService.stopSound("music");
        saveGameResult();
        setChanged();
        notifyObservers("GAME_ENDED");
    }

    private void attemptToFish(Ball targetBall) {
        if (hookedBall == null) {
            hookedBall = targetBall;
            hookedBall.setActive(false);
        }
    }

    private void updateHookedBall() {
        if (hookedBall == null) return;

        Point target = new Point(ROCKET_AREA.x + ROCKET_AREA.width / 2, ROCKET_AREA.y + ROCKET_AREA.height / 2);
        Point ballCenter = hookedBall.getCenter();

        if (target.distance(ballCenter) < reelSpeed) {
            processScore(hookedBall);
            balls.remove(hookedBall);
            hookedBall = null;
        } else {
            double dx = target.x - ballCenter.x;
            double dy = target.y - ballCenter.y;
            double distance = target.distance(ballCenter);
            int moveX = (int) (dx / distance * reelSpeed);
            int moveY = (int) (dy / distance * reelSpeed);
            hookedBall.setX(hookedBall.getX() + moveX);
            hookedBall.setY(hookedBall.getY() + moveY);
        }
    }

    private void processScore(Ball caughtBall) {
        soundService.playSound("catch");
        currentPlayer.addScore(caughtBall.getValue());

        String text = (caughtBall.getValue() > 0 ? "+" : "") + caughtBall.getValue();
        Color color = (caughtBall.getValue() > 0 ? new Color(173, 255, 47) : Color.RED);
        Point textPosition = new Point(playerPosition.x + 20, playerPosition.y);
        floatingTexts.add(new FloatingText(textPosition, text, color));

        if (caughtBall.getValue() == -50) {
            plutoHitCount++;
        }
    }

    private void updateFreeBalls() {
        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            if (ball.isActive()) {
                if (ball.isOutOfBounds(GAME_WIDTH)) {
                    iterator.remove();
                } else {
                    ball.update();
                }
            }
        }
    }

    private void movePlayer() {
        if (movingUp && playerPosition.y > PLAYER_AREA_TOP_Y)
            playerPosition.y -= playerSpeed;
        if (movingDown && playerPosition.y < PLAYER_AREA_BOTTOM_Y)
            playerPosition.y += playerSpeed;
        if (movingLeft && playerPosition.x > 0)
            playerPosition.x -= playerSpeed;
        if (movingRight && playerPosition.x < GAME_WIDTH - 80)
            playerPosition.x += playerSpeed;
    }

    private void spawnNewBall() {
        if (balls.size() < 30 && random.nextInt(100) < 12) {
            int value;
            int penaltyChance = 15 + (gameTimeSeconds / 20);
            penaltyChance = Math.min(penaltyChance, 40);
            int chance = random.nextInt(100);

            if (chance < 2) {
                value = 250;
            } else if (chance < penaltyChance) {
                value = -50;
            } else {
                value = random.nextInt(120) + 1;
            }

            int y, x, direction;
            if (random.nextBoolean()) {
                direction = -1;
                x = GAME_WIDTH;
                y = random.nextInt(PLAYER_AREA_TOP_Y - Ball.BALL_SIZE);
            } else {
                direction = 1;
                x = -Ball.BALL_SIZE;
                int spawnZoneHeight = GAME_HEIGHT - PLAYER_AREA_BOTTOM_Y;
                y = random.nextInt(spawnZoneHeight - Ball.BALL_SIZE) + PLAYER_AREA_BOTTOM_Y;
            }
            balls.add(new Ball(x, y, value, direction));
        }
    }

    private void updateGameTime() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTimeCheck >= 1000) {
            gameTimeSeconds++;
            lastTimeCheck = currentTime;
        }
    }

    private void updateFloatingTexts() {
        floatingTexts.removeIf(text -> !text.isAlive());
        for (FloatingText text : floatingTexts) {
            text.update();
        }
    }

    private void saveGameResult() {
        if (currentPlayer.getScore() > 0 || currentPlayer.getCount() > 0) {
            dbRepository.saveOrUpdatePlayer(currentPlayer);
        }
    }

    public Ball getHookedBall() { return hookedBall; }
    public List<Ball> getBalls() { return new ArrayList<>(balls); }
    public Point getPlayerPosition() { return new Point(playerPosition); }
    public List<FloatingText> getFloatingTexts() { return new ArrayList<>(floatingTexts); }
    public PlayerResult getCurrentPlayer() { return currentPlayer; }
    public int getGameTimeSeconds() { return gameTimeSeconds; }
    public int getPlutoHitCount() { return plutoHitCount; }
    public boolean isGameOver() { return isGameOver; }

    public void dispose() {
        if (soundService != null) {
            soundService.stopSound("music");
        }
    }
}