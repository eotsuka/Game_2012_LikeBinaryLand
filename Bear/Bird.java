package myGame;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Bird extends VanillaAARectangle{
		public static final int NO_COLLISION = 0;
		public static final int COLLISION = 1;
		
			
		
		Bird(){
			super("resources/s_bird.png");					
			position = new Vector2D(385, 110);

		}
		
		
		
		public void update(long deltaMs){
			position = position.translate(velocity.scale(deltaMs/100.0));
			position = position.clampX(0, Game.WORLD_WIDTH);
			
			
		}
		
		
		
		
}
