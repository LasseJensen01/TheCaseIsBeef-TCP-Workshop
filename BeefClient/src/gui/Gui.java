package gui;

import beef_commons.logic.*;
import beef_commons.utility.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.stage.StageStyle;
import networking.PlayerClient;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import static javafx.util.Duration.INDEFINITE;

public class Gui extends Application {
	public static PlayerClient pc;

	public static final int size = 30; 
	public static final int scene_height = size * 20 + 50;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right,hero_left,hero_up,hero_down;
	public static Event currentAction;
	public boolean running = true;

	
	/** The cells making up the maze */
	private static Label[][] cells;
	private static TextArea taScoreArea;








	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		Media mediaIntro = new Media(new File("Resources/Music/Game Intro music.mp3").toURI().toString());
		MediaPlayer player1 = new MediaPlayer(mediaIntro);
		player1.setVolume(0.25);
		player1.play();

		Media media = new Media(new File("Resources/Music/music.mp3").toURI().toString());
		MediaPlayer player2 = new MediaPlayer(media);
		player2.setVolume(0.25);
		player1.setOnEndOfMedia(() -> player2.play());
		player2.setCycleCount(MediaPlayer.INDEFINITE);


		try {

			initScene(primaryStage);


            // Putting default players on screen
			for (int i = 0; i < GameLogic.players.size(); i++) {
			  cells[GameLogic.players.get(i).getXpos()][GameLogic.players.get(i).getYpos()].setGraphic(new ImageView(hero_up));
			}
			// taScoreArea.setText(getTaScoreArea());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void initScene(Stage stage) throws Exception {


		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(0, 10, 0, 10));

		Text mazeLabel = new Text("Maze of Beef:");
		mazeLabel.setFont(Font.font("Impact", FontWeight.BOLD, 22));

		Text scoreLabel = new Text("Score:");
		scoreLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));

		taScoreArea = new TextArea();

		GridPane boardGrid = new GridPane();

		//Husk, folderen hedder Image(s)! nu, og URI'en findes ved skråstreg inden folderen. Altså, /Images.blabla.png
		image_wall  = new Image(getClass().getResourceAsStream("/Images/wall4.png"),size,size,false,false);
		image_floor = new Image(getClass().getResourceAsStream("/Images/floor1.png"),size,size,false,false);

		hero_right  = new Image(getClass().getResourceAsStream("/Images/heroRight.png"),size,size,false,false);
		hero_left   = new Image(getClass().getResourceAsStream("/Images/heroLeft.png"),size,size,false,false);
		hero_up     = new Image(getClass().getResourceAsStream("/Images/heroUp.png"),size,size,false,false);
		hero_down   = new Image(getClass().getResourceAsStream("/Images/heroDown.png"),size,size,false,false);

		cells = new Label[20][20];
		for (int j = 0; j < 20; j++) {
			for (int i = 0; i < 20; i++) {
				cells[i][j] = switch (Generel.board[j].charAt(i)) {
					case 'w' -> new Label("", new ImageView(image_wall));
					case ' ' -> new Label("", new ImageView(image_floor));
					default -> throw new Exception("Illegal field value: " + Generel.board[j].charAt(i));
				};
				boardGrid.add(cells[i][j], i, j);
			}
		}
		taScoreArea.setEditable(false);



		grid.add(mazeLabel,  0, 0);
		grid.add(scoreLabel, 1, 0);
		grid.add(boardGrid,  0, 1);
		grid.add(taScoreArea,  1, 1);

		Scene scene = new Scene(grid, scene_width, scene_height);
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.show();

		scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
				case UP -> {
					// playerMoved(0, -1, "up"); //Too bee removed
					currentAction = event;
					pc.declareAction("moveUp");
				}
				case DOWN -> {
					// playerMoved(0, +1, "down");
					currentAction = event;
					pc.declareAction("moveDown");
				}
				case LEFT -> {
					// playerMoved(-1, 0, "left");
					currentAction = event;
					pc.declareAction("moveLeft");
				}
				case RIGHT -> {
					// playerMoved(+1, 0, "right");
					currentAction = event;
					pc.declareAction("moveRight");
				}
				case SPACE ->  {
					// playerAttack();
					currentAction = event;
					pc.declareAction("Shoot");
				}
				case ESCAPE -> {
					pc.declareAction("Quit");
					System.exit(0);
					running = false;
				}
				default -> {}
			}
		});
	}
	
	public static void removePlayerOnScreen(PosXY oldPos) {
		Platform.runLater(() -> {cells[oldPos.getX()][oldPos.getY()].setGraphic(new ImageView(image_floor));});
	}
	
	public static void placePlayerOnScreen(PosXY newPos, String facingDir) {
		Platform.runLater(() -> {
			int newX = newPos.getX();
			int newY = newPos.getY();
			if (facingDir.equals("right")) {
				cells[newX][newY].setGraphic(new ImageView(hero_right));
			};
			if (facingDir.equals("left")) {
				cells[newX][newY].setGraphic(new ImageView(hero_left));
			};
			if (facingDir.equals("up")) {
				cells[newX][newY].setGraphic(new ImageView(hero_up));
			};
			if (facingDir.equals("down")) {
				cells[newX][newY].setGraphic(new ImageView(hero_down));
			};

		});

	}
	
	public static void movePlayerOnScreen(PosXY oldpos, PosXY newpos, String direction)
	{
		removePlayerOnScreen(oldpos);
		placePlayerOnScreen(newpos,direction);
	}


	public static void updateScoreTable()
	{
		Platform.runLater(() -> {
			taScoreArea.setText(getTaScoreArea());
			});
	}
	
	public static String getTaScoreArea() {
		StringBuffer str = new StringBuffer(100);
		//String r = "";
		for (Player p : GameLogic.players){
			str.append(p.toString() + "\n");
			//r += p.toString() + "\n";
		}
		return str.toString();
	}
}

