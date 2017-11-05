package controller.util;

import controller.util.TreeviewItems.FrvaTreeItem;
import controller.util.TreeviewItems.FrvaTreeRootItem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.FrvaModel;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaSerializer {

  private static final Logger logger = Logger.getLogger("FRVA");

  public static void serialize(TreeView treeView) {
    System.out.println("DB serialize");

    File file = new File(FrvaModel.LIBRARYPATH + File.separator + FrvaModel.TREESTRUCTURE);
    try {
      Writer writer = Files.newBufferedWriter(Paths.get(file.toURI()));
      for (Object object : treeView.getRoot().getChildren()) {
        serialize((TreeItem) object, writer);
      }
      writer.close();
    } catch (IOException ex) {

    }
  }

  public static void serializeImports(TreeView treeView) {
    File file = new File(FrvaModel.LIBRARYPATH + File.separator + FrvaModel.TREESTRUCTURE);
    File temp = new File(FrvaModel.LIBRARYPATH + File.separator + "temp" + FrvaModel.TREESTRUCTURE);


    try (BufferedReader br = new BufferedReader(new FileReader(file)); Writer wr = new FileWriter(temp)) {
      String root = br.readLine();
      wr.write(root + "\n");
      wr.flush();
      for (Object child : treeView.getRoot().getChildren()
          ) {
        ((FrvaTreeItem)child).setPathToLibrary();
        serialize((FrvaTreeItem) child, wr);
      }

      String line;
      while ((line = br.readLine()) != null) {
        wr.write(line + "\n");
        wr.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    file.renameTo(new File(file.getAbsoluteFile() + "beforeImport"));
    temp.renameTo(new File(FrvaModel.LIBRARYPATH + File.separator + FrvaModel.TREESTRUCTURE));

  }


  private static void serialize(TreeItem item, Writer writer) throws IOException {
    writer.write(((FrvaTreeItem) item).serialize() + "\n");
    writer.flush();
    if (!item.isLeaf()) {
      for (Object child : item.getChildren()
          ) {
        ((FrvaTreeItem)child).setPathToLibrary();
        serialize((TreeItem) child, writer);

      }
    }
  }

  /**
   * @param treeView the treeview the Elements are attached to
   */
  public static void deserializeDB(TreeView treeView, String filepath, FrvaModel model) {

    FrvaTreeItem currentItem = (FrvaTreeRootItem) treeView.getRoot();
    String path = filepath;
    File structure = new File(path);


    if (!structure.exists()) {
      try (Writer wr = new FileWriter(path)) {
        wr.write("TreeView Structure of FRVA-Application\n");
        wr.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    System.out.println(structure.getAbsolutePath());
    String line = "";
    try (BufferedReader br = new BufferedReader(new FileReader(structure))) {
      br.readLine();
      while ((line = br.readLine()) != null) {
        System.out.println(line);

        //System.out.println(line.substring(0, 10));
        String[] arr = line.split(";");
        FrvaTreeItem temp = FrvaTreeItem.createTreeItem(arr, model);
        int depthDifference = temp.getDepth() - currentItem.getDepth();
        //System.out.println("changed current item");
     //   System.out.println("depth diff=" + depthDifference + " temp " + temp.getDepth() + "/current " + currentItem.getDepth());

        //TODO what if difference > 0
        if (depthDifference == 0) {
          currentItem.getParent().getChildren().add(temp);
          System.out.println("here1");
        }

        if (depthDifference == 1) {
          currentItem.getChildren().add(temp);
          System.out.println("here2");
          currentItem = temp;
        }
        if (depthDifference < 0) {
          System.out.println("here3");
          while (depthDifference < 1) {
            currentItem = (FrvaTreeItem) currentItem.getParent();
            depthDifference++;
          }

        currentItem.getChildren().add(temp);
        currentItem = temp;

        }

      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    treeView.setShowRoot(true);

  }

}
