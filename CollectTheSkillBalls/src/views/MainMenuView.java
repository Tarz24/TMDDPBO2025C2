package views;

import models.PlayerResult;
import viewmodels.MainMenuViewModel;
import viewmodels.GameViewModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Observer;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("deprecation")
public class MainMenuView extends JFrame implements Observer {
    private final MainMenuViewModel viewModel;
    private JTextField usernameField;
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;
    private JButton playButton;
    private Timer animationTimer;
    private float glowIntensity = 0.0f;
    private boolean glowIncreasing = true;

    private static final Color SPACE_DARK = new Color(8, 12, 28);
    private static final Color SPACE_DARK_SECONDARY = new Color(15, 20, 35);
    private static final Color SPACE_BLUE = new Color(74, 144, 226);
    private static final Color SPACE_BLUE_BRIGHT = new Color(100, 180, 255);
    private static final Color SPACE_PURPLE = new Color(147, 51, 234);
    private static final Color SPACE_PURPLE_BRIGHT = new Color(168, 85, 247);
    private static final Color SPACE_CYAN = new Color(34, 211, 238);
    private static final Color SPACE_CYAN_BRIGHT = new Color(56, 240, 255);
    private static final Color STAR_WHITE = new Color(248, 250, 252);
    private static final Color SPACE_GREEN = new Color(16, 185, 129);
    private static final Color SPACE_GREEN_BRIGHT = new Color(34, 197, 94);
    private static final Color ACCENT_GOLD = new Color(251, 191, 36);
    private static final Color ACCENT_PINK = new Color(236, 72, 153);

