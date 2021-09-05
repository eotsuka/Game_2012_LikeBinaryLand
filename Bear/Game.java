package myGame;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;



import jig.engine.FontResource;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.util.Vector2D;
import jig.engine.audio.jsound.*;



/**
 * A simple game.
 *  
 * @author Eriko Otsuka  
 * 
 */
public class Game extends StaticScreenGame {
	
	static final int WORLD_WIDTH = 800;
	static final int WORLD_HEIGHT = 600;
	static final int ENEMY_SPEED = 5;
	static final int TOTAL_BLOCK = 104;
	static final int TOTAL_MAZE = 58;
	static final int TOTAL_FUTON = 20;
	static final int TOTAL_ENEMY = 5;
	static final int LIFE = 3;
	static final int NUM_LEVEL = 3;
	static final int RIGHT = 1;
	static final int LEFT = 2;
	static final int UP = 3;
	static final int DOWN = 4;
	static final int NO_COLLISION = 0;
	static final int COLLISION = 1;
	
	static final String SPRITE_SHEET = "resources/demo-spritesheet.png";
	
	//flags
	boolean gameRunning;
	boolean gameCleared; 		//clear one level
	boolean allLevelCleared;	//clear all levels
	boolean gameLost;			//lost one life
	boolean gameOver;			//lost all lives
	boolean justStarted = true;
	boolean gotStar;
	boolean giveLife = true;
	boolean flying_active;
	boolean main_active, sub_active, main_inFuton, sub_inFuton;
	boolean[ ] savedBird = new boolean[NUM_LEVEL];
	boolean[ ] e_hit_up = new boolean[TOTAL_ENEMY];
	boolean[ ] e_hit_down = new boolean[TOTAL_ENEMY];
	boolean[ ] e_hit_right = new boolean[TOTAL_ENEMY];
	boolean[ ] e_hit_left = new boolean[TOTAL_ENEMY];
	
	
	
	//variables
	int left, top;
	int level = 1;
	int dir   = 0;
	int lifeLeft = LIFE;
	int k, k2 = 100;
	int k3    = 0;
	int score = 0;
	int counter = 0;
	int futonNo = 100;
	int currentBear = RIGHT;
	
	
	int main = RIGHT;
	int BEAR_SPEED = 12;
	int intCurrentSec = 0;
	int timeLimit = 0;
	int countDown = 0;
	int starActivated = 0;
	int flyingDeactivated = 0;
	long start, current;
	float currentSec;
	double mx = 420.0;
	double my = 460.0;
	double sx = 340.0;
	double sy = 460.0;
	
	//instances
	Block[] block;
	Maze[] maze;
	Futon[] futon;
	InFuton mainFuton, subFuton;
	Bear mainBear, mainD, mainU, mainR, mainL; 
	Bear subBear, subD, subU, subR, subL;
	Bear tempBear;
	Vacuum[] v;
	Bird bird;
	Cage cage;
	Enemy[] normal;
	Enemy flying;
	Star star;
	AudioClip ac;
	AudioClip hit_ac, touchEnemy_ac, touchFuton_ac, 
	star_ac, goal_ac, hitFlying_ac;

	
	
	FontResource scoreboardFont;
	FontResource starFont;
	FontResource msgFont;
	FontResource winFont;
	FontResource loseFont;
	FontResource timerFont;
	
