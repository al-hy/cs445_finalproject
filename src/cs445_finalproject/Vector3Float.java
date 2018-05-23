/***************************************************************
* file: Vector3Float.java
* author: Adrian Hy, Jerahmeel Calma, Awais Ibrahim
* class: CS 445 – Computer Graphics
*
* assignment: Final Project
* date last modified: 05/23/18
*
* purpose: An object to hold our camera’s position in 3D space.
*
****************************************************************/
package cs445_finalproject;

public class Vector3Float {
    private float x, y, z;
    
    //constructor: Vector3fFloat
    //purpose: initializes the x, y, and z values of a vector
    public Vector3Float(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    //method: getX
    //purpose: returns x value of camera
    public float getX() {
        return x;
    }
    
    //method: setX
    //purpose: sets x value of camera
    public void setX(float x) {
        this.x = x;
    }
    
    //method: getY
    //purpose: returns y value of camera
    public float getY() {
        return y;
    }
    
    //method: setY
    //purpose: sets y value of camera
    public void setY(float y) {
        this.y = y;
    }
    
    //method: getZ
    //purpose: returns z value of camera
    public float getZ() {
        return z;
    }
    
    //method: setZ
    //purpose: sets z value of camera
    public void setZ(float z) {
        this.z = z;
    }
}
