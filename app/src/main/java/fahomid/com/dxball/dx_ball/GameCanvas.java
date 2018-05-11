package fahomid.com.dxball.dx_ball;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;


class GameCanvas extends View implements Runnable {

    //main activity
    private Activity mainActivity;
    private Context applicationContext;

    //game status flag
    public static boolean gameOver;
    public static int lifeCount;
    public static int gameLevel;
    public static boolean isDeath;
    public static boolean startMovingBall;
    public static boolean levelUp;

    //objects declaration
    private Ball ball;
    private BallController ballController;

    private int ballColor;
    private float ballRadius;
    private int ballControllerWidth, ballControllerHeight, ballControllerColor;
    private boolean startingFirstTime;
    private float ballMovingSpeed;
    private float ballMovingAngel;
    private float brickWidth, brickHeight;
    private Paint paint;
    public static int gameScore;
    private float topDataPanelHeight;
    private Brick[][] bricks;
    private GameController gameController;
    private boolean ballControllerMoving;
    public static int numberOfLevel;

    public GameCanvas(Context context, Activity mainActivity) {
        super(context);
        this.mainActivity = mainActivity;
        this.applicationContext = context;
        resetAll();
    }


    //reset all game settings
    private void resetAll() {
        gameOver = false;
        ballColor = getResources().getColor(R.color.ballColor);
        ballControllerColor = getResources().getColor(R.color.ballControllerColor);
        ballControllerWidth = getResources().getInteger(R.integer.ballControllerWidth);
        ballControllerHeight = getResources().getInteger(R.integer.ballControllerHeight);
        ballRadius = getResources().getInteger(R.integer.ballRadius);
        ballMovingSpeed = getResources().getInteger(R.integer.ballMovingSpeed);
        ballMovingAngel = getResources().getInteger(R.integer.ballMovingAngel);
        brickWidth = getResources().getInteger(R.integer.brickWidth);
        brickHeight = getResources().getInteger(R.integer.brickHeight);
        startingFirstTime = getResources().getBoolean(R.bool.startingFirstTime);
        paint = new Paint();
        gameScore = getResources().getInteger(R.integer.gameScore);
        gameLevel = getResources().getInteger(R.integer.gameLevel);
        topDataPanelHeight = getResources().getInteger(R.integer.topDataPanelHeight);
        bricks = new Brick[getResources().getInteger(R.integer.numberOfLevel)][getResources().getInteger(R.integer.bricksPerLevel)];
        ballControllerMoving = getResources().getBoolean(R.bool.ballControllerMoving);
        lifeCount = getResources().getInteger(R.integer.lifeCount);
        isDeath = getResources().getBoolean(R.bool.isDeath);
        startMovingBall = getResources().getBoolean(R.bool.startMovingBall);
        levelUp = getResources().getBoolean(R.bool.levelUp);
        numberOfLevel = getResources().getInteger(R.integer.numberOfLevel);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //check if levelUp
        if(levelUp) {
            int tempLvl = gameLevel, tempScore = gameScore;
            restartGame(canvas);
            gameLevel = tempLvl;
            gameScore = tempScore;
            startMovingBall = false;
            startingFirstTime = false;
            ballMovingSpeed += gameLevel;
        }


        //setting bg color
        Drawable d = getResources().getDrawable(R.mipmap.bg1);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);