	public Game() {
		//Constructor
		super(WORLD_WIDTH, WORLD_HEIGHT, false);
				
		ResourceFactory.getFactory().loadResources("resources/", "resources.xml");
	
		
		//create instances
		
		mainD = new Bear("resources/b-bear.png", 420, 460);
		subD  = new Bear("resources/w-bear.png", 340, 460);	
		mainU    = new Bear("resources/b-bearU.png", -200, -200);
		subU     = new Bear("resources/w-bearU.png", -200, -200);
		mainR    = new Bear("resources/b-bearR.png", -200, -200);
		subR     = new Bear("resources/w-bearR.png", -200, -200);
		mainL    = new Bear("resources/b-bearL.png", -200, -200);
		subL     = new Bear("resources/w-bearL.png", -200, -200);
		
		mainBear = mainD;
		subBear = subD;
		
		mainFuton = new InFuton("resources/b-in-futon.png", -50, -50);
		subFuton = new InFuton("resources/w-in-futon.png", -50, -50);

		block = new Block[TOTAL_BLOCK];
		maze = new Maze[TOTAL_MAZE];
		v = new Vacuum[8];
		futon = new Futon[TOTAL_FUTON];
		
		bird = new Bird();
		cage = new Cage();
		normal = new Enemy[TOTAL_ENEMY];
		flying = new Enemy(0);
		star = new Star(-200, -200);

		hit_ac   = AudioClip.createAudioClip("resources/hitenemy.wav");
		touchEnemy_ac = AudioClip.createAudioClip("resources/touchenemy.wav");
		touchFuton_ac = AudioClip.createAudioClip("resources/snore.wav");
		goal_ac       = AudioClip.createAudioClip("resources/harp.wav");
		hitFlying_ac  = AudioClip.createAudioClip("resources/swap.wav");
		star_ac  = AudioClip.createAudioClip("resources/star.wav");
	
		
		//init flags
		resetVariables();
		resetActivationAndPosition();
		
		/****************************************
		 * Set up vacuums 				
		 ****************************************/
		for(int i=0; i<8; i++){
			if(i<2){
				v[i] = new Vacuum("resources/v_left.png");
			}else if(i<4){
				v[i] = new Vacuum("resources/v_right.png");
			}else if(i<6){
				v[i] = new Vacuum("resources/v_up.png");
			}else{
				v[i] = new Vacuum("resources/v_down.png");
			}
		}
		
	
		
		
		/****************************************
		 * Set up maze & futon & enemy for level 1
		 ****************************************/
		for(int i=0; i<TOTAL_MAZE; i++){
			maze[i] = new Maze(-100, -100);
		}
		
		for(int i=0; i<TOTAL_FUTON; i++){
			futon[i] = new Futon(-100, -100);
		}
		
		for(int i=0; i<TOTAL_ENEMY; i++){
			normal[i] = new Enemy(-100, -100);
		}
		
		setSurroundingBlocks();
		setMazeLevel1();

		scoreboardFont = ResourceFactory.getFactory().getFontResource(new Font("VERDANA", Font.PLAIN, 12), Color.getHSBColor(0.05F, 0.69F, 0.29F), null);
		msgFont = ResourceFactory.getFactory().getFontResource(new Font("VERDANA", Font.PLAIN, 20), Color.getHSBColor(0.06F, 0.79F, 0.40F), null);		
		starFont = ResourceFactory.getFactory().getFontResource(new Font("VERDANA", Font.PLAIN, 50), Color.getHSBColor(0.95F, 0.70F, 1.00F), null);		
		winFont = ResourceFactory.getFactory().getFontResource(new Font("VERDANA", Font.PLAIN, 70), Color.getHSBColor(0.11F, 0.90F, 0.50F), null);
		loseFont = ResourceFactory.getFactory().getFontResource(new Font("VERDANA", Font.PLAIN, 70), Color.getHSBColor(0.06F, 0.79F, 0.44F), null);	
		timerFont = ResourceFactory.getFactory().getFontResource(new Font("VERDANA", Font.PLAIN, 50), Color.getHSBColor(0.95F, 0.70F, 1.00F), null);	
		
		BodyLayer<Body> enemy_layer = new AbstractBodyLayer.IterativeUpdate<Body>();
		BodyLayer<Body> bear_layer = new AbstractBodyLayer.IterativeUpdate<Body>();
		BodyLayer<Body> block_layer = new AbstractBodyLayer.IterativeUpdate<Body>();
		BodyLayer<Body> vacuum_layer = new AbstractBodyLayer.IterativeUpdate<Body>();
		BodyLayer<Body> futon_layer = new AbstractBodyLayer.IterativeUpdate<Body>();
		BodyLayer<Body> bird_layer = new AbstractBodyLayer.IterativeUpdate<Body>();
		BodyLayer<Body> cage_layer = new AbstractBodyLayer.IterativeUpdate<Body>();
		BodyLayer<Body> item_layer = new AbstractBodyLayer.IterativeUpdate<Body>();
		
		bear_layer.add(mainBear);
		bear_layer.add(subBear);
		bear_layer.add(mainD);
		bear_layer.add(subD);
		bear_layer.add(mainU);
		bear_layer.add(subU);
		bear_layer.add(mainR);
		bear_layer.add(subR);
		bear_layer.add(mainL);
		bear_layer.add(subL);

		for(int i=0; i<TOTAL_BLOCK; i++){
			block_layer.add(block[i]);
		}
		for(int i=0; i<8; i++){
			vacuum_layer.add(v[i]);
		}
		for(int i=0; i<58; i++){
			vacuum_layer.add(maze[i]);
		}
		for(int i=0; i<TOTAL_FUTON; i++){
			futon_layer.add(futon[i]);
		}
		for(int i=0; i<TOTAL_ENEMY; i++){
			enemy_layer.add(normal[i]);
		}
		futon_layer.add(mainFuton);
		futon_layer.add(subFuton);
		bird_layer.add(bird);
		cage_layer.add(cage);
		item_layer.add(star);
		enemy_layer.add(flying);
		
		gameObjectLayers.add(bear_layer);
		gameObjectLayers.add(block_layer);
		gameObjectLayers.add(vacuum_layer);
		gameObjectLayers.add(futon_layer);
		gameObjectLayers.add(bird_layer);
		gameObjectLayers.add(cage_layer);
		gameObjectLayers.add(enemy_layer);
		gameObjectLayers.add(item_layer);
	}

	
	/***********************************************
	 * render                                      
	 ***********************************************/
	public void render(RenderingContext rc) {
		super.render(rc);
		msgFont.render("Score: " + score, rc, AffineTransform.getTranslateInstance(15, 570));
		msgFont.render("Level: " + level, rc, AffineTransform.getTranslateInstance(700, 10));
		msgFont.render("Life : " + lifeLeft, rc, AffineTransform.getTranslateInstance(15, 10));
		timerFont.render("" + countDown, rc, AffineTransform.getTranslateInstance(367, 5));
		
		if(gotStar){
			starFont.render("STAR ACTIVATED", rc, AffineTransform.getTranslateInstance(200, 520));
		}
		
		
		if(gameOver){
			loseFont.render("GAME OVER", rc, AffineTransform.getTranslateInstance(180, 250));
			msgFont.render("Restart (Hit space key)", rc, AffineTransform.getTranslateInstance(260, 520));
		}else if(gameLost){
			loseFont.render("YOU LOST", rc, AffineTransform.getTranslateInstance(220, 250));
			msgFont.render("Restart Level (Hit space key)", rc, AffineTransform.getTranslateInstance(260, 520));	
		
		}else if(allLevelCleared){
			winFont.render("Conguratulations", rc, AffineTransform.getTranslateInstance(100, 250));
			msgFont.render("Start Level 1(Hit space key)", rc, AffineTransform.getTranslateInstance(250, 520));
			
		}else if(gameCleared){
			winFont.render("SAVED", rc, AffineTransform.getTranslateInstance(280, 250));
			msgFont.render("Start Next Level (Hit space key)", rc, AffineTransform.getTranslateInstance(250, 520));
		}
	}


