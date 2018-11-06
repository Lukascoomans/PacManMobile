/* Drew Schuster */

package be.pxl.pacmangame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import javax.imageio.*;
import javax.swing.*;
import java.lang.Math;
import java.util.*;
import java.io.*;

import be.pxl.pacmanapp.R;


/* Both Player and Ghost inherit Mover.  Has generic functions relevant to both*/
class Mover
{
  /* Framecount is used to count animation frames*/
  int frameCount=0;

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
  public Mover()
  {
    gridSize=20;
    increment = 4;
    max = 400;
    state = new boolean[20][20];
    for(int i =0;i<20;i++)
    {
      for(int j=0;j<20;j++)
      {
        state[i][j] = false;
      }
    }
  }

  /* Updates the state information */
  public void updateState(boolean[][] state)
  {
    for(int i =0;i<20;i++)
    {
      for(int j=0;j<20;j++)
      {
        this.state[i][j] = state[i][j];
      }
    }
  }

  /* Determines if a set of coordinates is a valid destination.*/
  public boolean isValidDest(int x, int y)
  {
    /* The first statements check that the x and y are inbounds.  The last statement checks the map to
       see if it's a valid location */
    if ((((x)%20==0) || ((y)%20)==0) && 20<=x && x<400 && 20<= y && y<400 && state[x/20-1][y/20-1] )
    {
      return true;
    }
    return false;
  } 
}

/* This is the pacman object */
class Player extends Mover
{
  /* Direction is used in demoMode, currDirection and desiredDirection are used in non demoMode*/ 
  char direction;
  char currDirection;
  char desiredDirection;

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
  public Player(int x, int y)
  {

    teleport=false;
    pelletsEaten=0;
    pelletX = x/gridSize-1;
    pelletY = y/gridSize-1;
    this.lastX=x;
    this.lastY=y;
    this.x = x;
    this.y = y;
    currDirection='L';
    desiredDirection='L';
  }


  /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
  public char newDirection()
  { 
     int random;
     char backwards='U';
     int newX=x,newY=y;
     int lookX=x,lookY=y;
     Set<Character> set = new HashSet<Character>();
    switch(direction)
    {
      case 'L':
         backwards='R';
         break;     
      case 'R':
         backwards='L';
         break;     
      case 'U':
         backwards='D';
         break;     
      case 'D':
         backwards='U';
         break;     
    }
     char newDirection = backwards;
     while (newDirection == backwards || !isValidDest(lookX,lookY))
     {
       if (set.size()==3)
       {
         newDirection=backwards;
         break;
       }
       newX=x;
       newY=y;
       lookX=x;
       lookY=y;
       random = (int)(Math.random()*4) + 1;
       if (random == 1)
       {
         newDirection = 'L';
         newX-=increment; 
         lookX-= increment;
       }
       else if (random == 2)
       {
         newDirection = 'R';
         newX+=increment; 
         lookX+= gridSize;
       }
       else if (random == 3)
       {
         newDirection = 'U';
         newY-=increment; 
         lookY-=increment;
       }
       else if (random == 4)
       {
         newDirection = 'D';
         newY+=increment; 
         lookY+=gridSize;
       }
       if (newDirection != backwards)
       {
         set.add(new Character(newDirection));
       }
     } 
     return newDirection;
  }

  /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
  public boolean isChoiceDest()
  {
    if (  x%gridSize==0&& y%gridSize==0 )
    {
      return true;
    }
    return false;
  }

  /* This function is used for demoMode.  It is copied from the Ghost class.  See that for comments */
  public void demoMove()
  {
    lastX=x;
    lastY=y;
    if (isChoiceDest())
    {
      direction = newDirection();
    }
    switch(direction)
    {
      case 'L':
         if ( isValidDest(x-increment,y))
         {
           x -= increment;
         }
         else if (y == 9*gridSize && x < 2 * gridSize)
         {
           x = max - gridSize*1;
           teleport = true; 
         }
         break;     
      case 'R':
         if ( isValidDest(x+gridSize,y))
         {
           x+= increment;
         }
         else if (y == 9*gridSize && x > max - gridSize*2)
         {
           x = 1*gridSize;
           teleport=true;
         }
         break;     
      case 'U':
         if ( isValidDest(x,y-increment))
           y-= increment;
         break;     
      case 'D':
         if ( isValidDest(x,y+gridSize))
           y+= increment;
         break;     
    }
    currDirection = direction;
    frameCount ++;
  }

