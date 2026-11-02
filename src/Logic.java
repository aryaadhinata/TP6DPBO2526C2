import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Objects;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Logic implements ActionListener, KeyListener {
    int frameWidth = 360;
    int frameHeight = 640;

    int playerStartPosX = frameWidth / 2;
    int playerStartPosY = frameHeight / 2;
    int playerWidth = 34;
    int playerHeight = 24;

    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    boolean started = false;
    boolean gameOver = false;
    Clip bgm;

    int score = 0;
    int bestScore = 0;
    java.io.File bestFile = new java.io.File("bestscore.txt");

    Timer gameLoop;
    Timer pipeCooldown;
    int gravity = 1;

    int pipeVelocityX = -2;

    View view;
    Image backgroundImage;

    Image birdImage;
    Player player;

    Image lowerPipeImage;
    Image upperPipeImage;
    ArrayList<Pipe> pipes;

    public Logic() {
        backgroundImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("assets/background.png"))).getImage();

        birdImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("assets/bird.png"))).getImage();
        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage, 0);

        lowerPipeImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("assets/lowerPipe.png"))).getImage();
        upperPipeImage = new ImageIcon(Objects.requireNonNull(getClass().getResource("assets/upperPipe.png"))).getImage();
        pipes = new ArrayList<>();

        pipeCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ActionEvent) {
                placePipes();
            }
        });
        loadBestScore();
        loadMusic();

        gameLoop = new Timer(1000/60, this);
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setView(View view) {
        this.view = view;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Pipe> getPipes() {
        return pipes;
    }

    public int getScore() { return score; }
    public int getBestScore() { return bestScore; }
    public boolean isGameOver() { return gameOver; }

    public boolean isStarted() { return started; }

    public void loadMusic() {
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getResource("assets/theme.wav")));
            bgm = AudioSystem.getClip();
            bgm.open(in);
        } catch (Exception e) {
            bgm = null; // game tetap jalan tanpa suara
            System.out.println("Audio tidak dapat dimuat: " + e.getMessage());
        }
    }

    public void playMusic() {
        if (bgm == null) return;
        bgm.stop();
        bgm.setFramePosition(0);
        bgm.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopMusic() {
        if (bgm != null) bgm.stop();
    }

    public void startGame() {
        if (started) return;
        started = true;
        gameLoop.start();
        pipeCooldown.start();
        playMusic();
        if (view != null) view.updateState();
    }

    public void exitGame() {
        System.exit(0);
    }

    public void loadBestScore() {
        try {
            if (bestFile.exists()) {
                String s = new String(java.nio.file.Files.readAllBytes(bestFile.toPath())).trim();
                bestScore = Integer.parseInt(s);
            }
        } catch (Exception e) {
            bestScore = 0;
        }
    }

    public void saveBestScore() {
        try {
            java.nio.file.Files.write(bestFile.toPath(), String.valueOf(bestScore).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gameOver() {
        if (gameOver) return;
        gameOver = true;
        stopMusic();
        gameLoop.stop();
        pipeCooldown.stop();
        if (score > bestScore) {
            bestScore = score;
            saveBestScore();
        }
        if (view != null) view.updateState();
    }

    public void restart() {
        player.setPosX(playerStartPosX);
        player.setPosY(playerStartPosY);
        player.setVelocityY(0);
        pipes.clear();
        score = 0;
        gameOver = false;
        gameLoop.start();
        pipeCooldown.start();
        playMusic();
        view.updateState();
    }

    public void placePipes() {
        int randomPosY = (int) (pipeStartPosY - ((double) pipeHeight /4) - Math.random() * ((double) pipeWidth /2));
        int openingSpace = frameHeight/4;

        Pipe upperPipe = new Pipe(pipeStartPosX,randomPosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, randomPosY + openingSpace + pipeHeight,
                pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

    public void move(){
        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0));

        if (player.getPosY() > frameHeight) {
            gameOver();
        }

        for (Pipe pipe : pipes) {
            pipe.setPosX(pipe.getPosX() + pipeVelocityX);

            if (collision(player, pipe)) {
                gameOver();
            }

            if (!pipe.getPassed() && pipe.getImage() == upperPipeImage
                    && player.getPosX() > pipe.getPosX() + pipe.getWidth()) {
                pipe.setPassed(true);
                score++;
            }
        }
        pipes.removeIf(p -> p.getPosX() + p.getWidth() < 0);

        if (player.getPosY() > frameHeight) {
            gameOver();
        }
    }

    public boolean collision(Player p, Pipe pipe) {
        return p.getPosX() < pipe.getPosX() + pipe.getWidth()
                && p.getPosX() + p.getWidth() > pipe.getPosX()
                && p.getPosY() < pipe.getPosY() + pipe.getHeight()
                && p.getPosY() + p.getHeight() > pipe.getPosY();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        if(view != null){
            view.repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        if(!started){
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                startGame();
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                exitGame();
            }
        }else if(!gameOver){
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                player.setVelocityY(-10);
            }
        }else{
            if (e.getKeyCode() == KeyEvent.VK_R) {
                restart();
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                exitGame();
            }
        }
    }
    public void keyReleased(KeyEvent e) {}

}
