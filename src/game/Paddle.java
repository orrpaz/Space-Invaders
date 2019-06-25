package game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import base.Velocity;
import base.Sprite;
import base.HitListener;
import base.Collidable;
import base.HitNotifier;
import biuoop.KeyboardSensor;
import biuoop.DrawSurface;
import geometry.Point;
import geometry.Rectangle;

/**
 * this class represents a paddle.
 */
public class Paddle implements Sprite, Collidable, HitNotifier {
    private biuoop.KeyboardSensor keyboard;
    private Rectangle rectangle;
    private Color color;
    private int horizontalBound;
    private int verticalBound;
    private int borderBlock;
    private int velocity;
    private long lastTimeShot;
    private List<HitListener> hitListeners;


    /**
     * construct a paddle from a rectangle, color,
     * gui object and margin size.
     *
     * @param rectangle   the given rectangle.
     * @param borderBlock the size of the border.
     * @param keyboard    the keyboard
     * @param color       the paddle's color.
     */
    public Paddle(Rectangle rectangle, int borderBlock, Color color, KeyboardSensor keyboard) {
        this.rectangle = rectangle;
        this.color = color;
        this.keyboard = keyboard;
        this.hitListeners = new ArrayList<HitListener>();
        this.lastTimeShot = 0;
        this.borderBlock = borderBlock;

    }

    /**
     * construct a paddle from an upper left point,
     * width, height, color, gui object and margin size.
     *
     * @param upperLeft   the the paddle's upper left point.
     * @param width       the paddle's width.
     * @param height      the paddle's height.
     * @param borderBlock the size of the border.
     * @param keyboard    the keyboard
     * @param color       the paddle's color.
     */
    public Paddle(Point upperLeft, double width, double height, int borderBlock,
                  Color color, KeyboardSensor keyboard) {
        this.rectangle = new Rectangle(upperLeft, width, height);
        this.color = color;
        this.keyboard = keyboard;
        this.borderBlock = borderBlock;
        this.lastTimeShot = 0;
        this.hitListeners = new ArrayList<HitListener>();
    }

    /**
     * construct a paddle from two coordinates,
     * width, height, color, gui object and margin size.
     *
     * @param x           the x coordinate of the initial location
     *                    of the paddle's upper left corner.
     * @param y           the y coordinate of the initial location
     *                    of the paddle's upper left corner.
     * @param width       the paddle's width.
     * @param height      the paddle's height.
     * @param borderBlock the size of the border.
     * @param keyboard    the keyboard
     * @param color       the paddle's color.
     */
    public Paddle(double x, double y, int width, int height, int borderBlock, Color color,
                  KeyboardSensor keyboard) {
        this.rectangle = new Rectangle(x, y, width, height);
        this.color = color;
        this.keyboard = keyboard;
        this.borderBlock = borderBlock;
        this.lastTimeShot = 0;
        this.hitListeners = new ArrayList<HitListener>();

    }

    /**
     * this method moves the paddle to the left.
     *
     * @param dt the dt value of this game.
     */
    public void moveLeft(double dt) {
        int vlc = (int) (Math.floor(this.velocity * dt));
        Point upperLeft = this.rectangle.getUpperLeft();
        if (keyboard.isPressed(KeyboardSensor.LEFT_KEY)) {
            if (this.rectangle.getUpperLeft().getX() - vlc > this.borderBlock) {
                this.rectangle = new Rectangle(upperLeft.getX() - vlc
                        , upperLeft.getY(), this.rectangle.getWidth(), this.rectangle.getHeight());
            } else if (this.rectangle.getUpperLeft().getX() > this.borderBlock) {
                this.rectangle = new Rectangle(this.borderBlock
                        , upperLeft.getY(), this.rectangle.getWidth(), this.rectangle.getHeight());
            }
        }

    }

    /**
     * this method moves the paddle to the right.
     *
     * @param dt the dt value of this game.
     */
    public void moveRight(double dt) {
        int vlc = (int) (Math.floor(this.velocity * dt));
        Point upperLeft = this.rectangle.getUpperLeft();
        if (keyboard.isPressed(KeyboardSensor.RIGHT_KEY)) {
            if (this.rectangle.getUpperLeft().getX() + this.rectangle.getWidth()
                    < this.horizontalBound - this.borderBlock - vlc) {
                this.rectangle = new Rectangle(upperLeft.getX() + vlc
                        , upperLeft.getY(), this.rectangle.getWidth(), this.rectangle.getHeight());
            } else if (this.rectangle.getUpperLeft().getX()
                    < this.horizontalBound - this.borderBlock - this.rectangle.getWidth()) {
                this.rectangle = new Rectangle(this.horizontalBound - this.borderBlock - this.rectangle.getWidth()
                        , upperLeft.getY(), this.rectangle.getWidth(), this.rectangle.getHeight());
            }
        }
    }

    /**
     * this method notifies the paddle that a time unit has passed.
     *
     * @param dt the dt value of this game.
     */
    public void timePassed(double dt) {
        this.moveLeft(dt);
        this.moveRight(dt);
    }