    public MainMenuView() {
        this.viewModel = new MainMenuViewModel();
        this.viewModel.addObserver(this);

        setTitle("Collect The Skill Balls - Cosmic Explorer Edition");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(false);

        setContentPane(new EnhancedSpaceBackgroundPanel());
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (animationTimer != null) {
                    animationTimer.cancel();
                }
                viewModel.dispose();
                super.windowClosing(e);
            }
        });

        initComponents();
        startAnimations();
        updateUI();
    }

    private void startAnimations() {
        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (glowIncreasing) {
                        glowIntensity += 0.02f;
                        if (glowIntensity >= 1.0f) {
                            glowIntensity = 1.0f;
                            glowIncreasing = false;
                        }
                    } else {
                        glowIntensity -= 0.02f;
                        if (glowIntensity <= 0.3f) {
                            glowIntensity = 0.3f;
                            glowIncreasing = true;
                        }
                    }
                    repaint();
                });
            }
        }, 0, 50);
    }

    private void initComponents() {
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setOpaque(false);
        mainContainer.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel titlePanel = createTitlePanel();
        mainContainer.add(titlePanel, BorderLayout.NORTH);

        JPanel centerPanel = createCenterPanel();
        mainContainer.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);

        this.add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                float alpha = 0.1f + (glowIntensity * 0.2f);

                for (int i = 0; i < 5; i++) {
                    int radius = 100 + (i * 30);
                    Color glowColor = new Color(SPACE_CYAN.getRed(), SPACE_CYAN.getGreen(),
                            SPACE_CYAN.getBlue(), (int)(alpha * 255 / (i + 1)));
                    g2d.setColor(glowColor);
                    g2d.fillOval(centerX - radius, centerY - radius/2, radius * 2, radius);
                }
                g2d.dispose();
            }
        };

        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setPreferredSize(new Dimension(0, 150));

        JLabel titleLabel = new JLabel("COSMIC SKILL COLLECTOR", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Orbitron", Font.BOLD, 56));
        titleLabel.setForeground(SPACE_CYAN_BRIGHT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("• Explore • Collect • Dominate •", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Orbitron", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(ACCENT_GOLD.getRed(), ACCENT_GOLD.getGreen(),
                ACCENT_GOLD.getBlue(), 180));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(Box.createVerticalStrut(20));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(subtitleLabel);
        titlePanel.add(Box.createVerticalStrut(20));

        return titlePanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(20, 30));
        centerPanel.setOpaque(false);

        JPanel inputSection = createInputSection();
        centerPanel.add(inputSection, BorderLayout.NORTH);

        JPanel leaderboardSection = createLeaderboardSection();
        centerPanel.add(leaderboardSection, BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createInputSection() {
        JPanel inputSection = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(SPACE_DARK_SECONDARY.getRed(), SPACE_DARK_SECONDARY.getGreen(),
                        SPACE_DARK_SECONDARY.getBlue(), 120));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(new Color(SPACE_BLUE.getRed(), SPACE_BLUE.getGreen(),
                        SPACE_BLUE.getBlue(), 80));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);

                g2d.dispose();
            }
        };

        inputSection.setOpaque(false);
        inputSection.setLayout(new BorderLayout());
        inputSection.setPreferredSize(new Dimension(0, 120));
        inputSection.setBorder(new EmptyBorder(25, 40, 25, 40));

        JPanel inputContent = new JPanel();
        inputContent.setOpaque(false);
        inputContent.setLayout(new BoxLayout(inputContent, BoxLayout.X_AXIS));

        inputContent.add(Box.createHorizontalGlue());

        JLabel usernameLabel = new JLabel("COMMANDER ID:");
        usernameLabel.setForeground(SPACE_CYAN_BRIGHT);
        usernameLabel.setFont(new Font("Orbitron", Font.BOLD, 22));
        inputContent.add(usernameLabel);

        inputContent.add(Box.createHorizontalStrut(20));

        usernameField = new JTextField(25);
        usernameField.setMaximumSize(new Dimension(400, 50));
        styleEnhancedTextField(usernameField);

        usernameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateUsername(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateUsername(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateUsername(); }
        });

        inputContent.add(usernameField);
        inputContent.add(Box.createHorizontalGlue());

        inputSection.add(inputContent, BorderLayout.CENTER);
        return inputSection;
    }

    private JPanel createLeaderboardSection() {
        JPanel leaderboardSection = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(SPACE_DARK_SECONDARY.getRed(), SPACE_DARK_SECONDARY.getGreen(),
                        SPACE_DARK_SECONDARY.getBlue(), 150),
                        0, getHeight(), new Color(SPACE_DARK.getRed(), SPACE_DARK.getGreen(),
                        SPACE_DARK.getBlue(), 100)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                float borderAlpha = 0.4f + (glowIntensity * 0.3f);
                g2d.setColor(new Color(SPACE_PURPLE.getRed(), SPACE_PURPLE.getGreen(),
                        SPACE_PURPLE.getBlue(), (int)(borderAlpha * 255)));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 25, 25);

                g2d.dispose();
            }
        };

        leaderboardSection.setOpaque(false);
        leaderboardSection.setLayout(new BorderLayout(10, 15));
        leaderboardSection.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel sectionTitle = new JLabel("🏆 HALL OF FAME 🏆", SwingConstants.CENTER);
        sectionTitle.setFont(new Font("Orbitron", Font.BOLD, 28));
        sectionTitle.setForeground(ACCENT_GOLD);
        sectionTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        leaderboardSection.add(sectionTitle, BorderLayout.NORTH);

        createEnhancedLeaderboardTable();
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        styleEnhancedScrollPane(scrollPane);
        scrollPane.setPreferredSize(new Dimension(0, 300));

        leaderboardSection.add(scrollPane, BorderLayout.CENTER);
        return leaderboardSection;
    }

    private void createEnhancedLeaderboardTable() {
        String[] columnNames = {"🥇", "COMMANDER", "SCORE", "ITEMS", "STATUS"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        leaderboardTable = new JTable(tableModel);
        styleEnhancedTable(leaderboardTable);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 0, 0, 0),
                        0, getHeight(), new Color(SPACE_DARK.getRed(), SPACE_DARK.getGreen(),
                        SPACE_DARK.getBlue(), 80)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };

        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setPreferredSize(new Dimension(0, 120));

        playButton = createEnhancedButton("🚀 LAUNCH MISSION", SPACE_BLUE, SPACE_BLUE_BRIGHT);
        playButton.addActionListener(e -> startGame());

        JButton aboutButton = createEnhancedButton("ℹ️ MISSION BRIEFING", SPACE_GREEN, SPACE_GREEN_BRIGHT);
        aboutButton.addActionListener(e -> showAboutDialog());

        JButton quitButton = createEnhancedButton("🔚 EXIT STATION", SPACE_PURPLE, SPACE_PURPLE_BRIGHT);
        quitButton.addActionListener(e -> {
            if (animationTimer != null) {
                animationTimer.cancel();
            }
            viewModel.dispose();
            System.exit(0);
        });

        buttonPanel.add(playButton);
        buttonPanel.add(aboutButton);
        buttonPanel.add(quitButton);

        return buttonPanel;
    }

    private JButton createEnhancedButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private float hoverIntensity = 0.0f;
            private Timer hoverTimer;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentColor = interpolateColor(baseColor, hoverColor, hoverIntensity);
                GradientPaint gradient = new GradientPaint(
                        0, 0, currentColor,
                        0, getHeight(), new Color(currentColor.getRed(), currentColor.getGreen(),
                        currentColor.getBlue(), 180)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                if (hoverIntensity > 0) {
                    for (int i = 1; i <= 5; i++) {
                        int alpha = (int)(30 * hoverIntensity / i);
                        g2d.setColor(new Color(hoverColor.getRed(), hoverColor.getGreen(),
                                hoverColor.getBlue(), alpha));
                        g2d.setStroke(new BasicStroke(i * 2));
                        g2d.drawRoundRect(-i, -i, getWidth() + 2*i, getHeight() + 2*i, 15 + i, 15 + i);
                    }
                }

                g2d.dispose();
                super.paintComponent(g);
            }

            @Override
            public void addNotify() {
                super.addNotify();
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (!isEnabled()) return;
                        isHovered = true;
                        if (hoverTimer != null) hoverTimer.cancel();
                        hoverTimer = new Timer();
                        hoverTimer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                SwingUtilities.invokeLater(() -> {
                                    if (isHovered && hoverIntensity < 1.0f) {
                                        hoverIntensity = Math.min(1.0f, hoverIntensity + 0.1f);
                                        repaint();
                                    }
                                });
                            }
                        }, 0, 20);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        if (hoverTimer != null) hoverTimer.cancel();
                        hoverTimer = new Timer();
                        hoverTimer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                SwingUtilities.invokeLater(() -> {
                                    if (!isHovered && hoverIntensity > 0.0f) {
                                        hoverIntensity = Math.max(0.0f, hoverIntensity - 0.1f);
                                        repaint();
                                        if (hoverIntensity <= 0.0f && hoverTimer != null) {
                                            hoverTimer.cancel();
                                        }
                                    }
                                });
                            }
                        }, 0, 20);
                    }
                });
            }
        };

        button.setFont(new Font("Orbitron", Font.BOLD, 16));
        button.setForeground(STAR_WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(220, 65));

        return button;
    }

    private Color interpolateColor(Color c1, Color c2, float ratio) {
        ratio = Math.max(0, Math.min(1, ratio));
        int r = (int)(c1.getRed() + ratio * (c2.getRed() - c1.getRed()));
        int g = (int)(c1.getGreen() + ratio * (c2.getGreen() - c1.getGreen()));
        int b = (int)(c1.getBlue() + ratio * (c2.getBlue() - c1.getBlue()));
        return new Color(r, g, b);
    }

    private void styleEnhancedTextField(JTextField textField) {
        textField.setFont(new Font("Orbitron", Font.BOLD, 18));
        textField.setOpaque(false);
        textField.setForeground(SPACE_CYAN_BRIGHT);
        textField.setCaretColor(SPACE_CYAN_BRIGHT);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setBorder(new EmptyBorder(12, 20, 12, 20));

        textField.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintBackground(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(SPACE_DARK_SECONDARY.getRed(), SPACE_DARK_SECONDARY.getGreen(),
                        SPACE_DARK_SECONDARY.getBlue(), 180));
                g2d.fillRoundRect(0, 0, textField.getWidth(), textField.getHeight(), 12, 12);

                g2d.setColor(new Color(SPACE_CYAN.getRed(), SPACE_CYAN.getGreen(),
                        SPACE_CYAN.getBlue(), textField.hasFocus() ? 255 : 120));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, textField.getWidth()-2, textField.getHeight()-2, 12, 12);

                g2d.dispose();
            }
        });
    }

    private void styleEnhancedTable(JTable table) {
        table.setOpaque(false);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 8));
        table.setRowHeight(45);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    setBackground(new Color(SPACE_BLUE.getRed(), SPACE_BLUE.getGreen(),
                            SPACE_BLUE.getBlue(), 180));
                    setForeground(STAR_WHITE);
                } else {
                    if (row % 2 == 0) {
                        setBackground(new Color(SPACE_DARK_SECONDARY.getRed(), SPACE_DARK_SECONDARY.getGreen(),
                                SPACE_DARK_SECONDARY.getBlue(), 100));
                    } else {
                        setBackground(new Color(SPACE_DARK.getRed(), SPACE_DARK.getGreen(),
                                SPACE_DARK.getBlue(), 120));
                    }

                    if (column == 0) {
                        if (row == 0) {
                            setForeground(ACCENT_GOLD);
                            setText("🥇 " + (row + 1));
                        } else if (row == 1) {
                            setForeground(new Color(192, 192, 192));
                            setText("🥈 " + (row + 1));
                        } else if (row == 2) {
                            setForeground(new Color(205, 127, 50));
                            setText("🥉 " + (row + 1));
                        } else {
                            setForeground(SPACE_CYAN);
                            setText("⭐ " + (row + 1));
                        }
                    } else if (column == 4) {
                        setForeground(SPACE_GREEN_BRIGHT);
                        setText(row < 3 ? "ELITE" : "ACTIVE");
                    } else {
                        setForeground(STAR_WHITE);
                    }
                }

                setOpaque(true);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Orbitron", row < 3 ? Font.BOLD : Font.PLAIN, 13));
                setBorder(new EmptyBorder(8, 12, 8, 12));
                return c;
            }
        };

        table.setDefaultRenderer(Object.class, cellRenderer);

        JTableHeader header = table.getTableHeader();
        header.setOpaque(false);
        header.setBackground(new Color(SPACE_PURPLE.getRed(), SPACE_PURPLE.getGreen(),
                SPACE_PURPLE.getBlue(), 200));
        header.setForeground(STAR_WHITE);
        header.setFont(new Font("Orbitron", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createEmptyBorder());

        table.setSelectionBackground(new Color(SPACE_BLUE.getRed(), SPACE_BLUE.getGreen(),
                SPACE_BLUE.getBlue(), 150));
        table.setSelectionForeground(STAR_WHITE);
    }

    private void styleEnhancedScrollPane(JScrollPane scrollPane) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(0, 0, 0, 0));

        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        scrollPane.getHorizontalScrollBar().setOpaque(false);
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 12));
    }

    private void updateUsername() {
        viewModel.setUsername(usernameField.getText());
    }

    private void startGame() {
        PlayerResult player = viewModel.createNewPlayer();
        if (player == null) {
            showEnhancedErrorDialog("Mission Control Alert",
                    "Please enter your commander ID before launching!");
            return;
        }

        this.setVisible(false);

        JFrame gameFrame = new JFrame("Cosmic Mission - Skill Ball Collection");
        GameViewModel gameViewModel = new GameViewModel(player);

        GameView gameView = new GameView(gameViewModel, () -> {
            gameFrame.dispose();
            viewModel.refreshLeaderboard();
            this.setVisible(true);
        });

        gameFrame.add(gameView);
        gameFrame.setSize(1280, 720);
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
        gameView.requestFocusInWindow();
    }

    private void showEnhancedErrorDialog(String title, String message) {
        JDialog errorDialog = new JDialog(this, title, true);
        errorDialog.setSize(400, 200);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setUndecorated(true);

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(SPACE_DARK);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(ACCENT_PINK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);

                g2d.dispose();
            }
        };
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>");
        messageLabel.setFont(new Font("Orbitron", Font.PLAIN, 14));
        messageLabel.setForeground(STAR_WHITE);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton okButton = createEnhancedButton("UNDERSTOOD", ACCENT_PINK, SPACE_PURPLE_BRIGHT);
        okButton.setPreferredSize(new Dimension(150, 40));
        okButton.addActionListener(e -> errorDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(okButton);

        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        errorDialog.add(panel);
        errorDialog.setVisible(true);
    }

    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(this, "Mission Briefing", true);
        aboutDialog.setSize(600, 700);
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setUndecorated(true);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, SPACE_DARK,
                        getWidth(), getHeight(), SPACE_DARK_SECONDARY
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                float borderAlpha = 0.6f + (glowIntensity * 0.4f);
                g2d.setColor(new Color(SPACE_CYAN.getRed(), SPACE_CYAN.getGreen(),
                        SPACE_CYAN.getBlue(), (int)(borderAlpha * 255)));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 20, 20);

                g2d.setColor(new Color(SPACE_BLUE.getRed(), SPACE_BLUE.getGreen(),
                        SPACE_BLUE.getBlue(), 30));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(5, 5, getWidth()-10, getHeight()-10, 15, 15);

                g2d.dispose();
            }
        };
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel titleLabel = new JLabel("🚀 MISSION BRIEFING 🚀", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Orbitron", Font.BOLD, 28));
        titleLabel.setForeground(SPACE_CYAN_BRIGHT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        String aboutText = "<html>"
                + "<body style='width: 520px; font-family: Arial, sans-serif; font-size: 12pt; color: #F8FAFC; background-color: transparent;'>"
                + "<div style='text-align: center; margin-bottom: 20px;'>"
                + "<h2 style='color: #22D3EE; margin: 15px 0; font-size: 18pt;'>COSMIC EXPLORATION PROTOCOL</h2>"
                + "</div>"
                + "<div style='background: linear-gradient(135deg, rgba(74,144,226,0.1), rgba(147,51,234,0.1)); "
                + "padding: 15px; border-radius: 10px; margin-bottom: 20px; border: 1px solid rgba(74,144,226,0.3);'>"
                + "<p style='text-align: justify; line-height: 1.6; margin: 0;'>"
                + "Game ini dibuat sebagai bagian dari <b style='color: #60A5FA;'>Tugas Masa Depan Individu</b> "
                + "mata kuliah <b style='color: #60A5FA;'>Desain dan Pemrograman Berorientasi Objek</b>. "
                + "Misi Anda adalah mengumpulkan skill balls sebanyak mungkin sambil menghindari rintangan kosmik."
                + "</p>"
                + "</div>"
                + "<h3 style='color: #FB BF24; margin: 20px 0 12px 0; font-size: 16pt;'>🎨 Visual Asset Credits:</h3>"
                + "<div style='background: rgba(251,191,36,0.1); padding: 12px; border-radius: 8px; border-left: 4px solid #FBBF24;'>"
                + "<ul style='margin: 5px 0; padding-left: 20px; line-height: 1.4;'>"
                + "<li style='margin-bottom: 6px;'><b>Background:</b> <span style='color: #22D3EE;'>Pinterest Space Collection</span></li>"
                + "<li style='margin-bottom: 6px;'><b>Star:</b> <span style='color: #22D3EE;'>Freepik - Flaticon</span></li>"
                + "<li style='margin-bottom: 6px;'><b>Asteroid:</b> <span style='color: #22D3EE;'>VectorPortal - Flaticon</span></li>"
                + "<li style='margin-bottom: 6px;'><b>Astronaut:</b> <span style='color: #22D3EE;'>iconfield - Flaticon</span></li>"
                + "<li style='margin-bottom: 6px;'><b>Meteorite:</b> <span style='color: #22D3EE;'>Vitaly Gorbachev - Flaticon</span></li>"
                + "<li style='margin-bottom: 6px;'><b>Net:</b> <span style='color: #22D3EE;'>Freepik - Flaticon</span></li>"
                + "<li style='margin-bottom: 6px;'><b>Pluto:</b> <span style='color: #22D3EE;'>Peerapak Takpho - Flaticon</span></li>"
                + "<li style='margin-bottom: 6px;'><b>Satellite:</b> <span style='color: #22D3EE;'>Muhammad Atif - Flaticon</span></li>"
                + "<li style='margin-bottom: 6px;'><b>Space-Shuttle:</b> <span style='color: #22D3EE;'>Freepik - Flaticon</span></li>"
                + "<li style='margin-bottom: 6px;'><b>UFO:</b> <span style='color: #22D3EE;'>Freepik - Flaticon</span></li>"
                + "</ul>"
                + "</div>"
                + "<h3 style='color: #10B981; margin: 20px 0 12px 0; font-size: 16pt;'>🎵 Audio Credits:</h3>"
                + "<div style='background: rgba(16,185,129,0.1); padding: 12px; border-radius: 8px; border-left: 4px solid #10B981;'>"
                + "<ul style='margin: 5px 0; padding-left: 20px; line-height: 1.4;'>"
                + "<li style='margin-bottom: 6px;'><b>Catch Sound Effect:</b> <span style='color: #FBBF24;'>Pixabay - Collect Points</span></li>"
                + "<li style='margin-bottom: 6px;'><b>Background Music:</b> <span style='color: #FBBF24;'>Retrowave by Walen (freetouse.com)</span></li>"
                + "</ul>"
                + "</div>"
                + "<h3 style='color: #EC4899; margin: 20px 0 12px 0; font-size: 16pt;'>🎯 Mission Objectives:</h3>"
                + "<div style='background: rgba(236,72,153,0.1); padding: 15px; border-radius: 8px; border-left: 4px solid #EC4899;'>"
                + "<p style='text-align: justify; line-height: 1.6; margin: 0;'>"
                + "• Navigate through the cosmic environment with precision<br>"
                + "• Collect skill balls to increase your score and ranking<br>"
                + "• Avoid space obstacles and hazards<br>"
                + "• Prove your piloting skills in the Hall of Fame<br>"
                + "• Master the art of cosmic exploration!"
                + "</p>"
                + "</div>"
                + "<div style='text-align: center; margin-top: 25px; padding: 15px; "
                + "background: linear-gradient(135deg, rgba(74,144,226,0.2), rgba(147,51,234,0.2)); "
                + "border-radius: 12px; border: 2px solid rgba(34,211,238,0.5);'>"
                + "<b style='color: #22D3EE; font-size: 16pt;'>Good luck, Commander! 🌟</b><br>"
                + "<span style='color: #A78BFA; font-size: 12pt;'>May the stars guide your journey!</span>"
                + "</div>"
                + "</body></html>";

        JEditorPane editorPane = new JEditorPane("text/html", aboutText);
        editorPane.setEditable(false);
        editorPane.setOpaque(false);
        editorPane.setBackground(new Color(0, 0, 0, 0));

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(550, 450));

        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        JButton closeButton = createEnhancedButton("🔙 CLOSE TRANSMISSION", SPACE_PURPLE, SPACE_PURPLE_BRIGHT);
        closeButton.setPreferredSize(new Dimension(250, 50));
        closeButton.addActionListener(e -> aboutDialog.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        aboutDialog.add(mainPanel);
        aboutDialog.setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg != null) {
            String event = arg.toString();
            switch (event) {
                case "USERNAME_CHANGED":
                    updatePlayButtonState();
                    break;
                case "LEADERBOARD_UPDATED":
                    updateLeaderboard();
                    break;
            }
        } else {
            updateUI();
        }
    }

    private void updateUI() {
        updatePlayButtonState();
        updateLeaderboard();
    }

    private void updatePlayButtonState() {
        boolean canStart = viewModel.canStartGame();
        playButton.setEnabled(canStart);

        if (canStart) {
            playButton.setForeground(STAR_WHITE);
        } else {
            playButton.setForeground(new Color(STAR_WHITE.getRed(), STAR_WHITE.getGreen(),
                    STAR_WHITE.getBlue(), 120));
        }
        playButton.repaint();
    }

    private void updateLeaderboard() {
        tableModel.setRowCount(0);
        List<PlayerResult> players = viewModel.getLeaderboard();
        int rank = 1;
        for (PlayerResult player : players) {
            tableModel.addRow(new Object[]{
                    rank++,
                    player.getUsername(),
                    String.format("%,d", player.getScore()),
                    player.getCount(),
                    rank <= 4 ? "ELITE" : "ACTIVE"
            });
        }
    }

    class EnhancedSpaceBackgroundPanel extends JPanel {
        private Image backgroundImage;
        private java.util.List<Particle> particles;
        private Timer particleTimer;

        public EnhancedSpaceBackgroundPanel() {
            try {
                backgroundImage = ImageIO.read(new File("resources/images/background2.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            particles = new java.util.ArrayList<>();
            for (int i = 0; i < 50; i++) {
                particles.add(new Particle());
            }

            particleTimer = new Timer();
            particleTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        for (Particle particle : particles) {
                            particle.update();
                        }
                        repaint();
                    });
                }
            }, 0, 100);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                GradientPaint gradient1 = new GradientPaint(
                        0, 0, new Color(0, 0, 0, 0),
                        0, getHeight() / 2, new Color(SPACE_DARK.getRed(), SPACE_DARK.getGreen(),
                        SPACE_DARK.getBlue(), 40)
                );
                g2d.setPaint(gradient1);
                g2d.fillRect(0, 0, getWidth(), getHeight() / 2);

                GradientPaint gradient2 = new GradientPaint(
                        0, getHeight() / 2, new Color(0, 0, 0, 0),
                        0, getHeight(), new Color(SPACE_DARK.getRed(), SPACE_DARK.getGreen(),
                        SPACE_DARK.getBlue(), 60)
                );
                g2d.setPaint(gradient2);
                g2d.fillRect(0, getHeight() / 2, getWidth(), getHeight() / 2);
            } else {
                GradientPaint gradient = new GradientPaint(
                        0, 0, SPACE_DARK,
                        getWidth(), getHeight(), SPACE_DARK_SECONDARY
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            for (Particle particle : particles) {
                particle.draw(g2d);
            }

            drawAmbientEffects(g2d);

            g2d.dispose();
        }

        private void drawAmbientEffects(Graphics2D g2d) {
            float alpha = 0.05f + (glowIntensity * 0.03f);

            RadialGradientPaint radial1 = new RadialGradientPaint(
                    0, 0, 300,
                    new float[]{0f, 1f},
                    new Color[]{
                            new Color(SPACE_CYAN.getRed(), SPACE_CYAN.getGreen(), SPACE_CYAN.getBlue(), (int)(alpha * 255)),
                            new Color(SPACE_CYAN.getRed(), SPACE_CYAN.getGreen(), SPACE_CYAN.getBlue(), 0)
                    }
            );
            g2d.setPaint(radial1);
            g2d.fillRect(0, 0, 300, 300);

            RadialGradientPaint radial2 = new RadialGradientPaint(
                    getWidth(), getHeight(), 300,
                    new float[]{0f, 1f},
                    new Color[]{
                            new Color(SPACE_PURPLE.getRed(), SPACE_PURPLE.getGreen(), SPACE_PURPLE.getBlue(), (int)(alpha * 255)),
                            new Color(SPACE_PURPLE.getRed(), SPACE_PURPLE.getGreen(), SPACE_PURPLE.getBlue(), 0)
                    }
            );
            g2d.setPaint(radial2);
            g2d.fillRect(getWidth() - 300, getHeight() - 300, 300, 300);
        }

        class Particle {
            private float x, y;
            private float vx, vy;
            private float alpha;
            private Color color;
            private float size;

            public Particle() {
                reset();
                x = (float)(Math.random() * getWidth());
                y = (float)(Math.random() * getHeight());
            }

            private void reset() {
                x = -10;
                y = (float)(Math.random() * getHeight());
                vx = 0.5f + (float)(Math.random() * 1.5f);
                vy = -0.5f + (float)(Math.random() * 1.0f);
                alpha = 0.3f + (float)(Math.random() * 0.7f);
                size = 1f + (float)(Math.random() * 2f);

                Color[] colors = {SPACE_CYAN, SPACE_BLUE, STAR_WHITE, ACCENT_GOLD};
                color = colors[(int)(Math.random() * colors.length)];
            }

            public void update() {
                x += vx;
                y += vy;
                alpha -= 0.005f;

                if (x > getWidth() + 10 || alpha <= 0) {
                    reset();
                }
            }

            public void draw(Graphics2D g2d) {
                if (alpha > 0) {
                    g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
                            (int)(alpha * 255)));
                    g2d.fillOval((int)(x - size/2), (int)(y - size/2), (int)size, (int)size);
                }
            }
        }
    }
}