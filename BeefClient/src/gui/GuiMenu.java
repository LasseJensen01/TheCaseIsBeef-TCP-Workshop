package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GuiMenu extends Application {
    public static final int size = 30;
    public static final int scene_height = size * 20 + 50;
    public static final int scene_width = size * 20 + 200;
    public static ListView<String> serverList = new ListView<>();
    public static TextField txtfName = new TextField();
    public static Button bttJoin = new Button("Join");
    @Override
    public void start(Stage stage) throws Exception {
        initScene(stage);
    }
    private void initScene(Stage stage) throws Exception {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));

        grid.add(new Label("ServerList"),0,0);
        grid.add(serverList,0,1);
        serverList.setPrefWidth(500);
        GridPane.setRowSpan(serverList,4);
        grid.add(new Label("Player name"), 1,1);
        grid.add(txtfName,1,2);
        grid.add(bttJoin,1,3);



        Scene scene = new Scene(grid, scene_width, scene_height);
        stage.setScene(scene);
        stage.show();
    }
}
