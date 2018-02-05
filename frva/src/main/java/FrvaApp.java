/*
 *     This file is part of FRVA
 *     Copyright (C) 2018 Andreas Hüni
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import controller.MainMenuController;
import controller.util.bluetooth.BluetoothConnection;
import controller.util.bluetooth.ConnectionStateSearching;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.FrvaModel;

/**
 * FrvaApp is the starter class for this application.
 * It creates the model used in this application and initializes the MainMenuController.
 */
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
