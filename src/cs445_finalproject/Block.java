/***************************************************************
* file: Block.java
* author: Adrian Hy, Jerahmeel Calma, Awais Ibrahim
* class: CS 445 – Computer Graphics
*
* assignment: Final Project
* date last modified: 05/07/18
*
* purpose: An object that holds information about a single block.
* Holds the block's type, its position, and whether it is active
* or not.
*
****************************************************************/

package cs445_finalproject;

public class Block {
    private boolean active;
    private BlockType type;
    private float x,y,z;
    
    //enum: BlockType
    //method: creates an enum BlockType which will determine the texture that
    // will be put on a block
    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);
        
        private int blockID;
        BlockType(int i) {
            blockID = i;
        }
        
        //method: getID
        //purpose: returns the blockID of a block
        public int getID() {
            return blockID;
        }
        
        //method: setID
        //purpose: changes the blockID to the inputted value
        public void setID(int i){
            blockID = i;
        }
    }
    
    //constructor: Block
    //purpose: initializes Block's instance variable type
    public Block(BlockType type) {
        this.type = type;
    }
    
    //method: setCoords
    //purpose: initializes Block's instance variables x, y, and z
    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    //method: isActive
    //purpose: returns boolean value of whether the block is active
    public boolean isActive() {
        return active;
    }
    
    //method: setActive
    //purpose: changes the boolean value of the instance variable active
    // to the inputted value
    public void setActive(boolean active) {
        this.active = active;
    }
    
    //method: getID
    //purpose: returns blockID from enum BlockType
    public int getID() {
        return type.getID();
    }
}
