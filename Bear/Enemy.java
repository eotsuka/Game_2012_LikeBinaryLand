package myGame;

import jig.engine.hli.physics.SpriteUpdateRules;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Enemy extends VanillaAARectangle{
		public static final int NO_COLLISION = 0;
		public static final int COLLISION = 1;
		
		SpriteUpdateRules updateRule;
		
		Enemy(int w, int h){
			super("resources/normal.png");					
			position = new Vector2D(w, h);
			updateRule = new SpriteUpdateRules(Game.WORLD_WIDTH-100, Game.WORLD_HEIGHT-100);
		}

		Enemy(int w){
			super("resources/flying.png");
			position = new Vector2D(-200, -200);
			updateRule = new SpriteUpdateRules(Game.WORLD_WIDTH-100, Game.WORLD_HEIGHT-100);
		}
		
		
		

		@Override
		public void update(long deltaMs) {
			position = position.translate(velocity.scale(deltaMs/100.0));
			updateRule.wrapOrReflect(this, SpriteUpdateRules.SIDE_EAST);
			updateRule.wrapOrReflect(this, SpriteUpdateRules.SIDE_NORTH);


			position = position.clampX(100, Game.WORLD_WIDTH - 100);
			position = position.clampY(100, Game.WORLD_HEIGHT - 100);
		}
		
		
		public void delete(){
			setActivation(false);
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
		
		public int collideWithVacuum(Vacuum v){
			double vacX = v.getPosition().getX();
			double vacY = v.getPosition().getY();
			int W = 40;
			
			//if the boundingBox of enemy contains any edges of vacuum, collision
			if(getBoundingBox().contains(vacX+20, vacY) || 
					getBoundingBox().contains(vacX+W, vacY+20) ||
					getBoundingBox().contains(vacX+20, vacY+W) ||
					getBoundingBox().contains(vacX, vacY+20)){
				
				return COLLISION;
			}else{
				return NO_COLLISION;
			}
		}
		
		
		
}
