package angryflappybird;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font; 
import javafx.scene.text.FontPosture; 
import javafx.scene.text.FontWeight; 
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.Random;
import java.util.stream.IntStream;

import javafx.beans.value.*;


//The Application layer
/**
 * Team Angycats
 * The game has a user control a bird (cat in our case) to avoid all obstacles 
 * (including pipes, floors and rats) while collecting as many eggs as possible. 
 * The user uses the start button to control the cat's flight
 */
public class AngryFlappyBird extends Application {
    
    private Defines DEF = new Defines();
    
    // create label for scores and lives
    Text scoreLabel;
    Text livesLabel;
	
	
    // time related attributes
    private long clickTime, startTime, st, elapsedTime;   
    private AnimationTimer timer;
    
    // game components
    private Sprite blob;
    private ArrayList<Sprite> floors;
    private ArrayList<Sprite> upperPipes;
    private ArrayList<Sprite> lowerPipes;
    private ArrayList<Sprite> rats;
    private ArrayList<Sprite> fishes;
    private ArrayList<Sprite> yarns;
    
    
    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER;
    
    // scene graphs
    private Group gameScene;     // the left half of the scene
    private Group gameControl;   // the right half of the GUI (control)
    private GraphicsContext gc;     

    
    // Backgrounds
    ImageView background = DEF.IMVIEW.get("background");
    ImageView night = DEF.IMVIEW.get("night");
    
    int tracker;
    boolean pipeCollision;
    
    // Autopilot variables
    boolean fishCollided;
    double doubleAutopilotInterval = 6 * DEF.SEC_TO_NANOSEC;
    long longAutopilotInterval = Math.round(doubleAutopilotInterval);
    long autopilotTimeTracker = System.nanoTime() - longAutopilotInterval;
    

    // the mandatory main method 
    /**
     * Necessary for JavaFX
     * @param args none
     */
    public static void main(String[] args) {
        launch(args);
    }
     
    
    
    // the start method sets the Stage layer
    /**
     * Initializes scene graphs and UIs, adds the scene graphs to 
     * the scene, and shows the stage
     * @param primaryStage a JavaFX class object
     * @throws Exception if start fails
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // initialize scene graphs and UIs
        resetGameControl();    // resets the gameControl
        resetGameScene(true);  // resets the gameScene
        
        HBox root = new HBox();
        HBox.setMargin(gameScene, new Insets(0,0,0,15));
        root.getChildren().add(gameScene);
        root.getChildren().add(gameControl);
        
        // add scene graphs to scene
        Scene scene = new Scene(root, DEF.APP_WIDTH, DEF.APP_HEIGHT);
        
        // finalize and show the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle(DEF.STAGE_TITLE);
        primaryStage.setResizable(false);
        primaryStage.show();
       
         
    }
    
    
    
    // the getContent method sets the Scene layer
    /**
     * Sets up start button, creates instruction labels with images, creates 
     * choiceBox with difficulty ratings, and adds all of them to gameControl
     */
    public void resetGameControl() {
        
        
        DEF.startButton.setOnMouseClicked(this::mouseClickHandler);
        

        // create instruction labels
        Text pointsLabel = new Text();
        pointsLabel.setText(String.valueOf("Bonus points"));
        pointsLabel.setX(70);
        pointsLabel.setY(200);
        pointsLabel.setFont(Font.font("comic sans ms", FontPosture.REGULAR, 15));
        pointsLabel.setFill(Color.BLACK);
        
        Text snoozeLabel = new Text();
        snoozeLabel.setText(String.valueOf("Lets you snooze"));
        snoozeLabel.setX(70);
        snoozeLabel.setY(280);
        snoozeLabel.setFont(Font.font("comic sans ms", FontPosture.REGULAR, 15));
        snoozeLabel.setFill(Color.BLACK);
        
        Text avoidLabel = new Text();
        avoidLabel.setText(String.valueOf("Avoid rats"));
        avoidLabel.setX(70);
        avoidLabel.setY(360);
        avoidLabel.setFont(Font.font("comic sans ms", FontPosture.REGULAR, 15));
        avoidLabel.setFill(Color.BLACK);
        
        // add images to instructions
        ImageView yarn = DEF.IMVIEW.get("yarn");
        yarn.setFitHeight(70);
        yarn.setFitWidth(50);
        yarn.setX(5);
        yarn.setY(160);
        
        ImageView fish = DEF.IMVIEW.get("Fish");
        fish.setFitHeight(100);
        fish.setFitWidth(60);
        fish.setX(0);
        fish.setY(250);
        
        ImageView rat = DEF.IMVIEW.get("Rat");
        rat.setFitHeight(70);
        rat.setFitWidth(50);
        rat.setX(0);
        rat.setY(310);
        
        // set choiceBox position
        DEF.choiceBox.setLayoutX(0);
        DEF.choiceBox.setLayoutY(40);
        
        gameControl = new Group();        

        gameControl.getChildren().addAll(DEF.startButton, DEF.choiceBox, pointsLabel, snoozeLabel, avoidLabel, yarn, fish, rat);
        
    
    }
    
