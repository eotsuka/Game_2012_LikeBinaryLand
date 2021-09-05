package myGame;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Block extends VanillaAARectangle{
	//class Brick extends VanillaAARectangle{
		public static final int NO_COLLISION = 0;
		public static final int DOWN = 1;
		public static final int LEFT = 2;
		public static final int RIGHT = 3;
		public static final int UP = 4;
		public static final int DOWNLEFT = 5;
		public static final int DOWNRIGHT = 6;
		public static final int UPLEFT = 7;
		public static final int UPRIGHT = 8;
		
		public boolean isDeleted;
		public int hitCount;
		public int power;
		
		
		
		Block(int w, int h){
			super("resources/block-outside.png");					
			position = new Vector2D(w, h);
			isDeleted = false;
			//hitCount = 1;
			
		}
		
		
		
		public void update(long deltaMs){
			position = position.translate(velocity.scale(deltaMs/100.0));
			position = position.clampX(0, Game.WORLD_WIDTH - getWidth());
			
			
		}
		
		
		public void delete(){
			setActivation(false);
			isDeleted = true;
		}
		
		
		public int collideWith(Bear b){
			double bearX = b.getPosition().getX();
			double bearY = b.getPosition().getY();
			//double d = b.getRadius() * 2;
			double W = 40;
			double M = 20;
			
			if((getBoundingBox().contains(bearX+M, bearY+W))){
				return UP;
			}else if((getBoundingBox().contains(bearX+M, bearY))){
				return DOWN;
			}else if((getBoundingBox().contains(bearX, bearY+M))){
				return RIGHT;
			}else if((getBoundingBox().contains(bearX+W, bearY+M))){
				return LEFT;
			}else if(getBoundingBox().contains(bearX, bearY)){
				return DOWNRIGHT;
			}else if(getBoundingBox().contains(bearX+W, bearY)){
				return DOWNLEFT;
			}else if(getBoundingBox().contains(bearX, bearY+W)){
				return UPRIGHT;
			}else if(getBoundingBox().contains(bearX+W, bearY+W)){
				return UPLEFT;
			}else{
				return NO_COLLISION;
			}
		}
		
	//}
}
