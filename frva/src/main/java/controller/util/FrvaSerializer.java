package controller.util;

import controller.util.TreeviewItems.FrvaTreeItem;
import controller.util.TreeviewItems.FrvaTreeRootItem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.FrvaModel;

/**
 * Created by patrick.wigger on 03.11.17.
 */
public class FrvaSerializer {


  public static void serializeDB(TreeView treeView) {
    System.out.println("DB serialize");

    File file = new File(FrvaModel.LIBRARYPATH + File.separator + FrvaModel.TREESTRUCTURE);
    try {
      Writer writer = Files.newBufferedWriter(Paths.get(file.toURI()));
      for (Object object : treeView.getRoot().getChildren()) {
        serializeDB((TreeItem) object, writer);
      }
      writer.close();
    } catch (IOException ex) {

    }
  }

  private static void serializeDB(TreeItem item, Writer writer) throws IOException {
    writer.write(((FrvaTreeItem) item).serialize() + "\n");
    writer.flush();
    if (!item.isLeaf()) {
      for (Object child : item.getChildren()
          ) {
        serializeDB((TreeItem) child, writer);

      }
    }
  }

  /**
   * @param treeView the treeview the Elements are attached to
   */
  public static void deserializeDB(TreeView<FrvaTreeRootItem> treeView, String filepath, FrvaModel model) {

    FrvaTreeItem currentItem = (FrvaTreeRootItem) treeView.getRoot();
    String path = filepath;
    File structure = new File(path);
    System.out.println(structure.getAbsolutePath());
    String line = "";
    try (BufferedReader br = new BufferedReader(new FileReader(structure))) {
      while ((line = br.readLine()) != null) {

        String[] arr = line.split(";");
        FrvaTreeItem temp =  FrvaTreeItem.createTreeItem(arr, model);
        int depthDifference = temp.getDepth() - currentItem.getDepth();

        if (depthDifference == 0) {
          currentItem.getParent().getChildren().add(temp);
        }

        if (depthDifference == 1) {
          currentItem.getChildren().add(temp);
          currentItem = temp;
        }
        if (depthDifference < 0) {
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

    treeView.setShowRoot(false);

  }

}
