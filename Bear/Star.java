package myGame;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Star extends VanillaAARectangle{
	public static final int NO_COLLISION = 0;
	public static final int COLLISION = 1;
	
	
	Star(int w, int h){
		super("resources/star.png");					
		position = new Vector2D(w, h);

	}
	public void update(long deltaMs){
		position = position.translate(velocity.scale(deltaMs/100.0));
		position = position.clampX(0, Game.WORLD_WIDTH - getWidth());
		
	}
	
	public int collideWithBear(Bear b){
		double bearX = b.getPosition().getX();
		double bearY = b.getPosition().getY();
		int W = 40;
		
		if(getBoundingBox().contains(bearX+20, bearY) || 
				getBoundingBox().contains(bearX+W, bearY+20) ||
				getBoundingBox().contains(bearX+20, bearY+W) ||
				getBoundingBox().contains(bearX, bearY+20)){
			
			return COLLISION;
		}else{
			return NO_COLLISION;
		}
	}
}
