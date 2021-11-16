
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_ANCHO = 600;
    static final int SCREEN_ALTO = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = ((SCREEN_ANCHO * SCREEN_ALTO) / UNIT_SIZE);
    static final int DELAY = 50;

    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];

    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;   // crea un objeto de tipo timer que sirve para iniciar acciones cada x tiempo
    Random random; // objeto de tipo random para generar numeros (posiciones) aleatorias


    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_ANCHO, SCREEN_ALTO));
        this.setBackground(Color.YELLOW.brighter());
        this.setFocusable(true); //hace que que los eventos incidan sobre el panel (hace que sea posible ponerle el foco)
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this); //contador que actua cada x tiempo sobre el objeto indicado (el panel si pones this)
        timer.start(); //comienza el contador

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green.darker().darker().darker());
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(Color.green.brighter());
                    g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
                g.setColor(Color.RED);
                g.setFont(new Font("Ink Free", Font.BOLD, 35));
                FontMetrics metrics = getFontMetrics(g.getFont());
                g.drawString("Score: " + applesEaten, (SCREEN_ANCHO - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
                g.drawLine(0, 50, SCREEN_ANCHO, 50);
            }
        } else {
            gameOver(g);
        }

    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_ANCHO / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) ((SCREEN_ALTO - 50) / UNIT_SIZE)) * UNIT_SIZE + 50;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        //revisa si la cabeza se choca con el cuerpo
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        //revisa si la cabeza toca borde izquierdo
        if (x[0] < 0) {
            x[0] = SCREEN_ANCHO - Math.abs(x[0]);
        }
        //revisa si la cabeza toca borde derecho
        if (x[0] >= SCREEN_ANCHO) {
            x[0] = x[0] % SCREEN_ANCHO;
        }
        //revisa si la cabeza toca borde superior
        if (y[0] < 50) {
            y[0] = SCREEN_ALTO - Math.abs(y[0]);
        }
        //revisa si la cabeza toca borde inferior
        if (y[0] >= SCREEN_ALTO) {
            y[0] = y[0] % (SCREEN_ALTO - 50);
        }
    }


    public void gameOver(Graphics g) {
        //GameOver text
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("GAME OVER", (SCREEN_ANCHO - metrics.stringWidth("GAME OVER")) / 2, SCREEN_ALTO / 2 - 20);

        //score
        g.setFont(new Font("Ink Free", Font.BOLD, 55));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Score:" + applesEaten, (SCREEN_ANCHO - metrics.stringWidth("Score:" + applesEaten)) / 2, SCREEN_ALTO / 2 + 40);

        //PLAY AGAIN
        g.setFont(new Font("Ink Free", Font.BOLD, 35));
        metrics = getFontMetrics(g.getFont());
        g.drawString("Press ENTER to play again", (SCREEN_ANCHO - metrics.stringWidth("Press ENTER to playa again")) / 2, SCREEN_ALTO / 2 + 80);

        int max = 0;
        try {
            String separator = System.getProperty("file.separator");
            Scanner maxScore = new Scanner(new File(getUsersProjectRootDirectory() + separator + "maxScore.txt"));

            if (maxScore.hasNext()) {
                int savedNumber = maxScore.nextInt();
                max = savedNumber > applesEaten ? savedNumber : applesEaten;
            } else {
                max = applesEaten;
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            //System.out.println("Error here");
        }

        g.drawString("MAX SCORE: " + max, (SCREEN_ANCHO - metrics.stringWidth("MAX SCORE: " + max)) / 2, SCREEN_ALTO / 2 + 100);

        saveMaxScore();
    }

    private void saveMaxScore() {

        String separator = System.getProperty("file.separator");
        File root = new File(getUsersProjectRootDirectory() + separator + "maxScore.txt");

        try {
            boolean success = root.createNewFile();
            if (success) {
                FileWriter fileWriter = new FileWriter(root);
                fileWriter.write(applesEaten);
            } else {
                Scanner reader = new Scanner(root);
                if(reader.hasNextInt()) {
                    if (applesEaten > reader.nextInt()) {
                        FileWriter fileWriter = new FileWriter(root, false);
                        fileWriter.write(applesEaten);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error creating file");
        }

    }

    public String getUsersProjectRootDirectory() {
        String envRootDir = System.getProperty("user.dir");
        Path rootDir = Paths.get(".").normalize().toAbsolutePath();
        if (rootDir.startsWith(envRootDir)) {
            return rootDir.toString();
        } else {
            throw new RuntimeException("Root dir not found in user directory.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) { //this is were i tried to close the old window
                SnakeGame.currentFrame.dispose();
                SnakeGame.currentFrame = new GameFrame();
            }
        }
    }
}


