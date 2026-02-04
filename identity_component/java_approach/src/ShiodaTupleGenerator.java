package identity_component.java_approach.src; // **remove this if this file is NOT in a folder called 'src'

//===== libraries that are used for this program =====
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class ShiodaTupleGenerator {
    private static final String outputPath = "identity_component\\java_approach\\outputs";
    private boolean isGeneratingFirstHalfOnly;
    private boolean isPrintingIndecomposable;
    private boolean isPrintingModified;
    private boolean isPrintingModifiedMax;
    private boolean isPrintingModifiedWithoutMax;
    private boolean isPrintingRelation;
    private boolean overwritingOutputs;
    private int pStart;
    private int pEnd;

    public static void main(String[] args) throws CustomException, IOException {
        if (args.length != 2) throw new CustomException("please enter two arguments representing the starting and ending p values");

        int pStart = Integer.valueOf(args[0]);
        int pEnd = Integer.valueOf(args[1]);

        ShiodaTupleGenerator generator = new ShiodaTupleGenerator(
            pStart,    // pStart (inclusive)
            pEnd,    // pEnd (inclusive)
            false,    // overwriteOutputs 
            true,    // firstHalfOnly 
            true,    // printIndecomposable 
            true,    // printModified 
            true,    // printModifiedMax 
            true,    // printModifiedWithoutMax 
            true    // printRelation
        );
        
        generator.generate();
    }

    // simple constructor
    public ShiodaTupleGenerator(int pStart, int pEnd) throws CustomException, FileNotFoundException {
        this(pStart, pEnd, false,true,true, true, true, true, true);
    }

// full constructor
    public ShiodaTupleGenerator(int pStart, int pEnd, boolean overwriteOutputs, boolean firstHalfOnly, boolean printIndecomposable, boolean printModified, boolean printModifiedMax, boolean printModifiedWithoutMax, boolean printRelation) throws CustomException, FileNotFoundException {
        if (pStart < 3) throw new CustomException("The minimum starting p value is 3.");

        this.pStart = pStart;
        this.pEnd = pEnd;
        
        // init flags
        isGeneratingFirstHalfOnly = firstHalfOnly;
        overwritingOutputs = overwriteOutputs;
        isPrintingIndecomposable = printIndecomposable;
        isPrintingModified = printModified;
        isPrintingModifiedMax = printModifiedMax;
        isPrintingModifiedWithoutMax = printModifiedWithoutMax;
        isPrintingRelation = printRelation;
    }

    public void generate() throws FileNotFoundException, CustomException {
        createFolderIfNotExist();

        for (int p = pStart; p <= pEnd; p++) {
            if (p % 2 == 0) continue; // odd p only
            if (!isPrime(p)) continue; // prime p only

            generateShiodaIndecomposables(p);
        }
    }

    public void createFolderIfNotExist() throws CustomException, FileNotFoundException {
        if (isPrintingIndecomposable) FileHelper.createFolderIfNotExist(new File(outputPath), "indecomposable_csvs");
        if (isPrintingModified) FileHelper.createFolderIfNotExist(new File(outputPath), "modified_csvs");
        if (isPrintingModifiedMax) FileHelper.createFolderIfNotExist(new File(outputPath), "modified_max_csvs");
        if (isPrintingModifiedWithoutMax) FileHelper.createFolderIfNotExist(new File(outputPath), "modified_without_max_csvs");
        if (isPrintingRelation) FileHelper.createFolderIfNotExist(new File(outputPath), "relation_csvs");
    }

    public Set<Tuple> generateShiodaIndecomposables(int p) throws FileNotFoundException, CustomException {
        return generateShiodaIndecomposables(
            p, // int p
            p*p, // int m
            (p+1)/2 // int d
        );
    }

    public Set<Tuple> generateShiodaIndecomposables(int m, int d) throws FileNotFoundException, CustomException {
        return generateShiodaIndecomposables(
            (d*2)-1, // int p
            m, // int m
            d // int d
        );
    }

    public Set<Tuple> generateShiodaIndecomposables(int p, int m, int d) throws FileNotFoundException, CustomException {
        int size = 2 * d;
        HashSet<Tuple> indecomposableTuples = new HashSet<>();

        int left = p / 100 * 100;
        int right = left + 100;

        String fileDirIndecomposable = outputPath + "\\indecomposable_csvs";
        String fileDirModified = outputPath + "\\modified_csvs";
        String fileDirModifiedMax = outputPath + "\\modified_max_csvs";
        String fileDirModifiedWithoutMax = outputPath + "\\modified_without_max_csvs";
        String fileDirRelation = outputPath + "\\relation_csvs";

        String filepath = "p_" + left + "_to_" + right + "";
        String filename = "p_" + p + "_m_" + m + "_d_" + d + ".csv";

        if (isPrintingIndecomposable) FileHelper.createFolderIfNotExist(new File(fileDirIndecomposable), filepath);
        if (isPrintingModified) FileHelper.createFolderIfNotExist(new File(fileDirModified), filepath);
        if (isPrintingModifiedMax) FileHelper.createFolderIfNotExist(new File(fileDirModifiedMax), filepath);
        if (isPrintingModifiedWithoutMax) FileHelper.createFolderIfNotExist(new File(fileDirModifiedWithoutMax), filepath);
        if (isPrintingRelation) FileHelper.createFolderIfNotExist(new File(fileDirRelation), filepath);

        File fileIndecomposable = isPrintingIndecomposable ? new File(fileDirIndecomposable + "\\" + filepath + "\\" + filename) : null;
        File fileModified = isPrintingModified ? new File(fileDirModified + "\\" + filepath + "\\" + filename) : null;
        File fileModifiedMax = isPrintingModifiedMax ? new File(fileDirModifiedMax + "\\" + filepath + "\\" + filename) : null;
        File fileModifiedWithoutMax = isPrintingModifiedWithoutMax ? new File(fileDirModifiedWithoutMax + "\\" + filepath + "\\" + filename) : null;
        File fileRelation = isPrintingRelation ? new File(fileDirRelation + "\\" + filepath + "\\" + filename) : null;

        PrintWriter pwIndecomposable = (isPrintingIndecomposable && (overwritingOutputs || !fileIndecomposable.exists())) ? new PrintWriter(fileIndecomposable) : null;
        PrintWriter pwModified = (isPrintingModified && (overwritingOutputs || !fileModified.exists())) ? new PrintWriter(fileModified) : null;
        PrintWriter pwModifiedMax = (isPrintingModifiedMax && (overwritingOutputs || !fileModifiedMax.exists())) ? new PrintWriter(fileModifiedMax) : null;
        PrintWriter pwModifiedWithoutMax = (isPrintingModifiedWithoutMax && (overwritingOutputs || !fileModifiedWithoutMax.exists())) ? new PrintWriter(fileModifiedWithoutMax) : null;
        PrintWriter pwRelation = (isPrintingRelation && (overwritingOutputs || !fileRelation.exists())) ? new PrintWriter(fileRelation) : null;
        
        int numTuplesToGenerate = isGeneratingFirstHalfOnly ? (p+1)/2 : p;

        for (int i = 1; i < numTuplesToGenerate; i++) {
            int[] arr = new int[size];

            // first
            arr[0] = i;

            // 2nd to 2nd last
            for (int j = 1; j < size - 1; j++) { 
                arr[j] = i + j * p;
            }
            
            // last
            arr[size - 1] = m - (p * i); 

            // ensure order
            Arrays.sort(arr);
            Tuple tuple = new Tuple(arr);

            indecomposableTuples.add(tuple);

            if (isPrintingIndecomposable && (overwritingOutputs || pwIndecomposable != null)) pwIndecomposable.println(tuple.toCsvString());

            if (!(isPrintingModified || isPrintingModifiedMax || isPrintingRelation || isPrintingModifiedWithoutMax)) continue;

            int mHalfFloored = m/2;
            for (int j = 0; j < size; j++) {
                if (arr[j] > mHalfFloored) arr[j] -= m;
            }
            tuple = new Tuple(arr);

            if (isPrintingModified && (overwritingOutputs || pwModified != null)) pwModified.println(tuple.toCsvString());
            
            int absoluteMax = tuple.getAbsoluteMax();
            Tuple tupleWithoutAbsoluteMax = tuple.getNewTupleWithout(absoluteMax);

            if (isPrintingModifiedMax && (overwritingOutputs || pwModifiedMax != null)) pwModifiedMax.println(absoluteMax);

            if (isPrintingRelation && (overwritingOutputs || pwRelation != null)) {
                Tuple maxRelation = tupleWithoutAbsoluteMax.negate();
                pwRelation.println(maxRelation.toCsvString());
            }

            if (isPrintingModifiedWithoutMax && (overwritingOutputs || pwModifiedWithoutMax != null)) {
                pwModifiedWithoutMax.println(tupleWithoutAbsoluteMax.toCsvString());
            }
        }

        if (isPrintingIndecomposable && pwIndecomposable != null) pwIndecomposable.close();
        if (isPrintingModified && pwModified != null) pwModified.close();
        if (isPrintingModifiedMax && pwModifiedMax != null) pwModifiedMax.close();
        if (isPrintingModifiedWithoutMax && pwModifiedWithoutMax != null) pwModifiedWithoutMax.close();
        if (isPrintingRelation && pwRelation != null) pwRelation.close();

        return indecomposableTuples;
    }

    public static boolean isPrime(int n) {
        if (n <= 1) return false;

        if (n == 2 || n == 3) return true;

        if (n % 2 == 0 || n % 3 == 0) return false;
        
        for (int i = 5; i <= Math.sqrt(n); i = i + 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }

        return true;
    }

    /** Return the gcd of a and b using the euclidean algorithm
     * 
     * @param a - an integer
     * @param b - an integer
     * @return the greatest common divisor of a and b
     */
    public static int gcd(int a, int b) {
        if (a == 0) return b;
        return gcd(b % a, a);
    }
}
