package entity;

public class Door extends Entity{
	
	private int x;
	private int y;
	@SuppressWarnings("unused")
	private int z;
	//TODO create door texture, default texture
	String textureLocation;
	private boolean lock = false;
	
	public Door(int x, int y){
		super(x,y);
	}
	
	public Door(int x, int y, int z){
		super(x,y);
	}
	
	@Override
	public void update() {
		
	}

	//set to be locked/unlocked
	public void setLocked(boolean lock){
		this.lock = lock;
	}	
	
	public boolean checkIfLocked(){
		return lock;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	
}
