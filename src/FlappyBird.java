import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 360;
    int boardHeight = 640;

    //images
    Image backgroundImage;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;

    int birdWidth = 34;
    int birdHeight = 24;




    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;
        }
    }


    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe{
        int x = pipeX;
        int y= pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image image;
        boolean passed = false;

        Pipe(Image image){
            this.image = image;
        }
    }

    //game logic
    Bird bird;

    int velocityX = -4;
    int  velocityY = 4;
    int gravity = 2;


    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;

    double score = 0;

    ArrayList<Pipe> pipes;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));

        setFocusable(true);
        addKeyListener(this);

        backgroundImage = new ImageIcon(getClass().getResource("/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("/bottompipe.png")).getImage();

        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });

        placePipesTimer.start();

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    public void placePipes(){
        int randomPipeY = (int)(pipeY - pipeHeight / 4 - Math.random() * (pipeHeight/2));
        int openingSpace = boardHeight/4;
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }



    public void draw(Graphics g) {

        //background
        g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);

        //bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        //pipe

        for (int i = 0; i < pipes.size(); i++){

            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.image, pipe.x, pipe.y, pipe.width, pipe.height, null);

        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));

        if(gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score + " points"), 10, 35 );
            g.drawString("Press SPACE to restart", 10, 65 );

        }else{
            g.drawString(String.valueOf((int) score), 10, 35);
        }


    }



    public void move(){

        velocityY += gravity * 0.5;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for (Pipe pipe : pipes) {

            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5; //1 point per 2 ppipes
            }

            if(collision(bird, pipe)){
                gameOver = true;
            }

        }

        if(bird.y > boardHeight){
            gameOver = true;
        }


    }

    public boolean collision(Bird bird, Pipe pipe){

        return bird.x < pipe.x + pipe.width &&
                bird.x + bird.width > pipe.x &&
                bird.y < pipe.y + pipe.height &&
                bird.y + bird.height > pipe.y;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        move();
        repaint();

        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            velocityY = -9;

            if(gameOver){
                //restart game (conditions)
                score = 0;
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }

        }



    }

    @Override
    public void keyTyped(KeyEvent e) {

    }



    @Override
    public void keyReleased(KeyEvent e) {

    }

}