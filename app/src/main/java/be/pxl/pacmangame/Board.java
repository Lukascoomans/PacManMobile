/* Drew Schuster */

package be.pxl.pacmangame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import javax.imageio.*;
import javax.swing.*;

import java.lang.Math;
import java.util.*;
import java.io.*;

import be.pxl.pacmanapp.R;


/* Both Player and Ghost inherit Mover.  Has generic functions relevant to both*/
class Mover {
    /* Framecount is used to count animation frames*/
    int frameCount = 0;

    /* State contains the game map */
    boolean[][] state;

    /* gridSize is the size of one square in the game.
       max is the height/width of the game.
       increment is the speed at which the object moves,
       1 increment per move() call */
    int gridSize;
    int max;
    int increment;

    /* Generic constructor */
    public Mover() {
        gridSize = 20;
        increment = 4;
        max = 400;
        state = new boolean[20][20];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                state[i][j] = false;
            }
        }
    }

    /* Updates the state information */
    public void updateState(boolean[][] state) {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                this.state[i][j] = state[i][j];
            }
        }
    }

    /* Determines if a set of coordinates is a valid destination.*/
    public boolean isValidDest(int x, int y) {
    /* The first statements check that the x and y are inbounds.  The last statement checks the map to
       see if it's a valid location */
        if ((((x) % 20 == 0) || ((y) % 20) == 0) && 20 <= x && x < 400 && 20 <= y && y < 400 && state[x / 20 - 1][y / 20 - 1]) {
            return true;
        }
        return false;
    }
}

/* This is the pacman object */
class Player extends Mover {
    /* Direction is used in demoMode, currDirection and desiredDirection are used in non demoMode*/
    int direction;
    int currDirection;
    int desiredDirection;

    /* Keeps track of pellets eaten to determine end of game */
    int pelletsEaten;

    /* Last location */
    int lastX;
    int lastY;

    /* Current location */
    int x;
    int y;

    /* Which pellet the pacman is on top of */
    int pelletX;
    int pelletY;

    /* teleport is true when travelling through the teleport tunnels*/
    boolean teleport;

    /* Stopped is set when the pacman is not moving or has been killed */
    boolean stopped = false;

    /* Constructor places pacman in initial location and orientation */
    public Player(int x, int y) {

        teleport = false;
        pelletsEaten = 0;
        pelletX = x / gridSize - 1;
        pelletY = y / gridSize - 1;
        this.lastX = x;
        this.lastY = y;
        this.x = x;
        this.y = y;
        currDirection = 0;
        desiredDirection = 0;
    }


    /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
    public int newDirection() {
        int random;
        char backwards = 0;
        int newX = x, newY = y;
        int lookX = x, lookY = y;
        Set<Integer> set = new HashSet<Integer>();
        switch (direction) {
            case 0:
                backwards = 2;
                break;
            case 1:
                backwards = 3;
                break;
            case 2:
                backwards = 0;
                break;
            case 3:
                backwards = 1;
                break;
        }
        int newDirection = backwards;
        while (newDirection == backwards || !isValidDest(lookX, lookY)) {
            if (set.size() == 3) {
                newDirection = backwards;
                break;
            }
            newX = x;
            newY = y;
            lookX = x;
            lookY = y;
            random = (int) (Math.random() * 4) + 1;
            if (random == 1) {
                newDirection = 0;
                newX -= increment;
                lookX -= increment;
            } else if (random == 2) {
                newDirection = 1;
                newX += increment;
                lookX += gridSize;
            } else if (random == 3) {
                newDirection = 2;
                newY -= increment;
                lookY -= increment;
            } else if (random == 4) {
                newDirection = 3;
                newY += increment;
                lookY += gridSize;
            }
            if (newDirection != backwards) {
                set.add(new Integer(newDirection));
            }
        }
        return newDirection;
    }

    /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
    public boolean isChoiceDest() {
        if (x % gridSize == 0 && y % gridSize == 0) {
            return true;
        }
        return false;
    }

    /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
    public void demoMove() {
        lastX = x;
        lastY = y;
        if (isChoiceDest()) {
            direction = newDirection();
        }
        switch (direction) {
            case 0:
                if (isValidDest(x, y - increment))
                    y -= increment;
                break;
            case 1:
                if (isValidDest(x + gridSize, y)) {
                    x += increment;
                } else if (y == 9 * gridSize && x > max - gridSize * 2) {
                    x = 1 * gridSize;
                    teleport = true;
                }
                break;
            case 2:
                if (isValidDest(x, y + gridSize))
                    y += increment;
                break;
            case 3:
                if (isValidDest(x - increment, y)) {
                    x -= increment;
                } else if (y == 9 * gridSize && x < 2 * gridSize) {
                    x = max - gridSize * 1;
                    teleport = true;
                }
                break;
        }
        currDirection = direction;
        frameCount++;
    }

    /* The move function moves the pacman for one frame in non demo mode */
    public void move() {
        int gridSize = 20;
        lastX = x;
        lastY = y;

        /* Try to turn in the direction input by the user */
        /*Can only turn if we're in center of a grid*/
        if (x % 20 == 0 && y % 20 == 0 ||
                /* Or if we're reversing*/
                (desiredDirection == 0 && currDirection == 2) ||
                (desiredDirection == 1 && currDirection == 3) ||
                (desiredDirection == 2 && currDirection == 0) ||
                (desiredDirection == 3 && currDirection == 1)
                ) {
            switch (desiredDirection) {
                case 0:
                    if (isValidDest(x, y - increment))
                        y -= increment;
                    break;
                case 1:
                    if (isValidDest(x + gridSize, y))
                        x += increment;
                    break;
                case 2:
                    if (isValidDest(x, y + gridSize))
                        y += increment;
                    break;
                case 3:
                    if (isValidDest(x - increment, y))
                        x -= increment;
                    break;
            }
        }
        /* If we haven't moved, then move in the direction the pacman was headed anyway */
        if (lastX == x && lastY == y) {
            switch (currDirection) {
                case 0:
                    if (isValidDest(x, y - increment))
                        y -= increment;
                    break;
                case 1:
                    if (isValidDest(x + gridSize, y))
                        x += increment;
                    else if (y == 9 * gridSize && x > max - gridSize * 2) {
                        x = 1 * gridSize;
                        teleport = true;
                    }
                    break;
                case 2:
                    if (isValidDest(x, y + gridSize))
                        y += increment;
                    break;
                case 3:
                    if (isValidDest(x - increment, y))
                        x -= increment;
                    else if (y == 9 * gridSize && x < 2 * gridSize) {
                        x = max - gridSize * 1;
                        teleport = true;
                    }
                    break;
            }
        }

        /* If we did change direction, update currDirection to reflect that */
        else {
            currDirection = desiredDirection;
        }

        /* If we didn't move at all, set the stopped flag */
        if (lastX == x && lastY == y)
            stopped = true;

            /* Otherwise, clear the stopped flag and increment the frameCount for animation purposes*/
        else {
            stopped = false;
            frameCount++;
        }
    }

