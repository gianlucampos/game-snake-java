package br.com.gianlucampos.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {

    public static int SCREEN_WIDTH = 1300;
    public static int SCREEN_HEIGHT = 750;
    public static int PIXEL_SIZE = 50;
    private static int DELAY = 100;
    public Timer TIMER;
    private Snake snake;
    private Rectangle apple;
    private int points = 0;

    public static void main(String[] args) {
        new SnakeGame().start();
    }

    public SnakeGame() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.initializeFrame();
        TIMER = new Timer(DELAY, this);
    }

    private void initializeFrame() {
        JFrame window = new JFrame();
        window.add(this);
        window.setTitle("Snake");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setResizable(false);
        window.setVisible(true);
        window.setLocationRelativeTo(null);
    }

    public void start() {
        randomizeApple();
        snake = new Snake();
        TIMER.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        checkBoundries();
        checkCollisions();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        draw(graphics);
    }

    public void draw(Graphics g) {
        //Draw snake
        snake.body.forEach(square -> {
            g.setColor(Color.green);
            g.fillRect(square.x, square.y, PIXEL_SIZE, PIXEL_SIZE);
        });

        //Draw apple
        g.setColor(Color.red);
        g.fillOval(apple.x, apple.y, PIXEL_SIZE, PIXEL_SIZE);


        //Draw score
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + points, (SCREEN_WIDTH - metrics.stringWidth("Score: " + points)) / 2, g.getFont().getSize());
    }

    public void move() {
        for (int i = snake.body.size() - 1; i > 0; i--) {
            snake.body.get(i).x = snake.body.get(i - 1).x;
            snake.body.get(i).y = snake.body.get(i - 1).y;
        }
        switch (snake.direction) {
            case UP -> snake.body.get(0).y -= PIXEL_SIZE;
            case DOWN -> snake.body.get(0).y += PIXEL_SIZE;
            case LEFT -> snake.body.get(0).x -= PIXEL_SIZE;
            case RIGHT -> snake.body.get(0).x += PIXEL_SIZE;
        }
    }

    //Checa paredes do jogo
    public void checkBoundries() {
        if (snake.body.get(0).x < 0) {
            restart();
        }
        if (snake.body.get(0).x > SCREEN_WIDTH) {
            restart();
        }
        if (snake.body.get(0).y > SCREEN_HEIGHT) {
            restart();
        }
        if (snake.body.get(0).y < 0) {
            restart();
        }

    }

    public void checkCollisions() {
        //Checa se comeu maça
        if (snake.body.get(0).intersects(apple)) {
            randomizeApple();
            increaseSize();
        }
        //Checa se comeu proprio corpo
        for (int i = snake.body.size() - 1; i > 0; i--) {
            if (snake.body.get(0).intersects(snake.body.get(i))) {
                restart();
            }
        }
    }

    public void increaseSize() {
        int size = snake.body.size();
        int x = snake.body.get(size - 1).x;
        int y = snake.body.get(size - 1).y;
        snake.body.add(new Rectangle(x, y, PIXEL_SIZE, PIXEL_SIZE));
        points++;
    }

    public void randomizeApple() {
        apple = new Rectangle(PIXEL_SIZE, PIXEL_SIZE);
        apple.x = new Random().nextInt(SCREEN_WIDTH / PIXEL_SIZE) * PIXEL_SIZE;
        apple.y = new Random().nextInt(SCREEN_HEIGHT / PIXEL_SIZE) * PIXEL_SIZE;
    }

    public void restart() {
        points = 0;
        TIMER.stop();
        start();
    }

    public void increaseSpeed() {
        if (DELAY == -50) return;
        DELAY -= 50;
        TIMER.setDelay(DELAY);
    }

    public void decreaseSpeed() {
        DELAY += 50;
        TIMER.setDelay(DELAY);
    }

    class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> {
                    if (snake.direction != Snake.Direction.RIGHT) {
                        snake.direction = Snake.Direction.LEFT;
                    }
                }
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> {
                    if (snake.direction != Snake.Direction.LEFT) {
                        snake.direction = Snake.Direction.RIGHT;
                    }
                }
                case KeyEvent.VK_UP, KeyEvent.VK_W -> {
                    if (snake.direction != Snake.Direction.DOWN) {
                        snake.direction = Snake.Direction.UP;
                    }
                }
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> {
                    if (snake.direction != Snake.Direction.UP) {
                        snake.direction = Snake.Direction.DOWN;
                    }
                }
                case KeyEvent.VK_R -> restart();
                case KeyEvent.VK_I -> increaseSpeed();
                case KeyEvent.VK_O -> decreaseSpeed();
                case KeyEvent.VK_Q -> System.exit(0);
            }
        }
    }

}

class Snake {
    public enum Direction {
        UP,
        RIGHT,
        LEFT,
        DOWN
    }

    public Direction direction = Direction.RIGHT;
    public ArrayList<Rectangle> body = new ArrayList<>();

    public Snake() {
        for (int i = 0; i < 3; i++) {
            Rectangle rectangle = new Rectangle(SnakeGame.PIXEL_SIZE, SnakeGame.PIXEL_SIZE);
            rectangle.setLocation(200, 200);
            body.add(rectangle);
        }
    }
}