    /**
     * Sets booleans for whether the game has started, tracks clickTime if it has,
     * and says that the mouse has been clicked when it has
     * @param e the JavaFX object that represents the mouse clicking
     * the start button
     */
    private void mouseClickHandler(MouseEvent e) {
        if (GAME_OVER) {
            resetGameScene(false);
        }
        else if (GAME_START){
            clickTime = System.nanoTime();   
        }
        GAME_START = true;
        CLICKED = true;
    }
    
    /**
     * Resets booleans relating to the start button, initializes 
     * the "moving" images(floor, rats, etc.), creates the score
     * and lives labels, initializes the bird, and initializes the 
     * timer
     * @param firstEntry a boolean that identifies whether the game
     * has been run
     */
    private void resetGameScene(boolean firstEntry) {
        
        // reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        floors = new ArrayList<>();
        upperPipes = new ArrayList<>();
        lowerPipes = new ArrayList<>();
        fishes = new ArrayList<>();
        rats = new ArrayList<>();
        yarns = new ArrayList<>();   
        fishCollided = false;

        // create a score label
        if(firstEntry) {
            scoreLabel = new Text();
            scoreLabel.setText(String.valueOf(DEF.score));
            scoreLabel.setX(10);
            scoreLabel.setY(50);
            scoreLabel.setFont(Font.font("comic sans ms", FontWeight.BOLD, FontPosture.REGULAR, 40));
            scoreLabel.setFill(Color.YELLOW);
            scoreLabel.setStrokeWidth(2);
            scoreLabel.setStroke(Color.BLACK);
        }
        
        
        // create a lives label
        if (firstEntry) {
        livesLabel = new Text();
        livesLabel.setText(String.valueOf(DEF.lives) + " lives left");
        livesLabel.setX(270);
        livesLabel.setY(530);
        livesLabel.setFont(Font.font("comic sans ms", FontWeight.BOLD, FontPosture.REGULAR, 20));
        livesLabel.setFill(Color.BLACK);
        }

       

        
        if(firstEntry) {
            // create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();


            
            
            // create the game scene
            gameScene = new Group();
            gameScene.getChildren().addAll(background, canvas, scoreLabel, livesLabel);
            tracker = 0;

        }
        
        // initialize floor
        for(int i=0; i<DEF.FLOOR_COUNT; i++) {
            
            int posX = i * DEF.FLOOR_WIDTH;
            int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
            
            Sprite floor = new Sprite(posX, posY, DEF.IMAGE.get("floor"));
            floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            floor.render(gc);
            
            floors.add(floor);
        }
        
        
        // initialize pipes
        // upperPipes
        for (int i = 0; i < DEF.PIPE_COUNT; i++) {
            
            int posX = (i+1) * 180;
            int posY = 0;
            
            Sprite pipe = new Sprite(posX, posY, DEF.IMAGE.get("pipe2"));
            pipe.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            pipe.render(gc);
            
            upperPipes.add(pipe);
        }
            
        // lowerPipes
        for (int i = 0; i < DEF.PIPE_COUNT; i++) {
            
            int posX = (i+1) * 180;
            int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT - DEF.PIPE_HEIGHT + 8;
            
            Sprite pipe = new Sprite(posX, posY, DEF.IMAGE.get("pipe1"));
            pipe.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            pipe.render(gc);
            
            lowerPipes.add(pipe);
        
        }
        
        // initialize rats   
        for (int i=0; i<DEF.RAT_COUNT; i++) {
            
            int posX = (i+1) * DEF.RAT_POS_X;
            int posY = DEF.RAT_POS_Y;
                       
            Sprite rat = new Sprite(posX, posY,DEF.IMAGE.get("Rat"));
            rat.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            rat.render(gc);
            
            rats.add(rat);
        }
        

        // initialize fishes
        for (int i=0; i<DEF.FISH_COUNT; i++) {
            
            int posX = ((i+1) * 720)-35;
            int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT - DEF.PIPE_HEIGHT - 40;
            
            Sprite fish = new Sprite(posX, posY, DEF.IMAGE.get("Fish"));
            fish.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            fish.render(gc);
            
            fishes.add(fish);
        }
        

        // initialize yarn
        for (int i=0; i<DEF.YARN_COUNT; i++) {
            
            int posX = (i+1) * 540;
            int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT - DEF.PIPE_HEIGHT - 60;
            
            Sprite yarn = new Sprite(posX, posY, DEF.IMAGE.get("yarn"));
            yarn.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            yarn.render(gc);
            
            yarns.add(yarn);
        }

        
        // initialize blob
        blob = new Sprite(DEF.BLOB_POS_X, DEF.BLOB_POS_Y,DEF.IMAGE.get("blob0"));
        blob.render(gc);
        
        
        // initialize timer
        startTime = System.nanoTime();
        st = System.nanoTime();
        timer = new MyTimer();
        timer.start();
    }

