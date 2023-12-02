package ThreadStuff.ThreadedFileSearchPlus;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;


public class TheGuardedBlocks {
    private static BlockingQueue<Path> newCopyList = new SynchronousQueue<>();
    private static BlockingQueue<Path> newModifiedPath = new SynchronousQueue<>();
    private Boolean run = true;
    private Boolean isEmpty = true;
    private static Boolean searching =false;
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
    public int getCopyListCapacity(){
        int retInt = newCopyList.remainingCapacity();
        return retInt;
    }
    public void addToNewCopyList(Path file){
        try{
            newCopyList.offer(file,10,TimeUnit.SECONDS);
                
                
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
    public Path removedFromNewCopyList(){
        Path retPath = null;
        try{
            retPath = newCopyList.poll(10,TimeUnit.SECONDS);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return retPath;
    }
    public Path removeFromModifiedPath(){
        Path retPath = null;
        try{
            retPath = newModifiedPath.poll(10,TimeUnit.SECONDS);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return retPath;
    }
    public void addToNewPath(Path file){
        try{
            newModifiedPath.offer(file,10,TimeUnit.SECONDS);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
    public void addToOriginalPath(Path file){
        int originalFileNum = cListNumber;
        originalFilePath.put(originalFileNum,file);
    }
    public Boolean isCopyEmpty(){
        
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
    public void clearCopyList(){
        copyList.clear();
    }
    public int getcListNumber(){
        return cListNumber;
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
    public void searchStatusToggle(){
        if(searching){
            searching = false;
        }else{
            searching = true;
        }
        notifyAll();
    }
    public Boolean getSearchStatus(){
        return searching;
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
        
            while(!searching){
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
    

