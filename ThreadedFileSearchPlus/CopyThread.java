package ThreadStuff.ThreadedFileSearchPlus;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CopyThread implements Runnable{
    TheGuardedBlocks blocks;
    int numberCopied = 0;
    CopyThread(TheGuardedBlocks blocks){
        this.blocks = blocks;
    }
    public void run(){
        Boolean searchStatus = true;
        blocks.waitingForFileFromSearch();
        do{

            try{
            Thread.sleep(0,50);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        Path o = blocks.removeFromOriginalPath();
        Path h = blocks.removeFromCopyList();
        if(o == null | h == null){
            try{
                wait();
            }catch(InterruptedException e){
                e.printStackTrace();
                System.exit(-1);
            }
        }
        copy(h,o);
        numberCopied++;
        synchronized(blocks){
            searchStatus = blocks.getSearchStatus();
        }
        }while(numberCopied != blocks.getcListNumber() && !searchStatus);

        synchronized(blocks){
            blocks.clearCopyList();
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
