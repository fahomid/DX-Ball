package fahomid.com.dxball.dx_ball;

import android.graphics.Paint;
import android.graphics.RectF;

class Brick {

    //variables & flags declaration
    private float topPosition, bottomPosition, leftPosition, rightPosition;
    private Paint Brick;
    private RectF rect;
    private int brickColor;
    private float PositionX, PositionY;

    Brick(float PositionX, float PositionY, float BrickWidth, float BrickHeight, int color) {
        this.topPosition = PositionY;
        this.leftPosition = PositionX;
        this.rightPosition = PositionX + BrickWidth;
        this.bottomPosition = PositionY + BrickHeight;
        this.Brick = new Paint();
        this.brickColor = color;
        this.Brick.setColor(color);
        this.PositionX = PositionX;
        this.PositionY = PositionY;
        this.rect = new RectF(leftPosition, topPosition, rightPosition, bottomPosition);
    }

    public RectF getRect() {
        return rect;
    }

    public float getTopPosition() {
        return topPosition;
    }

    public void setTopPosition(float topPosition) {
        this.topPosition = topPosition;
    }

    public float getBottomPosition() {
        return bottomPosition;
    }

    public void setBottomPosition(float bottomPosition) {
        this.bottomPosition = bottomPosition;
    }

    public float getLeftPosition() {
        return leftPosition;
    }

    public void setLeftPosition(float leftPosition) {
        this.leftPosition = leftPosition;
    }

    public float getRightPosition() {
        return rightPosition;
    }

    public void setRightPosition(float rightPosition) {
        this.rightPosition = rightPosition;
    }

    public Paint getBrick() {
        return Brick;
    }

    public void setBrick(Paint brick) {
        Brick = brick;
    }

    public int getBrickColor() {
        return brickColor;
    }

    public void setBrickColor(int brickColor) {
        this.brickColor = brickColor;
    }

    public float getPositionY() {
        return PositionY;
    }

    public void setPositionY(float positionY) {
        PositionY = positionY;
    }

    public float getPositionX() {
        return PositionX;
    }

    public void setPositionX(float positionX) {
        PositionX = positionX;
    }

}