	/***********************************************
	 * update                                      
	 ***********************************************/
	public void update(long deltaMs) {
		
		if(gameRunning){
			
			super.update(deltaMs);
						
			boolean left, right, up, down;
			boolean v_on;
			
			
			//calculate countdown timer
			current = System.currentTimeMillis() - start;
			currentSec = current/1000F;
			intCurrentSec = (int)(currentSec);
			countDown = timeLimit - intCurrentSec;

			
			

			/**********************************
			 * handle BEARS & STAR collision
			 **********************************/
			
			int main_star_collision = 0;
			int sub_star_collision = 0;
			
			main_star_collision = star.collideWithBear(mainBear);
			sub_star_collision = star.collideWithBear(subBear);
			
			if(main_star_collision == COLLISION || sub_star_collision == COLLISION){
				star_ac.play();
				score = score + 1000;
				star.setPosition(new Vector2D(-200, -200));
				star.setActivation(false);
				gotStar = true;
				starActivated = intCurrentSec;
			}

			
						
			/**********************************
			 * handle BEARS & CAGE(BIRD) collision
			 **********************************/
			
			
			int bear_cage_collision = 0;
			
			bear_cage_collision = cage.collideWithBear(mainBear, subBear);
			
			if(bear_cage_collision == Cage.COLLISION){
				score = score + countDown + level*100;
				savedBird[level-1] = true;
				cage.setActivation(false);
				main_active = false;
				sub_active = false;
				goal_ac.play();
			}
			
		
			
			
			/**********************************
			 * handle BEARS & NORMAL ENEMY collision
			 **********************************/
			
			int main_normal_collision = 0;
			int sub_normal_collision = 0;
			
			for(int i=0; i<TOTAL_ENEMY; i++){
				
				main_normal_collision = normal[i].collideWithBear(mainBear);
				sub_normal_collision = normal[i].collideWithBear(subBear);
				if(normal[i].isActive() && 
					(main_normal_collision == Enemy.COLLISION || sub_normal_collision == Enemy.COLLISION)){
					if(gotStar){
						normal[i].setActivation(false);
						hit_ac.play();
					}else{
						touchEnemy_ac.play();
						gameLost = true;
						gameRunning = false;
						main_active = false;
						sub_active = false;
					}
				}						
			}
			

			
			/**********************************
			 * handle NORMAL ENEMY & MAZE collision
			 **********************************/
			int enemy_maze_collision = 0;
			
			for(int i=0; i<58; i++){
				for(int j=0; j<TOTAL_ENEMY; j++){
					if(normal[j].isActive()){
						enemy_maze_collision = maze[i].collideWithEnemy(normal[j]);
						if(enemy_maze_collision == Maze.TOP){
							e_hit_up[j] = true;
						}else if(enemy_maze_collision == Maze.BOTTOM){
							e_hit_down[j] = true;
						}else if(enemy_maze_collision == Maze.RIGHT){
							e_hit_right[j] = true;
						}else if(enemy_maze_collision == Maze.LEFT){
							e_hit_left[j] = true;
						}	
					}
				}
			}	
				
			
			
			
			/**********************************
			 * handle move of flying enemies
			 **********************************/
			double mainX = mainBear.getPosition().getX();
			double mainY = mainBear.getPosition().getY();
			double flyX = flying.getPosition().getX();
			double flyY = flying.getPosition().getY();
			
			
			if(flying.isActive() && gotStar){
				if(mainX >= flyX && mainY >= flyY){
					flying.setVelocity(new Vector2D(-1, -1));
				}else if(mainX <= flyX && mainY >= flyY){
					flying.setVelocity(new Vector2D(1, -1));
				}else if(mainX <= flyX && mainY <= flyY){
					flying.setVelocity(new Vector2D(1, 1));
				}else if(mainX >= flyX && mainY <= flyY){
					flying.setVelocity(new Vector2D(-1, 1));
				}else{
					flying.setVelocity(new Vector2D(-1, 1));
				}	
			}else{
				if(flying.isActive() && main_active){
					if(mainX >= flyX && mainY >= flyY){
						flying.setVelocity(new Vector2D(5, 5));
					}else if(mainX <= flyX && mainY >= flyY){
						flying.setVelocity(new Vector2D(-5, 5));
					}else if(mainX <= flyX && mainY <= flyY){
						flying.setVelocity(new Vector2D(-5, -5));
					}else if(mainX >= flyX && mainY <= flyY){
						flying.setVelocity(new Vector2D(5, -5));
					}else{
						flying.setVelocity(new Vector2D(5, -5));
					}	
				}else{
				
					if(k3<35){
						flying.setVelocity(new Vector2D(0, -ENEMY_SPEED));
					}else if(k3<60){
						flying.setVelocity(new Vector2D(ENEMY_SPEED, 0));
					}else if(k3<80){
						flying.setVelocity(new Vector2D(0, ENEMY_SPEED));
					}else if(k3<105){
						flying.setVelocity(new Vector2D(-ENEMY_SPEED, 0));
						k3=0;
					}
					k3++;
				}
			}
			
			
			
			
			
			/**********************************
			 * handle BEARS & FLYING ENEMY collision
			 **********************************/
			
			int main_fly_collision = 0;
			
			
			main_fly_collision = flying.collideWithBear(mainBear);
			if(flying.isActive() && main_active  && sub_active && (main_fly_collision == Enemy.COLLISION )){
				double tempX, tempY;
				tempX = mainBear.getPosition().getX();
				tempY = mainBear.getPosition().getY();
				hitFlying_ac.play();
				if(main==RIGHT){
					main = LEFT;					
					mainBear.setPosition(new Vector2D(subBear.getPosition().getX(), subBear.getPosition().getY()));
					subBear.setPosition(new Vector2D(tempX, tempY));
				}else if(main==LEFT){
					main = RIGHT;					
					mainBear.setPosition(new Vector2D(subBear.getPosition().getX(), subBear.getPosition().getY()));
					subBear.setPosition(new Vector2D(tempX, tempY));

				}
				flying.setPosition(new Vector2D(400, 500));
				flying.setActivation(false);
				flyingDeactivated = intCurrentSec;
			}		
			
			
			
			/**********************************
			 * handle random move of normal enemies
			 **********************************/
			if(k>=100){
				
				for(int i=0; i<TOTAL_ENEMY; i++){
					if(normal[i].isActive()){
						int r = (int)(Math.random() * 4);
						switch(r){
						case 0://up
							if(!e_hit_down[i]){
								normal[i].setVelocity(new Vector2D(0, -5));
							}
							break;
						case 1://down
							if(!e_hit_up[i]){
								normal[i].setVelocity(new Vector2D(0, 5));
							}
							break;
						case 2://left
							if(!e_hit_right[i]){
								normal[i].setVelocity(new Vector2D(-5, 0));
							}
							break;
						case 3://right
							if(!e_hit_left[i]){
								normal[i].setVelocity(new Vector2D(5, 0));
							}
							break;
						default: 
							normal[i].setVelocity(new Vector2D(0, 0));
						}
						k=0;
					}
				}
			}
			k++;	
			
			
			for(int i=0; i<TOTAL_ENEMY; i++){
				if(normal[i].isActive()){
					double ex = normal[i].getVelocity().getX();
					double ey = normal[i].getVelocity().getY();
			
					if(ex == 0.0 && ey == -5.0 && (e_hit_down[i] )){
						normal[i].setVelocity(new Vector2D(0, 0));
					}else if(ex==0.0 && ey==5.0 && e_hit_up[i]){
						normal[i].setVelocity(new Vector2D(0, 0));
					}else if(ex==-5.0 && ey==0.0 && e_hit_right[i]){
						normal[i].setVelocity(new Vector2D(0, 0));
					}else if(ex==5.0 && ey==0.0 && e_hit_left[i]){
						normal[i].setVelocity(new Vector2D(0, 0));
					}
				}
			}
			
			

			
			
			e_hit_down[0] = e_hit_up[0] = e_hit_right[0] = e_hit_left[0] = false;
			

			
			

			/**********************************
			 * handle BEAR & FUTON collision
			 **********************************/
			int main_bear_futon_collision = 0;
			int sub_bear_futon_collision = 0;
			
			for(int i=0; i<TOTAL_FUTON; i++){	
				
					main_bear_futon_collision = futon[i].collideWith(mainBear);
				if(futon[i].isActive() && main_bear_futon_collision!=Futon.NO_COLLISION){
					if(gotStar){
						futon[i].setActivation(false);
						hit_ac.play();
					}else{
						
						mainBear.setPosition(new Vector2D(futon[i].getPosition().getX(), futon[i].getPosition().getY()));
						mainFuton.setActivation(true);
						mainFuton.setPosition(new Vector2D(futon[i].getPosition().getX(), futon[i].getPosition().getY()));
						mainBear.setActivation(false);
						
						futon[i].setActivation(false);
						main_active = false;
						main_inFuton = true;
						touchFuton_ac.play();
						
						
					}
				}
				
				
				sub_bear_futon_collision = futon[i].collideWith(subBear);
				if(futon[i].isActive() && sub_bear_futon_collision!=Futon.NO_COLLISION){
					if(gotStar){
						futon[i].setActivation(false);
						hit_ac.play();
					}else{
						
						subFuton.setPosition(new Vector2D(futon[i].getPosition().getX(), futon[i].getPosition().getY()));
						subFuton.setActivation(true);
						subBear.setPosition(new Vector2D(futon[i].getPosition().getX(), futon[i].getPosition().getY()));
						subBear.setActivation(false);
						futon[i].setActivation(false);
						sub_active = false;
						sub_inFuton = true;
						touchFuton_ac.play();
					}
				}
				
			}
			
			
			/**********************************
			 * key action
			 **********************************/
			
			left = keyboard.isPressed(KeyEvent.VK_A);
			right = keyboard.isPressed(KeyEvent.VK_D);
			up = keyboard.isPressed(KeyEvent.VK_W);
			down = keyboard.isPressed(KeyEvent.VK_S);
			
			v_on = keyboard.isPressed(KeyEvent.VK_V);
			
			mx = mainBear.getPosition().getX();
			my = mainBear.getPosition().getY();
			sx = subBear.getPosition().getX();
			sy = subBear.getPosition().getY();
			
			
			if(left && !right && !up && !down){
				dir = LEFT;
				if(!main_inFuton){
					mainL.setActivation(true);
					mainR.setActivation(false);
					mainU.setActivation(false);
					mainD.setActivation(false);
				
					mainL.setPosition(new Vector2D(mx, my));
					mainBear = mainL;
					currentBear = LEFT;
					mainBear.setVelocity(new Vector2D(-BEAR_SPEED, 0));
				
				}else{
					mainL.setActivation(false);
				}
				if(!sub_inFuton){
					subR.setActivation(true);
					subL.setActivation(false);
					subU.setActivation(false);
					subD.setActivation(false);
				
					subR.setPosition(new Vector2D(sx, sy));
					subBear = subR;
				
					subBear.setVelocity(new Vector2D(BEAR_SPEED, 0));
				}else{
					subR.setActivation(false);
				}
				activateVacuum(v_on, dir);
				
			}else if(!left && right && !up && !down){
				dir = RIGHT;activateVacuum(v_on, RIGHT);
				if(!main_inFuton){
					mainL.setActivation(false);
					mainR.setActivation(true);
					mainU.setActivation(false);
					mainD.setActivation(false);
				
					mainR.setPosition(new Vector2D(mx, my));
					mainBear = mainR;
					currentBear = RIGHT;
					mainBear.setVelocity(new Vector2D(BEAR_SPEED, 0));
				
				}else{
					mainR.setVelocity(new Vector2D(0, 0));
				}
				if(!sub_inFuton){
					subR.setActivation(false);
					subL.setActivation(true);
					subU.setActivation(false);
					subD.setActivation(false);
					
					subL.setPosition(new Vector2D(sx, sy));
					subBear = subL;
					subBear.setVelocity(new Vector2D(-BEAR_SPEED, 0));
				}else{
					subL.setVelocity(new Vector2D(0, 0));
				}
				activateVacuum(v_on, dir);
				
			}else if(!left && !right && up && !down){
				dir = UP;
				activateVacuum(v_on, UP);
				
				
				if(!main_inFuton){
					mainL.setActivation(false);
					mainR.setActivation(false);
					mainU.setActivation(true);
					mainD.setActivation(false);

				
					mainU.setPosition(new Vector2D(mx, my));
					mainBear = mainU;
					currentBear = UP;
					mainBear.setVelocity(new Vector2D(0, -BEAR_SPEED));
				
				}else{
					mainU.setVelocity(new Vector2D(0, 0));
				}
				
				if(!sub_inFuton){
					subL.setActivation(false);
					subR.setActivation(false);
					subU.setActivation(true);
					subD.setActivation(false);
					
					subU.setPosition(new Vector2D(sx, sy));
					subBear = subU;
					subBear.setVelocity(new Vector2D(0, -BEAR_SPEED));
				}else{
					subU.setVelocity(new Vector2D(0, 0));
				}
				activateVacuum(v_on, dir);
				
			}else if(!left && !right && !up && down){
				dir=DOWN;
				activateVacuum(v_on, DOWN);
				if(!main_inFuton){
					mainL.setActivation(false);
					mainR.setActivation(false);
					mainU.setActivation(false);
					mainD.setActivation(true);
				
					mainD.setPosition(new Vector2D(mx, my));
					mainBear = mainD;
					currentBear = DOWN;
					mainBear.setVelocity(new Vector2D(0, BEAR_SPEED));
				}else{
					mainD.setVelocity(new Vector2D(0, 0));
				}
				if(!sub_inFuton){
					subL.setActivation(false);
					subR.setActivation(false);
					subU.setActivation(false);
					subD.setActivation(true);
					
					subD.setPosition(new Vector2D(sx, sy));
					subBear = subD;
					subBear.setVelocity(new Vector2D(0, BEAR_SPEED));
				}else{
					subD.setVelocity(new Vector2D(0, 0));
				}
				activateVacuum(v_on, dir);
				
			}else{
				mainBear.setVelocity(new Vector2D(0, 0));
				subBear.setVelocity(new Vector2D(0, 0));
				activateVacuum(v_on, dir);
			}
			
			
			/**********************************
			 * handle BEAR & MAZE collision
			 **********************************/
			
			int main_bear_maze_collision = 0;
			int sub_bear_maze_collision = 0;

			for(int i=0; i<58; i++){
				
				if(main_active){
					main_bear_maze_collision = maze[i].collideWith(mainBear);
				
					if(main_bear_maze_collision == Maze.TOP){
						if (dir==DOWN) mainBear.setVelocity(new Vector2D(0, 0));
					}else if(main_bear_maze_collision == Maze.BOTTOM){
						if (dir==UP)   mainBear.setVelocity(new Vector2D(0, 0));
					}else if(main_bear_maze_collision == Maze.RIGHT){
						if (dir==LEFT) mainBear.setVelocity(new Vector2D(0, 0));
					}else if(main_bear_maze_collision == Maze.LEFT){
						if (dir==RIGHT)mainBear.setVelocity(new Vector2D(0, 0));
					}else if(main_bear_maze_collision == Maze.TOPLEFT){
						if (dir==LEFT) mainBear.setVelocity(new Vector2D(0, 0)); 
						if(dir==RIGHT) mainBear.setPosition(new Vector2D(mx+1, my-1));
						if(dir==DOWN)  mainBear.setPosition(new Vector2D(mx-1, my+1));
					}else if(main_bear_maze_collision == Maze.BOTTOMLEFT){
						if(dir==RIGHT) mainBear.setPosition(new Vector2D(mx+1, my+1));
						if(dir==UP)    mainBear.setPosition(new Vector2D(mx-1, my-1));
					}else if(main_bear_maze_collision == Maze.TOPRIGHT){
						if(dir==LEFT)  mainBear.setPosition(new Vector2D(mx-1, my-1));
						if(dir==DOWN)  mainBear.setPosition(new Vector2D(mx+1, my+1));
					}else if(main_bear_maze_collision == Maze.BOTTOMRIGHT){
						if(dir==LEFT)  mainBear.setPosition(new Vector2D(mx-1, my+1));
						if(dir==UP)    mainBear.setPosition(new Vector2D(mx+1, my-1));
					}
				}

				
				if(sub_active){
				sub_bear_maze_collision = maze[i].collideWith(subBear);
				
					if(sub_bear_maze_collision == Maze.TOP){
						if (dir==DOWN) subBear.setVelocity(new Vector2D(0, 0));
					}else if(sub_bear_maze_collision == Maze.BOTTOM){
						if (dir==UP)   subBear.setVelocity(new Vector2D(0, 0));
					}else if(sub_bear_maze_collision == Maze.RIGHT){
						if (dir==RIGHT)subBear.setVelocity(new Vector2D(0, 0));
					}else if(sub_bear_maze_collision == Maze.LEFT){
						if (dir==LEFT) subBear.setVelocity(new Vector2D(0, 0));
					}else if(sub_bear_maze_collision == Maze.TOPLEFT){
						if(dir==LEFT)  subBear.setPosition(new Vector2D(sx+1, sy-1));
						if(dir==DOWN)  subBear.setPosition(new Vector2D(sx-1, sy+1));
					}else if(sub_bear_maze_collision == Maze.BOTTOMLEFT){
						if(dir==LEFT)  subBear.setPosition(new Vector2D(sx+1, sy+1));
						if(dir==UP)    subBear.setPosition(new Vector2D(sx-1, sy-1));
					}else if(sub_bear_maze_collision == Maze.TOPRIGHT){
						if(dir==RIGHT) subBear.setPosition(new Vector2D(sx-1, sy-1));
						if(dir==DOWN)  subBear.setPosition(new Vector2D(sx+1, sy+1));
					}else if(sub_bear_maze_collision == Maze.BOTTOMRIGHT){
						if(dir==RIGHT) subBear.setPosition(new Vector2D(sx-1, sy+1));
						if(dir==UP)    subBear.setPosition(new Vector2D(sx+1, sy-1));
					}	
				}
			}
			
	
	
			
			
			
			
		
			//check if levels are cleared
			if((level == 1) && (savedBird[0])){
				gameRunning = false;
				gameCleared = true;
			}else if((level == 2) && (savedBird[1])){
				gameRunning = false;
				gameCleared = true;
			}else if((level == 3) && (savedBird[2])){
				gameRunning = false;
				allLevelCleared = true;
			}
			
			
			
			//if both bears are in futon, player lose
			if(main_inFuton && sub_inFuton){
				gameRunning = false;
				
				if(lifeLeft == 1){
					System.out.println("gameover");
					gameOver = true;
				}else{
					gameLost = true;
				}	
			}
			
			//if no time left, player lose
			if(countDown <= 0){
				gameRunning = false;
				
				if(lifeLeft == 1){
					gameOver = true;
				}else{
					gameLost = true;	
				}
			}
			
			//increase life if scored 5000
			if(giveLife && (score >= 5000)){
				lifeLeft = lifeLeft + 1;
				giveLife = false;
			}
			
			//if no life is left, game over
			if(lifeLeft <= 0){
				gameRunning = false;
				gameOver = true;
				gameLost = false;
			}
					
			
			//display star item after third time has passed
			if(star.isActive() && intCurrentSec >= timeLimit/3){
				star.setActivation(true);
				star.setPosition(new Vector2D(260, 380));
			}
			
			
			
			//stop star effect after 7 seconds
			if(intCurrentSec - starActivated >= 7){
				gotStar = false;
			}
	
			
			//activate flying after 1/7 time has passed
			if(justStarted && intCurrentSec >= timeLimit/7){
				flying.setActivation(true);
				justStarted = false;
			}
			
			//activate flying effect after 5 seconds
			if(intCurrentSec - flyingDeactivated >= 5){
				flying.setActivation(true);
			}
	
			

		}else if(gameCleared || allLevelCleared){	
			boolean space = keyboard.isPressed(KeyEvent.VK_SPACE);
			if(space){
				score = score + level*1000;
				space = false;
				if(level == 1){
					level = 2;
				}else if(level == 2){
					level = 3;
				}else if(level == 3){
					level = 1;
					}
				if(level == 1){
					setMazeLevel1();
				}else if(level == 2){
					setMazeLevel2();
				}else if(level == 3){
					setMazeLevel3();
				}
				if(allLevelCleared){
					setMazeLevel1();
					lifeLeft = LIFE;
					level = 1;
					score = 0;
					giveLife = true;
				}
				resetVariables();
				resetActivationAndPosition();
			}
		}else if(gameLost || gameOver){
			boolean space = keyboard.isPressed(KeyEvent.VK_SPACE);
	
			
			if(space){
				space = false;
				if(gameLost){
					lifeLeft--;
					if(level == 1 ){
						setMazeLevel1();
					}else if(level == 2){
						setMazeLevel2();
					}else if(level == 3){
						setMazeLevel3();
					}
				}else if(gameOver){
					setMazeLevel1();
					lifeLeft = LIFE;
					level = 1;
					score = 0;
					giveLife = true;
				}
				resetVariables();
				resetActivationAndPosition();
			}
		}//END IF checking flag gameRunning/gameCleared/gameEnded
	}//END UPDATE()
		

	
	public void resetVariables(){
		justStarted 	= true;
		gameRunning     = true;
		gameLost        = false;
		gameOver        = false;
		gameCleared     = false;
		allLevelCleared = false;
		main_inFuton    = false;
		sub_inFuton     = false;
		main_active     = true;
		sub_active      = true;
		gotStar 		= false;
		current = 0;
		currentSec = 0;
		intCurrentSec = 0;
		starActivated = 0;
		dir = 0;
		main = RIGHT;
		for (int i=0; i<NUM_LEVEL; i++){
			savedBird[i] = false;
		}
		start = System.currentTimeMillis();
		
			
	}
	
