import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Frame {
    private byte[] content;

    private boolean dirty;

    private boolean pinned;

    private int blockId;


    //constructor
    public Frame(byte[] Content, boolean Dirty, boolean Pinned, int BlockId) throws FileNotFoundException {
        content = Content;
        dirty = Dirty;
        pinned = Pinned;
        blockId = BlockId;
    }


    // Getters
    public byte[] getContent() {
        return Arrays.copyOf(content, content.length);
    }
    public boolean getDirty() {
        return dirty;
    }
    public boolean getPinned() {
        return pinned;
    }
    public int getBlockId() {
        return blockId;
    }

    // Setters
    public void setDirty(boolean newDirty) {
        this.dirty = newDirty;
    }
    public void setPinned(boolean newPinned) {
        this.pinned = newPinned;
    }
    public void setBlockId(int newBlockId) {
        this.blockId = newBlockId;
    }
    public void setContent(byte[] content) throws FileNotFoundException {
        this.content = content;
    }

    //Reads record content of inputted record number
    public String readRecord(int record) throws FileNotFoundException {
        File file = new File("Project1/F" + blockId +".txt");
        Scanner sc = new Scanner(file).useDelimiter("\\.");
        String rec = "";
        int k = 100-((blockId * 100)-record);
        for(int i = 0; i<k;i++){
            rec = sc.next();
        }
        return rec;
    }


}
