package myGame;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Maze extends VanillaAARectangle{
		public static final int NO_COLLISION = 0;
		public static final int TOP = 1;
		public static final int BOTTOM = 2;
		public static final int RIGHT = 3;
		public static final int LEFT = 4;
		
		public static final int TOPRIGHT = 13;
		public static final int TOPLEFT = 14;
		public static final int BOTTOMRIGHT = 23;
		public static final int BOTTOMLEFT = 24;
		
	
		
		Maze(int w, int h){
			super("resources/block.png");					
			position = new Vector2D(w, h);
		}
		
		
		
		public void update(long deltaMs){
			position = position.translate(velocity.scale(deltaMs/100.0));
			position = position.clampX(0, Game.WORLD_WIDTH - getWidth());
			
		}
		
		
		
		
		/*
		public int collideWith(Bear b){
			double bearX = b.getPosition().getX();
			double bearY = b.getPosition().getY();
			double W = 40;
			double M = 20;
			double g = 3;
			double h = g*2;
			
			if((getBoundingBox().contains(bearX+M, bearY+W-g)) ||
					(getBoundingBox().contains(bearX+g, bearY+W-g) && getBoundingBox().contains(bearX+h, bearY+W-g)) || 
					(getBoundingBox().contains(bearX+W-g, bearY+W-g) && getBoundingBox().contains(bearX+W-h, bearY+W-g) )){
				return TOP;
			}else if((getBoundingBox().contains(bearX+M, bearY+g)) || 
					(getBoundingBox().contains(bearX+g, bearY+g) && getBoundingBox().contains(bearX+h, bearY+g)) || 
					(getBoundingBox().contains(bearX+W-g, bearY+g) && getBoundingBox().contains(bearX+W-h, bearY+g) )){
				return BOTTOM;
			}else if((getBoundingBox().contains(bearX, bearY+M)) ||
					(getBoundingBox().contains(bearX+1, bearY) && getBoundingBox().contains(bearX+3, bearY)) || 
					(getBoundingBox().contains(bearX+W-1, bearY) && getBoundingBox().contains(bearX+W-3, bearY) )){
				return RIGHT;
			}else if((getBoundingBox().contains(bearX+W, bearY+M)) ||
					(getBoundingBox().contains(bearX+1, bearY) && getBoundingBox().contains(bearX+3, bearY)) || 
					(getBoundingBox().contains(bearX+W-1, bearY) && getBoundingBox().contains(bearX+W-3, bearY) )){
				return LEFT;
			}else{
				return NO_COLLISION;
			}
		}
		*/
		
		
		
		public int collideWith(Bear b){
			double bearX = b.getPosition().getX();
			double bearY = b.getPosition().getY();
			double W = 40;
			double M = 20;
			
			if((getBoundingBox().contains(bearX+M, bearY+W))){
				return TOP;
			}else if((getBoundingBox().contains(bearX+M, bearY))){
				return BOTTOM;
			}else if((getBoundingBox().contains(bearX, bearY+M))){
				return RIGHT;
			}else if((getBoundingBox().contains(bearX+W, bearY+M))){
				return LEFT;
			}else if(getBoundingBox().contains(bearX+1, bearY+1)){
				return BOTTOMRIGHT;
			}else if(getBoundingBox().contains(bearX+W-1, bearY+1)){
				return BOTTOMLEFT;
			}else if(getBoundingBox().contains(bearX+1, bearY+W-1)){
				return TOPRIGHT;
			}else if(getBoundingBox().contains(bearX+W-1, bearY+W-1)){
				return TOPLEFT;
			}else{
				return NO_COLLISION;
			}
		}
		
		
		
		
		public int collideWithEnemy(Enemy e){
			double enemyX = e.getPosition().getX();
			double enemyY = e.getPosition().getY();
			double W = 40;
			double M = 20;
			
			if((getBoundingBox().contains(enemyX+M, enemyY+W))){
				return TOP;
			}else if((getBoundingBox().contains(enemyX+M, enemyY))){
				return BOTTOM;
			}else if((getBoundingBox().contains(enemyX, enemyY+M))){
				return RIGHT;
			}else if((getBoundingBox().contains(enemyX+W, enemyY+M))){
				return LEFT;
			}else if(getBoundingBox().contains(enemyX, enemyY)){
				return BOTTOMRIGHT;
			}else if(getBoundingBox().contains(enemyX+W, enemyY)){
				return BOTTOMLEFT;
			}else if(getBoundingBox().contains(enemyX, enemyY+W)){
				return TOPRIGHT;
			}else if(getBoundingBox().contains(enemyX+W, enemyY+W)){
				return TOPLEFT;
			}else{
				return NO_COLLISION;
			}
		}
		
		
}
