import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JFrame {

    public static void main(String[] args) {
        new SnakeGame();
    }

    public SnakeGame() {
        add(new GamePanel());
        setTitle("Snake Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int TILE_SIZE = 25;
    private static final int GAME_SPEED = 100;

    private ArrayList<Point> snakeBody;
    private Point food;
    private char direction = 'R'; // U, D, L, R
    private boolean gameOver = false;
    private Timer timer;
    private int score = 0;
    private Random random;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        
        random = new Random();
        snakeBody = new ArrayList<>();
        initGame();
        
        timer = new Timer(GAME_SPEED, this);
        timer.start();
    }

    private void initGame() {
        snakeBody.clear();
        // Initial snake with 3 body parts
        snakeBody.add(new Point(5 * TILE_SIZE, 5 * TILE_SIZE));
        snakeBody.add(new Point(4 * TILE_SIZE, 5 * TILE_SIZE));
        snakeBody.add(new Point(3 * TILE_SIZE, 5 * TILE_SIZE));
        generateFood();
        score = 0;
        direction = 'R';
        gameOver = false;
    }

    private void generateFood() {
        int maxTiles = (WIDTH / TILE_SIZE) - 1;
        food = new Point(random.nextInt(maxTiles) * TILE_SIZE, 
                        random.nextInt(maxTiles) * TILE_SIZE);
        // Make sure food doesn't spawn on snake
        for (Point body : snakeBody) {
            if (food.equals(body)) {
                generateFood();
            }
        }
    }

    private void move() {
        if (gameOver) return;

        Point head = new Point(snakeBody.get(0));
        switch (direction) {
            case 'U': head.y -= TILE_SIZE; break;
            case 'D': head.y += TILE_SIZE; break;
            case 'L': head.x -= TILE_SIZE; break;
            case 'R': head.x += TILE_SIZE; break;
        }

        // Check collision with walls
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            gameOver = true;
            return;
        }

        // Check collision with self
        for (Point body : snakeBody) {
            if (head.equals(body)) {
                gameOver = true;
                return;
            }
        }

        snakeBody.add(0, head);

        // Check if food is eaten
        if (head.equals(food)) {
            score += 10;
            generateFood();
        } else {
            snakeBody.remove(snakeBody.size() - 1);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw snake
        for (Point p : snakeBody) {
            g.setColor(Color.GREEN);
            g.fillRect(p.x, p.y, TILE_SIZE - 2, TILE_SIZE - 2);
        }

        // Draw food
        g.setColor(Color.RED);
        g.fillOval(food.x, food.y, TILE_SIZE - 2, TILE_SIZE - 2);

        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 20);

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over!", WIDTH/2 - 100, HEIGHT/2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press SPACE to restart", WIDTH/2 - 90, HEIGHT/2 + 40);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (gameOver && key == KeyEvent.VK_SPACE) {
            initGame();
            return;
        }

        switch (key) {
            case KeyEvent.VK_UP:
                if (direction != 'D') direction = 'U';
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') direction = 'D';
                break;
            case KeyEvent.VK_LEFT:
                if (direction != 'R') direction = 'L';
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') direction = 'R';
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}