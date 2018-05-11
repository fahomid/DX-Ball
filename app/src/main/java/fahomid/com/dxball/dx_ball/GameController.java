package fahomid.com.dxball.dx_ball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.Arrays;

import java.util.ArrayList;

class GameController {

    //game objects and variables
    private Ball ball;
    private BallController ballController;
    private Brick[][] bricks;
    private Canvas canvas;
    private float canvasWidth, canvasHeight;

    GameController(Ball ball, BallController ballController, Brick[][] bricks, Canvas canvas) {
        this.ball = ball;
        this.ballController = ballController;
        this.bricks = bricks;
        this.canvas = canvas;
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
    }

    public void moveBall() {
        moveBall(0);
    }

    public void moveBall(float leaveTop) {

        if((ball.getBallPositionX() + ball.getBallRadius()) >= canvasWidth || (ball.getBallPositionX() - ball.getBallRadius()) <= 0){
            ball.setDx(ball.getDx() * (-1));
        }
        if((ball.getBallPositionY() - ball.getBallRadius()) <= leaveTop){
            ball.setDy(ball.getDy() * (-1));
        }

        if(((ball.getBallPositionY()+ ball.getBallRadius()) >= ballController.getTopPosition())&&((ball.getBallPositionY() + ball.getBallRadius()) <= ballController.getBottomPosition())&& ((ball.getBallPositionX()) >= ballController.getLeftPosition())&& ((ball.getBallPositionX()) <= ballController.getRightPosition())) {
            System.out.println("ballX Position: "+ ball.getBallPositionX() +" , barLeft: "+ ballController.getLeftPosition());
            setBallMovingAngel(ball.getBallPositionX() - ballController.getLeftPosition());
        } else if((ball.getBallPositionY() + ball.getBallRadius()) >= canvasHeight){
            GameCanvas.lifeCount--;
            GameCanvas.isDeath = true;
            GameCanvas.startMovingBall = false;
            if(GameCanvas.lifeCount < 1) GameCanvas.gameOver = true;
        }

        boolean allNull = true;

        for(int i=0;i < 25;i++) {
            if(bricks[GameCanvas.gameLevel][i] != null) allNull = false;
            if (bricks[GameCanvas.gameLevel][i] != null && ((ball.getBallPositionY() - ball.getBallRadius()) <= bricks[GameCanvas.gameLevel][i].getBottomPosition()) && ((ball.getBallPositionY() + ball.getBallRadius()) >= bricks[GameCanvas.gameLevel][i].getTopPosition()) && ((ball.getBallPositionX()) >= bricks[GameCanvas.gameLevel][i].getLeftPosition()) && ((ball.getBallPositionX()) <= bricks[GameCanvas.gameLevel][i].getRightPosition())) {
                GameCanvas.gameScore += bricks[GameCanvas.gameLevel][i].getBrickColor() == Color.RED ? 10 : 1;
                bricks[GameCanvas.gameLevel][i] = null;
                ball.setDy(ball.getDy() * (-1));
            }
        }

        if (allNull) {
            GameCanvas.gameLevel++;
            GameCanvas.levelUp = true;
            if(GameCanvas.gameLevel >= GameCanvas.numberOfLevel) {
                GameCanvas.gameOver = true;
            }
        }

        ball.setBallPositionX((float) (ball.getBallPositionX() + ball.getDx()));
        ball.setBallPositionY((float) (ball.getBallPositionY() + ball.getDy()));
    }

    private void setBallMovingAngel(double angle) {
        angle = Math.toRadians(angle * (180 / (double)ballController.getBallControllerWidth()));
        double cosVal = Math.cos(angle);
        double sinVal = Math.sin(angle);
        ball.setDx(cosVal * ball.getBallMovingSpeed() * (-1));
        ball.setDy(sinVal > 0 ?  sinVal * ball.getBallMovingSpeed() * (-1) : sinVal * ball.getBallMovingSpeed());
    }
}