	public void resetActivationAndPosition(){
		mainFuton.setActivation(false);
		subFuton.setActivation(false);
		star.setActivation(true);
		cage.setActivation(true);
		mainBear = mainD;
		subBear = subD;
		mainD.setActivation(true);
		mainU.setActivation(false);
		mainL.setActivation(false);
		mainR.setActivation(false);
		subD.setActivation(true);
		subU.setActivation(false);
		subL.setActivation(false);
		subR.setActivation(false);
		mainBear.setPosition(new Vector2D(420, 460));
		subBear.setPosition(new Vector2D(340, 460));
		star.setPosition(new Vector2D(-200, -200));
		flying.setActivation(false);
	}
		
	public void activateVacuum(boolean v_on, int dir){
		
		if(v_on){
			
			
			if (dir == LEFT){
				if(main_active)
					v[0].setPosition(new Vector2D(mainBear.getPosition().getX()-40, mainBear.getPosition().getY()));	
				if(sub_active)
					v[3].setPosition(new Vector2D(subBear.getPosition().getX()+40, subBear.getPosition().getY()));
			}else if (dir == RIGHT){
				if(main_active)
					v[2].setPosition(new Vector2D(mainBear.getPosition().getX()+40, mainBear.getPosition().getY()));
				if(sub_active)
					v[1].setPosition(new Vector2D(subBear.getPosition().getX()-40, subBear.getPosition().getY()));
			}else if (dir == UP){
				if(main_active)
					v[4].setPosition(new Vector2D(mainBear.getPosition().getX(), mainBear.getPosition().getY()-40));
				if(sub_active)
					v[5].setPosition(new Vector2D(subBear.getPosition().getX(), subBear.getPosition().getY()-40));
			}else if (dir == DOWN){
				if(main_active)
					v[6].setPosition(new Vector2D(mainBear.getPosition().getX(), mainBear.getPosition().getY()+40));
				if(sub_active)
					v[7].setPosition(new Vector2D(subBear.getPosition().getX(), subBear.getPosition().getY()+40));
			}
		}else{
			for(int i=0; i<8; i++){
				v[i].setPosition(new Vector2D(-50, -50));
			}
		}
		
		/**********************************
		 * handle VACUUM & NORMAL ENEMY collision
		 **********************************/
		int vacuum_enemy_collision = 0;
		
		for(int i=0; i<8; i++){
			for(int j=0; j<TOTAL_ENEMY; j++){
				vacuum_enemy_collision = normal[j].collideWithVacuum(v[i]);	
				if (normal[j].isActive() && vacuum_enemy_collision == Enemy.COLLISION){
					normal[j].setActivation(false);
					hit_ac.play();
					score = score + level*120;
				}
			}	
		}
		
		
		/**********************************
		 * handle VACUUM & INFUTON collision
		 **********************************/
		
		int vacuum_inFuton_collision = 0;
		
		for(int i=0; i<8; i++){
			vacuum_inFuton_collision = v[i].collideWithInFuton(mainFuton);
			if(vacuum_inFuton_collision == Vacuum.COLLISION){
				//mainBear.setActivation(true);
				//mainBear = tempBear;
				mainFuton.setActivation(false);
				main_active = true;
				main_inFuton = false;
				mainBear.setActivation(true);
				//mainBear.setPosition(new Vector2D(futon[futonNo].getPosition().getX(), futon[futonNo].getPosition().getY()));
				//mainBear.setPosition(new Vector2D(0, 0));
				
			}	
			vacuum_inFuton_collision = v[i].collideWithInFuton(subFuton);
			if(vacuum_inFuton_collision == Vacuum.COLLISION){
				//subBear.setActivation(true);
				//subBear = tempBear;
				subFuton.setActivation(false);
				sub_active = true;
				sub_inFuton = false;
				subBear.setActivation(true);
				//subBear.setPosition(new Vector2D(futon[futonNo].getPosition().getX(), futon[futonNo].getPosition().getY()));
				//subBear.setPosition(new Vector2D(100, 100));
				
				
			}
		}
		
		
		/**********************************
		 * handle VACUUM & FUTON collision
		 **********************************/
		int vacuum_futon_collision = 0;
		for(int i=0; i<8; i++){
			for(int j=0; j<TOTAL_FUTON; j++){
				vacuum_futon_collision = v[i].collideWithFuton(futon[j]);	
				if (futon[j].isActive() && vacuum_futon_collision == Vacuum.COLLISION){
					futon[j].setActivation(false);
					score = score + level*25;
					hit_ac.play();
				}
			}	
		}
	}//END ACTIVATE VACUUM
	
	
	
