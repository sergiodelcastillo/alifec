package view.main;

import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.controller.ALifeCJFXController;

import java.io.File;

public class ALifeCJFX extends Application {
    private String defaultPath;

    @Override
    public void start(Stage primaryStage) throws Exception{

        if(!view.controller.ALifeCJFXController.loadConfiguration()){

            Stage s = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/NewConfig.fxml"));
            s.setTitle("New config ");
            s.setScene(new Scene(root, 300, 275));
            s.showAndWait();
            System.out.println("a ver: " + new File("").getAbsolutePath());


        }

        Parent root = FXMLLoader.load(getClass().getResource("/ALifeCJFX.fxml"));
        primaryStage.setTitle("Welcome to Artificial Life Contest ");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
