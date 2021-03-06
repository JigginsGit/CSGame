package states;

import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_FOG;
import static org.lwjgl.opengl.GL11.GL_FOG_COLOR;
import static org.lwjgl.opengl.GL11.GL_FOG_DENSITY;
import static org.lwjgl.opengl.GL11.GL_FOG_END;
import static org.lwjgl.opengl.GL11.GL_FOG_HINT;
import static org.lwjgl.opengl.GL11.GL_FOG_MODE;
import static org.lwjgl.opengl.GL11.GL_FOG_START;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LIGHT_MODEL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glColorMaterial;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glFog;
import static org.lwjgl.opengl.GL11.glFogf;
import static org.lwjgl.opengl.GL11.glFogi;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glLightModel;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3d;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.CSGame;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import render.Model;
import render.OBJLoader;
import utils.Colour;



public class FirstPerson {
	/** The minimal distance from the camera where objects are rendered. */
    private static final float zNear = 0.3f;
    /** The maximal distance from the camera where objects are rendered. */
    private static final float zFar = 35;
    /** Defines the field of view.  */
    private static final int fov = 90;
    /** The distance where fog starts appearing. */
    private static final float fogNear = 20;
    /** The distance where the fog stops appearing (fully black here) */
    private static final float fogFar = 32;
    /**
     * The width and length of the floor and ceiling. Don't put anything above 1000, or OpenGL will start to freak out,
     * though.
     */
    private static final int gridSize = 10;
    /** The height of the ceiling. */
    private static final float ceilingHeight = 10;
    /** The height of the floor. */
    private static final float floorHeight = -1;
    
    
    private static final Colour fogColor = Colour.WHITE;
	private static Texture texture;
	private static String textureLocation;
	//GL Display Lists
	private static int objectDisplayList;
	private static int objectDisplayTest;
	private static int floorTexture;
	private static int wallDisplayList;
	private static int ceilingDisplayList;
	private static Vector3f position = new Vector3f(0, 0, 0);
	    /**
	     * The rotation of the axis (where to the player looks). The X component stands for the rotation along the x-axis,
	     * where 0 is dead ahead, 180 is backwards, and 360 is automatically set to 0 (dead ahead). The value must be between
	     * (including) 0 and 360. The Y component stands for the rotation along the y-axis, where 0 is looking straight
	     * ahead, -90 is straight up, and 90 is straight down. The value must be between (including) -90 and 90.
	     */
	    private static Vector3f rotation = new Vector3f(0, 0, 0);
		private static int floorDisplayList;
		private static long lastFrame;
		
		/** Defines the walking speed, where 10 is the standard. */
	    private static int walkingSpeed = 10;
	    /** Defines the mouse speed. */
	    private static int mouseSpeed = 2;
		private static int glassDisplayList;
	    /** Defines if the application utilizes vertical synchronization (eliminates screen tearing; caps fps to 60fps) */
	    private static final int maxLookUp = 85;
	    /** Defines the minimum angle at which the player can look down. */
	    private static final int maxLookDown = -85;
    
	public static void startup() {
		setTextureLocation("MissingBackground");
		setUpLighting();
		glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(fov, (float) Display.getWidth() / (float) Display.getHeight(), zNear, zFar);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_FOG);
        
        
            FloatBuffer fogColours = BufferUtils.createFloatBuffer(4);
            fogColours.put(new float[]{Colour.RED.red, Colour.RED.green, Colour.RED.blue, 1f});
            glClearColor(fogColor.red, fogColor.green, fogColor.blue, 1f);
            fogColours.flip();
            glFog(GL_FOG_COLOR, fogColours);
            glFogi(GL_FOG_MODE, GL_LINEAR);
            glHint(GL_FOG_HINT, GL_NICEST);
            glFogf(GL_FOG_START, fogNear);
            glFogf(GL_FOG_END, fogFar);
            glFogf(GL_FOG_DENSITY, 0.001f);
            