    /* Update what pellet the pacman is on top of */
    public void updatePellet() {
        if (x % gridSize == 0 && y % gridSize == 0) {
            pelletX = x / gridSize - 1;
            pelletY = y / gridSize - 1;
        }
    }
}

/* Ghost class controls the ghost. */
class Ghost extends Mover {
    /* Direction ghost is heading */
    int direction;

    /* Last ghost location*/
    int lastX;
    int lastY;

    /* Current ghost location */
    int x;
    int y;

    /* The pellet the ghost is on top of */
    int pelletX, pelletY;

    /* The pellet the ghost was last on top of */
    int lastPelletX, lastPelletY;

    /*Constructor places ghost and updates states*/
    public Ghost(int x, int y) {
        direction = 2;
        pelletX = x / gridSize - 1;
        pelletY = x / gridSize - 1;
        lastPelletX = pelletX;
        lastPelletY = pelletY;
        this.lastX = x;
        this.lastY = y;
        this.x = x;
        this.y = y;
        direction = 4;
    }

    /* update pellet status */
    public void updatePellet() {
        int tempX, tempY;
        tempX = x / gridSize - 1;
        tempY = y / gridSize - 1;
        if (tempX != pelletX || tempY != pelletY) {
            lastPelletX = pelletX;
            lastPelletY = pelletY;
            pelletX = tempX;
            pelletY = tempY;
        }

    }

    /* Determines if the location is one where the ghost has to make a decision*/
    public boolean isChoiceDest() {
        if (x % gridSize == 0 && y % gridSize == 0) {
            return true;
        }
        return false;
    }

    /* Chooses a new direction randomly for the ghost to move */
    public int newDirection() {
        int random;
        int backwards = 3;
        int newX = x, newY = y;
        int lookX = x, lookY = y;
        Set<Integer> set = new HashSet<Integer>();
        switch (direction) {
            case 0:
                backwards = 2;
                break;
            case 1:
                backwards = 3;
                break;
            case 2:
                backwards = 0;
                break;
            case 3:
                backwards = 1;
                break;
        }

        int newDirection = backwards;
        /* While we still haven't found a valid direction */
        while (newDirection == backwards || !isValidDest(lookX, lookY)) {
            /* If we've tried every location, turn around and break the loop */
            if (set.size() == 3) {
                newDirection = backwards;
                break;
            }

            newX = x;
            newY = y;
            lookX = x;
            lookY = y;

            /* Randomly choose a direction */
            random = (int) (Math.random() * 4) + 1;
            if (random == 1) {
                newDirection = 1;
                newX -= increment;
                lookX -= increment;
            } else if (random == 2) {
                newDirection = 2;
                newX += increment;
                lookX += gridSize;
            } else if (random == 3) {
                newDirection = 3;
                newY -= increment;
                lookY -= increment;
            } else if (random == 4) {
                newDirection = 4;
                newY += increment;
                lookY += gridSize;
            }
            if (newDirection != backwards) {
                set.add(new Integer(newDirection));
            }
        }
        return newDirection;
    }

    /* Random move function for ghost */
    public void move() {
        lastX = x;
        lastY = y;

        /* If we can make a decision, pick a new direction randomly */
        if (isChoiceDest()) {
            direction = newDirection();
        }

        /* If that direction is valid, move that way */
        switch (direction) {
            case 0:
                if (isValidDest(x - increment, y))
                    x -= increment;
                break;
            case 1:
                if (isValidDest(x + gridSize, y))
                    x += increment;
                break;
            case 2:
                if (isValidDest(x, y - increment))
                    y -= increment;
                break;
            case 3:
                if (isValidDest(x, y + gridSize))
                    y += increment;
                break;
        }
    }
}


