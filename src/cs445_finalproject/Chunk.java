/***************************************************************
* file: Chunk.java
* author: Adrian Hy, Jerahmeel Calma, Awais Ibrahim
* class: CS 445 – Computer Graphics
*
* assignment: Final Project
* date last modified: 05/28/18
*
* purpose: An object that holds 30 X 30 X 30 Block objects.
* Puts textures on each Block.
*
****************************************************************/

package cs445_finalproject;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture; 
import org.newdawn.slick.opengl.TextureLoader; 
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    private static final int CHUNK_SIZE = 90;
    private static final int CUBE_LENGTH = 2;
    private static final float persistenceMin = 0.07f;
    private static final float persistenceMax = 0.12f;
    private Block[][][] blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int startX, startY, startZ;
    private Random r;
    private int VBOTextureHandle;
    private Texture texture;
    
    //constructor: Chunk
    //purpose: initializes the instance variables of Chunk
    public Chunk(int startX, int startY, int startZ) {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png")); 
        }
        catch(Exception e) {
            System.out.print("Texture file not found.");
        }
        
        r = new Random();
        blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for(int x = 0; x < CHUNK_SIZE; x++) {
            for(int y = 0; y < CHUNK_SIZE; y++) {
                for(int z = 0; z < CHUNK_SIZE; z++) {
                    float random = r.nextFloat();
                    if(y == 0) {
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    } else if(y == 14 && random < 0.7f) {
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                    } else if(random > 0.8f && y <= 13) {
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    } else if(random <= 0.8f && y <= 13) {
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    } else if(y == 14 && random > 0.7f) {
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                    } else {
                        blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    }
                }
            }
        }
        VBOColorHandle= glGenBuffers();
        VBOVertexHandle= glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
    
    //method: render
    //purpose: draws a 30 X 30 X 30 Block object
    public void render() { 
        glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle); 
            glBindTexture(GL_TEXTURE_2D, 1); 
            glTexCoordPointer(2,GL_FLOAT,0,0L);
            glDrawArrays(GL_QUADS, 0, CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE*24);
        glPopMatrix();
    }
    
    //method: rebuildMesh
    //purpose: creates 30 X 30 X 30 Block object
    public void rebuildMesh(float startX, float startY, float startZ) {
        Random random = new Random();
        float persistence = 0;
        while(persistence < persistenceMin) {
            persistence = persistenceMax * random.nextFloat();
        }
        int seed = (int) (50 * random.nextFloat());
        SimplexNoise simplexNoise = new SimplexNoise(CHUNK_SIZE, persistence, seed);
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE* CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE* CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
        for(float x = 0; x < CHUNK_SIZE; x++) {
            for(float z = 0; z < CHUNK_SIZE; z++) {
                for(float y = 0; y < CHUNK_SIZE; y++) {
                    int height = (int) (startY + Math.abs((int)(CHUNK_SIZE * simplexNoise.getNoise((int)x, (int)z))) * CUBE_LENGTH) + 15;
                    if(y == height) {
                        break;
                    }
                    if(y == height - 1 && y != 14) {
                        blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Grass);
                    }
                    if(y == height - 2) {
                        blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Dirt);
                    }
                    if(y == height - 3 && y == 14) {
                        blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Dirt);
                    }
                    VertexPositionData.put(createCube((float) (startX+ x * CUBE_LENGTH), 
                            (float)(y*CUBE_LENGTH+(int)(CHUNK_SIZE*.8)), (float) (startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(blocks[(int) x][(int) y][(int) z])));
                    VertexTextureData.put(createTexCube((float) 0, (float)0,blocks[(int)(x)][(int) (y)][(int) (z)]));
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0); 
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle); 
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW); 
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    //method: createCubeVertexCol
    //purpose: creates an array that stores the color of a cube
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length*4*6];
        for(int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i%CubeColorArray.length];
        }
        return cubeColors;
    }
    
    //method: createCube
    //purpose: stores the vertices of a cube in an array
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
            // TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            // BOTTOM QUAD
            x + offset, y - offset, z - CUBE_LENGTH, 
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            // FRONT QUAD
            x + offset, y + offset, z - CUBE_LENGTH, 
            x - offset, y + offset, z - CUBE_LENGTH, 
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // BACK QUAD
            x + offset, y - offset, z, 
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH, 
            x - offset, y + offset, z, 
            x - offset, y - offset, z, 
            x - offset, y - offset, z - CUBE_LENGTH,
            // RIGHT QUAD
            x + offset, y + offset, z, 
            x + offset, y + offset, z - CUBE_LENGTH, 
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z };
    }
    
    //method: getCubeColor
    //purpose: returns default cube color which is white
    private float[] getCubeColor(Block block) {
        return new float[] { 1, 1, 1 };
    }
    
    //method: createTexCube
    //purpose: puts the respective textures on each type of Block
    public static float[] createTexCube(float x, float y, Block block) {
        
        float offset = (1024f/16)/1024f;
        switch (block.getID()) {
            case 0:
                //Grass Texture
                return new float[] {
                  //BOTTOM QUAD(DOWN=+y 
                    x+offset*3, y+offset*10,
                    x+offset*2, y+offset*10,
                    x+offset*2, y+offset*9,
                    x+offset*3, y+offset*9,
                    //TOP!
                    x+offset*3, y+offset*1,
                    x+offset*2, y+offset*1,
                    x+offset*2, y+offset*0,
                    x+offset*3, y+offset*0,
                    //FRONT QUAD
                    x+offset*3, y+offset*0,
                    x+offset*4, y+offset*0,
                    x+offset*4, y+offset*1,
                    x+offset*3, y+offset*1,
                    //BACK QUAD
                    x+offset*4, y+offset*1,
                    x+offset*3, y+offset*1,
                    x+offset*3, y+offset*0,
                    x+offset*4, y+offset*0,
                    //LEFT QUAD
                    x+offset*3, y+offset*0,
                    x+offset*4, y+offset*0,
                    x+offset*4, y+offset*1,
                    x+offset*3, y+offset*1,
                    //RIGHT QUAD
                    x+offset*3, y+offset*0,
                    x+offset*4, y+offset*0,
                    x+offset*4, y+offset*1,
                    x+offset*3, y+offset*1
                };
            case 1: 
                //Sand Texture
                return new float[] {
                  //BOTTOM QUAD(DOWN=+y 
                    x+offset*3, y+offset*2,
                    x+offset*2, y+offset*2,
                    x+offset*2, y+offset*1,
                    x+offset*3, y+offset*1,
                    //TOP!
                    x+offset*3, y+offset*2,
                    x+offset*2, y+offset*2,
                    x+offset*2, y+offset*1,
                    x+offset*3, y+offset*1,
                    //FRONT QUAD
                    x+offset*3, y+offset*2,
                    x+offset*2, y+offset*2,
                    x+offset*2, y+offset*1,
                    x+offset*3, y+offset*1,
                    //BACK QUAD
                    x+offset*3, y+offset*2,
                    x+offset*2, y+offset*2,
                    x+offset*2, y+offset*1,
                    x+offset*3, y+offset*1,
                    //LEFT QUAD
                    x+offset*3, y+offset*2,
                    x+offset*2, y+offset*2,
                    x+offset*2, y+offset*1,
                    x+offset*3, y+offset*1,
                    //RIGHT QUAD
                    x+offset*3, y+offset*2,
                    x+offset*2, y+offset*2,
                    x+offset*2, y+offset*1,
                    x+offset*3, y+offset*1
                };
            case 2:
                //Water Texture
                return new float[] {
                  //BOTTOM QUAD(DOWN=+y 
                    x+offset*3, y+offset*12,
                    x+offset*2, y+offset*12,
                    x+offset*2, y+offset*11,
                    x+offset*3, y+offset*11,
                    //TOP!
                    x+offset*3, y+offset*12,
                    x+offset*2, y+offset*12,
                    x+offset*2, y+offset*11,
                    x+offset*3, y+offset*11,
                    //FRONT QUAD
                    x+offset*3, y+offset*12,
                    x+offset*2, y+offset*12,
                    x+offset*2, y+offset*11,
                    x+offset*3, y+offset*11,
                    //BACK QUAD
                    x+offset*3, y+offset*12,
                    x+offset*2, y+offset*12,
                    x+offset*2, y+offset*11,
                    x+offset*3, y+offset*11,
                    //LEFT QUAD
                    x+offset*3, y+offset*12,
                    x+offset*2, y+offset*12,
                    x+offset*2, y+offset*11,
                    x+offset*3, y+offset*11,
                    //RIGHT QUAD
                    x+offset*3, y+offset*12,
                    x+offset*2, y+offset*12,
                    x+offset*2, y+offset*11,
                    x+offset*3, y+offset*11
                };
            case 3:
                //Dirt Texture
                return new float[] {
                  //BOTTOM QUAD(DOWN=+y 
                    x+offset*3, y+offset*1,
                    x+offset*2, y+offset*1,
                    x+offset*2, y+offset*0,
                    x+offset*3, y+offset*0,
                    //TOP!
                    x+offset*3, y+offset*1,
                    x+offset*2, y+offset*1,
                    x+offset*2, y+offset*0,
                    x+offset*3, y+offset*0,
                    //FRONT QUAD
                    x+offset*3, y+offset*1,
                    x+offset*2, y+offset*1,
                    x+offset*2, y+offset*0,
                    x+offset*3, y+offset*0,
                    //BACK QUAD
                    x+offset*3, y+offset*1,
                    x+offset*2, y+offset*1,
                    x+offset*2, y+offset*0,
                    x+offset*3, y+offset*0,
                    //LEFT QUAD
                    x+offset*3, y+offset*1,
                    x+offset*2, y+offset*1,
                    x+offset*2, y+offset*0,
                    x+offset*3, y+offset*0,
                    //RIGHT QUAD
                    x+offset*3, y+offset*1,
                    x+offset*2, y+offset*1,
                    x+offset*2, y+offset*0,
                    x+offset*3, y+offset*0
                };
            case 4: 
                //Stone Texture
                return new float[] {
                  //BOTTOM QUAD(DOWN=+y 
                    x+offset*1, y+offset*2,
                    x+offset*0, y+offset*2,
                    x+offset*0, y+offset*1,
                    x+offset*1, y+offset*1,
                    //TOP!
                    x+offset*1, y+offset*2,
                    x+offset*0, y+offset*2,
                    x+offset*0, y+offset*1,
                    x+offset*1, y+offset*1,
                    //FRONT QUAD
                    x+offset*1, y+offset*2,
                    x+offset*0, y+offset*2,
                    x+offset*0, y+offset*1,
                    x+offset*1, y+offset*1,
                    //BACK QUAD
                    x+offset*1, y+offset*2,
                    x+offset*0, y+offset*2,
                    x+offset*0, y+offset*1,
                    x+offset*1, y+offset*1,
                    //LEFT QUAD
                    x+offset*1, y+offset*2,
                    x+offset*0, y+offset*2,
                    x+offset*0, y+offset*1,
                    x+offset*1, y+offset*1,
                    //RIGHT QUAD
                    x+offset*1, y+offset*2,
                    x+offset*0, y+offset*2,
                    x+offset*0, y+offset*1,
                    x+offset*1, y+offset*1
                };
            case 5:
                //Bedrock Texture
                return new float[] {
                  //BOTTOM QUAD(DOWN=+y 
                    x+offset*2, y+offset*2,
                    x+offset*1, y+offset*2,
                    x+offset*1, y+offset*1,
                    x+offset*2, y+offset*1,
                    //TOP!
                    x+offset*2, y+offset*2,
                    x+offset*1, y+offset*2,
                    x+offset*1, y+offset*1,
                    x+offset*2, y+offset*1,
                    //FRONT QUAD
                    x+offset*2, y+offset*2,
                    x+offset*1, y+offset*2,
                    x+offset*1, y+offset*1,
                    x+offset*2, y+offset*1,
                    //BACK QUAD
                    x+offset*2, y+offset*2,
                    x+offset*1, y+offset*2,
                    x+offset*1, y+offset*1,
                    x+offset*2, y+offset*1,
                    //LEFT QUAD
                    x+offset*2, y+offset*2,
                    x+offset*1, y+offset*2,
                    x+offset*1, y+offset*1,
                    x+offset*2, y+offset*1,
                    //RIGHT QUAD
                    x+offset*2, y+offset*2,
                    x+offset*1, y+offset*2,
                    x+offset*1, y+offset*1,
                    x+offset*2, y+offset*1
                };
        }
        
        return new float[] {
                  //BOTTOM QUAD(DOWN=+y 
                    x+offset*3, y+offset*10,
                    x+offset*2, y+offset*10,
                    x+offset*2, y+offset*9,
                    x+offset*3, y+offset*9,
                    //TOP!
                    x+offset*3, y+offset*1,
                    x+offset*2, y+offset*1,
                    x+offset*2, y+offset*0,
                    x+offset*3, y+offset*0,
                    //FRONT QUAD
                    x+offset*3, y+offset*0,
                    x+offset*4, y+offset*0,
                    x+offset*4, y+offset*1,
                    x+offset*3, y+offset*1,
                    //BACK QUAD
                    x+offset*4, y+offset*1,
                    x+offset*3, y+offset*1,
                    x+offset*3, y+offset*0,
                    x+offset*4, y+offset*0,
                    //LEFT QUAD
                    x+offset*3, y+offset*0,
                    x+offset*4, y+offset*0,
                    x+offset*4, y+offset*1,
                    x+offset*3, y+offset*1,
                    //RIGHT QUAD
                    x+offset*3, y+offset*0,
                    x+offset*4, y+offset*0,
                    x+offset*4, y+offset*1,
                    x+offset*3, y+offset*1
                };
    }
    
}
