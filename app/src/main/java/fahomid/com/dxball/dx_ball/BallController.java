package fahomid.com.dxball.dx_ball;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

class BallController {

    //variables and flags
    private float topPosition, leftPosition, rightPosition, bottomPosition;
    private Paint ballController;
    private int ballControllerColor;
    private Canvas canvas;
    private int ballControllerWidth, ballControllerHeight;

    BallController(float PositionX, float PositionY, int Width, int Height, int color, Canvas canvas) {
        this.topPosition = PositionY;
        this.leftPosition = PositionX;
        this.rightPosition = PositionX + Width;
        this.bottomPosition = PositionY + Height;
        this.ballControllerColor = color;
        this.ballController = new Paint();
        this.ballController.setStrokeWidth(1);
        this.ballController.setStyle(Paint.Style.FILL);
        this.ballController.setColor(color);
        this.ballControllerWidth = Width;
        this.ballControllerHeight = Height;
        this.canvas = canvas;
    }

    public Paint getBallController() {
        return ballController;
    }

    public float getBottomPosition() {
        return bottomPosition;
    }

    public float getLeftPosition() {
        return leftPosition;
    }

    public float getRightPosition() {
        return rightPosition;
    }

    public float getTopPosition() {
        return topPosition;
    }

    public void setBallControllerColor(int ballControllerColor) {
        this.ballControllerColor = ballControllerColor;
    }

    public void setBottomPosition(float bottomPosition) {
        this.bottomPosition = bottomPosition;
    }

    public void setLeftPosition(float leftPosition) {
        this.leftPosition = leftPosition;
    }

    public void setRightPosition(float rightPosition) {
        this.rightPosition = rightPosition;
    }

    public int getBallControllerColor() {
        return ballControllerColor;
    }

    public void setTopPosition(float topPosition) {
        this.topPosition = topPosition;
    }

    public RectF getRect() {
        return new RectF(leftPosition, topPosition, rightPosition, bottomPosition);
    }

    public void moveLeftRight(float posX) {
        if(posX >= 0 && (posX + ballControllerWidth) <= (canvas.getWidth())) {
            //topPosition = posY;
            leftPosition = posX;
            rightPosition = posX + ballControllerWidth;
            //bottomPosition = posY + ballControllerHeight;
        }

    }

    public int getBallControllerHeight() {
        return ballControllerHeight;
    }

    public int getBallControllerWidth() {
        return ballControllerWidth;
    }

    public void setBallControllerHeight(int ballControllerHeight) {
        this.ballControllerHeight = ballControllerHeight;
    }
}
