package identity_component.java_approach.src;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileHelper {
    static String myRootDir = "identity_component\\java_approach";
    static String outputsDir = "identity_component\\java_approach\\outputs";
    public static void main(String[] args) throws CustomException, IOException {
        // printDirectoryAndContents(new File(myRootDir));
        // deleteAllEmptyFiles(new File(outputsDir));
        moveAllOutputFiles(new File(outputsDir), 1, 180);
        
    }

    public static void moveAllOutputFiles(File directory, int mMin, int mMax) throws CustomException, IOException {
        for (File filename : directory.listFiles()) { // for each file in this folder
            // if it is a folder, skip
            if (filename.isDirectory()) {
                continue;
            }

            // if it is empty, delete it
            if (filename.length() == 0) {
                if (filename.delete()) {
                    System.out.println("\"" + filename + "\" was deleted.");
                }
            }

            // get m and d value, skip if m value is not in the desired range
            int m = mOf(filename);
            if (m < mMin || m > mMax) continue;

            // create new directory (for m) if doesn't exist
            String newDirName = "m_" + m;
            createFolderIfNotExist(directory, newDirName);

            // move current file from current folder to the new folder
            moveFile(directory, newDirName, filename.getName());
        }
    }

    public static void deleteAllEmptyFiles(File directory) {
        for (File filename : directory.listFiles()) {
            if (filename.isDirectory()) {
                deleteAllEmptyFiles(filename);
            } else {
                if (filename.length() == 0) {
                    if (filename.delete()) {
                        System.out.println("\"" + filename + "\" was deleted.");
                    }
                }
            }
        }
    }

    /**Given a directory, print its name, and the name of all its contents (including files and more directories) recursively.
     * 
     * @param directory - a File object
     */
    public static void printDirectoryAndContents(File directory) {
        printDirectoryAndContents(directory, 0);
    }
    
    public static void printDirectoryAndContents(File directory, int depth) {
        System.out.println(nIndent(depth) + directory.getName() + ": ");
        for (File filename : directory.listFiles()) {
            if (filename.isDirectory()) {
                printDirectoryAndContents(filename, depth+1);
            } else {
                System.out.println(nIndent(depth+1) + filename.getName());
            }
        }
    }

    /** 
     * 
     * @param n - number of two-spaced indents to return
     * @return a String, representing two-spaced indents, repeated n times
     */
    public static String nIndent(int n) {
        String indent = "  ";
        if (n == 0) return "";
        if (n == 1) return indent;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(indent);
        }
        return sb.toString();
    }

    public static int pOf(File file) {
        String filename = file.getName();
        if (!filename.contains("output")) {
            return -1;
        }
        filename = filename.substring(filename.indexOf("p_") + 2, filename.indexOf("_m_"));
        return Integer.parseInt(filename);
    }

    public static int mOf(File file) {
        String filename = file.getName();
        if (!filename.contains("output")) {
            return -1;
        }
        filename = filename.substring(filename.indexOf("_m_") + 3, filename.indexOf("_d_"));
        return Integer.parseInt(filename);
    }

    public static int dOf(File file) {
        String filename = file.getName();
        if (!filename.contains("output")) {
            return -1;
        }
        filename = filename.substring(filename.indexOf("_d_") + 3, filename.indexOf(".txt"));
        return Integer.parseInt(filename);
    }

    public static void createFolderIfNotExist(File path, String folderName) throws CustomException {
        File newDirectory = new File(path, folderName);
        if (!newDirectory.exists()) {
            boolean created = newDirectory.mkdir();
            if (!created) throw new CustomException("Error occurred when trying to create " + newDirectory.getName());   
        }
    }

    public static void moveFile(File fromDir, String toDirName, String toMoveFileName) throws IOException {
        Path source = fromDir.toPath().resolve(toMoveFileName);
        Path target = fromDir.toPath().resolve(toDirName).resolve(toMoveFileName);
        Files.move(source, target);
    }
}
