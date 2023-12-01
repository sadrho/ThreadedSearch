package ThreadStuff.ThreadedFileSearchPlus;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CopyThread implements Runnable{
    TheGuardedBlocks blocks;
    CopyThread(TheGuardedBlocks blocks){
        this.blocks = blocks;
    }
    public void run(){
        blocks.waitingForFileFromSearch();
        while(blocks.runStatus()){
            try{
            Thread.sleep(0,50);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        Path o = blocks.removeFromOriginalPath();
        Path h = blocks.removeFromCopyList();
        copy(h,o);
        }
        synchronized(blocks){
            blocks.copyStatusToggle();
            blocks.notifyAll();
        }
    }
    public void copy(Path file, Path original){
        try{
            if(file != null)
            System.err.println("LOOOKKKKKKKKK AA A A TTTTT THIIISSS: " +file.getFileName().toString());
        Files.copy(original.toAbsolutePath(),Paths.get(blocks.getWhereToSend()+file.getFileName().toString()));
        
        
        }catch(AccessDeniedException e){
            System.out.format("You don't have permission to move %s%n",file.toString());
        }catch(IOException o){
            
        
            System.out.println(o.getLocalizedMessage());
        }
       
        
    }
     
}
