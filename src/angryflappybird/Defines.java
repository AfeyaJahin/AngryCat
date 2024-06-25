package angryflappybird;

import java.util.HashMap;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Contains coefficients relevant to the program and methods relating to
 * images
 */
public class Defines {
    
    // dimension of the GUI application
    final int APP_HEIGHT = 600;
    final int APP_WIDTH = 600;
    final int SCENE_HEIGHT = 570;
    final int SCENE_WIDTH = 400;
    
    // variable for score
    int score = 0;
    
    // variable for lives
    int lives = 3;
    
    // coefficients related to the blob
    final int BLOB_WIDTH = 70;
    final int BLOB_HEIGHT = 70;
    final int BLOB_POS_X = 70;
    final int BLOB_POS_Y = 200;
    final int BLOB_DROP_TIME = 300000000;   // the elapsed time threshold before the blob starts dropping
    final int BLOB_DROP_VEL = 300;          // the blob drop velocity
    final int BLOB_FLY_VEL = -50;
    final int BLOB_IMG_LEN = 4;
    final int BLOB_IMG_PERIOD = 5;
    
    // coefficients related to the floors
    final int FLOOR_WIDTH = 400;
    final int FLOOR_HEIGHT = 100;
    final int FLOOR_COUNT = 2;
    
    
    // coefficients related to the pipes
    final int PIPE_HEIGHT = 120;
    final int PIPE_WIDTH = 50;
    final int PIPE_COUNT = 400;
    final int PIPE_POS_X = 100;
    final int PIPE_POS_Y = 500;
    final int PIPE_VEL = 120;
    
    // coefficients related to time
    final int SCENE_SHIFT_TIME = 5;
    final double SCENE_SHIFT_INCR = -0.4;
    final double NANOSEC_TO_SEC = 1.0 / 1000000000.0;
    final double SEC_TO_NANOSEC = 1.0 * 1000000000.0;
    final double TRANSITION_TIME = 0.1;
    final int TRANSITION_CYCLE = 2;
    
    // coefficients related to rat
    final int RAT_COUNT = 1;
    final int RAT_WIDTH = 80;
    final int RAT_HEIGHT = 90;
    final int RAT_POS_X = 350;
    final int RAT_POS_Y = 0;
    final int RAT_IMG_LEN = 3;
    final int RAT_DROP_TIME = 120;
    final int RAT_IMG_PERIOD = 5;
    

    // coefficients related to fish
    final int FISH_COUNT = 400;
    final int FISH_WIDTH = 120;
    final int FISH_HEIGHT = 120;
    final int FISH_POS_X = 100;
    final int FISH_POS_Y = 270;
    final int FISH_IMG_LEN = 3;
    final int FISH_VEL = 120;
    
    // coefficients related to yarn
    final int YARN_COUNT = 400;
    final int YARN_WIDTH = 60;
    final int YARN_HEIGHT = 80;
    final int YARN_POS_X = 370;
    final int YARN_POS_Y = 420;
    final int YARN_IMG_LEN = 3;
    final int YARN_VEL = 120;
    
      
    // coefficients related to media display
    final String STAGE_TITLE = "Angry Flappy Bird";
    private final String IMAGE_DIR = "../resources/images/";

    final String[] IMAGE_FILES = {"background", "night", "blob0", "blob1", "blob2", "blob3", "floor", "yarn", "Fish", "Rat", "pipe1", "pipe2"};


    final HashMap<String, ImageView> IMVIEW = new HashMap<String, ImageView>();
    final HashMap<String, Image> IMAGE = new HashMap<String, Image>();
    
    //nodes on the scene graph
    Button startButton;
    ChoiceBox choiceBox;
    
    // constructor
    /**
     * Constructs a Defines object, initializes images and nodes
     */

    Defines() {
        
        // initialize images
        for(int i=0; i<IMAGE_FILES.length; i++) {
            Image img;
            if (i == 6) {
                img = new Image(pathImage(IMAGE_FILES[i]), FLOOR_WIDTH, FLOOR_HEIGHT, false, false);
            }
            else if (i == 2 || i == 3 || i == 4 || i == 5){
                img = new Image(pathImage(IMAGE_FILES[i]), BLOB_WIDTH, BLOB_HEIGHT, false, false);
            }
            else if (i == 9){
                img = new Image(pathImage(IMAGE_FILES[i]), RAT_WIDTH, RAT_HEIGHT, false, false);
            }
            else if (i == 8){
                img = new Image(pathImage(IMAGE_FILES[i]), FISH_WIDTH, FISH_HEIGHT, false, false);
            }

            else if (i == 7){
                img = new Image(pathImage(IMAGE_FILES[i]), YARN_WIDTH, YARN_HEIGHT, false, false);
            }
            else if (i == 10 || i == 11){
                img = new Image(pathImage(IMAGE_FILES[i]), PIPE_WIDTH, PIPE_HEIGHT, false, false);
            }
            else {
                img = new Image(pathImage(IMAGE_FILES[i]), SCENE_WIDTH, SCENE_HEIGHT, false, false);
            }
            IMAGE.put(IMAGE_FILES[i],img);
        }
        
        // initialize image views
        for(int i=0; i<IMAGE_FILES.length; i++) {
            ImageView imgView = new ImageView(IMAGE.get(IMAGE_FILES[i]));
            IMVIEW.put(IMAGE_FILES[i],imgView);
        }
        
        // initialize scene nodes
        startButton = new Button("Go!");
    
        // drop down menu for different levels
        choiceBox = new ChoiceBox();
        choiceBox.getItems().add("Easy");
        choiceBox.getItems().add("Medium");
        choiceBox.getItems().add("Hard");
        
        
    }
    /**
     * Creates a path for an image
     * @param filepath the name of the .png file
     * @return fullpath the properly formatted filepath that the 
     * program can use 
     */
    public String pathImage(String filepath) {
        String fullpath = getClass().getResource(IMAGE_DIR+filepath+".png").toExternalForm();
        return fullpath;
    }
    

    /** Resizes an image
     * @param filepath a string representing where the image is stored
     * @param width the width of the resized image
     * @param height the height of the resized image
     * @return resized image's filepath 
     */
    public Image resizeImage(String filepath, int width, int height) {
        IMAGE.put(filepath, new Image(pathImage(filepath), width, height, false, false));
        return IMAGE.get(filepath);
    }
}