	public void setSurroundingBlocks(){
		/****************************************
		 * Set up outside blocks 				
		 ****************************************/
		
		left = 80;
		top  = 80;
		for(int i=0; i<104; i++){
			if(i < 22){
				block[i] = new Block(left, top);
				top = top + 20;
			}else if(i >= 22 && i < 44){
				block[i] = new Block(left, top);
				top = top + 20;
			}else if(i >= 44 && i < 74){
				block[i] = new Block(left, top);
				left = left + 20;
			}else if(i >= 74){
				block[i] = new Block(left, top);
				left = left + 20;
			}
			if(i==21){
				left = 700;
				top = 80;
			}else if(i==43){
				left = 100;
				top = 80;
			}else if(i==73){
				left = 100;
				top = 500;
			}	
		}
	}//setSurroundingBlocks
	
	
	
	public void setMazeLevel1(){
		timeLimit = 50;
		
		// Futon setup/reset
		for(int i=0; i<TOTAL_FUTON; i++){
			futon[i].setActivation(false);
		}
		for(int i=0; i<2; i++){
			futon[i].setActivation(true);
		}
		
		futon[0].setPosition(new Vector2D(180, 220));
		futon[1].setPosition(new Vector2D(580, 300));
		
		// Normal enemy setup/reset
		for(int i=0; i<TOTAL_ENEMY; i++){
			normal[i].setActivation(false);
		}
		for(int i=0; i<2; i++){
			normal[i].setActivation(true);
		}
		
		normal[0].setPosition(new Vector2D(260, 140));
		normal[1].setPosition(new Vector2D(460, 100));
		
		top = 140;
		for(int i=0; i<9; i++){	
				maze[i].setPosition(new Vector2D(380, top));
				top = top + 40;	
		}
		
		left = 140;
		top = 420;
		for(int i=9; i<22; i++){
			maze[i].setPosition(new Vector2D(left, top));
			left = left + 40;
		}
		
		maze[22].setPosition(new Vector2D(100, 340));
		maze[23].setPosition(new Vector2D(140, 340));
		maze[24].setPosition(new Vector2D(220, 340));
		maze[25].setPosition(new Vector2D(300, 340));
		maze[26].setPosition(new Vector2D(460, 340));
		maze[27].setPosition(new Vector2D(540, 340));
		maze[28].setPosition(new Vector2D(620, 340));
		maze[29].setPosition(new Vector2D(660, 340));
		
		left = 140;
		top  = 260;
		for(int i=30; i<40; i++){
			maze[i].setPosition(new Vector2D(left, top));
			left = left + 40;	
			if(i==34){
				left = left + 120;
			}
		}
		
		maze[40].setPosition(new Vector2D(100, 180));
		maze[41].setPosition(new Vector2D(140, 180));
		maze[42].setPosition(new Vector2D(220, 180));
		maze[43].setPosition(new Vector2D(300, 180));
		maze[44].setPosition(new Vector2D(420, 180));
		maze[45].setPosition(new Vector2D(460, 180));
		maze[46].setPosition(new Vector2D(540, 180));
		maze[47].setPosition(new Vector2D(620, 180));	
		maze[48].setPosition(new Vector2D(140, 140));
		maze[49].setPosition(new Vector2D(180, 140));
		maze[50].setPosition(new Vector2D(220, 140));
		maze[51].setPosition(new Vector2D(300, 140));
		maze[52].setPosition(new Vector2D(340, 140));
		maze[53].setPosition(new Vector2D(460, 140));
		maze[54].setPosition(new Vector2D(500, 140));
		maze[55].setPosition(new Vector2D(540, 140));
		maze[56].setPosition(new Vector2D(620, 140));
		maze[57].setPosition(new Vector2D(660, 140));
	}
	
