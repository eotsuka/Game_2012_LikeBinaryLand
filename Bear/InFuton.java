package myGame;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class InFuton extends VanillaAARectangle{
			
		
		InFuton(String filename, int x, int y){
			super(filename);
			position = new Vector2D(x, y);
		}
	
	
		InFuton(int w, int h){
			super("resources/b-in-futon.png");					
			position = new Vector2D(w, h);

		}
		InFuton(boolean sub){
			super("resources/w-in-futon.png");					
			position = new Vector2D(-50, -50);

		}
		
		
		
		public void update(long deltaMs){
			position = position.translate(velocity.scale(deltaMs/100.0));
			position = position.clampX(0, Game.WORLD_WIDTH - getWidth());	
		}
		
		
	
		
}