        setUpDisplayLists();
            
                
        ceilingDisplayList = glGenLists(1);
        glNewList(ceilingDisplayList, GL_COMPILE);
	        glBegin(GL_QUADS);
		        glColor4f(Colour.WHITE.red, Colour.WHITE.green, Colour.WHITE.blue, 1);
		        glVertex3f(-gridSize, ceilingHeight, -gridSize);
		        glColor4f(Colour.WHITE.red, Colour.WHITE.green, Colour.WHITE.blue, 1);
		        glVertex3f(gridSize, ceilingHeight, -gridSize);
		        glColor4f(Colour.WHITE.red, Colour.WHITE.green, Colour.WHITE.blue, 1);
		        glVertex3f(gridSize, ceilingHeight, gridSize);
		        glColor4f(Colour.WHITE.red, Colour.WHITE.green, Colour.WHITE.blue, 1);
		        glVertex3f(-gridSize, ceilingHeight, gridSize);
	        glEnd();
        glEndList();

        wallDisplayList = glGenLists(1);
        glNewList(wallDisplayList, GL_COMPILE);
        
        glBegin(GL_QUADS);

	        // North wall
	
	        glColor4f(Colour.BLUE.red, Colour.BLUE.green, Colour.BLUE.blue, 1);
	        glVertex3f(-gridSize, floorHeight, -gridSize);
	        glColor4f(Colour.BLUE.red, Colour.BLUE.green, Colour.BLUE.blue, 1);
	        glVertex3f(gridSize, floorHeight, -gridSize);
        	glColor4f(Colour.BLUE.red, Colour.BLUE.green, Colour.BLUE.blue, 0);
	        glVertex3f(gridSize, ceilingHeight, -gridSize);
	        glColor4f(Colour.BLUE.red, Colour.BLUE.green, Colour.BLUE.blue, 0);
	        glVertex3f(-gridSize, ceilingHeight, -gridSize);
	
	        // West wall
	
	        glColor4f(Colour.LIGHT_BLUE.red, Colour.LIGHT_BLUE.green, Colour.LIGHT_BLUE.blue, 1);
	        glVertex3f(-gridSize, floorHeight, -gridSize);
	        glColor4f(Colour.LIGHT_BLUE.red, Colour.LIGHT_BLUE.green, Colour.LIGHT_BLUE.blue, 0);
	        glVertex3f(-gridSize, ceilingHeight, -gridSize);
	        glColor4f(Colour.LIGHT_BLUE.red, Colour.LIGHT_BLUE.green, Colour.LIGHT_BLUE.blue, 0);
	        glVertex3f(-gridSize, ceilingHeight, +gridSize);
	        glColor4f(Colour.LIGHT_BLUE.red, Colour.LIGHT_BLUE.green, Colour.LIGHT_BLUE.blue, 1);
	        glVertex3f(-gridSize, floorHeight, +gridSize);
	
	        // East wall
	
	        glColor4f(Colour.LIGHT_BLUE.red, Colour.LIGHT_BLUE.green, Colour.LIGHT_BLUE.blue, 1);
	        glVertex3f(+gridSize, floorHeight, -gridSize);
	        glColor4f(Colour.LIGHT_BLUE.red, Colour.LIGHT_BLUE.green, Colour.LIGHT_BLUE.blue, 1);
	        glVertex3f(+gridSize, floorHeight, +gridSize);
	        glColor4f(Colour.LIGHT_BLUE.red, Colour.LIGHT_BLUE.green, Colour.LIGHT_BLUE.blue, 0);
	        glVertex3f(+gridSize, ceilingHeight, +gridSize);
	        glColor4f(Colour.LIGHT_BLUE.red, Colour.LIGHT_BLUE.green, Colour.LIGHT_BLUE.blue, 0);
	        glVertex3f(+gridSize, ceilingHeight, -gridSize);
	
	        // South wall
	
