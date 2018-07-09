package alifec.simulation.sumulation.test;

/**
 * Created by Sergio Del Castillo on 02/07/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

// An alternative implementation of Example 3,
//    using the Timeline, KeyFrame, and Duration classes.

// Animation of Earth rotating around the sun. (Hello, world!)
public class Example3T extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage theStage)
    {
        theStage.setTitle( "Timeline Example" );

        Group root = new Group();
        Scene theScene = new Scene( root );
        theStage.setScene( theScene );

        Canvas canvas = new Canvas( 512, 512 );
        root.getChildren().add( canvas );

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image earth = new Image( "test/earth.png" );
        Image sun   = new Image( "test/sun.png" );
        Image space = new Image( "test/space.png" );

        Timeline gameLoop = new Timeline();
        gameLoop.setCycleCount( Timeline.INDEFINITE );

        final long timeStart = System.currentTimeMillis();

        KeyFrame kf = new KeyFrame(
                Duration.seconds(0.002),                // 60 FPS
                new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent ae)
                    {
                        double t = (System.currentTimeMillis() - timeStart) / 1000.0;
                        System.out.println(t);
                        double x = 232 + 128 * Math.cos(t);
                        double y = 232 + 128 * Math.sin(t);

                        // Clear the canvas
                        gc.clearRect(0, 0, 512,512);

                        // background image clears canvas
                        gc.drawImage( space, 0, 0 );
                        gc.drawImage( earth, x, y );
                        gc.drawImage( sun, 196, 196 );
                    }
                });

        gameLoop.getKeyFrames().add( kf );
        gameLoop.play();

        theStage.show();
    }
}