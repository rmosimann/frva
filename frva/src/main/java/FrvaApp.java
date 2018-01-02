import controller.MainMenuController;
import controller.util.bluetooth.BluetoothConnection;
import controller.util.bluetooth.ConnectionStateSearching;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.FrvaModel;

public class FrvaApp extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    long time = System.currentTimeMillis();

    FrvaModel model = new FrvaModel();

    FXMLLoader root = new FXMLLoader(getClass().getResource("view/mainMenu.fxml"));
    root.setController(new MainMenuController(model));
    primaryStage.setTitle(model.getApplicationName());
    primaryStage.setScene(new Scene(root.load()));
    primaryStage.getScene().getStylesheets().add(getClass()
        .getResource("css/master.css").toExternalForm());
    primaryStage.show();
    primaryStage.setOnCloseRequest(event -> Platform.exit());
  }
}
