package myGame;
import jig.engine.hli.physics.SpriteUpdateRules;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Bear extends VanillaAARectangle{
	public static final int NO_COLLISION = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	public static final int UP = 4;
	public static final int UPRIGHT = 5;
	public static final int UPLEFT = 6;
	public static final int DOWNRIGHT = 7;
	public static final int DOWNLEFT = 8;
	public static final int COLLISION = 9;
	public static final int W = 40;
	public static final int M = 20;
	
	SpriteUpdateRules updateRule;
	
	Bear(String filename, int x, int y){
		super(filename);
		position = new Vector2D(x, y);
		updateRule = new SpriteUpdateRules(Game.WORLD_WIDTH-100, Game.WORLD_HEIGHT-100);
	}
	
	
	public void update(long deltaMs){
		position = position.translate(velocity.scale(deltaMs/100.0));
		updateRule.wrapOrReflect(this, SpriteUpdateRules.SIDE_EAST);
		updateRule.wrapOrReflect(this, SpriteUpdateRules.SIDE_NORTH);

		position = position.clampX(100, Game.WORLD_WIDTH - 100);
		position = position.clampY(100, Game.WORLD_HEIGHT - 100);
	}
	
	public int collideWithBlock(Block b){
		double blockX = b.getPosition().getX();
		double blockY = b.getPosition().getY();
		
		
		if(getBoundingBox().contains(blockX, blockY+20) 
				&& getBoundingBox().contains(blockX+20, blockY+20)){
			return UP;
		}else if(getBoundingBox().contains(blockX, blockY) 
				&& getBoundingBox().contains(blockX+20, blockY)){
			return DOWN;
		}else if(getBoundingBox().contains(blockX, blockY) 
				&& getBoundingBox().contains(blockX, blockY+20)){
			return RIGHT;
		}else if(getBoundingBox().contains(blockX+20, blockY) 
				&& getBoundingBox().contains(blockX+20, blockY+20)){
			return LEFT;
		}else{
			return NO_COLLISION;
		}
	}

	
	
	public int collideWithMaze(Maze m){
		double mazeX = m.getPosition().getX();
		double mazeY = m.getPosition().getY();

		
		
		
		if((getBoundingBox().contains(mazeX+10, mazeY+W-1) || getBoundingBox().contains(mazeX+30, mazeY+W-1))){
			return UP;
		}else if((getBoundingBox().contains(mazeX+10, mazeY+1) || getBoundingBox().contains(mazeX+30, mazeY+1))){
			return DOWN;
		}else if((getBoundingBox().contains(mazeX+1, mazeY+10) || getBoundingBox().contains(mazeX+1, mazeY+30))){
			return RIGHT;
		}else if((getBoundingBox().contains(mazeX+W-1, mazeY+10) && getBoundingBox().contains(mazeX+W-1, mazeY+30))){
			return LEFT;
		}else if(getBoundingBox().contains(mazeX, mazeY)){
			return DOWNRIGHT;
		}else if(getBoundingBox().contains(mazeX+W, mazeY)){
			return DOWNLEFT;
		}else if(getBoundingBox().contains(mazeX, mazeY+W)){
			return UPRIGHT;
		}else if(getBoundingBox().contains(mazeX+W, mazeY+W)){
			return UPLEFT;
		}else{
			return NO_COLLISION;
		}
		
	}
	
	
	public int collideWithFuton(Futon f){
		double futonX = f.getPosition().getX();
		double futonY = f.getPosition().getY();
		
		if(getBoundingBox().contains(futonX+20, futonY+2) || 
				getBoundingBox().contains(futonX+W-2, futonY+20) ||
				getBoundingBox().contains(futonX+20, futonY+W-2) ||
				getBoundingBox().contains(futonX+2, futonY+20)){
			
			return COLLISION;
		}else{
			return NO_COLLISION;
		}
	}
	
	
	public int collideWithCage(Cage c){
		double cageX = c.getPosition().getX();
		double cageY = c.getPosition().getY();
		
		if(getBoundingBox().contains(cageX+20, cageY) || 
				getBoundingBox().contains(cageX+40, cageY+20) ||
				getBoundingBox().contains(cageX+20, cageY+40) ||
				getBoundingBox().contains(cageX, cageY+20)){
			
			return COLLISION;
		}else{
			return NO_COLLISION;
		}
	}
	
}



