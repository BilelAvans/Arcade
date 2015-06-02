package model.gameState;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.GameModel;
import model.drawObjects.Bullet;
import model.drawObjects.Enemy;
import model.drawObjects.Player;
import model.objects.InfoPanel;
import model.objects.PlayArea;
import control.GameStateManager;
import control.button.ButtonEvent;
import control.joystick.JoystickEvent;

public class PlayState extends GameState{

	public static final Rectangle2D borderRect = new Rectangle2D.Double(256, 0, 1024, 1024);
	private PlayArea area;
	private InfoPanel infoPanel;
	private Player player;
	private List<Enemy> enemys;	
	private List<Bullet> bullets;
	private Iterator<Enemy> enemyIterator;
	private Iterator<Bullet> bulletIterator;

	public static int currentScore = 0; 
	public static int lifePoints = 100;
	
	public PlayState(GameStateManager gsm) {
		super(gsm);
		area = new PlayArea((int) borderRect.getX(),1024,1024,100);
		infoPanel = new InfoPanel(0, 0);
		enemys = new ArrayList<Enemy>();
		bullets =new ArrayList<Bullet>();
		player = new Player(1280-1024+1024/2, 1024/2);
		for(int i = 0; i < 8; i++){
			Line2D line = area.getLine(i);
			addEnemy(line, Color.RED, 200);
		}
		
	}

	@Override
	public void init() {
		
		
	}

	@Override
	public void update() {		
		player.update();	
		bulletIterator = bullets.iterator();
		while(bulletIterator.hasNext()){
			Bullet b = bulletIterator.next();

			//als de bullet de border raakt verwijder hem, anders update je hem
			if(!borderRect.intersectsLine(b.getBullet())){
				bulletIterator.remove();
				break;
			}else{
				b.update();
			}
		}
		
		enemyIterator = enemys.iterator();
		while(enemyIterator.hasNext()){
			
			Enemy e = enemyIterator.next();
			e.update();
			//als de enemy de octagon raakt verwijder hem
			if(area.octagon.intersects(e.getCircle().getBounds2D())){
				lifePoints -= 5;
				
				if(lifePoints < 10){
					lifePoints += 10;	//Dit is voor te testen.
				}
				enemyIterator.remove();
			}
			
			//nu kijken of je de enemy  raakt met een bullet
			else{				
				bulletIterator = bullets.iterator();
				
				while(bulletIterator.hasNext()){
					Bullet b = bulletIterator.next();
					
					//kijkt of de enemy een bullet tegen komt, zoja verwijder de bullet, zoniet update de enemy.
					if(e.bulletHitMe(b)){											
						bulletIterator.remove();
						
						//kijkt of de bullet die de enemy heeft gehit, ook dezelfde kleur heeft als de enemy, zoja verwijder de enemy
						if(e.ColorHitMe(b)){
							
						currentScore += 1;
						if(lifePoints < 100) {
							lifePoints += 5;
						}							
							enemyIterator.remove();
							break;
						}					
					}							
				}				
			}
			infoPanel.updateIPanel();
		}	
		

		while(enemys.size() < 8){
			int index = (int)(Math.random()*8);
			int color = (int)(Math.random()*GameModel.colors.length);
			Line2D line = area.getLine(index);
			addEnemy(line,GameModel.colors[color],200);
		}		
	}

	@Override
	public void draw(Graphics2D g2) {	
		try{
			infoPanel.draw(g2);
			g2.setClip(borderRect);	
			area.draw(g2);		
			
			if(enemys != null){
				for(Enemy enemy : enemys){
					enemy.draw(g2);
				}
			}		

			g2.setStroke(new BasicStroke(5));
			for(Bullet b : bullets){
				b.draw(g2);
			}

			if(player != null)
				player.draw(g2);
		}catch(Exception e){};
		
	}
	
	

	@Override
	public void buttonPressed(ButtonEvent e) {	
		if(e.getButton().getButtonID() != 0){
			addBullet(GameModel.colors[e.getButton().getButtonID()-1],player.getIndex());
		}
	}

	@Override
	public void buttonReleased(ButtonEvent e) {		
	}

	@Override
	public void onJoystickMoved(JoystickEvent e) {	
		switch(e.getJoystick().getPos()){
		case CENTER:
			break;
		case DOWN:
			player.setIndex(4);
			break;
		case DOWN_LEFT:
			player.setIndex(5);
			break;
		case DOWN_RIGHT:
			player.setIndex(3);
			break;
		case LEFT:
			player.setIndex(6);
			break;
		case RIGHT:
			player.setIndex(2);
			break;
		case UP:
			player.setIndex(0);
			break;
		case UP_LEFT:
			player.setIndex(7);
			break;
		case UP_RIGHT:
			player.setIndex(1);
			break;
		default:			
			break;
		}
	}

	public void addEnemy(Line2D path,Color c,double speed){		
		enemys.add(new Enemy(path,c,20,speed));	
	}
	
	public void addBullet(Color c,int index){		;
		bullets.add(new Bullet(10, c, 10, index,area.paths.get(index)));		
	}
	
	public void removeBullet(Bullet bullet){
		bullets.remove(bullet);
	}

}
