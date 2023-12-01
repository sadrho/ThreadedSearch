package ThreadStuff.ThreadedFileSearchPlus;

public class TestRun {
    public static void main(String... args){
        
        
        TheGuardedBlocks heyo= new TheGuardedBlocks();
        Thread a = new Thread(new SearchThread(heyo));
        Thread b = new Thread(new CopyThread(heyo));
        a.start();
        b.start();
        
    }
}
