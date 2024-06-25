package angryflappybird;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


/**
 * Contains helper methods for class objects
 */
public class Sprite {  

	
    private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    private boolean collided = false;
	
    /**
     * Establishes position and velocity
     */
    public Sprite() { 

        this.positionX = 0;
        this.positionY = 0;
        this.velocityX = 0;
        this.velocityY = 0;
    }
    /**
     * Creates a Sprite class object and initializes velocity as 0
     * @param pX the position of the object in regards to x
     * @param pY the position of the object in regards to y
     * @param image the image of the object 
     * 
     */
    public Sprite(double pX, double pY, Image image) {
    	setPositionXY(pX, pY);
        setImage(image);
        this.velocityX = 0;
        this.velocityY = 0;
    }
    
    /**
     * @param image
     */
    public void setImage(Image image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }
    
    /**
     * @param positionX
     * @param positionY
     */
    public void setPositionXY(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }
    
    /**
     * @return positionX
     */
    public double getPositionX() {
        return positionX;
    }

    /**
     * @return positionY
     */
    public double getPositionY() {
        return positionY;
    }
    
    /**
     * @param velocityX
     * @param velocityY
     */
    public void setVelocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }
    
    /**
     * Adds input to existing velocity for the object
     * @param x
     * @param y
     */
    public void addVelocity(double x, double y) {
        this.velocityX += x;
        this.velocityY += y;
    }
    
    /**
     * @return velocityX
     */
    public double getVelocityX() {
        return velocityX;
    }
    
    /**
     * @return velocityY
     */
    public double getVelocityY() {
        return velocityY;
    }
    
    /**
     * @return width
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * @param gc
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY);
    }

    /**
     * @return Rectangle2D a rectangle with the dimensions of the object
     */
    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, width, height);
    }
    
    /**
     * @param s another Sprite class object
     * @return boolean true if intersects, false if it does not
     */
    public boolean intersectsSprite(Sprite s) {
        return s.getBoundary().intersects(this.getBoundary());
    }
    
    /**
     * Changes the position of the object based on the current time
     * @param time
     */
    public void update(double time) {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }
    
    /**
     * @return collided a boolean stating whether or not an object has collided
     */
    public boolean getCollided() {
        return collided;
    }
    
    /**
     * @param collided the updated boolean stating whether or not an object has collided
     */
    public void setCollided(boolean collided) {
        this.collided = collided;
    }
}
