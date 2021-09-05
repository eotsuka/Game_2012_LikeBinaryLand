package myGame;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Futon extends VanillaAARectangle{
		public static final int NO_COLLISION = 0;
		public static final int COLLISION = 5;
		public static final int DOWN = 1;
		public static final int LEFT = 2;
		public static final int RIGHT = 3;
		public static final int UP = 4;
			
		
		Futon(int w, int h){
			super("resources/futon.png");					
			position = new Vector2D(w, h);

		}
		
		
		
		public void update(long deltaMs){
			position = position.translate(velocity.scale(deltaMs/100.0));
			position = position.clampX(0, Game.WORLD_WIDTH - getWidth());
			
		}
		
		public void delete(){
			setActivation(false);
		
		}
		
		
		public int collideWith(Bear b){
			double bearX = b.getPosition().getX();
			double bearY = b.getPosition().getY();
			int W = 40;
			
			if(getBoundingBox().contains(bearX+20, bearY+2) || 
					getBoundingBox().contains(bearX+W-2, bearY+20) ||
					getBoundingBox().contains(bearX+20, bearY+W-2) ||
					getBoundingBox().contains(bearX+2, bearY+20)){
				
				return COLLISION;
			}else{
				return NO_COLLISION;
			}
		}
		
}