	public void setMazeLevel2(){
		
		timeLimit = 70;
		
		for(int i=0; i<TOTAL_FUTON; i++){
			futon[i].setActivation(false);
		}
		for(int i=0; i<8; i++){
			futon[i].setActivation(true);
		}
		futon[0].setPosition(new Vector2D(100, 380));
		futon[1].setPosition(new Vector2D(100, 220));
		futon[2].setPosition(new Vector2D(180, 300));
		futon[3].setPosition(new Vector2D(300, 380));
		futon[4].setPosition(new Vector2D(340, 220));
		futon[5].setPosition(new Vector2D(420, 300));
		futon[6].setPosition(new Vector2D(580, 380));
		futon[7].setPosition(new Vector2D(660, 220));
		
		
		// Normal enemy setup/reset
		for(int i=0; i<TOTAL_ENEMY; i++){
			normal[i].setActivation(false);
		}
		for(int i=0; i<3; i++){
			normal[i].setActivation(true);
		}
		
		normal[0].setPosition(new Vector2D(260, 140));
		normal[1].setPosition(new Vector2D(460, 100));
		normal[2].setPosition(new Vector2D(180, 140));
		
		top = 140;
		for(int i=0; i<9; i++){	
				maze[i].setPosition(new Vector2D(380, top));
				top = top + 40;	
		}
		
		top  = 140;
		left = 220;
		for(int i=9; i<13; i++){	
			maze[i].setPosition(new Vector2D(left, top));
			top = top + 40;	
		}
		for(int i=13; i<16; i++){
			top = top + 40;
			maze[i].setPosition(new Vector2D(left, top));
		}
		top  = 340;
		left = 460;
		for(int i=16; i<21; i++){
			maze[i].setPosition(new Vector2D(left, top));
			left = left + 40;
		}
			
		maze[21].setPosition(new Vector2D(100, 340));
		maze[22].setPosition(new Vector2D(140, 140));
		maze[23].setPosition(new Vector2D(140, 180));
		maze[24].setPosition(new Vector2D(140, 260));
		maze[25].setPosition(new Vector2D(140, 340));
		maze[26].setPosition(new Vector2D(140, 420));
		maze[27].setPosition(new Vector2D(180, 260));
		maze[28].setPosition(new Vector2D(260, 340));
		maze[29].setPosition(new Vector2D(300, 140));
		maze[30].setPosition(new Vector2D(300, 180));
		maze[31].setPosition(new Vector2D(300, 260));
		maze[32].setPosition(new Vector2D(300, 340));
		maze[33].setPosition(new Vector2D(300, 420));
		maze[34].setPosition(new Vector2D(300, 460));
		maze[35].setPosition(new Vector2D(340, 140));
		maze[36].setPosition(new Vector2D(340, 260));
		maze[37].setPosition(new Vector2D(420, 140));
		maze[38].setPosition(new Vector2D(420, 260));
		maze[39].setPosition(new Vector2D(420, 420));
		maze[40].setPosition(new Vector2D(460, 140));
		maze[41].setPosition(new Vector2D(460, 180));
		maze[42].setPosition(new Vector2D(460, 260));
		maze[43].setPosition(new Vector2D(460, 420));
		maze[44].setPosition(new Vector2D(500, 180));
		maze[45].setPosition(new Vector2D(540, 140));
		maze[46].setPosition(new Vector2D(540, 180));
		maze[47].setPosition(new Vector2D(540, 220));
		maze[48].setPosition(new Vector2D(540, 260));
		maze[49].setPosition(new Vector2D(540, 420));
		maze[50].setPosition(new Vector2D(580, 180));
		maze[51].setPosition(new Vector2D(620, 140));
		maze[52].setPosition(new Vector2D(620, 180));
		maze[53].setPosition(new Vector2D(620, 260));
		maze[54].setPosition(new Vector2D(620, 420));
		maze[55].setPosition(new Vector2D(660, 260));
		maze[56].setPosition(new Vector2D(660, 420));
		maze[57].setPosition(new Vector2D(-100, -100));

	}
	