  /* The move function moves the pacman for one frame in non demo mode */
  public void move()
  {
    int gridSize=20;
    lastX=x;
    lastY=y;
     
    /* Try to turn in the direction input by the user */
    /*Can only turn if we're in center of a grid*/
    if (x %20==0 && y%20==0 ||
       /* Or if we're reversing*/
       (desiredDirection=='L' && currDirection=='R')  ||
       (desiredDirection=='R' && currDirection=='L')  ||
       (desiredDirection=='U' && currDirection=='D')  ||
       (desiredDirection=='D' && currDirection=='U')
       )
    {
      switch(desiredDirection)
      {
        case 'L':
           if ( isValidDest(x-increment,y))
             x -= increment;
           break;     
        case 'R':
           if ( isValidDest(x+gridSize,y))
             x+= increment;
           break;     
        case 'U':
           if ( isValidDest(x,y-increment))
             y-= increment;
           break;     
        case 'D':
           if ( isValidDest(x,y+gridSize))
             y+= increment;
           break;     
      }
    }
    /* If we haven't moved, then move in the direction the pacman was headed anyway */
    if (lastX==x && lastY==y)
    {
      switch(currDirection)
      {
        case 'L':
           if ( isValidDest(x-increment,y))
             x -= increment;
           else if (y == 9*gridSize && x < 2 * gridSize)
           {
             x = max - gridSize*1;
             teleport = true; 
           }
           break;     
        case 'R':
           if ( isValidDest(x+gridSize,y))
             x+= increment;
           else if (y == 9*gridSize && x > max - gridSize*2)
           {
             x = 1*gridSize;
             teleport=true;
           }
           break;     
        case 'U':
           if ( isValidDest(x,y-increment))
             y-= increment;
           break;     
        case 'D':
           if ( isValidDest(x,y+gridSize))
             y+= increment;
           break;     
      }
    }

    /* If we did change direction, update currDirection to reflect that */
    else
    {
      currDirection=desiredDirection;
    }
   
    /* If we didn't move at all, set the stopped flag */    
    if (lastX == x && lastY==y)
      stopped=true;
  
    /* Otherwise, clear the stopped flag and increment the frameCount for animation purposes*/
    else
    {
      stopped=false;
      frameCount ++;
    }
  }

  /* Update what pellet the pacman is on top of */
  public void updatePellet()
  {
    if (x%gridSize ==0 && y%gridSize == 0)
    {
    pelletX = x/gridSize-1;
    pelletY = y/gridSize-1;
    }
  } 
}

/* Ghost class controls the ghost. */
class Ghost extends Mover
{ 
  /* Direction ghost is heading */
  char direction;

  /* Last ghost location*/
  int lastX;
  int lastY;

  /* Current ghost location */
  int x;
  int y;

  /* The pellet the ghost is on top of */
  int pelletX,pelletY;

  /* The pellet the ghost was last on top of */
  int lastPelletX,lastPelletY;

  /*Constructor places ghost and updates states*/
  public Ghost(int x, int y)
  {
    direction='L';
    pelletX=x/gridSize-1;
    pelletY=x/gridSize-1;
    lastPelletX=pelletX;
    lastPelletY=pelletY;
    this.lastX = x;
    this.lastY = y;
    this.x = x;
    this.y = y;
  }

  /* update pellet status */
  public void updatePellet()
  {
    int tempX,tempY;
    tempX = x/gridSize-1;
    tempY = y/gridSize-1;
    if (tempX != pelletX || tempY != pelletY)
    {
      lastPelletX = pelletX;
      lastPelletY = pelletY;
      pelletX=tempX;
      pelletY = tempY;
    }
     
  } 
 
  /* Determines if the location is one where the ghost has to make a decision*/ 
  public boolean isChoiceDest()
  {
    if (  x%gridSize==0&& y%gridSize==0 )
    {
      return true;
    }
    return false;
  }

