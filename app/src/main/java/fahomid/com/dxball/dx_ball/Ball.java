package fahomid.com.dxball.dx_ball;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

class Ball {

    //variables and flags
    private float ballPositionX, ballPositionY, ballRadius;
    private Paint ball;
    private double dx, dy, ballMovingAngel, ballMovingSpeed;
    private Canvas canvas;
    private int canvasWidth, canvasHeight;

    public Ball(int ballControllerHeight, float Radius, int BallColor, double ballMovingSpeed, float ballMovingAngel, Canvas canvas) {
        this.canvas = canvas;
        this.canvasWidth = canvas.getWidth();
        this.canvasHeight = canvas.getHeight();
        this.ballPositionX = this.canvasWidth / 2;
        this.ballPositionY = this.canvasHeight - ballControllerHeight - Radius;
        this.ballRadius = Radius;
        this.ballMovingAngel = ballMovingAngel;
        this.ballMovingSpeed = ballMovingSpeed;
        dx = Math.cos(ballMovingAngel) * ballMovingSpeed;
        dy = Math.sin(ballMovingAngel) * ballMovingSpeed;
        this.ball = new Paint();
        this.ball.setColor(BallColor);
    }

    public Paint getBall() {
        return ball;
    }

    public float getBallPositionX() {
        return ballPositionX;
    }

    public float getBallPositionY() {
        return ballPositionY;
    }

    public float getBallRadius() {
        return ballRadius;
    }

    public double getBallMovingAngel() {
        return ballMovingAngel;
    }

    public void setBallMovingAngel(double ballMovingAngel) {
        this.ballMovingAngel = ballMovingAngel;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public void setBallRadius(float ballRadius) {
        this.ballRadius = ballRadius;
    }

    public void setBallPositionX(float ballPositionX) {
        this.ballPositionX = ballPositionX;
    }

    public void setBallPositionY(float ballPositionY) {
        this.ballPositionY = ballPositionY;
    }

    public void setBallMovingSpeed(double ballMovingSpeed) {
        this.ballMovingSpeed = ballMovingSpeed;
    }

    public double getBallMovingSpeed() {
        return ballMovingSpeed;
    }
}
