package views;

import models.Ball;
import models.FloatingText;
import models.PlayerResult;
import viewmodels.GameViewModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class GameView extends JPanel implements Observer {
    private final GameViewModel viewModel;
    private Timer gameLoop;
    private final Runnable onGameEnd;
    private Image backgroundImage, playerImage, rocketImage, netImage;
    public static final int PLAYER_WIDTH = 80, PLAYER_HEIGHT = 80;

    private long lastTime = System.currentTimeMillis();
    private double animationTime = 0;
    private final Color[] starColors = {Color.WHITE, Color.CYAN, Color.YELLOW, Color.PINK};
    private final Point[] stars = new Point[100];
    private final double[] starSizes = new double[100];
    private final Color[] starColorArray = new Color[100];

    private java.util.List<Particle> particles = new java.util.ArrayList<>();

    private final Color DARK_OVERLAY = new Color(0, 0, 0, 150);
    private final Color ACCENT_COLOR = new Color(64, 224, 255);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color WARNING_COLOR = new Color(255, 193, 7);
    private final Color DANGER_COLOR = new Color(231, 76, 60);

    public GameView(GameViewModel viewModel, Runnable onGameEndCallback) {
        this.viewModel = viewModel;
        this.onGameEnd = onGameEndCallback;
        this.viewModel.addObserver(this);

        this.setPreferredSize(new Dimension(GameViewModel.GAME_WIDTH, GameViewModel.GAME_HEIGHT));
        setFocusable(true);
        addKeyListener(new GameKeyListener());
        addMouseListener(new FishingMouseListener());

        initializeStars();

        gameLoop = new Timer(16, e -> {
            updateAnimations();
            viewModel.updateGame();
            repaint();
        });

        loadImages();
        gameLoop.start();
    }

    private void initializeStars() {
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Point(
                    (int) (Math.random() * GameViewModel.GAME_WIDTH),
                    (int) (Math.random() * GameViewModel.GAME_HEIGHT)
            );
            starSizes[i] = Math.random() * 3 + 1;
            starColorArray[i] = starColors[(int) (Math.random() * starColors.length)];
        }
    }

    private void updateAnimations() {
        long currentTime = System.currentTimeMillis();
        animationTime += (currentTime - lastTime) / 1000.0;
        lastTime = currentTime;
        particles.removeIf(particle -> particle.update());
    }

    private void addParticleEffect(Point location, Color color) {
        for (int i = 0; i < 5; i++) {
            particles.add(new Particle(location.x, location.y, color));
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String message = (String) arg;
            if ("GAME_OVER".equals(message)) {
                gameLoop.stop();
                showEnhancedGameOverDialog();
                onGameEnd.run();
            } else if ("GAME_ENDED".equals(message)) {
                gameLoop.stop();
                onGameEnd.run();
            }
        }
        repaint();
    }

    private void showEnhancedGameOverDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Game Over");
        dialog.setModal(true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(74, 144, 226),
                        0, getHeight(), new Color(80, 227, 194));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BorderLayout());

        JLabel message = new JLabel("<html><center><h2>GAME OVER!</h2><p>Anda mengenai pluto terlalu banyak.</p></center></html>");
        message.setForeground(Color.WHITE);
        message.setHorizontalAlignment(JLabel.CENTER);
        panel.add(message, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dialog.dispose());
        panel.add(okButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void loadImages() {
        try {
            backgroundImage = ImageIO.read(new File("resources/images/background2.jpg"));
            playerImage = ImageIO.read(new File("resources/images/astronaut.png"));
            rocketImage = ImageIO.read(new File("resources/images/space-shuttle.png"));
            netImage = ImageIO.read(new File("resources/images/net.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        drawEnhancedBackground(g2d);
        drawAnimatedStars(g2d);
        drawEnhancedRocket(g2d);

        for (Ball ball : viewModel.getBalls()) {
            drawEnhancedBall(g2d, ball);
        }

        drawEnhancedPlayer(g2d);
        drawEnhancedHookAndNet(g2d);
        drawParticles(g2d);
        drawEnhancedFloatingTexts(g2d);
        drawModernScoreboard(g2d);
    }

    private void drawEnhancedBackground(Graphics2D g2d) {
        if (backgroundImage != null) {
            Composite oldComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.setComposite(oldComposite);
        } else {
            GradientPaint gradient = new GradientPaint(0, 0, new Color(25, 25, 112),
                    0, getHeight(), new Color(0, 0, 0));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void drawAnimatedStars(Graphics2D g2d) {
        for (int i = 0; i < stars.length; i++) {
            Point star = stars[i];
            double twinkle = Math.sin(animationTime * 2 + i) * 0.5 + 0.5;

            Composite oldComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)twinkle * 0.8f));
            g2d.setColor(starColorArray[i]);

            int size = (int)(starSizes[i] * (0.5 + twinkle * 0.5));
            g2d.fillOval(star.x - size/2, star.y - size/2, size, size);
            g2d.setComposite(oldComposite);
        }
    }

    private void drawEnhancedRocket(Graphics2D g2d) {
        if (rocketImage != null) {
            g2d.drawImage(rocketImage,
                    GameViewModel.ROCKET_AREA.x, GameViewModel.ROCKET_AREA.y,
                    GameViewModel.ROCKET_AREA.width, GameViewModel.ROCKET_AREA.height, this);
        }
    }

    private void drawEnhancedPlayer(Graphics2D g2d) {
        Point playerPos = viewModel.getPlayerPosition();
        if (playerImage != null) {
            Composite oldComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.setColor(Color.BLACK);
            g2d.fillOval(playerPos.x + 5, playerPos.y + PLAYER_HEIGHT - 10, PLAYER_WIDTH - 10, 20);
            g2d.setComposite(oldComposite);

            double bounce = Math.sin(animationTime * 4) * 2;
            g2d.drawImage(playerImage, playerPos.x, playerPos.y + (int)bounce, PLAYER_WIDTH, PLAYER_HEIGHT, this);
        }
    }

    private void drawEnhancedHookAndNet(Graphics2D g2d) {
        Ball hookedBall = viewModel.getHookedBall();
        if (hookedBall != null) {
            Point playerPos = viewModel.getPlayerPosition();
            Point ballCenter = hookedBall.getCenter();

            GradientPaint lineGradient = new GradientPaint(
                    playerPos.x + PLAYER_WIDTH / 2, playerPos.y + PLAYER_HEIGHT / 2, ACCENT_COLOR,
                    ballCenter.x, ballCenter.y, Color.WHITE
            );
            g2d.setPaint(lineGradient);
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(playerPos.x + PLAYER_WIDTH / 2, playerPos.y + PLAYER_HEIGHT / 2,
                    ballCenter.x, ballCenter.y);

            int netSize = 40;
            if (netImage != null) {
                AffineTransform oldTransform = g2d.getTransform();
                g2d.translate(ballCenter.x, ballCenter.y);
                g2d.rotate(animationTime * 2);
                g2d.drawImage(netImage, -netSize/2, -netSize/2, netSize, netSize, this);
                g2d.setTransform(oldTransform);
            }

            if (Math.random() < 0.3) {
                addParticleEffect(ballCenter, ACCENT_COLOR);
            }
        }
    }

    private void drawEnhancedBall(Graphics2D g2d, Ball ball) {
        Image ballImage = ball.getImage();
        if (ballImage != null) {
            g2d.drawImage(ballImage, ball.getX(), ball.getY(), Ball.BALL_SIZE, Ball.BALL_SIZE, this);
        }
    }

    private void drawParticles(Graphics2D g2d) {
        for (Particle particle : particles) {
            particle.draw(g2d);
        }
    }

    private void drawEnhancedFloatingTexts(Graphics2D g2d) {
        for (FloatingText text : viewModel.getFloatingTexts()) {
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.setFont(new Font("Arial", Font.BOLD, 22));
            g2d.drawString(text.getText(), text.getPosition().x + 2, text.getPosition().y + 2);

            g2d.setColor(text.getColor());
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString(text.getText(), text.getPosition().x, text.getPosition().y);
        }
    }

    private void drawModernScoreboard(Graphics2D g2d) {
        int boardX = 15;
        int boardY = 20;
        int boardWidth = 280;
        int boardHeight = 120;

        RoundRectangle2D board = new RoundRectangle2D.Double(boardX, boardY, boardWidth, boardHeight, 20, 20);

        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.fill(board);

        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(board);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));

        PlayerResult player = viewModel.getCurrentPlayer();
        int totalSeconds = viewModel.getGameTimeSeconds();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);

        int hits = viewModel.getPlutoHitCount();
        int maxHits = GameViewModel.MAX_PLUTO_HITS;

        Color hitColor;
        if (hits >= maxHits * 0.75) {
            hitColor = DANGER_COLOR;
        } else if (hits >= maxHits * 0.5) {
            hitColor = WARNING_COLOR;
        } else {
            hitColor = SUCCESS_COLOR;
        }

        int yOffset = boardY + 28;
        int lineHeight = 23;

        g2d.setColor(Color.WHITE);
        g2d.drawString("⚡ PLUTO HITS", boardX + 20, yOffset);
        g2d.setColor(hitColor);
        g2d.drawString(hits + "/" + maxHits, boardX + 180, yOffset);

        int barWidth = 120;
        int barHeight = 6;
        int barX = boardX + 150;
        int barY = yOffset + 5;

        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.fillRoundRect(barX, barY, barWidth, barHeight, 3, 3);

        g2d.setColor(hitColor);
        int progress = (int)((double)hits / maxHits * barWidth);
        g2d.fillRoundRect(barX, barY, progress, barHeight, 3, 3);

        yOffset += lineHeight;
        g2d.setColor(Color.WHITE);
        g2d.drawString("⏱️ TIME", boardX + 20, yOffset);
        g2d.drawString(timeString, boardX + 180, yOffset);

        yOffset += lineHeight;
        g2d.drawString("🎯 SCORE", boardX + 20, yOffset);
        g2d.drawString(String.format("%,d", player.getScore()), boardX + 180, yOffset);

        yOffset += lineHeight;
        g2d.drawString("🌟 BALLS", boardX + 20, yOffset);
        g2d.drawString(String.valueOf(player.getCount()), boardX + 180, yOffset);
    }

    private static class Particle {
        private double x, y, vx, vy;
        private Color color;
        private double life, maxLife;
        private double size;

        public Particle(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.vx = (Math.random() - 0.5) * 4;
            this.vy = (Math.random() - 0.5) * 4;
            this.color = color;
            this.maxLife = this.life = 1.0;
            this.size = Math.random() * 3 + 1;
        }

        public boolean update() {
            x += vx;
            y += vy;
            vy += 0.1;
            life -= 0.02;
            return life <= 0;
        }

        public void draw(Graphics2D g2d) {
            float alpha = (float)(life / maxLife);
            Composite oldComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.setColor(color);
            g2d.fillOval((int)(x - size/2), (int)(y - size/2), (int)size, (int)size);
            g2d.setComposite(oldComposite);
        }
    }

    private class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            updateMovement(keyCode, true);

            if (keyCode == KeyEvent.VK_SPACE) {
                viewModel.endGame();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            updateMovement(keyCode, false);
        }

        private void updateMovement(int keyCode, boolean pressed) {
            boolean up = false, down = false, left = false, right = false;

            switch (keyCode) {
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    up = pressed;
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_DOWN:
                    down = pressed;
                    break;
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    left = pressed;
                    break;
                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    right = pressed;
                    break;
            }
            viewModel.setPlayerMovement(up, down, left, right);
        }
    }

    private class FishingMouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            requestFocusInWindow();
            viewModel.handleMouseClick(e.getPoint());
            addParticleEffect(e.getPoint(), ACCENT_COLOR);
        }
    }

    public void dispose() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        viewModel.deleteObserver(this);
        viewModel.dispose();
    }
}