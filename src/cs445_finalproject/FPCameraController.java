/***************************************************************
* file: Minecraft.java
* author: Adrian Hy, Jerahmeel Calma, Awais Ibrahim
* class: CS 445 – Computer Graphics
*
* assignment: Final Project
* date last modified: 05/07/18
*
* purpose: Moves the camera's position using the keys w, a, s, d, 
* left shift, and space. Moves the camera's view on a point using 
* the mouse.
*
****************************************************************/

package cs445_finalproject;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard; 
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display; 
import static org.lwjgl.opengl.GL11.*; 
import org.lwjgl.Sys;

public class FPCameraController {
    
    //3d vector to store the camera's position in 
    private Vector3f position = null;
    private Vector3f lPosition = null;
    //the rotation around the Y axis of the camera 
    private float yaw = 0.0f;
    //the rotation around the X axis of the camera 
    private float pitch = 0.0f;
    private Vector3Float me;
    Chunk chunk;
    
    //constructor: FPCameraController
    //purpose: initializes the instance variables of FPCameraController
    public FPCameraController(float x, float y, float z) {
        //instantiate position Vector3f to the x y z params. 
        position = new Vector3f(x, y, z);
        lPosition = new Vector3f(x, y, z);
        lPosition.x = 0f;
        lPosition.y = 15f;
        lPosition.z = 0f;
    }
    
    //method: yaw
    //purpose: increment the camera's current yaw rotation 
    public void yaw(float amount) {
        //increment the yaw by the amount param
        yaw += amount; 
    }
    
    //method: pitch
    //purpose: increment the camera's current yaw rotation 
    public void pitch(float amount) {
        //increment the pitch by the amount param
        pitch -= amount; 
    }
    
    //method: walkForward
    //purpose: moves the camera forward in the direction it is facing
    public void walkForward(float distance) {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw)); 
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw)); 
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    //method: walkBackwards
    //purpose: moves the camera backward relative to its current rotation (yaw) 
    public void walkBackwards(float distance) {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw)); 
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw)); 
        position.x += xOffset;
        position.z -= zOffset;
    }
    
    //method: strafeLeft
    //purpose: strafes the camera left relative to its current rotation (yaw) 
    public void strafeLeft(float distance) {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw-90)); 
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw-90)); 
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    //method: strafeRight
    //purpose: strafes the camera right relative to its current rotation (yaw) 
    public void strafeRight(float distance) {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw+90)); 
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw+90)); 
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    //method: moveUp
    //purpose: moves the camera up relative to its current rotation (yaw) 
    public void moveUp(float distance) {
        position.y -= distance; 
    }
    
    //method: moveDown
    //purpose: moves the camera down
    public void moveDown(float distance) {
        position.y += distance; 
    }
    
    //method: lookThrough
    //purpose: translates and rotate the matrix so that it looks through the camera //this does basically what gluLookAt() does
    public void lookThrough() {
        //roatate the pitch around the X axis 
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //roatate the yaw around the Y axis 
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector's location 
        glTranslatef(position.x, position.y, position.z);
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.getX()).put(lPosition.getY()).put(lPosition.getZ()).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method: gameLoop
    //purpose: allows the user to control the view and position of the camera
    public void gameLoop() {
        FPCameraController camera = new FPCameraController(0, -100, 0);
        chunk = new Chunk(-25,0,-50);
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f;
        float lastTime = 0.0f; // when the last frame was 
        long time = 0;
        float mouseSensitivity = 0.09f; 
        float movementSpeed = .35f; //hide the mouse 
        Mouse.setGrabbed(true);
        
        // keep looping till the display window is closed the ESC key is down 
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            time = Sys.getTime(); 
            lastTime = time;
            //distance in mouse movement //from the last getDX() call.
            dx = Mouse.getDX();
            //distance in mouse movement //from the last getDY() call.
            dy = Mouse.getDY();
            
            //controll camera yaw from x movement from the mouse 
            camera.yaw(dx * mouseSensitivity);
            
            //controll camera pitch from y movement from the mouse 
            camera.pitch(dy * mouseSensitivity);
            
            //when passing in the distance to move
            //we times the movementSpeed with dt this is a time scale
            //so if its a slow frame u move more then a fast frame
            //so on a slow computer you move just as fast as on a fast computer
            
            //move forward
            if(Keyboard.isKeyDown(Keyboard.KEY_W)){
                camera.walkForward(movementSpeed); 
            }
            
            //move backwards 
            if(Keyboard.isKeyDown(Keyboard.KEY_S)){
                camera.walkBackwards(movementSpeed); 
            }
            
            //strafe left 
            if(Keyboard.isKeyDown(Keyboard.KEY_A)) {
                camera.strafeLeft(movementSpeed);
            }
            
            //strafe right
            if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
                camera.strafeRight(movementSpeed); 
            }
            
            //move up 
            if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) { 
                camera.moveUp(movementSpeed);
            }
            
            //move down
            if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                camera.moveDown(movementSpeed);
            }
            
            //set the modelview matrix back to the identity 
            glLoadIdentity();
            //look through the camera before you draw anything 
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //you would draw your scene here.
            chunk.render();
            //draw the buffer to the screen 
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
      }
    
    //method: render
    //purpose: draws a cube of width 2 and with each side a different color
    private void render() { 
        try {
            glBegin(GL_QUADS);
            //Top square
            glColor3f(0.0f,0.0f,1.0f); 
            glVertex3f(2.0f, 2.0f, -2.0f);
            glVertex3f(-2.0f, 2.0f, -2.0f);
            glVertex3f(-2.0f, 2.0f, 2.0f);
            glVertex3f(2.0f, 2.0f, 2.0f);
            //Bottom square
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(2.0f, -2.0f, 2.0f); 
            glVertex3f(-2.0f, -2.0f, 2.0f); 
            glVertex3f(-2.0f, -2.0f, -2.0f); 
            glVertex3f(2.0f, -2.0f, -2.0f);
            //Front square
            glColor3f(0.0f,1.0f,0.0f);
            glVertex3f( 2.0f, 2.0f, 2.0f); 
            glVertex3f(-2.0f, 2.0f, 2.0f); 
            glVertex3f(-2.0f, -2.0f, 2.0f); 
            glVertex3f( 2.0f, -2.0f, 2.0f);
            //Back square
            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f(2.0f, -2.0f, -2.0f); 
            glVertex3f(-2.0f, -2.0f, -2.0f); 
            glVertex3f(-2.0f, 2.0f, -2.0f);
            glVertex3f(2.0f, 2.0f, -2.0f);
            //Left square
            glColor3f(1.0f, 0.0f, 1.0f);
            glVertex3f(-2.0f, 2.0f, 2.0f); 
            glVertex3f(-2.0f, 2.0f, -2.0f); 
            glVertex3f(-2.0f, -2.0f, -2.0f); 
            glVertex3f(-2.0f, -2.0f, 2.0f);
            //Right square
            glColor3f(0.0f, 1.0f, 1.0f);
            glVertex3f(2.0f, 2.0f, -2.0f); 
            glVertex3f(2.0f, 2.0f, 2.0f); 
            glVertex3f(2.0f, -2.0f, 2.0f); 
            glVertex3f(2.0f, -2.0f, -2.0f);
            glEnd();   
        } catch(Exception e) {
            e.printStackTrace();
        } 
    }

}
    
