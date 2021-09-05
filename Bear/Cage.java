package myGame;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Cage extends VanillaAARectangle{
	public static final int NO_COLLISION = 0;
	public static final int COLLISION = 1;
			
		
		Cage(){
			super("resources/cage.png");					
			position = new Vector2D(380, 100);

		}
		
		
		
		public void update(long deltaMs){
			position = position.translate(velocity.scale(deltaMs/100.0));
			position = position.clampX(0, Game.WORLD_WIDTH - getWidth());
			
		}
		
		public int collideWithBear(Bear b, Bear w){
			double bX = b.getPosition().getX();
			double bY = b.getPosition().getY();
			double wX = w.getPosition().getX();
			double wY = w.getPosition().getY();
			int W = 40;
			
			if((getBoundingBox().contains(bX, bY+10) && 
					getBoundingBox().contains(bX, bY+30) &&
					getBoundingBox().contains(wX+W, wY+10) &&
					getBoundingBox().contains(wX+W, wY+30)) || 
					(getBoundingBox().contains(wX, wY+10) && 
					getBoundingBox().contains(wX, wY+30) &&
					getBoundingBox().contains(bX+W, bY+10) &&
					getBoundingBox().contains(bX+W, bY+30))){
				
				return COLLISION;
			}else{
				return NO_COLLISION;
			}
		}
		
		
}
