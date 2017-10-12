
Readme First
============
This is FHNW project-repository IP5 FloX RoX of Roland Mosimann and Patrick Wigger. This document contains all necessary information for working with the repository.
[Link](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet) to github Readme formatting.

# Architecture

## Environment 
The applications target OS is Linux. 

## Model
We follow the Model-View-Controller (MVC) approach. The model contains the data which are measurements of a FloX/RoX spectroanalysis device.
Data is stored within a specified folder as copy of an SD-Card. The application loads all contained folders of the specified folder into the application.

## View
We use SceneBuilder to generate FXML files as views. Each GUI element contains a unique FX-ID and can be accessed over this ID within the code.

## Controller
Each View has a seperate Controller which acts as a connection between View and Model. The controller contains the logic and all functions which can be accessed over the view. While being initialised the controller gets the model. 

## Model
The model contains a List of SdCards (library). Each SdCard has its Calibration-file.
An SdCard contains * Datafiles. A Datafile contains * MeasureSequences. 


## Code Structure
      frva
      |-src
        |-main
          |-java
            |-controller        > Put all conrtollers here
            |-model             > Contain the model where al data are stored
            |-FrvaApp.java      > Main Application
          |-resources
            |-css               
            |-icons
            |-images
            |-view              > Put all FXML-Files here
        |- build.gradle         > builds configuration
        |- gradlew              > gradle-wrapper to build the project


# Technologies
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
| Documentation: JavaDoc           |           |


# Build information
Precondition: install Oracle JDK 1.8.0_144

Build:
```shell
git clone https://gitlab.fhnw.ch/IP56/floxrox.git
cd frva
./gradlew assemble
```    
 
# Coding Conventions
We follow Google Java Code Style Guidelines which can be found under [Styleguide](https://google.github.io/styleguide/javaguide.html)
[How to install](https://medium.com/@jayanga/how-to-configure-checkstyle-and-findbugs-plugins-to-intellij-idea-for-wso2-products-c5f4bbe9673a)
 Checkstyle Plugin 
The configuration file can be found under config/checkstyle/checkstyle.xml



# Getting Started

These views are loaded the following way:
```java
FXMLLoader root = new FXMLLoader(getClass().getResource("view/mainView.fxml"));
root.setController(new MainController(model));
primaryStage.setTitle(model.getApplicationName());
primaryStage.setScene(new Scene(root.load()));
primaryStage.show();
```


# License

 This file is part of FloX-RoX Visualization Application (FRVA).

 FRVA is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 FRVA is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with FRVA. If not, see <http://www.gnu.org/licenses/>.
