package gui;

import beef_commons.logic.*;
import javafx.application.Application;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import gui.Gui;
import javafx.event.Event;
import logic.*;

public class App {
	public static Player me;
	public static void main(String[] args) throws Exception{	
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Indtast spillernavn");
		String navn = "Jønke"; //inFromUser.readLine();
		me = GameLogic.makePlayer(navn);
		GameLogic.makeVirtualPlayer(); // to be removed
		Application.launch(Gui.class);
	}
}