    //timer stuff
    
    /**
     * Handles the timing related functions of the game
     */
    class MyTimer extends AnimationTimer {

        
        int counter = 0;
              
         /**
         * Initializes elapsedTime and startTime, clears the scene,
         * and if the game has started, calls all of the methods
         * relating to the functionality of "moving" objects and changes
         * the background periodically
         * @param now or System.nanoTime()
         */
        
         int time = 0;
         
         @Override
         public void handle(long now) {          
             // time keeping
             elapsedTime = now - startTime;
             startTime = now;
             
             time ++;
             if(time%90 == 0) {
                 DEF.score ++;
                 scoreLabel.setText(String.valueOf(DEF.score));
             }
             // clear current scene
             gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);

             if (GAME_START) {
                 // step1: update floor
                 moveFloor();
                 
                 // step2: update pipes
                 moveUpperPipe();
                 moveLowerPipe();
                 
                 // step3: drop rats
                 moveRats();
               
                 // step4: introduce fishes
                 moveFishes();
                 
                 // step5: introduce yarns
                 moveYarns();
                 
                 livesLabel.setText(DEF.lives + " lives left");
                 
                 
                 // Switches background from day to night
                 long switchTime = 500;
                 long halfSwitchTime = switchTime/2;
                 int backCounter = 0;


                if((startTime - st)% switchTime == 0) {
                    background.setImage(DEF.IMAGE.get("night"));
                    backCounter ++;
                } 
                if(((startTime - st) + halfSwitchTime) % switchTime == 0) {
                    background.setImage(DEF.IMAGE.get("background"));
                    backCounter++;
                }
                if(counter == 2) {
                    st = now;
                    backCounter = 0;
                }
               
                
                // Changes difficulty level by making the fly velocity higher
               String level[] = {"Easy", "Medium", "Hard"};
	            DEF.choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
	              public void changed(ObservableValue ov, Number value, Number new_value)
	              {
	                  if(level[new_value.intValue()] == "Easy") {
	                      tracker = 1;
	                  } else if(level[new_value.intValue()] == "Medium") {
	                      tracker = 2;
	                  } else if(level[new_value.intValue()] == "Hard") {
	                      tracker = 3;
	                  }
	              }
	          });
	            
