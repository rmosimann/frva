import controller.MainController;
import javafx.application.Application;
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

    FrvaModel model = new FrvaModel();

    FXMLLoader root = new FXMLLoader(getClass().getResource("view/mainView.fxml"));
    root.setController(new MainController(model));
    primaryStage.setTitle(model.getApplicationName());
    primaryStage.setScene(new Scene(root.load()));
    primaryStage.show();
  }
}