	        glColor4f(Colour.BLUE.red, Colour.BLUE.green, Colour.BLUE.blue, 1);
	        glVertex3f(-gridSize, floorHeight, +gridSize);
	        glColor4f(Colour.BLUE.red, Colour.BLUE.green, Colour.BLUE.blue, 0);
	        glVertex3f(-gridSize, ceilingHeight, +gridSize);
	        glColor4f(Colour.BLUE.red, Colour.BLUE.green, Colour.BLUE.blue, 0);
	        glVertex3f(+gridSize, ceilingHeight, +gridSize);
	        glColor4f(Colour.BLUE.red, Colour.BLUE.green, Colour.BLUE.blue, 1);
	        glVertex3f(+gridSize, floorHeight, +gridSize);
	
        glEnd();

        glEndList();

        floorDisplayList = glGenLists(1);
        glNewList(floorDisplayList, GL_COMPILE);
	        glBegin(GL_QUADS);
				glColor4f(Colour.YELLOW.red, Colour.YELLOW.green, Colour.YELLOW.blue, 1);
				glVertex3f(-gridSize, floorHeight, -gridSize);
				glColor4f(Colour.YELLOW.red, Colour.YELLOW.green, Colour.YELLOW.blue, 1);
				glVertex3f(-gridSize, floorHeight, gridSize);
				glColor4f(Colour.YELLOW.red, Colour.YELLOW.green, Colour.YELLOW.blue, 1);
				glVertex3f(gridSize, floorHeight, gridSize);
				glColor4f(Colour.YELLOW.red, Colour.YELLOW.green, Colour.YELLOW.blue, 1);
				glVertex3f(gridSize, floorHeight, -gridSize);
	        glEnd();
        glEndList();
        glColor4f(1, 1, 1, 1);
        

        
        objectDisplayTest = glGenLists(1);
        glNewList(objectDisplayTest, GL_COMPILE);
        {
            double topPoint = 0.75;
            //point along z axis
            double zdistance = -5;
            //point along along x axis
            double xdistance = 0;
     
            glBegin(GL_TRIANGLES);
	          
            glColor4f(1, 1, 0, 1f);
            glVertex3d(xdistance, topPoint, zdistance); //top
            glColor4f(0, 0, 1, 1f);
            glVertex3d(-1, -topPoint, -4); //corner1 
            glColor4f(0, 0, 1, 1f);
            glVertex3d(1, -topPoint, -4); //corner2

            glColor4f(1, 1, 0, 1f);
            glVertex3d(xdistance, topPoint, zdistance); //top
            glColor4f(0, 0, 1, 1f);
            glVertex3d(1, -topPoint, -4); //corner2
            glColor4f(0, 0, 1, 1f);
            glVertex3d(1, -topPoint, -6); //corner3

            glColor4f(1, 1, 0, 1f);
            glVertex3d(xdistance, topPoint, zdistance); //top
            glColor4f(0, 0, 1, 1f);
            glVertex3d(1, -topPoint, -6); //corner3
            glColor4f(0, 0, 1, 1f);
            glVertex3d(-1, -topPoint, -6); //corner4

            glColor4f(1, 1, 0, 1f);
            glVertex3d(xdistance, topPoint, zdistance); //top
            glColor4f(0, 0, 1, 1f);
            glVertex3d(-1, -topPoint, -6); //corner4
            glColor4f(0, 0, 1, 1f);
            glVertex3d(-1, -topPoint, -4); //corner1
            
            glEnd();
            glColor4f(1, 1, 1, 1);
        }
        glEndList();
        

	}
		private static void setUpDisplayLists() {
	        glassDisplayList = glGenLists(1);
	        glNewList(glassDisplayList, GL_COMPILE);
	        {
	            Model m = null;
	            try {
	                m = OBJLoader.loadTexturedModel(new File("bin/resources/models/Glass.obj"));
	            } catch (FileNotFoundException e) {
	                e.printStackTrace();
	                Display.destroy();
	                System.exit(1);
	            } catch (IOException e) {
	                e.printStackTrace();
	                Display.destroy();
	                System.exit(1);
	            }
	            
	            glColor4f(2f, 1f, 1f,1);
	            glBegin(GL_TRIANGLES);
	            for (Model.Face face : m.getFaces()) {
	                Vector3f n1 = m.getNormals().get(face.getNormalIndices()[0] - 1);
	                glNormal3f(n1.x, n1.y, n1.z);
	                Vector3f v1 = m.getVertices().get(face.getVertexIndices()[0] - 1);
	                glVertex3f(v1.x, v1.y, v1.z);
	                Vector3f n2 = m.getNormals().get(face.getNormalIndices()[1] - 1);
	                glNormal3f(n2.x, n2.y, n2.z);
	                Vector3f v2 = m.getVertices().get(face.getVertexIndices()[1] - 1);
	                glVertex3f(v2.x, v2.y, v2.z);
	                Vector3f n3 = m.getNormals().get(face.getNormalIndices()[2] - 1);
	                glNormal3f(n3.x, n3.y, n3.z);
	                Vector3f v3 = m.getVertices().get(face.getVertexIndices()[2] - 1);
	                glVertex3f(v3.x, v3.y, v3.z);
	            }
	            glEnd();
	        }
	        glEndList();
    }
		
		
	//sets up lighting
	private static void setUpLighting() {
	        glShadeModel(GL_SMOOTH);
	        glEnable(GL_DEPTH_TEST);
	        glEnable(GL_LIGHTING);
	        glEnable(GL_LIGHT0);
	        //light intensity R G B intensity
	        glLightModel(GL_LIGHT_MODEL_AMBIENT, asFlippedFloatBuffer(new float[]{0.4f, 0.4f, .02f, 1f}));
	        glLight(GL_LIGHT0, GL_POSITION, asFlippedFloatBuffer(new float[]{0, 0, 0, 1}));
	        glEnable(GL_CULL_FACE);
	        glCullFace(GL_BACK);
	        glEnable(GL_COLOR_MATERIAL);
	        glColorMaterial(GL_FRONT, GL_DIFFUSE);
	    }
	
	public static FloatBuffer asFlippedFloatBuffer(float... values) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
        buffer.put(values);
        buffer.flip();
        return buffer;
    }
	

	public static void loop() {
		
		while (CSGame.state == States.FirstPerson) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
						
			int delta = getDelta();
			glBindTexture(GL_TEXTURE_2D, floorTexture);
			
			
			glEnable(GL_CULL_FACE);
			glDisable(GL_DEPTH_TEST);
			glCallList(floorDisplayList);
			glCallList(ceilingDisplayList);
			glCallList(wallDisplayList);
//			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		    glCallList(glassDisplayList);
			glEnable(GL_DEPTH_TEST);
			glDisable(GL_CULL_FACE);
			glBindTexture(GL_TEXTURE_2D, 0);
			glCallList(objectDisplayList);
			glCallList(objectDisplayTest);
			
			
			glLoadIdentity();
			glRotatef(rotation.x, 1, 0, 0);
			glRotatef(rotation.y, 0, 1, 0);
			glRotatef(rotation.z, 0, 0, 1);
			glTranslatef(position.x, position.y, position.z);
						
			if (Mouse.isGrabbed()) {
				float mouseDX = Mouse.getDX() * mouseSpeed * 0.16f;
				float mouseDY = Mouse.getDY() * mouseSpeed * 0.16f;
				if (rotation.y + mouseDX >= 360) {
					rotation.y = rotation.y + mouseDX - 360;
				} else if (rotation.y + mouseDX < 0) {
					rotation.y = 360 - rotation.y + mouseDX;
				} else {
					rotation.y += mouseDX;
				}
				if (rotation.x - mouseDY >= maxLookDown && rotation.x - mouseDY <= maxLookUp) {
					rotation.x += -mouseDY;
				} else if (rotation.x - mouseDY < maxLookDown) {
					rotation.x = maxLookDown;
				} else if (rotation.x - mouseDY > maxLookUp) {
					rotation.x = maxLookUp;
				}
			}
						
			
			boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W);
			boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S);
			boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);
			boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
			boolean flyUp = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
			boolean flyDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
			boolean moveFaster = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
			boolean moveSlower = Keyboard.isKeyDown(Keyboard.KEY_TAB);
						
			if (moveFaster && !moveSlower) {
				walkingSpeed *= 4f;
			}
			if (moveSlower && !moveFaster) {
				walkingSpeed /= 10f;
			}
			
			if (keyUp && keyRight && !keyLeft && !keyDown) {
				float angle = rotation.y + 45;
				Vector3f newPosition = new Vector3f(position);
				float hypotenuse = (walkingSpeed * 0.0002f) * delta;
				float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
				float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
				newPosition.z += adjacent;
				newPosition.x -= opposite;
				position.z = newPosition.z;
				position.x = newPosition.x;
			}
			if (keyUp && keyLeft && !keyRight && !keyDown) {
				float angle = rotation.y - 45;
				Vector3f newPosition = new Vector3f(position);
				float hypotenuse = (walkingSpeed * 0.0002f) * delta;
				float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
				float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
				newPosition.z += adjacent;
				newPosition.x -= opposite;
				position.z = newPosition.z;
				position.x = newPosition.x;
			}
			if (keyUp && !keyLeft && !keyRight && !keyDown) {
				float angle = rotation.y;
				Vector3f newPosition = new Vector3f(position);
				float hypotenuse = (walkingSpeed * 0.0002f) * delta;
				float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
				float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
				newPosition.z += adjacent;
				newPosition.x -= opposite;
				position.z = newPosition.z;
				position.x = newPosition.x;
			}
			if (keyDown && keyLeft && !keyRight && !keyUp) {
				float angle = rotation.y - 135;
				Vector3f newPosition = new Vector3f(position);
				float hypotenuse = (walkingSpeed * 0.0002f) * delta;
				float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
				float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
				newPosition.z += adjacent;
				newPosition.x -= opposite;
				position.z = newPosition.z;
				position.x = newPosition.x;
			}
			if (keyDown && keyRight && !keyLeft && !keyUp) {
				float angle = rotation.y + 135;
				Vector3f newPosition = new Vector3f(position);
				float hypotenuse = (walkingSpeed * 0.0002f) * delta;
				float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
				float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
				newPosition.z += adjacent;
				newPosition.x -= opposite;
				position.z = newPosition.z;
				position.x = newPosition.x;
			}
			if (keyDown && !keyUp && !keyLeft && !keyRight) {
				float angle = rotation.y;
				Vector3f newPosition = new Vector3f(position);
				float hypotenuse = -(walkingSpeed * 0.0002f) * delta;
				float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
				float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
				newPosition.z += adjacent;
				newPosition.x -= opposite;
				position.z = newPosition.z;
				position.x = newPosition.x;
			}
			if (keyLeft && !keyRight && !keyUp && !keyDown) {
				float angle = rotation.y - 90;
				Vector3f newPosition = new Vector3f(position);
				float hypotenuse = (walkingSpeed * 0.0002f) * delta;
				float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
				float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
				newPosition.z += adjacent;
				newPosition.x -= opposite;
				position.z = newPosition.z;
				position.x = newPosition.x;
			}
			if (keyRight && !keyLeft && !keyUp && !keyDown) {
				float angle = rotation.y + 90;
				Vector3f newPosition = new Vector3f(position);
				float hypotenuse = (walkingSpeed * 0.0002f) * delta;
				float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
				float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
				newPosition.z += adjacent;
				newPosition.x -= opposite;
				position.z = newPosition.z;
				position.x = newPosition.x;
			}
			
			if (flyUp && !flyDown) {
				double newPositionY = (walkingSpeed * 0.0002) * delta;
				position.y -= newPositionY;
			}
			if (flyDown && !flyUp) {
				double newPositionY = (walkingSpeed * 0.0002) * delta;
				position.y += newPositionY;
			}
			if (moveFaster && !moveSlower) {
				walkingSpeed /= 4f;
			}
			if (moveSlower && !moveFaster) {
				walkingSpeed *= 10f;
			}
			while (Mouse.next()) {
				if (Mouse.isButtonDown(0)) {
					Mouse.setGrabbed(true);
				}
				if (Mouse.isButtonDown(1)) {
					Mouse.setGrabbed(false);
				}
			}
			while (Keyboard.next()) {
				if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
					position = new Vector3f(0, 0, 0);
					rotation = new Vector3f(0, 0, 0);
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
					mouseSpeed += 1;
					System.out.println("Mouse speed changed to " + mouseSpeed + ".");
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
					if (mouseSpeed - 1 > 0) {
						mouseSpeed -= 1;
						System.out.println("Mouse speed changed to " + mouseSpeed + ".");
					}
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
					System.out.println("Walking speed changed to " + walkingSpeed + ".");
					walkingSpeed += 1;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
					System.out.println("Walking speed changed to " + walkingSpeed + ".");
					walkingSpeed -= 1;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_F11)) {
					try {
						Display.setFullscreen(!Display.isFullscreen());
						if (!Display.isFullscreen()) {
							Display.setResizable(true);
							Display.setDisplayMode(new DisplayMode(800, 600));
							glViewport(0, 0, Display.getWidth(), Display.getHeight());
							glMatrixMode(GL_PROJECTION);
							glLoadIdentity();
							gluPerspective(fov, (float) Display.getWidth() / (float) Display.getHeight(), zNear, zFar);
							glMatrixMode(GL_MODELVIEW);
							glLoadIdentity();
						} else {
							glViewport(0, 0, Display.getWidth(), Display.getHeight());
							glMatrixMode(GL_PROJECTION);
							glLoadIdentity();
							gluPerspective(fov, (float) Display.getWidth() / (float) Display.getHeight(), zNear, zFar);
							glMatrixMode(GL_MODELVIEW);
							glLoadIdentity();
						}
					} catch (LWJGLException ex) {
						Logger.getLogger(FirstPerson.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
					if (!Mouse.isGrabbed() || Display.isFullscreen()) {
						CSGame.state = States.Closing;
					} else {
						Mouse.setGrabbed(false);
					}
				}
			}
			
			if (Display.wasResized()) {
				glViewport(0, 0, Display.getWidth(), Display.getHeight());
				glMatrixMode(GL_PROJECTION);
				glLoadIdentity();
				gluPerspective(fov, (float) Display.getWidth() / (float) Display.getHeight(), zNear, zFar);
				glMatrixMode(GL_MODELVIEW);
				glLoadIdentity();
			}
			
			if (Display.isCloseRequested()) {
				CSGame.state = States.Closing;
				break;
			}
			
			Display.update();
			Display.sync(60);
		}
    }
	
	public static void stop() {
		glDeleteTextures(floorTexture);
        glDeleteLists(floorDisplayList, 1);
        glDeleteLists(ceilingDisplayList, 1);
        glDeleteLists(wallDisplayList, 1);
        glDeleteLists(objectDisplayList, 1);
        glDeleteLists(objectDisplayTest, 1);
        Display.destroy();
        System.exit(0);
		CSGame.state = States.Main_Menu;
	}
	
	private static int getDelta() {
        long currentTime = getTime();
        int delta = (int) (currentTime - lastFrame);
        lastFrame = getTime();
        return delta;
    }
	
	private static long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
	
	public static String getTextureLocation() {
		return textureLocation;
	}
	
	public static Texture loadTexture(){
		if (getTextureLocation() == null) {
			setTextureLocation("MissingTexture");
		}
		
		try {
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/MissingBackGround.png"));
		}	
		catch(IOException e){
			System.out.println("Not a valid Texture Location: " + getTextureLocation());
			e.printStackTrace();
			Display.destroy();
			System.exit(1);
		}
		catch(NullPointerException e) {
			System.out.println("Null Pointer " + getTextureLocation());
			e.printStackTrace();Display.destroy();
			System.exit(1);
		}
		return texture;
	}

	public static void setTextureLocation(String textureLocation) {
		if (textureLocation == null) {
			System.out.println("Missing Texture for " + "FirstPerson");
			textureLocation = "resources/MissingBackground.png";
			return;
		}
		textureLocation = "resources/" + textureLocation + ".png";
	}
}
