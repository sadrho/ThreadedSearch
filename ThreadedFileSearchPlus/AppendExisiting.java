package ThreadStuff.ThreadedFileSearchPlus;

import java.io.*;
import java.nio.file.*;

public class AppendExisiting {
    private static int copyCount = 0;
    private static String testResetCount = null;
    public static Path appendExisiting(File f,Path d){
        if(testResetCount == null){
            testResetCount = f.toPath().getFileName().toString();
        }
        Path returnPath = null;
        int fileExtentionIndex = 0;
        String fileName = f.toPath().getFileName().toString();
        
        StringBuilder destinationPath = new StringBuilder();
        if(d.toString().endsWith("/")){
            destinationPath.append(d+String.format("%s",fileName));
        }else if(!d.toString().endsWith("/")){
            destinationPath.append(d+String.format("/%s",fileName));
        }
        
        StringBuilder modifiedPath = new StringBuilder();
        if(fileName.contains(".")){
            
            String splits[] = fileName.split("/W");
            int count = 0;
            for(String split: splits){
                System.out.println("THIS!"+split);
                count++;
            }
            System.out.println(count);

            if(Paths.get(destinationPath.toString()).toFile().exists()){
                copyCount++;
                fileExtentionIndex = destinationPath.toString().lastIndexOf(".");
                modifiedPath.append(destinationPath.toString().substring(0, fileExtentionIndex));
                modifiedPath.append(String.format("Copy(%d)",copyCount));
                modifiedPath.append(destinationPath.toString().substring(fileExtentionIndex,
                (destinationPath.toString().length()%modifiedPath.length())));
                returnPath = Paths.get(modifiedPath.toString());
            }
        }else if(!fileName.contains(".")){
            if(Paths.get(destinationPath.toString()).toFile().exists()){
                copyCount++;
                modifiedPath.delete(0,modifiedPath.length());
                modifiedPath.append(destinationPath + String.format("Copy(%d)", copyCount));
                returnPath = Paths.get(modifiedPath.toString());
            }else{
                returnPath = Paths.get(destinationPath.toString());
            }        
        }
    return returnPath;
    }
}
