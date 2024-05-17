import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class BufferPool {

    private Frame[] buffers;

    public BufferPool(Frame[] Buffers){
        buffers = Buffers;
    }

    public String GET(int record) throws IOException {
        int fileNum = 0;
        if(1<=record && record<=100){
            fileNum = 1;
        }
        else if(101<=record && record<=200){
            fileNum = 2;
        }
        else if(201<=record && record<=300){
            fileNum = 3;
        }
        else if(301<=record && record<=400){
            fileNum = 4;
        }
        else if(401<=record && record<=500){
            fileNum = 5;
        }
        else if(501<=record && record<=600){
            fileNum = 6;
        }
        else if(601<=record && record<=700) {
            fileNum = 7;
        }
        Frame Fr = contentInBuf(fileNum);
        if(Fr.getBlockId() == 0){
            return "The corresponding block #" + fileNum + " cannot be accessed " +
                    "from disk because the memory buffers are full";
        }
        String rec = "";

        //find block in buffer and then grab desired record
        for(int i = 0; i<buffers.length;i++){
            if(buffers[i] != null){
                if(buffers[i].getBlockId() == fileNum){
                    String con = new String(buffers[i].getContent());
                    int k = (100-((fileNum * 100)-record)) * 40;
                    rec = con.substring(k-40, k);
                }
            }
        }
        return rec;
    }


    public String SET(int record, String str) throws IOException {
        int fileNum = 0;
        if(1<=record && record<=100){
            fileNum = 1;
        }
        else if(101<=record && record<=200){
            fileNum = 2;
        }
        else if(201<=record && record<=300){
            fileNum = 3;
        }
        else if(301<=record && record<=400){
            fileNum = 4;
        }
        else if(401<=record && record<=500){
            fileNum = 5;
        }
        else if(501<=record && record<=600){
            fileNum = 6;
        }
        else if(601<=record && record<=700) {
            fileNum = 7;
        }

        Frame Fr = contentInBuf(fileNum);
        if(Fr.getBlockId() == 0){
            System.out.println("Write Was Not Successful");
            return "The corresponding block #" + fileNum + " cannot be accessed " +
                    "from disk because the memory buffers are full";
        }

        int frameNum = 0;
        //find block in buffer and set content to user inputted content
        for(int i = 0; i<buffers.length;i++){
            if(buffers[i] != null){
                if(buffers[i].getBlockId() == fileNum){
                    String con = new String(buffers[i].getContent());
                    int k = (100-((fileNum * 100)-record)) * 40;
                    con = con.replace(con.substring(k-40, k),str);
                    buffers[i].setContent(con.getBytes());
                    buffers[i].setDirty(true);
                    System.out.println("Write Was Successful");
                    frameNum = i;
                }
            }
        }

        return "Frame #" + frameNum + " Contains The Modified Block.";
    }

    public String PIN(int blockId) throws IOException {
        Frame Fr = contentInBuf(blockId);
        int frameNum = 0;
        if(Fr.getBlockId() == 0){
            return "The corresponding block #" + blockId + " cannot be pinned " +
                    "because the memory buffers are full.";
        }
        for(int i = 0; i<buffers.length;i++){
            if(buffers[i] != null){
                if(buffers[i].getBlockId() == blockId){
                    if(buffers[i].getPinned() == true){
                        System.out.println("Pinned Flag Is Already True");
                    }
                    else {
                        buffers[i].setPinned(true);
                        System.out.println("Pinned Flag Was Set To True");
                    }
                    frameNum = i;
                    break;
                }
            }
        }
        return "Frame #" + frameNum + " Is Pinned.";
    }

    public String UNPIN(int blockId) throws FileNotFoundException {
        int frameNum = 0;
        for(int i = 0; i<buffers.length;i++){
            if(buffers[i] != null){
                if(buffers[i].getBlockId() == blockId){
                    if(buffers[i].getPinned() == false){
                        System.out.println("Pinned Flag Is Already False");
                    }
                    else {
                        buffers[i].setPinned(false);
                        System.out.println("Pinned Flag Was Set To False");
                    }
                    frameNum = i;
                    break;
                }
                else{
                    return("The corresponding block #" + blockId + " cannot be unpinned " +
                            "because it is not in memory.");
                }
            }
        }
        return "Frame #" + frameNum + " Is Unpinned.";
    }


    //initializes the buffer pool
    public void initialize(int frames){
        buffers = new Frame[frames];
    }


    //returns content in a Buffer spot if there is a block present else calls other function
    public Frame contentInBuf(int fileNum) throws IOException {
        Frame blockContent = null;
        int buffNum = buffNumber(fileNum);
        //if block is in memory return the blocks data
        if(buffNum != -1){
            blockContent = buffers[buffNum];
            System.out.println("Block Was Already In Memory.");
            System.out.println("Block Is In Frame #" + buffNum + ".");
        }
        //if block is not in memory(buffer) enter this function to try to bring it into buffer
        else{
            return blockNotInBuffer(fileNum);
        }
        return blockContent;
    }

    //returns slot number in the array where the inputted block(file) is
    public int buffNumber(int fileNum){
        int slotNum = -1;
        for(int i = 0; i < buffers.length;i++) {
            if(buffers[i] != null){
                if(buffers[i].getBlockId() == fileNum){
                    slotNum = i;
                    break;
                }
            }
        }
        return slotNum;
    }

    //if block is not in buffer add it into an empty frame. if no available empty frames
    //replace one frame.
    public Frame blockNotInBuffer(int fileNum) throws IOException {
        File file = new File("Project1/F" + fileNum + ".txt");
        Scanner sc = new Scanner(file).useDelimiter("\\.");
        String recContent = "";
        int emptyFrame = searchEmptyFrame();
        //reads context from disk
        while(sc.hasNext()){
            recContent += sc.next() + ".";
        }
        //if there is in empty frame set new frame to that location
        if(emptyFrame != -1){
            buffers[emptyFrame] = new Frame(recContent.getBytes(), false, false, fileNum);
            System.out.println("Block Was Brought From Disk.");
            System.out.println("Block Is In Frame #" + emptyFrame + ".");
            return buffers[emptyFrame];
        }
        else
            //if there are no empty frames but there is an unpinned frame enter if statement
            if(searchUnpinnedFrame() != -1){
                int unPind = searchUnpinnedFrame();
                //if unpinned frame is not dirty then overwrite data
                if(buffers[unPind].getDirty() == false) {
                    buffers[unPind] = new Frame(recContent.getBytes(), false, false, fileNum);
                    System.out.println("Block Was Brought From Disk.");
                    System.out.println("Block Is In Frame #" + unPind + ".");
                    return buffers[unPind];
                }
                //if unpinned frame is dirty then write data to disk and then overwrite frame
                else{
                    System.out.println("HEEEERRRREEEE");
                    System.out.println(buffers[unPind].getBlockId());
                    byte[] newRecContent = buffers[unPind].getContent();
                    Path path = Paths.get("Project1/F" + buffers[unPind].getBlockId() + ".txt");
                    Files.write(path,newRecContent);
                    buffers[unPind] = new Frame(recContent.getBytes(), false, false, fileNum);
                    System.out.println("Block Was Brought From Disk.");
                    System.out.println("Block Is In Frame #" + unPind + ".");
                    return buffers[unPind];
                }
            }


        return new Frame(recContent.getBytes(), false, false, 0);

    }

    //Searches for in empty frame in buffer
    public int searchEmptyFrame(){
        int emptyF = -1;
        for(int i = 0; i < buffers.length;i++) {
            if (buffers[i] == null) {
                emptyF = i;
                break;
            }
        }
        return emptyF;
    }

    //searchs for an unpinned frame in buffer
    public int searchUnpinnedFrame(){
        int unPind = -1;
        for(int i = 0; i < buffers.length;i++) {
            if (buffers[i].getPinned() == false) {
                unPind = i;
                break;
            }
        }
        return unPind;

    }
}