        //check if game ended
        if(gameOver) {
            mainActivity.setContentView(R.layout.activity_main);
            final SharedPreferences prefs = mainActivity.getSharedPreferences("app_data", MODE_PRIVATE);
            if(gameScore > prefs.getInt("highScore", 0)) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("highScore", gameScore);
                editor.commit();
            }
            mainActivity.setContentView(R.layout.activity_main);
            LinearLayout temp = ((Activity)(applicationContext)).findViewById(R.id.mainScreen);
            temp.setVisibility(GONE);
            temp = ((Activity)(applicationContext)).findViewById(R.id.gameOverScreen);
            temp.setVisibility(VISIBLE);
            TextView t = temp.findViewById(R.id.gameOverScore);
            t.setText(String.valueOf(gameScore));


            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {
                    LinearLayout temp = ((Activity)(applicationContext)).findViewById(R.id.gameOverScreen);
                    temp.setVisibility(GONE);
                    temp = ((Activity)(applicationContext)).findViewById(R.id.mainScreen);
                    temp.setVisibility(VISIBLE);
                    TextView t = temp.findViewById(R.id.highScore);
                    t.setText(String.valueOf(prefs.getInt("highScore", gameScore)));
                    resetAll();
                }}, 2000
            );
            gameLevel = 0;
        }

        //check if game starting for first time
        if(startingFirstTime) {
            startingFirstTime = false;
            resetGameSetup(canvas);
            constructBrickPatterns(canvas);
            gameController = new GameController(ball, ballController, bricks, canvas);
        }

        //check if death
        if(isDeath) {
            isDeath = false;
            restartGame(canvas);
        }

        //setting up ball
        canvas.drawCircle(ball.getBallPositionX(), ball.getBallPositionY(), ball.getBallRadius(), ball.getBall());

        //setting up ball controller
        canvas.drawRoundRect(ballController.getRect(), 25, 25, ballController.getBallController());
        //System.out.println("Ball moving angel: "+ ball.getBallMovingAngel());

        //setting up bricks
        createBricks(canvas);

        //setting up level and score option
        paint.setColor(Color.CYAN);
        RectF rectangleBox = new RectF(0, 0, canvas.getWidth(), topDataPanelHeight);
        canvas.drawRect(rectangleBox, paint);
        paint.setColor(Color.RED);
        paint.setTextSize(40);
        paint.setFakeBoldText(true);
        canvas.drawText("Life :"+ lifeCount, 40, 40, paint);
        canvas.drawText("Level : "+ String.valueOf(gameLevel + 1), (canvas.getWidth() / 2) - (paint.measureText("Level : "+ String.valueOf(gameLevel + 1)) / 2), 40, paint);
        canvas.drawText("Score : "+ gameScore, canvas.getWidth() - paint.measureText("Score : "+ gameScore) - 40, 40, paint);

        if(levelUp) {
            paint.setColor(Color.GREEN);
            paint.setTextSize(80);
            paint.setFakeBoldText(true);
            canvas.drawText("Level Up!", (canvas.getWidth() / 2) - (paint.measureText("Level Up!") / 2), (canvas.getHeight() / 2) - 30, paint);
            new android.os.Handler().postDelayed(new Runnable() {
                public void run() {
                    levelUp = false;
                }}, 3000
            );
        }

        if(startMovingBall) gameController.moveBall(topDataPanelHeight);
        this.run();
    }


    //restart after death
    private void restartGame(Canvas canvas) {
        ball.setBallPositionX(canvas.getWidth() / 2);
        ball.setBallPositionY(canvas.getHeight() - ballRadius - ballControllerHeight);
        ballController.setLeftPosition((canvas.getWidth() / 2) - (ballControllerWidth / 2));
        ballController.setRightPosition((canvas.getWidth() / 2) + (ballControllerWidth / 2));
    }

    //simply reset game data to default
    private void resetGameSetup(Canvas canvas) {
        resetGameSetup(canvas, gameLevel);
    }
    private void resetGameSetup(Canvas canvas, int level) {
        ball = new Ball(ballControllerHeight, ballRadius, ballColor, ballMovingSpeed, ballMovingAngel, canvas);
        ballController = new BallController((canvas.getWidth() / 2) - (ballControllerWidth / 2), canvas.getHeight() - ballControllerHeight, ballControllerWidth, ballControllerHeight, ballControllerColor, canvas);
    }

    //create bricks
    private void createBricks(Canvas canvas) {
        for(int i=0; i < 25; i++) {
            if(bricks[gameLevel][i] != null) {
                canvas.drawRoundRect(bricks[gameLevel][i].getRect(), 10, 10, bricks[gameLevel][i].getBrick());
            }
        }
    }

    //construct brick patterns
    private void constructBrickPatterns(Canvas canvas) {
        //int possibleBrickInRow = (int)(canvas.getWidth() / brickWidth);
        float next = (canvas.getWidth() - 9 * brickWidth - 12) / 2;
        //row 1
        bricks[0][0] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[0][1] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[0][2] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[0][3] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.RED);
        next += brickWidth + brickWidth;
        bricks[0][4] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[0][5] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[0][6] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[0][7] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.GRAY);
        //row 2
        next = ((canvas.getWidth() - (7 * brickWidth) - 100) / 2) - 6;
        bricks[0][8] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[0][9] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[0][10] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += (brickWidth / 2) + brickWidth;
        bricks[0][11] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += (brickWidth / 2) + brickWidth;
        bricks[0][12] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[0][13] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[0][14] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.RED);
        //row 3
        next = (canvas.getWidth() - 9 * brickWidth - 12) / 2;
        bricks[0][15] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[0][16] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[0][17] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[0][18] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + brickWidth;
        bricks[0][19] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[0][20] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[0][21] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[0][22] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);

        //row 4
        next = (canvas.getWidth() / 2) - brickWidth - (brickWidth / 2);
        bricks[0][23] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.RED);
        next = (canvas.getWidth() / 2) + (brickWidth / 2);
        bricks[0][24] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.LTGRAY);

        //Level 2
        next = (canvas.getWidth() - 9 * brickWidth - 12) / 2;
        //row 1
        bricks[1][0] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.MAGENTA);
        next += brickWidth + 2;
        bricks[1][1] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.MAGENTA);
        next += brickWidth + 2;
        bricks[1][2] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.MAGENTA);
        next += brickWidth + 2;
        bricks[1][3] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.RED);
        next += brickWidth + brickWidth;
        bricks[1][4] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.MAGENTA);
        next += brickWidth + 2;
        bricks[1][5] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.MAGENTA);
        next += brickWidth + 2;
        bricks[1][6] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.MAGENTA);
        next += brickWidth + 2;
        bricks[1][7] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.MAGENTA);
        //row 2
        next = ((canvas.getWidth() - (7 * brickWidth) - 100) / 2) - 6;
        bricks[1][8] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[1][9] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[1][10] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += (brickWidth / 2) + brickWidth;
        bricks[1][11] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += (brickWidth / 2) + brickWidth;
        bricks[1][12] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[1][13] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.RED);
        next += brickWidth + 2;
        bricks[1][14] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        //row 3
        next = (canvas.getWidth() - 9 * brickWidth - 12) / 2;
        //bricks[0][15] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[1][15] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.RED);
        next += brickWidth + 2;
        bricks[1][16] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[1][17] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + brickWidth;
        bricks[1][18] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[1][19] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[1][20] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        //bricks[0][22] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);

        //row 4
        next = (canvas.getWidth() / 8);
        bricks[1][21] = new Brick(next + next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.CYAN);
        bricks[1][22] = new Brick(next + next + next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.CYAN);
        bricks[1][23] = new Brick(next + next + next + next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.RED);
        bricks[1][24] = new Brick(next + next + next + next + next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.CYAN);

        //level 3

        //row 1
        next = (canvas.getWidth() / 8);
        bricks[2][0] = new Brick(next + next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.RED);
        bricks[2][1] = new Brick(next + next + next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.CYAN);
        bricks[2][2] = new Brick(next + next + next + next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.CYAN);
        bricks[2][3] = new Brick(next + next + next + next + next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.CYAN);

        //row 2
        next = (canvas.getWidth() - 9 * brickWidth - 12) / 2;
        next += brickWidth + 2;
        bricks[2][4] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.GREEN);
        next += brickWidth + 2;
        bricks[2][5] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.GREEN);
        next += brickWidth + 2;
        bricks[2][6] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.GREEN);
        next += brickWidth + brickWidth;
        bricks[2][7] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.GREEN);
        next += brickWidth + 2;
        bricks[2][8] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.GREEN);
        next += brickWidth + 2;
        bricks[2][9] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.GREEN);
        next += brickWidth + 2;

        //row 3
        next = (canvas.getWidth() - 9 * brickWidth - 12) / 2;
        bricks[2][10] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[2][11] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.RED);
        next += brickWidth + 2;
        bricks[2][12] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[2][13] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + brickWidth;
        bricks[2][14] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[2][15] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[2][16] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.RED);
        next += brickWidth + 2;
        bricks[2][17] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.GRAY);

        //row 4
        next = ((canvas.getWidth() - (7 * brickWidth) - 100) / 2) - 6;
        bricks[2][18] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[2][19] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[2][20] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.LTGRAY);
        next += (brickWidth / 2) + brickWidth;
        bricks[2][21] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.LTGRAY);
        next += (brickWidth / 2) + brickWidth;
        bricks[2][22] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.RED);
        next += brickWidth + 2;
        bricks[2][23] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[2][24] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.LTGRAY);


        //level 4
        //row 1
        next = (canvas.getWidth() - 9 * brickWidth - 12) / 2;
        next += brickWidth + 2;
        bricks[3][1] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.RED);
        next += brickWidth + 2;
        bricks[3][2] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[3][3] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + brickWidth;
        bricks[3][4] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[3][5] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[3][6] = new Brick(next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.GRAY);

        //row 2
        next = ((canvas.getWidth() - (7 * brickWidth) - 100) / 2) - 6;
        bricks[3][7] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[3][8] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[3][9] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += (brickWidth / 2) + brickWidth;
        bricks[3][10] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += (brickWidth / 2) + brickWidth;
        bricks[3][11] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.RED);
        next += brickWidth + 2;
        bricks[3][12] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[3][13] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.LTGRAY);

        //row 3
        next = (canvas.getWidth() / 8);
        bricks[3][14] = new Brick(next + next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.CYAN);
        bricks[3][15] = new Brick(next + next + next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.CYAN);
        bricks[3][16] = new Brick(next + next + next + next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.RED);
        bricks[3][17] = new Brick(next + next + next + next + next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.CYAN);

        //row 4
        next = (canvas.getWidth() - 9 * brickWidth - 12) / 2;
        bricks[3][18] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[3][19] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[3][20] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.RED);
        next += brickWidth + 2;
        bricks[3][21] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + brickWidth;
        bricks[3][22] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[3][23] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.GRAY);
        next += brickWidth + 2;
        bricks[3][24] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.GRAY);


        //level 5
        //row 1
        next = (canvas.getWidth() / 8);
        bricks[4][1] = new Brick(next + next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.MAGENTA);
        bricks[4][2] = new Brick(next + next + next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.MAGENTA);
        bricks[4][3] = new Brick(next + next + next + next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.RED);
        bricks[4][4] = new Brick(next + next + next + next + next, topDataPanelHeight + brickHeight, brickWidth, brickHeight, Color.MAGENTA);

        //row 2
        next = (canvas.getWidth() - 9 * brickWidth - 12) / 2;
        bricks[4][5] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.GREEN);
        next += brickWidth + 2;
        bricks[4][6] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.GREEN);
        next += brickWidth + 2;
        bricks[4][7] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.GREEN);
        next += brickWidth + 2;
        bricks[4][8] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.GREEN);
        next += brickWidth + brickWidth;
        bricks[4][9] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.RED);
        next += brickWidth + 2;
        bricks[4][10] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.GREEN);
        next += brickWidth + 2;
        bricks[4][11] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.GREEN);
        next += brickWidth + 2;
        bricks[4][12] = new Brick(next, topDataPanelHeight + (brickHeight * 3), brickWidth, brickHeight, Color.GREEN);

        //row 3
        next = ((canvas.getWidth() - (7 * brickWidth) - 100) / 2) - 6;
        bricks[4][13] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[4][14] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[4][15] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.LTGRAY);
        next += (brickWidth / 2) + brickWidth;
        bricks[4][16] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.LTGRAY);
        next += (brickWidth / 2) + brickWidth;
        bricks[4][17] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[4][18] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[4][19] = new Brick(next, topDataPanelHeight + (brickHeight * 5), brickWidth, brickHeight, Color.RED);

        //row 4
        next = ((canvas.getWidth() - (7 * brickWidth) - 100) / 2) - 6;
        next += brickWidth + 2;
        bricks[4][20] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[4][21] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.LTGRAY);
        next += (brickWidth / 2) + brickWidth;
        bricks[4][22] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.LTGRAY);
        next += (brickWidth / 2) + brickWidth;
        bricks[4][23] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.LTGRAY);
        next += brickWidth + 2;
        bricks[4][24] = new Brick(next, topDataPanelHeight + (brickHeight * 7), brickWidth, brickHeight, Color.LTGRAY);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                ballControllerMoving = true;
                if(!startMovingBall) {
                    startMovingBall = true;
                    ballControllerMoving = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(ballControllerMoving) {
                    ballController.moveLeftRight(event.getX());
                }
                break;
            case MotionEvent.ACTION_UP:
                ballControllerMoving = false;
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }


    @Override
    public void run() {
        invalidate();
    }
}