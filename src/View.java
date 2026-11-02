import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class View extends JPanel {
    int width =  360;
    int height =  640;

    private Logic logic;

    public View(Logic logic) {
        this.logic = logic;
        setPreferredSize(new Dimension(width,height));
        setBackground(Color.cyan);

        setFocusable(true);
        addKeyListener(logic);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(logic.getBackgroundImage(), 0, 0, width,height, null);

        Player player = logic.getPlayer();
        if (player != null) {
            g.drawImage(player.getImage(), player.getPosX(), player.getPosY(),
                    player.getWidth(), player.getHeight(), null);
        }

        ArrayList<Pipe> pipes = logic.getPipes();
        if(!pipes.isEmpty()){
            for (Pipe pipe : pipes) {
                g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(),
                        pipe.getWidth(), pipe.getHeight(), null);
            }
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Consolas", Font.BOLD, 32));
        g.drawString(String.valueOf(logic.getScore()), 20, 50);

        if (logic.isGameOver()) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, width, height);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Consolas", Font.BOLD, 40));
            g.drawString("GAME OVER", 55, 240);

            g.setFont(new Font("Consolas", Font.PLAIN, 22));
            g.drawString("Skor: " + logic.getScore(), 120, 300);
            g.drawString("Best: " + logic.getBestScore(), 120, 335);

            g.setFont(new Font("Consolas", Font.BOLD, 18));
            g.drawString("[R] Restart", 110, 410);
            g.drawString("[ESC] Keluar", 110, 445);
        }
    }
}
