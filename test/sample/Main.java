package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(final Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
      primaryStage.setTitle("AxxG Fullscreen");



      StackPane sp = new StackPane();

      // Button
      Button btn = new Button();
      btn.setText("Vollbild bitte!");

      // Action des Buttons
      btn.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          //++++++++++++++++++++++++++++++++
          // Full Screen Modus!!!
          //++++++++++++++++++++++++++++++++
          primaryStage.setFullScreen(true);
        }
      });
      primaryStage.setFullScreen(true);

      // Button dem Panel adden
      sp.getChildren().add(btn);

      Scene scene = new Scene(sp);
      primaryStage.setScene(scene);
      primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
