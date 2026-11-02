import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/** Renders the game scene and manages menu and game-over controls. */
public class View extends JPanel {
    int width = 360;
    int height = 640;

    private final int cardW = 320;
    private final int cardX = (width - cardW) / 2;
    private final int menuH = 190;
    private final int overH = 290;

    private final int btnW = 130;
    private final int btnH = 40;
    private final int btnGap = 20;

    private final JLabel scoreLabel = new JLabel("0", SwingConstants.CENTER);

    private Logic logic;

    // Load the bundled pixel font once; use a system font if the asset is unavailable.
    private static Font pixelBase;
    static {
        try (java.io.InputStream is = View.class.getResourceAsStream("assets/PressStart2P-Regular.ttf")) {
            pixelBase = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            pixelBase = null;
        }
    }

    private static Font pixel(int size) {
        return pixelBase != null ? pixelBase.deriveFont((float) size)
                : new Font("Monospaced", Font.BOLD, size);
    }

    private final RoundButton playButton = new RoundButton("Play", new Color(76, 175, 80));
    private final RoundButton menuExitButton = new RoundButton("Exit", new Color(229, 57, 53));
    private final RoundButton restartButton = new RoundButton("Restart", new Color(76, 175, 80));
    private final RoundButton overExitButton = new RoundButton("Exit", new Color(229, 57, 53));

    /** Creates the game panel and connects its controls to the game logic. */
    public View(Logic logic) {
        this.logic = logic;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.cyan);
        setLayout(null);

        scoreLabel.setFont(pixel(32));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(0, 40, width, 50);
        add(scoreLabel);

        setFocusable(true);
        addKeyListener(logic);

        playButton.addActionListener(e -> logic.startGame());
        menuExitButton.addActionListener(e -> logic.exitGame());
        restartButton.addActionListener(e -> logic.restart());
        overExitButton.addActionListener(e -> logic.exitGame());

        add(playButton);
        add(menuExitButton);
        add(restartButton);
        add(overExitButton);

        updateState();
    }

    /** Updates control visibility and layout to match the current game state. */
    public void updateState() {
        boolean menu = !logic.isStarted();
        boolean over = logic.isGameOver();

        int menuY = (height - menuH) / 2;
        int overY = (height - overH) / 2;
        int x1 = cardX + (cardW - (btnW * 2 + btnGap)) / 2;
        int x2 = x1 + btnW + btnGap;

        playButton.setBounds(x1, menuY + 100, btnW, btnH);
        menuExitButton.setBounds(x2, menuY + 100, btnW, btnH);
        restartButton.setBounds(x1, overY + 180, btnW, btnH);
        overExitButton.setBounds(x2, overY + 180, btnW, btnH);

        playButton.setVisible(menu);
        menuExitButton.setVisible(menu);
        restartButton.setVisible(over);
        overExitButton.setVisible(over);

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    /** Draws the background, game entities, score, and state-specific overlay. */
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        g.drawImage(logic.getBackgroundImage(), 0, 0, width, height, null);

        Player player = logic.getPlayer();
        if (player != null) {
            double angle = Math.clamp(player.getVelocityY() * 0.07, -0.45, 1.2);

            java.awt.geom.AffineTransform old = g2.getTransform();
            g2.rotate(angle,
                    player.getPosX() + player.getWidth() / 2.0,
                    player.getPosY() + player.getHeight() / 2.0);
            g.drawImage(player.getImage(), player.getPosX(), player.getPosY(),
                    player.getWidth(), player.getHeight(), null);
            g2.setTransform(old);
        }

        ArrayList<Pipe> pipes = logic.getPipes();
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(),
                    pipe.getWidth(), pipe.getHeight(), null);
        }

        if (logic.isStarted() && !logic.isGameOver()) {
            g2.setFont(pixel(32));
            scoreLabel.setText(String.valueOf(logic.getScore()));
            scoreLabel.setVisible(logic.isStarted() && !logic.isGameOver());
        }

        if (!logic.isStarted()) {
            drawMenuCard(g2);
        } else if (logic.isGameOver()) {
            drawGameOverCard(g2);
        }
    }

    private void drawMenuCard(Graphics2D g2) {
        dimBackground(g2);
        int y = (height - menuH) / 2;
        drawCard(g2, cardX, y, cardW, menuH);

        // Center the title and subtitle together as one visual group.
        Font titleFont = pixel(20);
        Font smallFont = pixel(8);
        FontMetrics tm = g2.getFontMetrics(titleFont);
        FontMetrics sm = g2.getFontMetrics(smallFont);
        String title = "Flappy Bird";
        String sub = "Imitasi";
        int gap = 4;
        int total = tm.stringWidth(title) + gap + sm.stringWidth(sub);
        int tx = cardX + (cardW - total) / 2;
        int ty = y + 62;

        g2.setColor(new Color(40, 40, 40));
        g2.setFont(titleFont);
        g2.drawString(title, tx, ty);
        g2.setColor(Color.GRAY);
        g2.setFont(smallFont);
        g2.drawString(sub, tx + tm.stringWidth(title) + gap, ty);

        drawHint(g2, "Spasi = Play   |   Esc = Exit", y + menuH - 22);
    }

    private void drawGameOverCard(Graphics2D g2) {
        dimBackground(g2);
        int y = (height - overH) / 2;
        drawCard(g2, cardX, y, cardW, overH);

        g2.setColor(new Color(40, 40, 40));
        g2.setFont(pixel(20));
        drawCentered(g2, "Game Over", width / 2, y + 62);

        g2.setFont(pixel(14));
        drawCentered(g2, "Skor : " + logic.getScore(), width / 2, y + 108);
        drawCentered(g2, "Best : " + logic.getBestScore(), width / 2, y + 142);

        drawHint(g2, "R = Restart   |   Esc = Exit", y + overH - 22);
    }

    private void dimBackground(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 110));
        g2.fillRect(0, 0, width, height);
    }

    private void drawCard(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillRoundRect(x + 4, y + 6, w, h, 28, 28);
        g2.setColor(new Color(255, 250, 235));
        g2.fillRoundRect(x, y, w, h, 28, 28);
        g2.setColor(new Color(222, 184, 100));
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(x, y, w, h, 28, 28);
    }

    private void drawHint(Graphics2D g2, String text, int baselineY) {
        g2.setColor(Color.GRAY);
        g2.setFont(pixel(8));
        drawCentered(g2, text, width / 2, baselineY);
    }

    private void drawCentered(Graphics2D g2, String text, int cx, int baselineY) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, cx - fm.stringWidth(text) / 2, baselineY);
    }

    private void drawCenteredOutlined(Graphics2D g2, String text, int cx) {
        FontMetrics fm = g2.getFontMetrics();
        int x = cx - fm.stringWidth(text) / 2;
        g2.setColor(Color.BLACK);
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                g2.drawString(text, x + dx, 80 + dy);
            }
        }
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, 80);
    }

    private static class RoundButton extends JButton {
        private final Color base;

        RoundButton(String text, Color base) {
            super(text);
            this.base = base;
            // Keep keyboard input on the game panel rather than this button.
            setFocusable(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(pixel(12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

            Color c = base;
            if (getModel().isPressed()) c = base.darker().darker();
            else if (getModel().isRollover()) c = base.brighter();

            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

            g2.setColor(getForeground());
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(getText())) / 2;
            int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), tx, ty);
            g2.dispose();
        }
    }
}