package ThreadStuff.ThreadedFileSearchPlus;
import static java.nio.file.FileVisitResult.CONTINUE;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;


import Paths.MkDir.CopyTo;

public class SearchThread implements Runnable{
    TheGuardedBlocks blocks;
    static int whichText = 0;
    SearchThread(TheGuardedBlocks blocks){
        this.blocks = blocks;
    }
    class ClimbingTree extends SimpleFileVisitor<Path>{
        String directoryToSearch = whatToFind();
        String whatToFind = whatToFind();
        String whereToSend = whatToFind();

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attr){
            String finding = whatToFind;
            if(finding == null){
                System.out.println("Value to find must not be null...");
                System.exit(-1);
            }
            Path modifiedExisiting = null;
            if(attr.isSymbolicLink()){
                System.out.format("Symbolic link at %s", file);
            }else if(attr.isRegularFile()){
                System.out.format("File is at %s", file);
                if(file.toString().contains(finding)){
                    if(Files.exists(Paths.get(whereToSend+file.getFileName()))){
                        modifiedExisiting = AppendExisiting.appendExisiting
                        (file.toFile(),Paths.get(whereToSend));
                        synchronized(blocks){ 
                            blocks.addToOriginalPath(file);
                            blocks.setWhereToSend(whereToSend);                           
                            blocks.addToCopyList(modifiedExisiting);
                            blocks.notifyAll();
                        }
                        
                    }else{
                        // CopyTo.copyTo(file.toAbsolutePath(), Paths.get
                        // (whereToSend+file.toString()));
                        
                        synchronized(blocks){
                            blocks.addToOriginalPath(file);
                            blocks.setWhereToSend(whereToSend); 
                            blocks.addToCopyList(file);                            
                            blocks.notifyAll();
                        }
                    }
                }
            }else if(attr.isOther()){
                System.out.format("Other: %s", file);
                if(file.toString().contains(finding)){
                    synchronized(blocks){
                        //blocks.addToCopyList(file.toAbsolutePath(),Paths.get(whereToSend+file.getFileName()));
                    }
                    
                }
            }
            System.out.println("(" + attr.size() + " bytes)");
            return CONTINUE;
        }
        public FileVisitResult visitFile(Path file, BasicFileAttributes attr,
        EnumSet<FileVisitOption> opts,int depth){ 
            String finding = whatToFind;       
            Path modifiedExisiting = null;
            if(Files.isSymbolicLink(file)){
                System.out.format("Symbolic link at %s", file);
            }else if(Files.isRegularFile(file)){
                System.out.format("File is at %s", file);
                if(file.toString().contains(finding)){
                    if(Files.exists(Paths.get(whereToSend+finding))){
                        modifiedExisiting = AppendExisiting.appendExisiting
                        (file.toFile(),Paths.get(whereToSend));
                        CopyTo.copyTo(file,modifiedExisiting.toAbsolutePath());
                    }
                    CopyTo.copyTo(file.toAbsolutePath(), Paths.get
                    (whereToSend+finding));
                }
            }else if(attr.isOther()){
                System.out.format("Other: %s", file);
                if(file.toString().contains(finding)){
                    CopyTo.copyTo( file.toAbsolutePath(),Paths.get
                    (whereToSend+finding));
                }
            }
            System.out.println("(" + attr.size() + " bytes)");
            return CONTINUE;
        }
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc){
            System.out.format("Directory %s%n",dir);
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc){
            System.err.format("%s",exc.toString());
            return CONTINUE;
        }
    }
    private static String whatToFind(){
        if(whichText == 0){
            System.out.println("Enter what directory to search...");
            whichText++;
        }else if(whichText == 1){
            System.out.println("Enter part or all of the files name to be searched for...");
            whichText++;
        }else if(whichText ==2){
            System.out.println("Enter where you would like to send files found...");
            whichText++;
        }else{
            System.err.println("Something has gone wrong...");
            System.exit(-1);
        }
        StringBuilder search = new StringBuilder();
        String retString = null;        
        try{               
            while(search.append(Character.toString
            (System.in.read())).toString().charAt(search.length() -1)!='\n'
            && search.charAt(search.length() -1)  != '\r'+'\n');               
                System.out.println(search.toString());
        }catch(IOException e){
            e.printStackTrace();
        }
        search.deleteCharAt(search.length()-1);
        retString = search.toString();
        return retString;
    }
    public void run(){
        ClimbingTree h = new ClimbingTree();
        try{
            Files.walkFileTree(Paths.get(h.directoryToSearch),h);
        }catch(IOException e){
            System.out.println("Failed to find directory");
        }        
        synchronized(blocks){
            blocks.searchStatusToggle();
            blocks.waitForCopyToEmpty();
            blocks.runStatusToggle();
            blocks.notifyAll();
        }
    }

}
