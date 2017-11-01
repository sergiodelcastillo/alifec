package alifec.simulation.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Created by Sergio Del Castillo on 01/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class SimulationMain extends Application{
    public static void main(String[] args) {
        //this is a tests. It could be the implementation of the simulation view.
        System.out.println("run");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Simulation View");
        VBox root = new VBox();

        Canvas dishCanvas = new Canvas(300, 250);
        GraphicsContext dishGc = dishCanvas.getGraphicsContext2D();
        drawDish(dishGc);

        Canvas opponentsCanvas = new Canvas(300,500);

        GraphicsContext opponentsGc = opponentsCanvas.getGraphicsContext2D();

        drawOpponents(opponentsGc);

        root.getChildren().addAll(dishCanvas, opponentsCanvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void drawOpponents(GraphicsContext gc) {

        gc.setFill(Color.RED);
        gc.setStroke(Color.AQUA);
        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);

    }

    private void drawDish(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 200, 30, 30);
       /* gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);*/
    }
}