/*This board class contains the player, ghosts, pellets, and most of the game logic.*/
public class Board extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    //CODE FROM PACMAN ANDROID PROJECT
    private Thread thread;
    private SurfaceHolder holder;
    private boolean canDraw = true;
    private Context context;

    private Paint paint;
    private Bitmap[] pacmanRight, pacmanDown, pacmanLeft, pacmanUp;
    private Bitmap[] ghost1Bitmap, ghost2Bitmap, ghost3Bitmap, ghost4Bitmap;
    private int totalFrame = 2;             // Total amount of frames fo each direction
    private int currentPacmanFrame = 0;     // Current Pacman frame to draw
    private long frameTicker;               // Current time since last frame has been drawn
    public Player pacman;
    public Ghost ghost1;
    public Ghost ghost2;
    public Ghost ghost3;
    public Ghost ghost4;
    int xDistance;
    int yDistance;
    private float x1, x2, y1, y2;           // Initial/Final positions of swipe
    private int direction = 4;              // Direction of the swipe, initial direction is right
    private int screenWidth;                // Width of the phone screen
    private int blockSize;                  // Size of a block on the map
    public static int LONG_PRESS_TIME = 750;  // Time in milliseconds
    private int currentScore = 0;           //Current game score
    final Handler handler = new Handler();

    //CODE FROM PACMAN JAVA PROJECT
    /* Initialize the player and ghosts */
    /*Player pacman = new Player(200,300);
    Ghost ghost1 = new Ghost(180,180);
    Ghost ghost2 = new Ghost(200,180);
    Ghost ghost3 = new Ghost(220,180);
    Ghost ghost4 = new Ghost(220,180);*/

    /* Timer is used for playing sound effects and animations */
    long timer = System.currentTimeMillis();

    /* Dying is used to count frames in the dying animation.  If it's non-zero,
       pacman is in the process of dying */
    int dying = 0;

    /* Score information */
    int currScore;
    int highScore;

    /* if the high scores have been cleared, we have to update the top of the screen to reflect that */
    boolean clearHighScores = false;

    int numLives = 2;

    /*Contains the game map, passed to player and ghosts */
    boolean[][] state;

    /* Contains the state of all pellets*/
    boolean[][] pellets;

    /* Game dimensions */
    int gridSize;
    int max;

    /* State flags*/
    boolean stopped;
    boolean titleScreen;
    boolean winScreen = false;
    boolean overScreen = false;
    boolean demo = false;
    int New;

    /* Used to call sound effects */
    GameSounds sounds;

    int lastPelletEatenX = 0;
    int lastPelletEatenY = 0;

    /* This is the font used for the menus */
    //Font font = new Font("Monospaced",Font.BOLD, 12);

    public Board(Context context) {
        super(context);
        this.context = context;
        holder = getHolder();
        holder.addCallback(this);
        frameTicker = 1000 / totalFrame;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        blockSize = screenWidth / 17;
        blockSize = (blockSize / 5) * 5;
        pacman = new Player(8 * blockSize, 13 * blockSize);
        ghost1 = new Ghost(8 * blockSize, 4 * blockSize);
        ghost2 = new Ghost(8 * blockSize, 4 * blockSize);
        ghost3 = new Ghost(8 * blockSize, 4 * blockSize);
        ghost4 = new Ghost(8 * blockSize, 4 * blockSize);

        loadBitmapImages();
        Log.i("info", "Constructor");

        initHighScores();
        sounds = new GameSounds();
        currScore = 0;
        stopped = false;
        max = 400;
        gridSize = 20;
        New = 0;
        titleScreen = true;
    }

    private void loadBitmapImages() {
        // Scales the sprites based on screen
        int spriteSize = screenWidth / 17;        // Size of Pacman & Ghost
        spriteSize = (spriteSize / 5) * 5;      // Keep it a multiple of 5

        // Add bitmap images of pacman facing right
        pacmanRight = new Bitmap[totalFrame];
        pacmanRight[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman), spriteSize, spriteSize, false);
        pacmanRight[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacmanright), spriteSize, spriteSize, false);
        // Add bitmap images of pacman facing down
        pacmanDown = new Bitmap[totalFrame];
        pacmanDown[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman), spriteSize, spriteSize, false);
        pacmanDown[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacmandown), spriteSize, spriteSize, false);
        // Add bitmap images of pacman facing left
        pacmanLeft = new Bitmap[totalFrame];
        pacmanLeft[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman), spriteSize, spriteSize, false);
        pacmanLeft[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacmanleft), spriteSize, spriteSize, false);
        // Add bitmap images of pacman facing up
        pacmanUp = new Bitmap[totalFrame];
        pacmanUp[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacman), spriteSize, spriteSize, false);
        pacmanUp[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.pacmanup), spriteSize, spriteSize, false);

        ghost1Bitmap = new Bitmap[totalFrame];
        ghost1Bitmap[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.ghost10), spriteSize, spriteSize, false);
        ghost1Bitmap[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.ghost11), spriteSize, spriteSize, false);

        ghost2Bitmap = new Bitmap[totalFrame];
        ghost2Bitmap[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.ghost20), spriteSize, spriteSize, false);
        ghost2Bitmap[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.ghost21), spriteSize, spriteSize, false);

        ghost3Bitmap = new Bitmap[totalFrame];
        ghost3Bitmap[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.ghost30), spriteSize, spriteSize, false);
        ghost3Bitmap[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.ghost31), spriteSize, spriteSize, false);

        ghost4Bitmap = new Bitmap[totalFrame];
        ghost4Bitmap[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.ghost40), spriteSize, spriteSize, false);
        ghost4Bitmap[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.ghost41), spriteSize, spriteSize, false);
    }

    @Override
    public void run() {
        Log.i("info", "Run");
        while (canDraw) {
            if (!holder.getSurface().isValid()) {
                continue;
            }
            Canvas canvas = holder.lockCanvas();
            // Set background color to Transparent
            if (canvas != null) {
                canvas.drawColor(Color.BLACK);
                drawMap(canvas);
                //drawArrowIndicators(canvas);

                updateFrame(System.currentTimeMillis());

                //moveGhost(canvas);
                ghost1.move();
                ghost2.move();
                ghost3.move();
                ghost4.move();

                // Moves the pacman based on his direction
                //movePacman(canvas);
                pacman.move();

                // Draw the pellets
                drawPellets(canvas);

                //Update current and high scores
                updateScores(canvas);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public void updateScores(Canvas canvas) {
        paint.setTextSize(blockSize);

        Globals g = Globals.getInstance();
        int highScore = g.getHighScore();
        if (currentScore > highScore) {
            g.setHighScore(currentScore);
        }

        String formattedHighScore = String.format("%05d", highScore);
        String hScore = "High Score : " + formattedHighScore;
        canvas.drawText(hScore, 0, 2 * blockSize - 10, paint);

        String formattedScore = String.format("%05d", currentScore);
        String score = "Score : " + formattedScore;
        canvas.drawText(score, 11 * blockSize, 2 * blockSize - 10, paint);
    }

    /*public void moveGhost(Canvas canvas) {
        short ch;

        xDistance = xPosPacman - xPosGhost;
        yDistance = yPosPacman - yPosGhost;

        if ((xPosGhost % blockSize == 0) && (yPosGhost % blockSize == 0)) {
            ch = leveldata1[yPosGhost / blockSize][xPosGhost / blockSize];

            if (xPosGhost >= blockSize * 17) {
                xPosGhost = 0;
            }
            if (xPosGhost < 0) {
                xPosGhost = blockSize * 17;
            }


            if (xDistance >= 0 && yDistance >= 0) { // Move right and down
                if ((ch & 4) == 0 && (ch & 8) == 0) {
                    if (Math.abs(xDistance) > Math.abs(yDistance)) {
                        ghostDirection = 1;
                    } else {
                        ghostDirection = 2;
                    }
                }
                else if ((ch & 4) == 0) {
                    ghostDirection = 1;
                }
                else if ((ch & 8) == 0) {
                    ghostDirection = 2;
                }
                else
                    ghostDirection = 3;
            }
            if (xDistance >= 0 && yDistance <= 0) { // Move right and up
                if ((ch & 4) == 0 && (ch & 2) == 0 ) {
                    if (Math.abs(xDistance) > Math.abs(yDistance)) {
                        ghostDirection = 1;
                    } else {
                        ghostDirection = 0;
                    }
                }
                else if ((ch & 4) == 0) {
                    ghostDirection = 1;
                }
                else if ((ch & 2) == 0) {
                    ghostDirection = 0;
                }
                else ghostDirection = 2;
            }
            if (xDistance <= 0 && yDistance >= 0) { // Move left and down
                if ((ch & 1) == 0 && (ch & 8) == 0) {
                    if (Math.abs(xDistance) > Math.abs(yDistance)) {
                        ghostDirection = 3;
                    } else {
                        ghostDirection = 2;
                    }
                }
                else if ((ch & 1) == 0) {
                    ghostDirection = 3;
                }
                else if ((ch & 8) == 0) {
                    ghostDirection = 2;
                }
                else ghostDirection = 1;
            }
            if (xDistance <= 0 && yDistance <= 0) { // Move left and up
                if ((ch & 1) == 0 && (ch & 2) == 0) {
                    if (Math.abs(xDistance) > Math.abs(yDistance)) {
                        ghostDirection = 3;
                    } else {
                        ghostDirection = 0;
                    }
                }
                else if ((ch & 1) == 0) {
                    ghostDirection = 3;
                }
                else if ((ch & 2) == 0) {
                    ghostDirection = 0;
                }
                else ghostDirection = 2;
            }
            // Handles wall collisions
            if ( (ghostDirection == 3 && (ch & 1) != 0) ||
                    (ghostDirection == 1 && (ch & 4) != 0) ||
                    (ghostDirection == 0 && (ch & 2) != 0) ||
                    (ghostDirection == 2 && (ch & 8) != 0) ) {
                ghostDirection = 4;
            }
        }

        if (ghostDirection == 0) {
            yPosGhost += -blockSize / 20;
        } else if (ghostDirection == 1) {
            xPosGhost += blockSize / 20;
        } else if (ghostDirection == 2) {
            yPosGhost += blockSize / 20;
        } else if (ghostDirection == 3) {
            xPosGhost += -blockSize / 20;
        }

        canvas.drawBitmap(ghostBitmap, xPosGhost, yPosGhost, paint);
    }*/


    // Updates the character sprite and handles collisions
   /* public void movePacman(Canvas canvas) {
        short ch;

        // Check if xPos and yPos of pacman is both a multiple of block size
        if ( (pacman.x % blockSize == 0) && (pacman.y  % blockSize == 0) ) {

            // When pacman goes through tunnel on
            // the right reappear at left tunnel
            if (pacman.x >= blockSize * 17) {
                pacman.x = 0;
            }

            // Is used to find the number in the level array in order to
            // check wall placement, pellet placement, and candy placement
            ch = leveldata1[pacman.y / blockSize][pacman.x / blockSize];

            // If there is a pellet, eat it
            if ((ch & 16) != 0) {
                // Toggle pellet so it won't be drawn anymore
                leveldata1[pacman.y / blockSize][pacman.x / blockSize] = (short) (ch ^ 16);
                currentScore += 10;
            }

            // Checks for direction buffering
            if (!((pacman.desiredDirection == 3 && (ch & 1) != 0) ||
                    (pacman.desiredDirection == 1 && (ch & 4) != 0) ||
                    (pacman.desiredDirection == 0 && (ch & 2) != 0) ||
                    (pacman.desiredDirection == 2 && (ch & 8) != 0))) {
                viewDirection = direction = pacman.desiredDirection;
            }

            // Checks for wall collisions
            if ((direction == 3 && (ch & 1) != 0) ||
                    (direction == 1 && (ch & 4) != 0) ||
                    (direction == 0 && (ch & 2) != 0) ||
                    (direction == 2 && (ch & 8) != 0)) {
                direction = 4;
            }
        }

        // When pacman goes through tunnel on
        // the left reappear at right tunnel
        if (pacman.x < 0) {
            pacman.x = blockSize * 17;
        }

        drawPacman(canvas);

        // Depending on the direction move the position of pacman
        if (direction == 0) {
            pacman.y += -blockSize/15;
        } else if (direction == 1) {
            pacman.x += blockSize/15;
        } else if (direction == 2) {
            pacman.y += blockSize/15;
        } else if (direction == 3) {
            pacman.x += -blockSize/15;
        }
    }*/

    /*private void drawArrowIndicators(Canvas canvas) {
        switch(pacman.desiredDirection) {
            case(0):
                canvas.drawBitmap(arrowUp[currentArrowFrame],5*blockSize , 20*blockSize, paint);
                break;
            case(1):
                canvas.drawBitmap(arrowRight[currentArrowFrame],5*blockSize , 20*blockSize, paint);
                break;
            case(2):
                canvas.drawBitmap(arrowDown[currentArrowFrame],5*blockSize , 20*blockSize, paint);
                break;
            case(3):
                canvas.drawBitmap(arrowLeft[currentArrowFrame],5*blockSize , 20*blockSize, paint);
                break;
            default:
                break;
        }

    }*/

    // Method that draws pacman based on his viewDirection
    public void drawPacman(Canvas canvas) {
        switch (pacman.currDirection) {
            case (0):
                canvas.drawBitmap(pacmanUp[currentPacmanFrame], pacman.x, pacman.y, paint);
                break;
            case (1):
                canvas.drawBitmap(pacmanRight[currentPacmanFrame], pacman.x, pacman.y, paint);
                break;
            case (3):
                canvas.drawBitmap(pacmanLeft[currentPacmanFrame], pacman.x, pacman.y, paint);
                break;
            default:
                canvas.drawBitmap(pacmanDown[currentPacmanFrame], pacman.x, pacman.y, paint);
                break;
        }
    }

    // Method that draws pellets and updates them when eaten
    public void drawPellets(Canvas canvas) {
        float x;
        float y;
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 17; j++) {
                x = j * blockSize;
                y = i * blockSize;
                // Draws pellet in the middle of a block
                if ((leveldata1[i][j] & 16) != 0)
                    canvas.drawCircle(x + blockSize / 2, y + blockSize / 2, blockSize / 10, paint);
            }
        }

        /*Paint yellowPaint = new Paint();
        yellowPaint.setColor(Color.YELLOW);
        yellowPaint.setStyle(Paint.Style.FILL);
        for (int i=1;i<20;i++)
        {
            for (int j=1;j<20;j++)
            {
                if ( pellets[i-1][j-1])
                    canvas.drawOval(i*20+8,j*20+8,4,4, yellowPaint);
            }
        }*/
    }

    /* Draws one individual pellet.  Used to redraw pellets that ghosts have run over */
    public void fillPellet(int x, int y, Canvas c) {
        Paint yellowPaint = new Paint();
        yellowPaint.setColor(Color.YELLOW);
        c.drawOval(x * 20 + 28, y * 20 + 28, 4, 4, yellowPaint);
    }

    // Method to draw map layout
    public void drawMap(Canvas canvas) {
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2.5f);
        int x;
        int y;
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 17; j++) {
                x = j * blockSize;
                y = i * blockSize;
                if ((leveldata1[i][j] & 1) != 0) // draws left
                    canvas.drawLine(x, y, x, y + blockSize - 1, paint);

                if ((leveldata1[i][j] & 2) != 0) // draws top
                    canvas.drawLine(x, y, x + blockSize - 1, y, paint);

                if ((leveldata1[i][j] & 4) != 0) // draws right
                    canvas.drawLine(
                            x + blockSize, y, x + blockSize, y + blockSize - 1, paint);
                if ((leveldata1[i][j] & 8) != 0) // draws bottom
                    canvas.drawLine(
                            x, y + blockSize, x + blockSize - 1, y + blockSize, paint);
            }
        }
        paint.setColor(Color.WHITE);

        /*Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStyle(Paint.Style.FILL);
        Paint bluePaint = new Paint();
        bluePaint.setColor(Color.BLUE);
        bluePaint.setStyle(Paint.Style.FILL);

        canvas.drawRect(0,0,600,600, blackPaint);
        canvas.drawRect(0,0,420,420, blackPaint);

        canvas.drawRect(0,0,20,600, blackPaint);
        canvas.drawRect(0,0,600,20, blackPaint);

        canvas.drawRect(19,19,382,382, paint);

        canvas.drawRect(40,40,60,20, bluePaint);
        updateMap(40,40,60,20);
        canvas.drawRect(120,40,60,20, bluePaint);
        updateMap(120,40,60,20);
        canvas.drawRect(200,20,20,40,bluePaint);
        updateMap(200,20,20,40);
        canvas.drawRect(240,40,60,20,bluePaint);
        updateMap(240,40,60,20);
        canvas.drawRect(320,40,60,20,bluePaint);
        updateMap(320,40,60,20);
        canvas.drawRect(40,80,60,20,bluePaint);
        updateMap(40,80,60,20);
        canvas.drawRect(160,80,100,20,bluePaint);
        updateMap(160,80,100,20);
        canvas.drawRect(200,80,20,60,bluePaint);
        updateMap(200,80,20,60);
        canvas.drawRect(320,80,60,20,bluePaint);
        updateMap(320,80,60,20);

        canvas.drawRect(20,120,80,60,bluePaint);
        updateMap(20,120,80,60);
        canvas.drawRect(320,120,80,60,bluePaint);
        updateMap(320,120,80,60);
        canvas.drawRect(20,200,80,60,bluePaint);
        updateMap(20,200,80,60);
        canvas.drawRect(320,200,80,60,bluePaint);
        updateMap(320,200,80,60);

        canvas.drawRect(160,160,40,20,bluePaint);
        updateMap(160,160,40,20);
        canvas.drawRect(220,160,40,20,bluePaint);
        updateMap(220,160,40,20);
        canvas.drawRect(160,180,20,20,bluePaint);
        updateMap(160,180,20,20);
        canvas.drawRect(160,200,100,20,bluePaint);
        updateMap(160,200,100,20);
        canvas.drawRect(240,180,20,20,bluePaint);
        updateMap(240,180,20,20);


        canvas.drawRect(120,120,60,20,bluePaint);
        updateMap(120,120,60,20);
        canvas.drawRect(120,80,20,100,bluePaint);
        updateMap(120,80,20,100);
        canvas.drawRect(280,80,20,100,bluePaint);
        updateMap(280,80,20,100);
        canvas.drawRect(240,120,60,20,bluePaint);
        updateMap(240,120,60,20);

        canvas.drawRect(280,200,20,60,bluePaint);
        updateMap(280,200,20,60);
        canvas.drawRect(120,200,20,60,bluePaint);
        updateMap(120,200,20,60);
        canvas.drawRect(160,240,100,20,bluePaint);
        updateMap(160,240,100,20);
        canvas.drawRect(200,260,20,40,bluePaint);
        updateMap(200,260,20,40);

        canvas.drawRect(120,280,60,20,bluePaint);
        updateMap(120,280,60,20);
        canvas.drawRect(240,280,60,20,bluePaint);
        updateMap(240,280,60,20);

        canvas.drawRect(40,280,60,20,bluePaint);
        updateMap(40,280,60,20);
        canvas.drawRect(80,280,20,60,bluePaint);
        updateMap(80,280,20,60);
        canvas.drawRect(320,280,60,20,bluePaint);
        updateMap(320,280,60,20);
        canvas.drawRect(320,280,20,60,bluePaint);
        updateMap(320,280,20,60);

        canvas.drawRect(20,320,40,20,bluePaint);
        updateMap(20,320,40,20);
        canvas.drawRect(360,320,40,20,bluePaint);
        updateMap(360,320,40,20);
        canvas.drawRect(160,320,100,20,bluePaint);
        updateMap(160,320,100,20);
        canvas.drawRect(200,320,20,60,bluePaint);
        updateMap(200,320,20,60);

        canvas.drawRect(40,360,140,20,bluePaint);
        updateMap(40,360,140,20);
        canvas.drawRect(240,360,140,20,bluePaint);
        updateMap(240,360,140,20);
        canvas.drawRect(280,320,20,40,bluePaint);
        updateMap(280,320,20,60);
        canvas.drawRect(120,320,20,60,bluePaint);
        updateMap(120,320,20,60);
        drawLives(canvas);*/
    }

    Runnable longPressed = new Runnable() {
        public void run() {
            Log.i("info", "LongPress");
            Intent pauseIntent = new Intent(getContext(), PauseActivity.class);
            getContext().startActivity(pauseIntent);
        }
    };

    // Method to get touch events
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case (MotionEvent.ACTION_DOWN): {
                x1 = event.getX();
                y1 = event.getY();
                handler.postDelayed(longPressed, LONG_PRESS_TIME);
                break;
            }
            case (MotionEvent.ACTION_UP): {
                x2 = event.getX();
                y2 = event.getY();
                calculateSwipeDirection();
                handler.removeCallbacks(longPressed);
                break;
            }
        }
        return true;
    }

    // Calculates which direction the user swipes
    // based on calculating the differences in
    // initial position vs final position of the swipe
    private void calculateSwipeDirection() {
        float xDiff = (x2 - x1);
        float yDiff = (y2 - y1);

        // Directions
        // 0 means going up
        // 1 means going right
        // 2 means going down
        // 3 means going left
        // 4 means stop moving, look at move function

        // Checks which axis has the greater distance
        // in order to see which direction the swipe is
        // going to be (buffering of direction)
        if (Math.abs(yDiff) > Math.abs(xDiff)) {
            if (yDiff < 0) {
                pacman.desiredDirection = 0;
            } else if (yDiff > 0) {
                pacman.desiredDirection = 2;
            }
        } else {
            if (xDiff < 0) {
                pacman.desiredDirection = 3;
            } else if (xDiff > 0) {
                pacman.desiredDirection = 1;
            }
        }
    }

    // Check to see if we should update the current frame
    // based on time passed so the animation won't be too
    // quick and look bad
    private void updateFrame(long gameTime) {

        // If enough time has passed go to next frame
        if (gameTime > frameTicker + (totalFrame * 30)) {
            frameTicker = gameTime;

            // Increment the frame
            currentPacmanFrame++;
            // Loop back the frame when you have gone through all the frames
            if (currentPacmanFrame >= totalFrame) {
                currentPacmanFrame = 0;
            }
        }
        /*if (gameTime > frameTicker + (50)) {
            currentArrowFrame++;
            if (currentArrowFrame >= 7) {
                currentArrowFrame = 0;
            }
        }*/
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("info", "Surface Created");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("info", "Surface Changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("info", "Surface Destroyed");
    }

    public void pause() {
        Log.i("info", "pause");
        canDraw = false;
        thread = null;
    }

    public void resume() {
        Log.i("info", "resume");
        if (thread != null) {
            thread.start();
        }
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
            Log.i("info", "resume thread");
        }
        canDraw = true;
    }

    /* Reads the high scores file and saves it */
    public void initHighScores() {
        File file = new File("highScores.txt");
        Scanner sc;
        try {
            sc = new Scanner(file);
            highScore = sc.nextInt();
            sc.close();
        } catch (Exception e) {
        }
    }

    /* Writes the new high score to a file and sets flag to update it on screen */
    public void updateScore(int score) {
        PrintWriter out;
        try {
            out = new PrintWriter("highScores.txt");
            out.println(score);
            out.close();
        } catch (Exception e) {
        }
        highScore = score;
        clearHighScores = true;
    }

    /* Wipes the high scores file and sets flag to update it on screen */
    public void clearHighScores() {
        PrintWriter out;
        try {
            out = new PrintWriter("highScores.txt");
            out.println("0");
            out.close();
        } catch (Exception e) {
        }
        highScore = 0;
        clearHighScores = true;
    }

    /* Reset occurs on a new game*/
    public void reset() {
        numLives = 2;
        state = new boolean[20][20];
        pellets = new boolean[20][20];

        /* Clear state and pellets arrays */
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                state[i][j] = true;
                pellets[i][j] = true;
            }
        }

        /* Handle the weird spots with no pellets*/
        for (int i = 5; i < 14; i++) {
            for (int j = 5; j < 12; j++) {
                pellets[i][j] = false;
            }
        }
        pellets[9][7] = false;
        pellets[8][8] = false;
        pellets[9][8] = false;
        pellets[10][8] = false;

    }


    /* Function is called during drawing of the map.
       Whenever the a portion of the map is covered up with a barrier,
       the map and pellets arrays are updated accordingly to note
       that those are invalid locations to travel or put pellets
    */
    public void updateMap(int x, int y, int width, int height) {
        for (int i = x / gridSize; i < x / gridSize + width / gridSize; i++) {
            for (int j = y / gridSize; j < y / gridSize + height / gridSize; j++) {
                state[i - 1][j - 1] = false;
                pellets[i - 1][j - 1] = false;
            }
        }
    }


    /* Draws the appropriate number of lives on the bottom left of the screen.
       Also draws the menu */
    public void drawLives(Canvas c) {
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);
        backgroundPaint.setStyle(Paint.Style.FILL);
        Paint pelletPaint = new Paint();
        pelletPaint.setColor(Color.YELLOW);
        pelletPaint.setStyle(Paint.Style.FILL);
        Paint menuPaint = new Paint();
        menuPaint.setColor(Color.YELLOW);
        menuPaint.setStyle(Paint.Style.FILL);
        Typeface font = Typeface.createFromAsset(context.getAssets(), String.format(Locale.US, "fonts/%s", "joystix.ttf"));
        menuPaint.setTypeface(font);


        /*Clear the bottom bar*/
        c.drawRect(0, max + 5, 600, gridSize, backgroundPaint);

        for (int i = 0; i < numLives; i++) {
            /*Draw each life */
            c.drawOval(gridSize * (i + 1), max + 5, gridSize, gridSize, pelletPaint);
        }
        /* Draw the menu items */
        c.drawText("Reset", 100, max + 5 + gridSize, menuPaint);
        c.drawText("Clear High Scores", 180, max + 5 + gridSize, menuPaint);
        c.drawText("Exit", 350, max + 5 + gridSize, menuPaint);
    }

    /* This is the main function that draws one entire frame of the game */
    public void paint(Canvas c) {
        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStyle(Paint.Style.FILL);

        Paint yellowPaint = new Paint();
        yellowPaint.setColor(Color.YELLOW);
        yellowPaint.setStyle(Paint.Style.FILL);
        Typeface font = Typeface.createFromAsset(context.getAssets(), String.format(Locale.US, "fonts/%s", "joystix.ttf"));
        yellowPaint.setTypeface(font);

    /* If we're playing the dying animation, don't update the entire screen.
       Just kill the pacman*/
        if (dying > 0) {
            /* Stop any pacman eating sounds */
            sounds.nomNomStop();

            /* Draw the pacman */
            c.drawBitmap(pacmanRight[0], pacman.x, pacman.y, blackPaint);

            /* Kill the pacman */
            if (dying == 4)
                c.drawRect(pacman.x, pacman.y, 20, 7, blackPaint);
            else if (dying == 3)
                c.drawRect(pacman.x, pacman.y, 20, 14, blackPaint);
            else if (dying == 2)
                c.drawRect(pacman.x, pacman.y, 20, 20, blackPaint);
            else if (dying == 1) {
                c.drawRect(pacman.x, pacman.y, 20, 20, blackPaint);
            }
     
      /* Take .1 seconds on each frame of death, and then take 2 seconds
         for the final frame to allow for the sound effect to end */
            long currTime = System.currentTimeMillis();
            long temp;
            if (dying != 1)
                temp = 100;
            else
                temp = 2000;
            /* If it's time to draw a new death frame... */
            if (currTime - timer >= temp) {
                dying--;
                timer = currTime;
                /* If this was the last death frame...*/
                if (dying == 0) {
                    if (numLives == -1) {
                        /* Demo mode has infinite lives, just give it more lives*/
                        if (demo)
                            numLives = 2;
                        else {
                            /* Game over for player.  If relevant, update high score.  Set gameOver flag*/
                            if (currScore > highScore) {
                                updateScore(currScore);
                            }
                            overScreen = true;
                        }
                    }
                }
            }
            return;
        }

        /* If need to update the high scores, redraw the top menu bar */
        if (clearHighScores) {
            c.drawRect(0, 0, 600, 18, blackPaint);
            clearHighScores = false;
            if (demo)
                c.drawText("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: " + highScore, 20, 10, yellowPaint);
            else
                c.drawText("Score: " + (currScore) + "\t High Score: " + highScore, 20, 10, yellowPaint);
        }

        /* oops is set to true when pacman has lost a life */
        boolean oops = false;

        /* Game initialization */
        if (New == 1) {
            reset();
            pacman = new Player(200, 300);
            ghost1 = new Ghost(180, 180);
            ghost2 = new Ghost(200, 180);
            ghost3 = new Ghost(220, 180);
            ghost4 = new Ghost(220, 180);
            currScore = 0;
            //drawBoard(c);
            drawPellets(c);
            drawLives(c);
            /* Send the game map to player and all ghosts */
            pacman.updateState(state);
            /* Don't let the player go in the ghost box*/
            pacman.state[9][7] = false;
            ghost1.updateState(state);
            ghost2.updateState(state);
            ghost3.updateState(state);
            ghost4.updateState(state);

            /* Draw the top menu bar*/
            if (demo)
                c.drawText("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: " + highScore, 20, 10, yellowPaint);
            else
                c.drawText("Score: " + (currScore) + "\t High Score: " + highScore, 20, 10, yellowPaint);
            New++;
        }
        /* Second frame of new game */
        else if (New == 2) {
            New++;
        }
        /* Third frame of new game */
        else if (New == 3) {
            New++;
            /* Play the newGame sound effect */
            sounds.newGame();
            timer = System.currentTimeMillis();
            return;
        }
        /* Fourth frame of new game */
        else if (New == 4) {
            /* Stay in this state until the sound effect is over */
            long currTime = System.currentTimeMillis();
            if (currTime - timer >= 5000) {
                New = 0;
            } else
                return;
        }

        /* Drawing optimization */
        /*c.copyArea(pacman.x - 20, pacman.y - 20, 80, 80, 0, 0);
        c.copyArea(ghost1.x - 20, ghost1.y - 20, 80, 80, 0, 0);
        c.copyArea(ghost2.x - 20, ghost2.y - 20, 80, 80, 0, 0);
        c.copyArea(ghost3.x - 20, ghost3.y - 20, 80, 80, 0, 0);
        c.copyArea(ghost4.x - 20, ghost4.y - 20, 80, 80, 0, 0);*/



        /* Detect collisions */
        if (pacman.x == ghost1.x && Math.abs(pacman.y - ghost1.y) < 10)
            oops = true;
        else if (pacman.x == ghost2.x && Math.abs(pacman.y - ghost2.y) < 10)
            oops = true;
        else if (pacman.x == ghost3.x && Math.abs(pacman.y - ghost3.y) < 10)
            oops = true;
        else if (pacman.x == ghost4.x && Math.abs(pacman.y - ghost4.y) < 10)
            oops = true;
        else if (pacman.y == ghost1.y && Math.abs(pacman.x - ghost1.x) < 10)
            oops = true;
        else if (pacman.y == ghost2.y && Math.abs(pacman.x - ghost2.x) < 10)
            oops = true;
        else if (pacman.y == ghost3.y && Math.abs(pacman.x - ghost3.x) < 10)
            oops = true;
        else if (pacman.y == ghost4.y && Math.abs(pacman.x - ghost4.x) < 10)
            oops = true;

        /* Kill the pacman */
        if (oops && !stopped) {
            /* 4 frames of death*/
            dying = 4;

            /* Play death sound effect */
            sounds.death();
            /* Stop any pacman eating sounds */
            sounds.nomNomStop();

            /*Decrement lives, update screen to reflect that.  And set appropriate flags and timers */
            numLives--;
            stopped = true;
            drawLives(c);
            timer = System.currentTimeMillis();
        }

        /* Delete the players and ghosts */
        c.drawRect(pacman.lastX, pacman.lastY, 20, 20, blackPaint);
        c.drawRect(ghost1.lastX, ghost1.lastY, 20, 20, blackPaint);
        c.drawRect(ghost2.lastX, ghost2.lastY, 20, 20, blackPaint);
        c.drawRect(ghost3.lastX, ghost3.lastY, 20, 20, blackPaint);
        c.drawRect(ghost4.lastX, ghost4.lastY, 20, 20, blackPaint);

        /* Eat pellets */
        if (pellets[pacman.pelletX][pacman.pelletY] && New != 2 && New != 3) {
            lastPelletEatenX = pacman.pelletX;
            lastPelletEatenY = pacman.pelletY;

            /* Play eating sound */
            sounds.nomNom();

            /* Increment pellets eaten value to track for end game */
            pacman.pelletsEaten++;

            /* Delete the pellet*/
            pellets[pacman.pelletX][pacman.pelletY] = false;

            /* Increment the score */
            currScore += 50;

            /* Update the screen to reflect the new score */
            c.drawRect(0, 0, 600, 20, blackPaint);
            if (demo)
                c.drawText("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: " + highScore, 20, 10, yellowPaint);
            else
                c.drawText("Score: " + (currScore) + "\t High Score: " + highScore, 20, 10, yellowPaint);

            /* If this was the last pellet */
            if (pacman.pelletsEaten == 173) {
                /*Demo mode can't get a high score */
                if (!demo) {
                    if (currScore > highScore) {
                        updateScore(currScore);
                    }
                    winScreen = true;
                } else {
                    titleScreen = true;
                }
                return;
            }
        }

        /* If we moved to a location without pellets, stop the sounds */
        else if ((pacman.pelletX != lastPelletEatenX || pacman.pelletY != lastPelletEatenY) || pacman.stopped) {
            /* Stop any pacman eating sounds */
            sounds.nomNomStop();
        }


        /* Replace pellets that have been run over by ghosts */
        if (pellets[ghost1.lastPelletX][ghost1.lastPelletY])
            fillPellet(ghost1.lastPelletX, ghost1.lastPelletY, c);
        if (pellets[ghost2.lastPelletX][ghost2.lastPelletY])
            fillPellet(ghost2.lastPelletX, ghost2.lastPelletY, c);
        if (pellets[ghost3.lastPelletX][ghost3.lastPelletY])
            fillPellet(ghost3.lastPelletX, ghost3.lastPelletY, c);
        if (pellets[ghost4.lastPelletX][ghost4.lastPelletY])
            fillPellet(ghost4.lastPelletX, ghost4.lastPelletY, c);


        /*Draw the ghosts */
        if (ghost1.frameCount < 5) {
            /* Draw first frame of ghosts */
            c.drawBitmap(ghost1Bitmap[0], ghost1.x, ghost1.y, blackPaint);
            c.drawBitmap(ghost2Bitmap[0], ghost2.x, ghost2.y, blackPaint);
            c.drawBitmap(ghost3Bitmap[0], ghost3.x, ghost3.y, blackPaint);
            c.drawBitmap(ghost4Bitmap[0], ghost4.x, ghost4.y, blackPaint);
            ghost1.frameCount++;
        } else {
            /* Draw second frame of ghosts */
            c.drawBitmap(ghost1Bitmap[1], ghost1.x, ghost1.y, blackPaint);
            c.drawBitmap(ghost2Bitmap[1], ghost2.x, ghost2.y, blackPaint);
            c.drawBitmap(ghost3Bitmap[1], ghost3.x, ghost3.y, blackPaint);
            c.drawBitmap(ghost4Bitmap[1], ghost4.x, ghost4.y, blackPaint);
            if (ghost1.frameCount >= 10)
                ghost1.frameCount = 0;
            else
                ghost1.frameCount++;
        }

        /* Draw the pacman */
        if (pacman.frameCount < 5) {
            /* Draw mouth closed */
            c.drawBitmap(pacmanRight[0], pacman.x, pacman.y, blackPaint);
        } else {
            /* Draw mouth open in appropriate direction */
            if (pacman.frameCount >= 10)
                pacman.frameCount = 0;

            switch (pacman.currDirection) {
                case 0:
                    c.drawBitmap(pacmanUp[0], pacman.x, pacman.y, blackPaint);
                    break;
                case 1:
                    c.drawBitmap(pacmanRight[0], pacman.x, pacman.y, blackPaint);
                    break;
                case 2:
                    c.drawBitmap(pacmanDown[0], pacman.x, pacman.y, blackPaint);
                    break;
                case 3:
                    c.drawBitmap(pacmanLeft[0], pacman.x, pacman.y, blackPaint);
                    break;
            }
        }

        /* Draw the border around the game in case it was overwritten by ghost movement or something */
        Paint whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        c.drawRect(19, 19, 382, 382, whitePaint);

    }

    final short leveldata1[][] = new short[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {19, 26, 26, 18, 26, 26, 26, 22, 0, 19, 26, 26, 26, 18, 26, 26, 22},
            {21, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 21},
            {17, 26, 26, 16, 26, 18, 26, 24, 26, 24, 26, 18, 26, 16, 26, 26, 20},
            {25, 26, 26, 20, 0, 25, 26, 22, 0, 19, 26, 28, 0, 17, 26, 26, 28},
            {0, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 0},
            {0, 0, 0, 21, 0, 19, 26, 24, 26, 24, 26, 22, 0, 21, 0, 0, 0},
            {26, 26, 26, 16, 26, 20, 0, 0, 0, 0, 0, 17, 26, 16, 26, 26, 26},
            {0, 0, 0, 21, 0, 17, 26, 26, 26, 26, 26, 20, 0, 21, 0, 0, 0},
            {0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0},
            {19, 26, 26, 16, 26, 24, 26, 22, 0, 19, 26, 24, 26, 16, 26, 26, 22},
            {21, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 21},
            {25, 22, 0, 21, 0, 0, 0, 17, 2, 20, 0, 0, 0, 21, 0, 19, 28}, // "2" in this line is for
            {0, 21, 0, 17, 26, 26, 18, 24, 24, 24, 18, 26, 26, 20, 0, 21, 0}, // pacman's spawn
            {19, 24, 26, 28, 0, 0, 25, 18, 26, 18, 28, 0, 0, 25, 26, 24, 22},
            {21, 0, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 0, 21},
            {25, 26, 26, 26, 26, 26, 26, 24, 26, 24, 26, 26, 26, 26, 26, 26, 28},
    };
}
