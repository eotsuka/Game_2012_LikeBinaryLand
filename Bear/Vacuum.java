package myGame;

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Vacuum extends VanillaAARectangle{
	public static final int NO_COLLISION = 0;
	public static final int COLLISION = 1;

	
	Vacuum(String filename){
		super(filename);
		position = new Vector2D(-20, -20);
	}

	
	public void update(long deltaMs){
		position = position.translate(velocity.scale(deltaMs/100.0));
		position = position.clampX(0, Game.WORLD_WIDTH - getWidth());
	}
	
	
	
	public int collideWithFuton(Futon f){
		double futonX = f.getPosition().getX();
		double futonY = f.getPosition().getY();
				
		if(getBoundingBox().contains(futonX+20, futonY) ||
				getBoundingBox().contains(futonX+40, futonY+20) ||
				getBoundingBox().contains(futonX+20, futonY+40) ||
				getBoundingBox().contains(futonX, futonY+20)){ 
			return COLLISION;
		}else{
			return NO_COLLISION;
		}
	}
	
	public int collideWithInFuton(InFuton f){
		double futonX = f.getPosition().getX();
		double futonY = f.getPosition().getY();
				
		if(getBoundingBox().contains(futonX+20, futonY) ||
				getBoundingBox().contains(futonX+40, futonY+20) ||
				getBoundingBox().contains(futonX+20, futonY+40) ||
				getBoundingBox().contains(futonX, futonY+20)){ 
			return COLLISION;
		}else{
			return NO_COLLISION;
		}
	}
	
}
