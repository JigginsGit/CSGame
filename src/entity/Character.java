package entity;

public class Character extends Entity{
	
		
		public Character(int x, int y) {
		super(x, y);
		
		//TODO make images
//		setTextureLocation(" ");	
		}			
		public void moveToLeft(){
			if(x > 0){
				setPosition(this.x - 10, this.y);
			}
		}
		
		public void moveToRight(){
			if(x < 640 - this.getTexture().getImageWidth()){
				setPosition(this.x + 10, this.y);
			}
		}
		
		public void moveUp(){
			if(y > 0){
				setPosition(this.x, this.y - 10);
			}
		}
		
		public void moveDown(){
			if(y < 480 - this.getTexture().getImageHeight()){
				setPosition(this.x, this.y + 10);
			}
		}
		

	}