	public void setMazeLevel3(){
		
		timeLimit = 90;
		
		for(int i=0; i<TOTAL_FUTON; i++){
			futon[i].setActivation(false);
		}
		for(int i=0; i<16; i++){
			futon[i].setActivation(true);
		}
		
		futon[0].setPosition(new Vector2D(100, 140));
		futon[1].setPosition(new Vector2D(100, 180));
		futon[2].setPosition(new Vector2D(100, 220));
		futon[3].setPosition(new Vector2D(100, 380));
		futon[4].setPosition(new Vector2D(140, 300));
		futon[5].setPosition(new Vector2D(180, 180));
		futon[6].setPosition(new Vector2D(180, 380));
		futon[7].setPosition(new Vector2D(260, 100));
		futon[8].setPosition(new Vector2D(260, 300));
		futon[9].setPosition(new Vector2D(340, 180));
		futon[10].setPosition(new Vector2D(340, 380));
		futon[11].setPosition(new Vector2D(420, 220));
		futon[12].setPosition(new Vector2D(420, 380));
		futon[13].setPosition(new Vector2D(500, 300));
		futon[14].setPosition(new Vector2D(620, 380));
		futon[15].setPosition(new Vector2D(660, 220));
		
		// Normal enemy setup/reset
		for(int i=0; i<TOTAL_ENEMY; i++){
			normal[i].setActivation(false);
		}
		for(int i=0; i<TOTAL_ENEMY; i++){
			normal[i].setActivation(true);
		}
		
		normal[0].setPosition(new Vector2D(260, 140));
		normal[1].setPosition(new Vector2D(500, 140));
		normal[2].setPosition(new Vector2D(180, 220));
		normal[3].setPosition(new Vector2D(580, 220));
		normal[4].setPosition(new Vector2D(340, 300));
		
		top = 140;
		for(int i=0; i<9; i++){	
				maze[i].setPosition(new Vector2D(380, top));
				top = top + 40;	
		}
		
		top  = 260;
		left = 140;
		for(int i=9; i<15; i++){	
			maze[i].setPosition(new Vector2D(left, top));
			left = left + 40;	
		}
		left = left + 40;
		for(int i=15; i<18; i++){
			left = left + 40;
			maze[i].setPosition(new Vector2D(left, top));
		}
		
		top  = 140;
		left = 140;
		for(int i=18; i<21; i++){
			maze[i].setPosition(new Vector2D(left, top));
			top = top + 40;
		}
		
		top = top + 80;
		for(int i=21; i<25; i++){
			maze[i].setPosition(new Vector2D(left, top));
			top = top + 40;
		}
		
		top  = 100;
		left = 220;
		for (int i=25; i<28; i++){
			maze[i].setPosition(new Vector2D(left, top));
			top = top + 40;
		}
		top = top + 80;
		for (int i=28; i<31; i++){
			maze[i].setPosition(new Vector2D(left, top));
			top = top + 40;
		}
		
		top  = 140;
		left = 300;
		for (int i=31; i<34; i++){
			maze[i].setPosition(new Vector2D(left, top));
			top = top + 40;
		}
		top = top + 80;
		for (int i=34; i<37; i++){
			maze[i].setPosition(new Vector2D(left, top));
			top = top + 40;
		}
		
		top  = 140;
		left = 540;
		for (int i=37; i<40; i++){
			maze[i].setPosition(new Vector2D(left, top));
			top = top + 40;
		}
		top = top + 80;
		for (int i=40; i<43; i++){
			maze[i].setPosition(new Vector2D(left, top));
			top = top + 40;
		}
					
		maze[44].setPosition(new Vector2D(420, 340));
		maze[45].setPosition(new Vector2D(460, 140));
		maze[46].setPosition(new Vector2D(460, 180));
		maze[47].setPosition(new Vector2D(460, 340));
		maze[48].setPosition(new Vector2D(460, 420));
		maze[49].setPosition(new Vector2D(580, 340));
		maze[50].setPosition(new Vector2D(620, 140));
		maze[51].setPosition(new Vector2D(620, 180));
		maze[52].setPosition(new Vector2D(620, 260));
		maze[53].setPosition(new Vector2D(620, 340));
		maze[54].setPosition(new Vector2D(620, 420));
		maze[55].setPosition(new Vector2D(620, 460));
		maze[56].setPosition(new Vector2D(660, 140));
		maze[57].setPosition(new Vector2D(660, 260));
	}
	
		
	
	public static void main(String[] args) {
		Game g = new Game();
		g.run();
	}
}
	
	



