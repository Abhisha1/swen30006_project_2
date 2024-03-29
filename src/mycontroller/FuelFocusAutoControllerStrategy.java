package mycontroller;

import controller.CarController;
import world.Car;
import java.util.HashMap;

import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.WorldSpatial;
public class FuelFocusAutoControllerStrategy extends CarController{


	// How many minimum units the wall is away from the player.
	private int wallSensitivity = 1;
	
	private boolean isFollowingWall = false; // This is set to true when the car starts sticking to a wall.
	
	// Car Speed to move at
	private final int CAR_MAX_SPEED = 1;
	
	public FuelFocusAutoControllerStrategy(Car car) {
		super(car);
	}
	
	// Coordinate initialGuess;
	// boolean notSouth = true;
	@Override
	public void update() {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = getView();
		// checkStateChange();
		if(getSpeed() < CAR_MAX_SPEED){       // Need speed to turn and progress toward the exit
			applyForwardAcceleration();   // Tough luck if there's a wall in the way
		}
		if (findParcel(getOrientation(), currentView)) {
		}
		else if (isFollowingWall) {
			// If wall no longer on left, turn left
			if(!checkFollowingWall(getOrientation(), currentView)) {
				turnLeft();
			} else {
				// If wall on left and wall straight ahead, turn right
				if(checkWallAhead(getOrientation(), currentView)) {
					turnRight();
				}
			}
		}
		else {
			if(checkWallAhead(getOrientation(),currentView)) {
				turnLeft();
			}
		}
	}

	/**
	 * Check if you have a wall in front of you!
	 * @param orientation the orientation we are in based on WorldSpatial
	 * @param currentView what the car can currently see
	 * @return
	 */
	private boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
		switch(orientation){
		case EAST:
			return checkEast(currentView);
		case NORTH:
			return checkNorth(currentView);
		case SOUTH:
			return checkSouth(currentView);
		case WEST:
			return checkWest(currentView);
		default:
			return false;
		}
	}
	
	/**
	 * Check if the wall is on your left hand side given your orientation
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	private boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
		
		switch(orientation){
		case EAST:
			return checkNorth(currentView);
		case NORTH:
			return checkWest(currentView);
		case SOUTH:
			return checkEast(currentView);
		case WEST:
			return checkSouth(currentView);
		default:
			return false;
		}	
	}
	
	/**
	 * Method below just iterates through the list and check in the correct coordinates.
	 * i.e. Given your current position is 10,10
	 * checkEast will check up to wallSensitivity amount of tiles to the right.
	 * checkWest will check up to wallSensitivity amount of tiles to the left.
	 * checkNorth will check up to wallSensitivity amount of tiles to the top.
	 * checkSouth will check up to wallSensitivity amount of tiles below.
	 */
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkWest(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkNorth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to towards the top
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkSouth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	private boolean findParcel(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
		Coordinate currentPosition = new Coordinate(getPosition());
		System.out.println(orientation);
		switch(orientation){
		case EAST:
			for(int i = 0; i <= 3; i++){
				MapTile tile_down = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
				MapTile tile_up = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
				if(tile_down.isType(MapTile.Type.TRAP)){
					TrapTile trap = (TrapTile) tile_down;
					if (trap.getTrap() == "parcel") {
						turnRight();
						return true;
					}
				}
				else if(tile_up.isType(MapTile.Type.TRAP)){
					TrapTile trap = (TrapTile) tile_up;
					if (trap.getTrap() == "parcel") {
						turnLeft();
						return true;
					}
				}
			}
			return false;
		case WEST:
			for(int i = 0; i <= 3; i++){
				MapTile tile_up= currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
				MapTile tile_down = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
				if(tile_up.isType(MapTile.Type.TRAP)){
					TrapTile trap = (TrapTile) tile_up;
					if (trap.getTrap() == "parcel") {
						turnRight();
						return true;
					}
				}
				else if(tile_down.isType(MapTile.Type.TRAP)){
					TrapTile trap = (TrapTile) tile_down;
					if (trap.getTrap() == "parcel") {
						turnLeft();
						return true;
					}
				}
			}
			return false;
		case NORTH:
			System.out.println("NORTH NORTH NORTH");
			for(int i = 0; i <= 3; i++){
				MapTile tile_right = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
				MapTile tile_left = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
				if(tile_right.isType(MapTile.Type.TRAP)){
					TrapTile trap = (TrapTile) tile_right;
					if (trap.getTrap() == "parcel") {
						turnRight();
						return true;
					}
				}
				else if(tile_left.isType(MapTile.Type.TRAP)){
					TrapTile trap = (TrapTile) tile_left;
					if (trap.getTrap() == "parcel") {
						turnLeft();
						return true;
					}
				}
			}
			return false;
		case SOUTH:
			for(int i = 0; i <= 3; i++){
				MapTile tile_right = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
				MapTile tile_left = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
				if(tile_right.isType(MapTile.Type.TRAP)){
					TrapTile trap = (TrapTile) tile_right;
					if (trap.getTrap() == "parcel") {
						turnRight();
						return true;
					}
				}
				else if(tile_left.isType(MapTile.Type.TRAP)){
					TrapTile trap = (TrapTile) tile_left;
					if (trap.getTrap() == "parcel") {
						turnLeft();
						return true;
					}
				}
			}
			return false;
		default:
			return false;
		}	
	}
}