	          if(tracker == 1 | tracker == 0) {
	              moveBlob(DEF.BLOB_FLY_VEL);
              } else if(tracker == 2 ) {
                  moveBlob(DEF.BLOB_FLY_VEL+10);
              } else if (tracker == 3 ) {
                  moveBlob(DEF.BLOB_FLY_VEL+20);
              } 
	          
	          
              if(fishCollided == false) {
                checkCollision();
              }
                 
             }
         }
         
         // step1: update floor
         /**
          * Moves floor by moving through the floors arrayList
          */
         private void moveFloor() {
            
            for(int i=0; i<DEF.FLOOR_COUNT; i++) {
                if (floors.get(i).getPositionX() <= -DEF.FLOOR_WIDTH) {
                    double nextX = floors.get((i+1)%DEF.FLOOR_COUNT).getPositionX() + DEF.FLOOR_WIDTH;
                    double nextY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
                    floors.get(i).setPositionXY(nextX, nextY);
                }
                floors.get(i).render(gc);
                floors.get(i).update(DEF.SCENE_SHIFT_TIME);
            }
         }
         
         
         // step2: update blob
         /**
          * Moves blob and handles autopilot
          */
         private void moveBlob(int velocity) {
             
            long diffTime = System.nanoTime() - clickTime;
            
            // blob flies upward with animation
            if (CLICKED && diffTime <= DEF.BLOB_DROP_TIME) {
                
                int imageIndex = Math.floorDiv(counter++, DEF.BLOB_IMG_PERIOD);
                imageIndex = Math.floorMod(imageIndex, DEF.BLOB_IMG_LEN);
                blob.setImage(DEF.IMAGE.get("blob"+String.valueOf(imageIndex)));
                blob.setVelocity(0, velocity);
            }
            // Handles autopilot case
            else if(fishCollided) {
                long additionInterval = Math.round(50);
                if(autopilotTimeTracker < System.nanoTime()) {
                    blob.setPositionXY(DEF.BLOB_POS_X, DEF.BLOB_POS_Y);
                    
                    autopilotTimeTracker = autopilotTimeTracker + (System.nanoTime() - autopilotTimeTracker) + additionInterval;
                    
                }
                autopilotTimeTracker = autopilotTimeTracker + (System.nanoTime() - autopilotTimeTracker) + additionInterval;
                if(autopilotTimeTracker >= System.nanoTime()) {
                    fishCollided = false;
                }
                
                
            }
            // blob drops after a period of time without button click
            else {
                blob.setVelocity(0, DEF.BLOB_DROP_VEL); 
                CLICKED = false;
            }

            // render blob on GUI
            blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
            blob.render(gc);
            
         }
         

         
         // update pipes
         private void moveUpperPipe() {
             for (Sprite pipe : upperPipes) {
                 pipe.setVelocity(-DEF.PIPE_VEL, 0);
                 pipe.update(elapsedTime * DEF.NANOSEC_TO_SEC);
                 pipe.render(gc);
             }
         }
       
    	 private void moveLowerPipe() {

             for (Sprite pipe : lowerPipes) {
                 pipe.setVelocity(-DEF.PIPE_VEL, 0);
                 pipe.update(elapsedTime * DEF.NANOSEC_TO_SEC);
                 pipe.render(gc);
             }
         }

         
         
         // update rats
         private void moveRats() {
             for (Sprite rat : rats) {
                 rat.setVelocity(-DEF.PIPE_VEL, 300);
                 if (rat.getPositionX() <= -DEF.FLOOR_WIDTH + 180) {
                     double nextX = rat.getPositionX() + 360 ;
                     double nextY = 0;
                     rat.setPositionXY(nextX, nextY);
                 }
                 rat.update(elapsedTime * DEF.NANOSEC_TO_SEC);
                 rat.render(gc); 
             }
          }
          
         // update fishes
         private void moveFishes() {

             for (Sprite fish : fishes) {
                 fish.setVelocity(-DEF.FISH_VEL, 0);
                 fish.update(elapsedTime * DEF.NANOSEC_TO_SEC);
                 fish.render(gc);
             }         
          }
          
          // update yarns
          private void moveYarns() {
              for (Sprite yarn : yarns) {
                  yarn.setVelocity(-DEF.YARN_VEL, 0);
                  yarn.update(elapsedTime * DEF.NANOSEC_TO_SEC);
                  yarn.render(gc);
              }
             
          }
          
         
         /**
          * Checks all collisions between objects and performs 
          * necessary functions if they are collided
          */
         public void checkCollision() {
             
            // check collision  
            for (Sprite floor: floors) {
                GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);
            }
            
            for (Sprite pipe : upperPipes) {                  
                if (blob.intersectsSprite(pipe)) {
                    pipeCollision = true;
                }
                GAME_OVER = GAME_OVER || blob.intersectsSprite(pipe);
            }
            
            for (Sprite pipe : lowerPipes) {
                if (blob.intersectsSprite(pipe)) {
                    pipeCollision = true;
                }
                GAME_OVER = GAME_OVER || blob.intersectsSprite(pipe);          
            }  
            
            // decrease lives when cat collides with pipe
            if (pipeCollision == true & DEF.lives > 0) {
                DEF.lives = DEF.lives - 1;
                pipeCollision = false;
            }
            
	    // reset lives to 3 
            if (pipeCollision == true & DEF.lives == 0) {
                GAME_OVER = true;
                DEF.lives = 3;
            }
            
            for(Sprite r : rats) {
                GAME_OVER = GAME_OVER || blob.intersectsSprite(r); 
            }
            
            for(Sprite fish : fishes) {
                if(blob.intersectsSprite(fish)) {
                    fishCollided = true;
                } 
            }
          //if rat hits yarn DEF.score -= 5
            for (Sprite yarn : yarns) {
                for (Sprite rat: rats) {
                    if(rat.intersectsSprite(yarn)) {
                        if (rat.getCollided() == true) {
                            scoreLabel.setText(String.valueOf(DEF.score));
                        }
                        else if(rat.getCollided() == false){
                             DEF.score -= 2;
                             System.out.println(DEF.score);
                             scoreLabel.setText(String.valueOf(DEF.score));
                            
                         }
                        rat.setCollided(true);
                        
            }}}
            
        
            //if blob hits yarn +=5
            for (Sprite Yarn : yarns) {
                  if(blob.intersectsSprite(Yarn)) {
                        if (Yarn.getCollided() == true) {
                            scoreLabel.setText(String.valueOf(DEF.score));
                        }
                        else if(Yarn.getCollided() == false){
                             DEF.score += 5;
                             System.out.println(DEF.score);
                             scoreLabel.setText(String.valueOf(DEF.score));
                            
                         }
                        Yarn.setCollided(true);
                    
                    }
                
                  }
            // If rat hits yarn, yarn "disappears" and score decreases
            for(Sprite ra : rats) {
                for(Sprite yarn : yarns) {
                    if(ra.intersectsSprite(yarn) ) {
                        DEF.score = DEF.score -3;
                        scoreLabel.setText(String.valueOf(DEF.score));
                        yarn.setPositionXY(-50, -50);
                    } 
                    
                    
                }
                
            }
            
            // end the game when blob hit stuff
            if (GAME_OVER) {
                showHitEffect(); 
                for (Sprite floor: floors) {
                    floor.setVelocity(0, 0);
                }
                
                timer.stop();
                
            }
            
         }
         

         
         /**
          * Shows effect for when the bird hits something
          */
         private void showHitEffect() {
            ParallelTransition parallelTransition = new ParallelTransition();
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(DEF.TRANSITION_TIME), gameScene);
            fadeTransition.setToValue(0);
            fadeTransition.setCycleCount(DEF.TRANSITION_CYCLE);
            fadeTransition.setAutoReverse(true);
            parallelTransition.getChildren().add(fadeTransition);
            parallelTransition.play();
         }
         
    } // End of MyTimer class

} // End of AngryFlappyBird Class
