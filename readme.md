
Readme First
============
This is FHNW project-repository IP5 FloX RoX of Roland Mosimann and Patrick Wigger. This document contains all necessary information for working with the 

Architcture
============



Environnement: The applications target OS is Linux. 

Model
We follow the Model-View-Controller approach. The model contains the data which are measurements of a FloX/RoX spectroanalysis device.
Data is stored within a specified folder as copy of an SD-Card. The application loads all contained folders of the specified folder.

View
We use SceneBuilder to generate FXML files as views. Each GUI element contains a unique FX-ID and can be accessed over this ID within the code.

Controller
Each View has a seperate Controller which acts as a connection between View and Model. The controller contains the logic and all functions which can be accessed over the view. While being initialised the controller gets the model. 


List of Views
-mainView.fxml
-tabContent.fxml


List of Controllers
MainController
TabController

Model
The model contains a List of SdCards (library). Each SdCard has its Calibration-file.
An SdCard contains * Datafiles. A Datafile contains * MeasureSequences. 


Code Structure
============
Technologies
============
In this project we apply the following technologies:


| Technology                       | Version   |
|----------------------------------|-----------|
| Programming language: Java       | 1.8.0_144 |
| Build Tool: Gradle               |           |
| GUI-Editor: JavaFX Scene Builder | 8.3.0     |
| Code coverage: Jacoco            | 0.7.6     |
| Code quality: Checkstyle         | 8.2       |
| Testing: JUnit                   | 4.12      |
| CI: Gitlab CI Runner             |           |



Coding Conventions
============

Getting Started
============
These views are loaded the following way:
FXMLLoader root = new FXMLLoader(getClass().getResource("view/mainView.fxml"));
root.setController(new MainController(model));
primaryStage.setTitle(model.getApplicationName());
primaryStage.setScene(new Scene(root.load()));
primaryStage.show();