  /* Chooses a new direction randomly for the ghost to move */
  public char newDirection()
  { 
    int random;
    char backwards='U';
    int newX=x,newY=y;
    int lookX=x,lookY=y;
    Set<Character> set = new HashSet<Character>();
    switch(direction)
    {
      case 'L':
         backwards='R';
         break;     
      case 'R':
         backwards='L';
         break;     
      case 'U':
         backwards='D';
         break;     
      case 'D':
         backwards='U';
         break;     
    }

    char newDirection = backwards;
    /* While we still haven't found a valid direction */
    while (newDirection == backwards || !isValidDest(lookX,lookY))
    {
      /* If we've tried every location, turn around and break the loop */
      if (set.size()==3)
      {
        newDirection=backwards;
        break;
      }

      newX=x;
      newY=y;
      lookX=x;
      lookY=y;
      
      /* Randomly choose a direction */
      random = (int)(Math.random()*4) + 1;
      if (random == 1)
      {
        newDirection = 'L';
        newX-=increment; 
        lookX-= increment;
      }
      else if (random == 2)
      {
        newDirection = 'R';
        newX+=increment; 
        lookX+= gridSize;
      }
      else if (random == 3)
      {
        newDirection = 'U';
        newY-=increment; 
        lookY-=increment;
      }
      else if (random == 4)
      {
        newDirection = 'D';
        newY+=increment; 
        lookY+=gridSize;
      }
      if (newDirection != backwards)
      {
        set.add(new Character(newDirection));
      }
    } 
    return newDirection;
  }

  /* Random move function for ghost */
  public void move()
  {
    lastX=x;
    lastY=y;
 
    /* If we can make a decision, pick a new direction randomly */
    if (isChoiceDest())
    {
      direction = newDirection();
    }
    
    /* If that direction is valid, move that way */
    switch(direction)
    {
      case 'L':
         if ( isValidDest(x-increment,y))
           x -= increment;
         break;     
      case 'R':
         if ( isValidDest(x+gridSize,y))
           x+= increment;
         break;     
      case 'U':
         if ( isValidDest(x,y-increment))
           y-= increment;
         break;     
      case 'D':
         if ( isValidDest(x,y+gridSize))
           y+= increment;
         break;     
    }
  }
}


/*This board class contains the player, ghosts, pellets, and most of the game logic.*/
public class Board extends SurfaceView implements Runnable, SurfaceHolder.Callback
{
    //CODE FROM PACMAN ANDROID PROJECT
    private Thread thread;
    private SurfaceHolder holder;
    private boolean canDraw = true;

    private Paint paint;
    private Bitmap[] pacmanRight, pacmanDown, pacmanLeft, pacmanUp;
    private Bitmap[] ghost1Bitmap, ghost2Bitmap, ghost3Bitmap, ghost4Bitmap;
    private int totalFrame = 2;             // Total amount of frames fo each direction
    private int currentPacmanFrame = 0;     // Current Pacman frame to draw
    private long frameTicker;               // Current time since last frame has been drawn
    private Player pacman;
    private Ghost ghost1;
    private Ghost ghost2;
    private Ghost ghost3;
    private Ghost ghost4;
    int xDistance;
    int yDistance;
    private float x1, x2, y1, y2;           // Initial/Final positions of swipe
    private int direction = 4;              // Direction of the swipe, initial direction is right
    private int nextDirection = 4;          // Buffer for the next direction you choose
    private int viewDirection = 2;          // Direction that pacman is facing
    private int ghostDirection;
    private int screenWidth;                // Width of the phone screen
    private int blockSize;                  // Size of a block on the map
    public static int LONG_PRESS_TIME=750;  // Time in milliseconds
    private int currentScore = 0;           //Current game score
    final Handler handler = new Handler();

    //CODE FROM PACMAN JAVA PROJECT
    /* Initialize the player and ghosts */
    Player pacman = new Player(200,300);
    Ghost ghost1 = new Ghost(180,180);
    Ghost ghost2 = new Ghost(200,180);
    Ghost ghost3 = new Ghost(220,180);
    Ghost ghost4 = new Ghost(220,180);

    /* Timer is used for playing sound effects and animations */
    long timer = System.currentTimeMillis();

    /* Dying is used to count frames in the dying animation.  If it's non-zero,
       pacman is in the process of dying */
    int dying=0;

    /* Score information */
    int currScore;
    int highScore;

    /* if the high scores have been cleared, we have to update the top of the screen to reflect that */
    boolean clearHighScores= false;

    int numLives=2;

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
    int lastPelletEatenY=0;

    /* This is the font used for the menus */
    Font font = new Font("Monospaced",Font.BOLD, 12);

