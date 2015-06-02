package main;


import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import model.GameModel;
import view.GameView;
import control.GameControl;
import control.GameStateManager;
import control.LedHandler;
import control.button.ButtonHandler;
import control.joystick.JoystickHandler;

public class Window extends JFrame {	
	private static final long serialVersionUID = -9222956702898533696L;
	public static boolean ON_RASP;
	
	public Window(boolean ON_RASP)
	{
		//Create window
		super("Arcade");
		setSize(1280, 1024);
				
		//Set window close listener
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		//Create Events
		LedHandler led = null;
		Window.ON_RASP = ON_RASP;
		
		if(ON_RASP){ //Only on the raspberry pi
			led = new LedHandler();
			GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] devices =  graphicsEnvironment.getScreenDevices();

			if (!devices[0].isFullScreenSupported ()){
			     throw new UnsupportedOperationException ("Fullscreen mode is unsupported.");
			}else{
				devices[0].setFullScreenWindow(this);
			}			
			//Remove cursor
			BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
			this.setCursor(blankCursor);
		}
		
		ButtonHandler bth = new ButtonHandler(led);
		JoystickHandler jsh = new JoystickHandler();
		
		//Create Instances
		GameStateManager gsm = new GameStateManager();
		GameView view = new GameView(led,gsm);
		GameModel model = new GameModel(view,gsm);
		GameControl control = new GameControl(model, view,gsm);
		setContentPane(view);
		
		//Create EventListeners
		if(!Window.ON_RASP){
			addKeyListener(bth);
			addKeyListener(jsh);
		}
		bth.addButtonListener(control);
		jsh.addJoystickListener(control);
		
		//Display
		pack();
		setVisible(true);
	}
}