    /**
     * this method draws the paddle on given DrawSurface.
     *
     * @param d the DrawSurface to draw on.
     */
    public void drawOn(DrawSurface d) {
        Point upperLeft = this.rectangle.getUpperLeft();
        d.setColor(this.color);
        d.fillRectangle((int) Math.round(upperLeft.getX()), (int) Math.round(upperLeft.getY()),
                (int) Math.round(this.rectangle.getWidth()), (int) Math.round(this.rectangle.getHeight()));
        d.setColor(Color.BLACK);
        d.drawRectangle((int) Math.round(upperLeft.getX()), (int) Math.round(upperLeft.getY()),
                (int) Math.round(this.rectangle.getWidth()), (int) Math.round(this.rectangle.getHeight()));

    }

    /**
     * this method returns the rectangle that defines the paddle.
     *
     * @return the rectangle that defines the paddle.
     */
    public Rectangle getCollisionRectangle() {
        return this.rectangle;
    }

    /**
     * this method gets collision point and velocity
     * and returns a new velocity according to the hit properties.
     * the up line of the paddle divided to 5 part.
     *
     * @param collisionPoint  - the collision point.
     * @param currentVelocity - the current velocity.
     * @param hitter          thr ball hitter
     * @return the velocity according to the hit properties.
     */
    public Velocity hit(Shots hitter, Point collisionPoint, Velocity currentVelocity) {
        double upperLeftX = this.rectangle.getUpperLeft().getX();
        double widthOfRegions = this.rectangle.getWidth() / 5;
        double endLeft = upperLeftX + widthOfRegions;
        double endMiddleLeft = upperLeftX + 2 * widthOfRegions;
        double endCenter = upperLeftX + 3 * widthOfRegions;
        double endMiddleRight = upperLeftX + 4 * widthOfRegions;
        double endRight = upperLeftX + 5 * widthOfRegions;

        // region  1
        if (collisionPoint.getX() >= upperLeftX && collisionPoint.getX() < endLeft) {
            this.notifyHit(hitter);
            return Velocity.fromAngleAndSpeed(300, currentVelocity.getSpeed());
        }
        // region  2
        if (collisionPoint.getX() >= endLeft && collisionPoint.getX() < endMiddleLeft) {
            this.notifyHit(hitter);
            return Velocity.fromAngleAndSpeed(330, currentVelocity.getSpeed());
        }
        // region  3
        if (collisionPoint.getX() >= endMiddleLeft && collisionPoint.getX() < endCenter) {
            this.notifyHit(hitter);
            return new Velocity(currentVelocity.getDx(), -currentVelocity.getDy());
        }
        // region  4
        if (collisionPoint.getX() >= endCenter && collisionPoint.getX() < endMiddleRight) {
            this.notifyHit(hitter);
            return Velocity.fromAngleAndSpeed(30, currentVelocity.getSpeed());
        }
        // region  5
        if (collisionPoint.getX() >= endMiddleRight && collisionPoint.getX() < endRight) {
            this.notifyHit(hitter);
            return Velocity.fromAngleAndSpeed(60, currentVelocity.getSpeed());
        }
        // hit on sides of paddle.
        if (this.rectangle.getLeft().isContainPoint(collisionPoint)) {
            this.notifyHit(hitter);
            return Velocity.fromAngleAndSpeed(290, currentVelocity.getSpeed());
        }
        if (this.rectangle.getRight().isContainPoint(collisionPoint)) {
            this.notifyHit(hitter);
            return Velocity.fromAngleAndSpeed(80, currentVelocity.getSpeed());
        }
        return currentVelocity;
    }

    /**
     * this method adds the paddle to a game.
     *
     * @param g - the game.
     */
    public void addToGame(GameLevel g) {
        g.addCollidable(this);
        g.addSprite(this);
    }

    /**
     * set bounds.
     *
     * @param hBound -  the horizontal Bound
     * @param vBound the vertical Bound
     */
    public void setBounds(int hBound, int vBound) {
        this.horizontalBound = hBound;
        this.verticalBound = vBound;
    }

    /**
     * set velocity to paddle.
     *
     * @param v - the velocity
     */
    public void setVelocity(int v) {
        this.velocity = v;
    }

    /**
     * create shot to the paddle.
     * @param game thr game.
     * @param dt the dt
     */
    public void shoot(GameLevel game, double dt) {
        if (this.keyboard.isPressed(KeyboardSensor.SPACE_KEY)) {
            if (System.currentTimeMillis() - lastTimeShot > 350) {
                // create new shoot.
                Shots shot = game.getPlayerShoot(new Point(
                        this.getCollisionRectangle().getUp().middle().getX(),
                        this.getCollisionRectangle().getUp().middle().getY() - 4));
                shot.setVelocity(0, -300);
                shot.addToGame(game);
                this.lastTimeShot = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void addHitListener(HitListener hl) {
        this.hitListeners.add(hl);
    }

    @Override
    public void removeHitListener(HitListener hl) {
        this.hitListeners.remove(hl);
    }

    /**
     * gets a ball(shoot) hitter that is hitting the block and notify
     * all the hit listeners about the hit.
     *
     * @param hitter the hitting ball.
     */
    private void notifyHit(Shots hitter) {
        List<HitListener> listeners =
                new ArrayList<HitListener>(this.hitListeners);
        for (HitListener hl : listeners) {
            hl.hitEvent(this, hitter);
        }
    }

    /**
     * remove sprite an colidibale from the game.
     * @param game thr game.
     */
    public void removeFromGame(GameLevel game) {
        game.removeCollidable(this);
        game.removeSprite(this);
    }
}