    public Board(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        frameTicker = 1000/totalFrame;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        blockSize = screenWidth/17;
        blockSize = (blockSize / 5) * 5;
        Player player = new Player(8 * blockSize,13 * blockSize);
        Ghost ghost1 = new Ghost(8 * blockSize,4 * blockSize);
        Ghost ghost2 = new Ghost(8 * blockSize,4 * blockSize);
        Ghost ghost3 = new Ghost(8 * blockSize,4 * blockSize);
        Ghost ghost4 = new Ghost(8 * blockSize,4 * blockSize);
        ghostDirection = 4;

        loadBitmapImages();
        Log.i("info", "Constructor");

        initHighScores();
        sounds = new GameSounds();
        currScore=0;
        stopped=false;
        max=400;
        gridSize=20;
        New=0;
        titleScreen = true;
        }

    private void loadBitmapImages() {
        // Scales the sprites based on screen
        int spriteSize = screenWidth/17;        // Size of Pacman & Ghost
        spriteSize = (spriteSize / 5) * 5;      // Keep it a multiple of 5

        // Add bitmap images of pacman facing right
        pacmanRight = new Bitmap[totalFrame];
        pacmanRight[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(),R.drawable.pacman), spriteSize, spriteSize, false);
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
                getResources(),R.drawable.ghost10), spriteSize, spriteSize, false);
        ghost1Bitmap[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.ghost11), spriteSize, spriteSize, false);

        ghost2Bitmap = new Bitmap[totalFrame];
        ghost2Bitmap[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(),R.drawable.ghost20), spriteSize, spriteSize, false);
        ghost2Bitmap[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.ghost21), spriteSize, spriteSize, false);

        ghost3Bitmap = new Bitmap[totalFrame];
        ghost3Bitmap[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(),R.drawable.ghost30), spriteSize, spriteSize, false);
        ghost3Bitmap[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.ghost31), spriteSize, spriteSize, false);

        ghost4Bitmap = new Bitmap[totalFrame];
        ghost4Bitmap[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(),R.drawable.ghost40), spriteSize, spriteSize, false);
        ghost4Bitmap[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.ghost41), spriteSize, spriteSize, false);
    }

  /* Reads the high scores file and saves it */
  public void initHighScores()
  {
    File file = new File("highScores.txt");
    Scanner sc;
    try
    {
        sc = new Scanner(file);
        highScore = sc.nextInt();
        sc.close();
    }
    catch(Exception e)
    {
    }
  }

  /* Writes the new high score to a file and sets flag to update it on screen */
  public void updateScore(int score)
  {
    PrintWriter out;
    try
    {
      out = new PrintWriter("highScores.txt");
      out.println(score);
      out.close();
    }
    catch(Exception e)
    {
    }
    highScore=score;
    clearHighScores=true;
  }

  /* Wipes the high scores file and sets flag to update it on screen */
  public void clearHighScores()
  {
    PrintWriter out;
    try
    {
      out = new PrintWriter("highScores.txt");
      out.println("0");
      out.close();
    }
    catch(Exception e)
    {
    }
    highScore=0;
    clearHighScores=true;
  }

  /* Reset occurs on a new game*/
  public void reset()
  {
    numLives=2;
    state = new boolean[20][20];
    pellets = new boolean[20][20];

    /* Clear state and pellets arrays */
    for(int i=0;i<20;i++)
    {
      for(int j=0;j<20;j++)
      {
        state[i][j]=true;
        pellets[i][j]=true;
      }
    }

    /* Handle the weird spots with no pellets*/
    for(int i = 5;i<14;i++)
    {
      for(int j = 5;j<12;j++)
      {
        pellets[i][j]=false;
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
  public void updateMap(int x,int y, int width, int height)
  {
    for (int i =x/gridSize; i<x/gridSize+width/gridSize;i++)
    {
      for (int j=y/gridSize;j<y/gridSize+height/gridSize;j++)
      {
        state[i-1][j-1]=false;
        pellets[i-1][j-1]=false;
      }
    }
  } 


  /* Draws the appropriate number of lives on the bottom left of the screen.
     Also draws the menu */
  public void drawLives(Graphics g)
  {
    g.setColor(Color.BLACK);

    /*Clear the bottom bar*/
    g.fillRect(0,max+5,600,gridSize);
    g.setColor(Color.YELLOW);
    for(int i = 0;i<numLives;i++)
    {
      /*Draw each life */
      g.fillOval(gridSize*(i+1),max+5,gridSize,gridSize);
    }
    /* Draw the menu items */
    g.setColor(Color.YELLOW);
    g.setFont(font);
    g.drawString("Reset",100,max+5+gridSize);
    g.drawString("Clear High Scores",180,max+5+gridSize);
    g.drawString("Exit",350,max+5+gridSize);
  }
  
  
  /*  This function draws the board.  The pacman board is really complicated and can only feasibly be done
      manually.  Whenever I draw a wall, I call updateMap to invalidate those coordinates.  This way the pacman
      and ghosts know that they can't traverse this area */ 
  public void drawBoard(Graphics g)
  {
        g.setColor(Color.BLACK);
        g.fillRect(0,0,600,600);
        g.setColor(Color.BLACK);
        g.fillRect(0,0,420,420);
        
        g.setColor(Color.BLACK);
        g.fillRect(0,0,20,600);
        g.fillRect(0,0,600,20);
        g.setColor(Color.WHITE);
        g.drawRect(19,19,382,382);
        g.setColor(Color.BLUE);

        g.fillRect(40,40,60,20);
          updateMap(40,40,60,20);
        g.fillRect(120,40,60,20);
          updateMap(120,40,60,20);
        g.fillRect(200,20,20,40);
          updateMap(200,20,20,40);
        g.fillRect(240,40,60,20);
          updateMap(240,40,60,20);
        g.fillRect(320,40,60,20);
          updateMap(320,40,60,20);
        g.fillRect(40,80,60,20);
          updateMap(40,80,60,20);
        g.fillRect(160,80,100,20);
          updateMap(160,80,100,20);
        g.fillRect(200,80,20,60);
          updateMap(200,80,20,60);
        g.fillRect(320,80,60,20);
          updateMap(320,80,60,20);

        g.fillRect(20,120,80,60);
          updateMap(20,120,80,60);
        g.fillRect(320,120,80,60);
          updateMap(320,120,80,60);
        g.fillRect(20,200,80,60);
          updateMap(20,200,80,60);
        g.fillRect(320,200,80,60);
          updateMap(320,200,80,60);

        g.fillRect(160,160,40,20);
          updateMap(160,160,40,20);
        g.fillRect(220,160,40,20);
          updateMap(220,160,40,20);
        g.fillRect(160,180,20,20);
          updateMap(160,180,20,20);
        g.fillRect(160,200,100,20);
          updateMap(160,200,100,20);
        g.fillRect(240,180,20,20);
        updateMap(240,180,20,20);
        g.setColor(Color.BLUE);


        g.fillRect(120,120,60,20);
          updateMap(120,120,60,20);
        g.fillRect(120,80,20,100);
          updateMap(120,80,20,100);
        g.fillRect(280,80,20,100);
          updateMap(280,80,20,100);
        g.fillRect(240,120,60,20);
          updateMap(240,120,60,20);

        g.fillRect(280,200,20,60);
          updateMap(280,200,20,60);
        g.fillRect(120,200,20,60);
          updateMap(120,200,20,60);
        g.fillRect(160,240,100,20);
          updateMap(160,240,100,20);
        g.fillRect(200,260,20,40);
          updateMap(200,260,20,40);

        g.fillRect(120,280,60,20);
          updateMap(120,280,60,20);
        g.fillRect(240,280,60,20);
          updateMap(240,280,60,20);

        g.fillRect(40,280,60,20);
          updateMap(40,280,60,20);
        g.fillRect(80,280,20,60);
          updateMap(80,280,20,60);
        g.fillRect(320,280,60,20);
          updateMap(320,280,60,20);
        g.fillRect(320,280,20,60);
          updateMap(320,280,20,60);

        g.fillRect(20,320,40,20);
          updateMap(20,320,40,20);
        g.fillRect(360,320,40,20);
          updateMap(360,320,40,20);
        g.fillRect(160,320,100,20);
          updateMap(160,320,100,20);
        g.fillRect(200,320,20,60);
          updateMap(200,320,20,60);

        g.fillRect(40,360,140,20);
          updateMap(40,360,140,20);
        g.fillRect(240,360,140,20);
          updateMap(240,360,140,20);
        g.fillRect(280,320,20,40);
          updateMap(280,320,20,60);
        g.fillRect(120,320,20,60);
          updateMap(120,320,20,60);
        drawLives(g);
  } 


  /* Draws the pellets on the screen */
  public void drawPellets(Graphics g)
  {
        g.setColor(Color.YELLOW);
        for (int i=1;i<20;i++)
        {
          for (int j=1;j<20;j++)
          {
            if ( pellets[i-1][j-1])
            g.fillOval(i*20+8,j*20+8,4,4);
          }
        }
  }

  /* Draws one individual pellet.  Used to redraw pellets that ghosts have run over */
  public void fillPellet(int x, int y, Graphics g)
  {
    g.setColor(Color.YELLOW);
    g.fillOval(x*20+28,y*20+28,4,4);
  }

  /* This is the main function that draws one entire frame of the game */
  public void paint(Graphics g)
  {
    /* If we're playing the dying animation, don't update the entire screen.
       Just kill the pacman*/ 
    if (dying > 0)
    {
      /* Stop any pacman eating sounds */
      sounds.nomNomStop();

      /* Draw the pacman */
      g.drawImage(pacmanImage,pacman.x,pacman.y,Color.BLACK,null);
      g.setColor(Color.BLACK);
      
      /* Kill the pacman */
      if (dying == 4)
        g.fillRect(pacman.x,pacman.y,20,7);
      else if ( dying == 3)
        g.fillRect(pacman.x,pacman.y,20,14);
      else if ( dying == 2)
        g.fillRect(pacman.x,pacman.y,20,20);
      else if ( dying == 1)
      {
        g.fillRect(pacman.x,pacman.y,20,20);
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
      if (currTime - timer >= temp)
      {
        dying--;
        timer = currTime;
        /* If this was the last death frame...*/
        if (dying == 0)
        {
          if (numLives==-1)
          {
            /* Demo mode has infinite lives, just give it more lives*/
            if (demo)
              numLives=2;
            else
            {
            /* Game over for player.  If relevant, update high score.  Set gameOver flag*/
              if (currScore > highScore)
              {
                updateScore(currScore);
              }
              overScreen=true;
            }
          }
        }
      }
      return;
    }

    /* If this is the title screen, draw the title screen and return */
    if (titleScreen)
    {
      g.setColor(Color.BLACK);
      g.fillRect(0,0,600,600);
      g.drawImage(titleScreenImage,0,0,Color.BLACK,null);

      /* Stop any pacman eating sounds */
      sounds.nomNomStop();
      New = 1;
      return;
    } 

    /* If this is the win screen, draw the win screen and return */
    else if (winScreen)
    {
      g.setColor(Color.BLACK);
      g.fillRect(0,0,600,600);
      g.drawImage(winScreenImage,0,0,Color.BLACK,null);
      New = 1;
      /* Stop any pacman eating sounds */
      sounds.nomNomStop();
      return;
    }

    /* If this is the game over screen, draw the game over screen and return */
    else if (overScreen)
    {
      g.setColor(Color.BLACK);
      g.fillRect(0,0,600,600);
      g.drawImage(gameOverImage,0,0,Color.BLACK,null);
      New = 1;
      /* Stop any pacman eating sounds */
      sounds.nomNomStop();
      return;
    }

    /* If need to update the high scores, redraw the top menu bar */
    if (clearHighScores)
    {
      g.setColor(Color.BLACK);
      g.fillRect(0,0,600,18);
      g.setColor(Color.YELLOW);
      g.setFont(font);
      clearHighScores= false;
      if (demo)
        g.drawString("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: "+highScore,20,10);
      else
        g.drawString("Score: "+(currScore)+"\t High Score: "+highScore,20,10);
    }
   
    /* oops is set to true when pacman has lost a life */ 
    boolean oops=false;
    
    /* Game initialization */
    if (New==1)
    {
      reset();
      pacman = new Player(200,300);
      ghost1Bitmap = new Ghost(180,180);
      ghost2Bitmap = new Ghost(200,180);
      ghost3Bitmap = new Ghost(220,180);
      ghost4Bitmap = new Ghost(220,180);
      currScore = 0;
      drawBoard(g);
      drawPellets(g);
      drawLives(g);
      /* Send the game map to player and all ghosts */
      pacman.updateState(state);
      /* Don't let the player go in the ghost box*/
      pacman.state[9][7]=false;
      ghost1Bitmap.updateState(state);
      ghost2Bitmap.updateState(state);
      ghost3Bitmap.updateState(state);
      ghost4Bitmap.updateState(state);
   
      /* Draw the top menu bar*/
      g.setColor(Color.YELLOW);
      g.setFont(font);
      if (demo)
        g.drawString("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: "+highScore,20,10);
      else
        g.drawString("Score: "+(currScore)+"\t High Score: "+highScore,20,10);
      New++;
    }
    /* Second frame of new game */
    else if (New == 2)
    {
      New++;
    }
    /* Third frame of new game */
    else if (New == 3)
    {
      New++;
      /* Play the newGame sound effect */
      sounds.newGame();
      timer = System.currentTimeMillis();
      return;
    }
    /* Fourth frame of new game */
    else if (New == 4)
    {
      /* Stay in this state until the sound effect is over */
      long currTime = System.currentTimeMillis();
      if (currTime - timer >= 5000)
      {
        New=0;
      }
      else
        return;
    }
    
    /* Drawing optimization */
    g.copyArea(pacman.x-20,pacman.y-20,80,80,0,0);
    g.copyArea(ghost1Bitmap.x-20, ghost1Bitmap.y-20,80,80,0,0);
    g.copyArea(ghost2Bitmap.x-20, ghost2Bitmap.y-20,80,80,0,0);
    g.copyArea(ghost3Bitmap.x-20, ghost3Bitmap.y-20,80,80,0,0);
    g.copyArea(ghost4Bitmap.x-20, ghost4Bitmap.y-20,80,80,0,0);
    


    /* Detect collisions */
    if (pacman.x == ghost1Bitmap.x && Math.abs(pacman.y- ghost1Bitmap.y) < 10)
      oops=true;
    else if (pacman.x == ghost2Bitmap.x && Math.abs(pacman.y- ghost2Bitmap.y) < 10)
      oops=true;
    else if (pacman.x == ghost3Bitmap.x && Math.abs(pacman.y- ghost3Bitmap.y) < 10)
      oops=true;
    else if (pacman.x == ghost4Bitmap.x && Math.abs(pacman.y- ghost4Bitmap.y) < 10)
      oops=true;
    else if (pacman.y == ghost1Bitmap.y && Math.abs(pacman.x- ghost1Bitmap.x) < 10)
      oops=true;
    else if (pacman.y == ghost2Bitmap.y && Math.abs(pacman.x- ghost2Bitmap.x) < 10)
      oops=true;
    else if (pacman.y == ghost3Bitmap.y && Math.abs(pacman.x- ghost3Bitmap.x) < 10)
      oops=true;
    else if (pacman.y == ghost4Bitmap.y && Math.abs(pacman.x- ghost4Bitmap.x) < 10)
      oops=true;

    /* Kill the pacman */
    if (oops && !stopped)
    {
      /* 4 frames of death*/
      dying=4;
      
      /* Play death sound effect */
      sounds.death();
      /* Stop any pacman eating sounds */
      sounds.nomNomStop();

      /*Decrement lives, update screen to reflect that.  And set appropriate flags and timers */
      numLives--;
      stopped=true;
      drawLives(g);
      timer = System.currentTimeMillis();
    }

    /* Delete the players and ghosts */
    g.setColor(Color.BLACK);
    g.fillRect(pacman.lastX,pacman.lastY,20,20);
    g.fillRect(ghost1Bitmap.lastX, ghost1Bitmap.lastY,20,20);
    g.fillRect(ghost2Bitmap.lastX, ghost2Bitmap.lastY,20,20);
    g.fillRect(ghost3Bitmap.lastX, ghost3Bitmap.lastY,20,20);
    g.fillRect(ghost4Bitmap.lastX, ghost4Bitmap.lastY,20,20);

    /* Eat pellets */
    if ( pellets[pacman.pelletX][pacman.pelletY] && New!=2 && New !=3)
    {
      lastPelletEatenX = pacman.pelletX;
      lastPelletEatenY = pacman.pelletY;

      /* Play eating sound */
      sounds.nomNom();
      
      /* Increment pellets eaten value to track for end game */
      pacman.pelletsEaten++;

      /* Delete the pellet*/
      pellets[pacman.pelletX][pacman.pelletY]=false;

      /* Increment the score */
      currScore += 50;

      /* Update the screen to reflect the new score */
      g.setColor(Color.BLACK);
      g.fillRect(0,0,600,20);
      g.setColor(Color.YELLOW);
      g.setFont(font);
      if (demo)
        g.drawString("DEMO MODE PRESS ANY KEY TO START A GAME\t High Score: "+highScore,20,10);
      else
        g.drawString("Score: "+(currScore)+"\t High Score: "+highScore,20,10);

      /* If this was the last pellet */
      if (pacman.pelletsEaten == 173)
      {
        /*Demo mode can't get a high score */
        if (!demo)
        {
          if (currScore > highScore)
          {
            updateScore(currScore);
          }
          winScreen = true;
        }
        else
        {
          titleScreen = true;
        }
        return;
      }
    }

    /* If we moved to a location without pellets, stop the sounds */
    else if ( (pacman.pelletX != lastPelletEatenX || pacman.pelletY != lastPelletEatenY ) || pacman.stopped)
    {
      /* Stop any pacman eating sounds */
      sounds.nomNomStop();
    }


    /* Replace pellets that have been run over by ghosts */
    if ( pellets[ghost1Bitmap.lastPelletX][ghost1Bitmap.lastPelletY])
      fillPellet(ghost1Bitmap.lastPelletX, ghost1Bitmap.lastPelletY,g);
    if ( pellets[ghost2Bitmap.lastPelletX][ghost2Bitmap.lastPelletY])
      fillPellet(ghost2Bitmap.lastPelletX, ghost2Bitmap.lastPelletY,g);
    if ( pellets[ghost3Bitmap.lastPelletX][ghost3Bitmap.lastPelletY])
      fillPellet(ghost3Bitmap.lastPelletX, ghost3Bitmap.lastPelletY,g);
    if ( pellets[ghost4Bitmap.lastPelletX][ghost4Bitmap.lastPelletY])
      fillPellet(ghost4Bitmap.lastPelletX, ghost4Bitmap.lastPelletY,g);


    /*Draw the ghosts */
    if (ghost1Bitmap.frameCount < 5)
    {
      /* Draw first frame of ghosts */
      g.drawImage(ghost10, ghost1Bitmap.x, ghost1Bitmap.y,Color.BLACK,null);
      g.drawImage(ghost20, ghost2Bitmap.x, ghost2Bitmap.y,Color.BLACK,null);
      g.drawImage(ghost30, ghost3Bitmap.x, ghost3Bitmap.y,Color.BLACK,null);
      g.drawImage(ghost40, ghost4Bitmap.x, ghost4Bitmap.y,Color.BLACK,null);
      ghost1Bitmap.frameCount++;
    }
    else
    {
      /* Draw second frame of ghosts */
      g.drawImage(ghost11, ghost1Bitmap.x, ghost1Bitmap.y,Color.BLACK,null);
      g.drawImage(ghost21, ghost2Bitmap.x, ghost2Bitmap.y,Color.BLACK,null);
      g.drawImage(ghost31, ghost3Bitmap.x, ghost3Bitmap.y,Color.BLACK,null);
      g.drawImage(ghost41, ghost4Bitmap.x, ghost4Bitmap.y,Color.BLACK,null);
      if (ghost1Bitmap.frameCount >=10)
        ghost1Bitmap.frameCount=0;
      else
        ghost1Bitmap.frameCount++;
    }

    /* Draw the pacman */
    if (pacman.frameCount < 5)
    {
      /* Draw mouth closed */
      g.drawImage(pacmanImage,pacman.x,pacman.y,Color.BLACK,null);
    }
    else
    {
      /* Draw mouth open in appropriate direction */
      if (pacman.frameCount >=10)
        pacman.frameCount=0;

      switch(pacman.currDirection)
      {
        case 'L':
           g.drawImage(pacmanLeftImage,pacman.x,pacman.y,Color.BLACK,null);
           break;     
        case 'R':
           g.drawImage(pacmanRightImage,pacman.x,pacman.y,Color.BLACK,null);
           break;     
        case 'U':
           g.drawImage(pacmanUpImage,pacman.x,pacman.y,Color.BLACK,null);
           break;     
        case 'D':
           g.drawImage(pacmanDownImage,pacman.x,pacman.y,Color.BLACK,null);
           break;     
      }
    }

    /* Draw the border around the game in case it was overwritten by ghost movement or something */
    g.setColor(Color.WHITE);
    g.drawRect(19,19,382,382);

  }
}
