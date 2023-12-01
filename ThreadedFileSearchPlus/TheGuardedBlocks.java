package ThreadStuff.ThreadedFileSearchPlus;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;


public class TheGuardedBlocks {
    private Boolean run = true;
    private Boolean searching =true;
    private static Integer bListNumber = 0;
    private static Integer cListNumber = 0;
    private static Integer mListNumber = 0;
    private Boolean copying = false;
    private String whereToSend;
    private Boolean moving = false;
    private static ConcurrentHashMap<Integer,Path> originalFilePath = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer,Path> copyList = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer,Path> modifiedList = new ConcurrentHashMap<>();
    // private static ConcurrentHashMap<Integer,Path> moveList = new ConcurrentHashMap<Integer,Path>();
    public void addToCopyList(Path file){
        copyList.put(cListNumber,file);        
        cListNumber++;
    }
    public void addToOriginalPath(Path file){
        int originalFileNum = cListNumber;
        originalFilePath.put(originalFileNum,file);
    }
    public Boolean isCopyEmpty(){
        Boolean isEmpty = null;
        if(copyList.size() == 0){
            isEmpty = true;
        }else{
            isEmpty = false;
        }
        return isEmpty;
    }

    
    public String getWhereToSend(){
        return whereToSend;
    }

    public void waitForCopyToEmpty(){
        while(!isCopyEmpty()){
            try{
                wait();
            }catch(InterruptedException e){
                System.out.println(Thread.currentThread().getName() + "was interrupted");
            }
        }
    }
    public void setWhereToSend(String whereToSend){
        this.whereToSend = whereToSend;
    }
    public Boolean runStatus(){
        return run;
    }
    public void runStatusToggle(){
        if(run){
            run = false;
        }else{
            run = true;
        }
    }
    public void copyStatusToggle(){
        if(copyList.size() > 0){
            copying = true;
        }else{
            copying = false;
        }
    }
    public Path getModifiedPath(){        
        Path returnedPath = modifiedList.get(modifiedList.size() - 1);
        
        return returnedPath;
    }
    public Path removeFromCopyList(){
        if(copyList.size() > 0){
            synchronized(this){
                copying = true;
            }
        }else{
            synchronized(this){
                copying = false;
            }
        }
        Path returnPath = copyList.get(bListNumber);
        // moveList.put(bListNumber, returnPath);
        copyList.remove(bListNumber);
        
        bListNumber++;
        return returnPath;
    }
    public Path removeFromOriginalPath(){
        if(originalFilePath.size() > 0){
            synchronized(this){
                copying = true;
            }
        }else{
            synchronized(this){
                copying = false;
            }
        }
        Path returnPath = originalFilePath.get(mListNumber);
        // moveList.put(mListNumber, returnPath);
        originalFilePath.remove(mListNumber);
        
        mListNumber++;
        return returnPath;
    }

    public synchronized void findSendContinue(){
        while(!searching){
            try{
                wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        System.out.println("Continuing");
    }

    public synchronized void waitingForFileFromSearch(){
        
            while(copyList.size() <= 0){
                try{
                    if(run)
                    wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        
    }
    public synchronized void waitingForFileCopy(){
        while(!moving){
            try{
                wait();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
    

