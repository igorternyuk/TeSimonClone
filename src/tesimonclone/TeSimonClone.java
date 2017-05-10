/**
 * Java. Game TeSimonClone
 *
 * @author Igor Ternyuk
 * @version 0.1 dated May 10, 2017
 */
package tesimonclone;

import java.awt.*;
import java.awt.event.*;
import static java.awt.event.KeyEvent.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class TeSimonClone implements ActionListener {
    public static TeSimonClone simon;
    private static final int WIDTH = 700, HEIGHT = 700;
    private static final int DELAY = 20, GLOW_DELAY = 4;
    private static final String TITLE_OF_THE_GAME = "TeSimonClone";
    private static final String LOSS_MESSAGE = "YOU LOST!";
    private static final int CENTRAL_RECT_LEFT = 175, CENTRAL_RECT_TOP = 175;
    private static final int CENTRAL_RECT_WIDTH = 350, CENTRAL_RECT_HEIGHT = 350, CENTAL_RECT_FILLET_RADIUS = 300;
    private final JFrame frame;
    private final Timer timer;
    private final Renderer renderer;
    private int dark, ticks = 0, indexPattern = 0, level = 1;
    private GameState gameState = GameState.PENDING;
    private GameButton flashedButton = GameButton.ZERO;
    private final Random random = new Random();
    private final ArrayList<GameButton> pattern = new ArrayList<>();
    private final Color OUTLINE_COLOR = new Color(85, 0, 127);
    private enum GameState{
        PENDING,
        CREATING_PATTERN,
        REPEATING,
        GAME_OVER
    }
    private enum GameButton {
        ZERO,
        GREEN,
        RED,
        BLUE,
        ORANGE
    }
    public TeSimonClone() {
        this.dark = 0;
        frame = new JFrame();
        timer = new Timer(DELAY, this);
        renderer = new Renderer();
        frame.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased(MouseEvent e){
                int cursorX = e.getX(), cursorY = e.getY();
                if (gameState == GameState.REPEATING) {
                    if (cursorX > 0 && cursorX < WIDTH / 2 && cursorY > 0 && cursorY < HEIGHT / 2) {
                        flashedButton = GameButton.GREEN;
                        ticks = 1;
                    } else if (cursorX > WIDTH / 2 && cursorX < WIDTH && cursorY > 0 && cursorY < HEIGHT / 2) {
                        flashedButton = GameButton.RED;
                        ticks = 1;
                    } else if (cursorX > 0 && cursorX < WIDTH / 2 && cursorY > HEIGHT / 2 && cursorY < HEIGHT) {
                        flashedButton = GameButton.BLUE;
                        ticks = 1;
                    } else if (cursorX > WIDTH / 2 && cursorX < WIDTH && cursorY > HEIGHT / 2 && cursorY < HEIGHT) {
                        flashedButton = GameButton.ORANGE;
                        ticks = 1;
                    }
                    //If the player releases one of the colored buttons we will check if that button corresponds to the pattern
                    if(flashedButton != GameButton.ZERO){
                        if(pattern.get(indexPattern) == flashedButton){
                            ++indexPattern;
                        } else {
                            gameState = GameState.GAME_OVER;
                        }
                    }
                } else if(gameState == GameState.GAME_OVER) {
                    gameState = GameState.PENDING;
                }                
            }
        }
        );
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e){
                if(e.getKeyCode() == VK_N){
                    prepareNewGame(1);
                } else if(e.getKeyCode() == VK_ESCAPE){
                    System.exit(0);
                }
            }
            
        });
        frame.setTitle(TITLE_OF_THE_GAME);
        frame.setSize(WIDTH + 2, HEIGHT + 28);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.add(renderer);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
    }
    
    public static void main(String[] args) {
        simon = new TeSimonClone();
        simon.run();
    }
    
    public void run(){
        timer.start();
    }
    
    private void prepareNewGame(int level){
        gameState = GameState.CREATING_PATTERN;
        this.level = level;        
        pattern.clear();
        for(int i = 0; i < level; ++i){
            pattern.add(randButton());
        }
        indexPattern = 0;
        flashedButton = GameButton.ZERO;
        dark = GLOW_DELAY;
        ticks = 0;
    }
    
    private GameButton randButton(){
        int rand = random.nextInt(100);
        if(rand < 25){
            return GameButton.GREEN;
        } else if(rand < 50) {
            return GameButton.RED;
        } else if(rand < 75) {
            return GameButton.BLUE;
        } else {
            return GameButton.ORANGE;
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) { 
        ++ticks;
        if(ticks % DELAY == 0){
            flashedButton = GameButton.ZERO;
            if(dark >= 0){
                --dark;
            }
            if(gameState == GameState.CREATING_PATTERN){
                if(dark <= 0) {
                    if(indexPattern >= level) {
                        flashedButton = GameButton.ZERO;
                        indexPattern = 0;
                        gameState = GameState.REPEATING;
                    } else {
                        flashedButton = pattern.get(indexPattern);
                        ++indexPattern;
                    }
                    dark = GLOW_DELAY;
                }
            } else if(gameState == GameState.REPEATING && indexPattern == level){
                gameState = GameState.CREATING_PATTERN;
                prepareNewGame(++level);
            }
        }
        renderer.repaint();
    }
    
    private String prepareInfoString(){
        String result = "";
        switch(gameState){
            case GAME_OVER :
                result = LOSS_MESSAGE;
                break;
            case CREATING_PATTERN :
                result = "0/" + String.valueOf(indexPattern);
                break;
            case REPEATING :
                result = String.valueOf(indexPattern) + "/" + String.valueOf(level);
                break;
            case PENDING :
                result = "Press N";
        }
        return result;
    }

    public void paint(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(flashedButton == GameButton.GREEN ? Color.GREEN : Color.GREEN.darker());
        g.fillRect(0, 0, WIDTH / 2, HEIGHT / 2);
        
        g.setColor(flashedButton == GameButton.RED ? Color.RED : Color.RED.darker());
        g.fillRect(WIDTH / 2, 0, WIDTH / 2, HEIGHT / 2);
        
        g.setColor(flashedButton == GameButton.BLUE ? Color.BLUE : Color.BLUE.darker());
        g.fillRect(0, HEIGHT / 2, WIDTH / 2, HEIGHT / 2);
        
        g.setColor(flashedButton == GameButton.ORANGE ? Color.ORANGE : Color.ORANGE.darker());
        g.fillRect(WIDTH / 2, HEIGHT / 2, WIDTH / 2, HEIGHT / 2);
        
        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(CENTRAL_RECT_LEFT, CENTRAL_RECT_TOP, CENTRAL_RECT_WIDTH, CENTRAL_RECT_HEIGHT,
                        CENTAL_RECT_FILLET_RADIUS, CENTAL_RECT_FILLET_RADIUS);
        g.fillRect(6 * WIDTH / 14, 0, WIDTH / 7, HEIGHT);
        g.fillRect(0, 6 * HEIGHT / 14, WIDTH, HEIGHT / 7);
        
        g.setColor(Color.GRAY);
        g.setStroke(new BasicStroke(150));
        g.drawOval(-75, -75, WIDTH + 150, HEIGHT + 150);
        
        g.setColor(OUTLINE_COLOR);
        g.setStroke(new BasicStroke(10));
        g.drawOval(5, 5, WIDTH - 10, HEIGHT - 10);
        int fontSize = 0, fontLeft = 0, fontTop = 0;
        switch(gameState){
            case GAME_OVER :
                fontSize = 80;
                fontLeft = WIDTH / 2 - 200;
                fontTop = HEIGHT / 2 + 22;
                break;
            case CREATING_PATTERN :
                fontSize = 142;
                fontLeft = WIDTH / 2 - 100;
                fontTop = HEIGHT / 2 + 42;
                break;
            case REPEATING :
                fontSize = 142;
                fontLeft = WIDTH / 2 - 100;
                fontTop = HEIGHT / 2 + 42;
                break;
            case PENDING :
                fontSize = 80;
                fontLeft = WIDTH / 2 - 150;
                fontTop = HEIGHT / 2 + 22;
                break;
        }
        g.setFont(new Font("Arial", 1, fontSize));
        g.setColor(Color.WHITE);
        g.drawString(prepareInfoString(), fontLeft, fontTop);
        frame.setTitle(TITLE_OF_THE_GAME + " - LEVEL: " + level);
    }    
}
