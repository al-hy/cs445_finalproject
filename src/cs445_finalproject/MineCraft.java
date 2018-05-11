/***************************************************************
* file: Minecraft.java
* author: Adrian Hy, Jerahmeel Calma, Awais Ibrahim
* class: CS 445 – Computer Graphics
*
* assignment: Final Project
* date last modified: 05/07/18
*
* purpose: Creates a 640 x 480 window that allows the user to 
* view a cube with six different colored sides. The user can move
* the camera using the mouse and keyboard.
*
****************************************************************/
package cs445_finalproject;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class MineCraft {
    private FPCameraController fp;
    private DisplayMode displayMode;
    
    //method: start
    //purpose: calls methods to set up the window for the program
    public void start() {
        try {
            createWindow();
            initGL();
            fp = new FPCameraController(0f,0f,0f);
            fp.gameLoop();//render();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //method: createWindow
    //purpose: disables fullscreen; sets the size of the window; sets the 
    // title of the window; creates the window for the program
    private void createWindow() throws Exception{
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for(int i = 0; i < d.length; i++) {
            if(d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) { 
                displayMode= d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode); 
        Display.setTitle("Bootleg Minecraft lol");
        Display.create();
    }
    
    //method: initGL
    //purpose: sets the window's background color to black; loads the camera
    // to view what is being displayed on the window; loads the identity
    // matrix; set the scene to model view; provides some rendering hints
    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float) displayMode.getWidth() / (float) displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState (GL_TEXTURE_COORD_ARRAY);
    }
    
    //method: main
    //purpose: creates an object for the program; allows the program to run
    public static void main(String[] args) {
        MineCraft mineCraft = new MineCraft();
        mineCraft.start();
    }
    
}
