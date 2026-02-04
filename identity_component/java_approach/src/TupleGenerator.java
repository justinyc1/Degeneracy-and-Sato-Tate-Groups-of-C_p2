package identity_component.java_approach.src; // **remove this if this file is NOT in a folder called 'src'

//===== libraries that are used in this file =====
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Iterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TupleGenerator {
    private static final String outputPath = "identity_component\\java_approach\\outputs";
    private boolean skipThisMAndD;
    private boolean terminateIfTimeLimit;
    
    private boolean isPrintingAutomatically;
    private boolean isPrintingOutputs;
    private boolean isOverwritingOutputs;
    private boolean printingExceptionalCycles;
    private int timeLimit = -1; // in seconds, <= 0 for no limit
    private String generatingMethod = "halves"; // "recursion" or "halves". uses halves by default

    private boolean isValidatingSplitWhenUsingHalves;
    private int validateSplitWhenUsingHalvesCount = 0;
    private boolean isCheckingTupleSum;
    private int validTupleSumCount = 0;
    private boolean checkingOnlyCombinationsWithIndecomposables;
    private boolean isPrintingStartAndEndTimes;
    
    private long startTimeInNano = -1;

    private int mStart;
    private int mEnd;
    private int dStart;
    private int dEnd;

    public static void main(String[] args) throws CustomException, IOException {
        if (args.length != 4) throw new CustomException("please enter four arguments representing the starting and ending m and d values");
        
        int mStart = Integer.valueOf(args[0]);
        int mEnd = Integer.valueOf(args[1]);
        int dStart = Integer.valueOf(args[2]);
        int dEnd = Integer.valueOf(args[3]);

        TupleGenerator generator = new TupleGenerator(
            mStart,    // mStart (inclusive)
            mEnd,    // mEnd (inclusive)
            dStart,    // dStart (inclusive)
            dEnd,    // dEnd (inclusive)
            true,    // automated
            false,    // printOutputs
            false,    // overwriteOutputs
            false,    // printExceptionalCycles
            3600,    // maxSecondsAllowed, in seconds
            "halves",    // method, generally "halves" > "recursion"
            false,    // validateHalves
            false,    // checkSum
            false,    // onlyCheckCombinationsWithIndecomposables
            true    // printStartAndEndTimes
        );

        generator.generate();
    }

    // simple constructor
    public TupleGenerator(int mStart, int mEnd, int dStart, int dEnd) throws IOException {
        this(mStart, mEnd, dStart, dEnd, true, false, false, false, 1800, "halves", false, false, false, true);
    }

    // full constructor - test all m and d combinations
    public TupleGenerator(int mStart, int mEnd, int dStart, int dEnd, boolean automated, boolean printOutputs, boolean overwriteOutputs, boolean printExceptionalCycles, int maxSecondsAllowed, String method, boolean validateHalves, boolean checkSum, boolean onlyCheckCombinationsWithIndecomposables, boolean printStartAndEndTimes) throws IOException {
        this.mStart = mStart;
        this.mEnd = mEnd;
        this.dStart = dStart;
        this.dEnd = dEnd;
        // init flags

        isPrintingAutomatically = automated;
        isPrintingOutputs = printOutputs;
        isOverwritingOutputs = overwriteOutputs;
        printingExceptionalCycles = printExceptionalCycles;
        timeLimit = maxSecondsAllowed;
        generatingMethod = method;
        isValidatingSplitWhenUsingHalves = validateHalves;
        isCheckingTupleSum = checkSum;
        checkingOnlyCombinationsWithIndecomposables = onlyCheckCombinationsWithIndecomposables;
        isPrintingStartAndEndTimes = printStartAndEndTimes;
    }

    public void generate() throws CustomException, IOException {
        Scanner sc = new Scanner(System.in);

        if (!isPrintingAutomatically) System.out.println("Press the Enter Key to process the next m and d values");
        sc.useDelimiter("\r"); // a single enter press is now the separator.

        for (int i = mStart; i <= mEnd; ++i) {
            FileHelper.deleteAllEmptyFiles(new File(FileHelper.outputsDir)); // delete empty output files

            if (i % 2 == 0) continue;
            // if (!isPerfectSquare(i)) continue; // for only m=p^2

            int dLimit = (i-1)/2;
            int jSkipToValue = -1;

            for (int j = dStart; j <= dLimit && j <= dEnd; ++j) {
                if (checkingOnlyCombinationsWithIndecomposables && j != (Math.sqrt(i)+1)/2) continue; // FOR SPECIAL m,d WITH INDECOMPOSABLE TUPLES

                File exceptionalCycleFileForThisMAndD = new File(outputPath + "\\exceptional_cycle_csvs\\m_" + i + "\\m_" + i + "_d_" + j + ".txt");
                System.out.println(exceptionalCycleFileForThisMAndD.exists());

                if (!isOverwritingOutputs && exceptionalCycleFileForThisMAndD.exists()) continue; // skip this i and j if output file already exists and overwrite is disabled

                if (skipThisMAndD) {
                    System.out.printf("Previous iteration with m = %d, d = %d exceeded the %d second%s time limit.\n", i, j-1, timeLimit, plural(timeLimit));
                    jSkipToValue = dLimit - j;
                    skipThisMAndD = false;
                }

                if (j <= jSkipToValue + 1) {
                    System.out.println("Skipping at m = " + i + ", d = " + j);
                    continue;
                }

                if (isPrintingStartAndEndTimes) System.out.println("Starting time for m = " + i + ", d = " + j + " is " + new Date());

                try {
                    System.gc(); // free up memory
                    generateForMAndD(i, j);
                } catch (CustomException e) {
                    continue;
                }

                if (isPrintingStartAndEndTimes) System.out.println("Ending time for m = " + i + ", d = " + j + " is " + new Date());

                if (!isPrintingAutomatically) sc.next();
            }
        }
        sc.close();
    }

    /**
     * @param m - An positve odd integer
     * @param d - An positive integer in the range: 1 <= d <= (m-1)/2
     */
    public void generateForMAndD(int m, int d) throws CustomException, IOException {
        System.out.println("Running method generateForMAndD(" + redString("m = ", m) + ", " + redString("d = ", d) + ") and generating using " + generatingMethod + ":\n");
        
        validateMAndD(m, d);

        String folderName = "\\tuples";
        String folderPath = outputPath + folderName;
        String fileName = "m_" + m + "\\output_for_m_" + m + "_d_" + d + ".txt";
        if (isPrintingOutputs) FileHelper.createFolderIfNotExist(new File(outputPath), folderName);
        if (isPrintingOutputs) FileHelper.createFolderIfNotExist(new File(folderPath), "m_" + m);
        File currentFile = new File(folderPath + fileName);

        // if allowing printing outputs IS allowed but overwriting existing output files IS NOT allowed, exception will be thrown for this m, d
        if (isPrintingOutputs && !isOverwritingOutputs && currentFile.exists() && !currentFile.isDirectory()) {
            throw new CustomException("overwrite_outputs disabled and printing to file enabled, while file for m = " + m + ", d = " + d + " already exists.");
        }

        PrintWriter pw = isPrintingOutputs ? new PrintWriter(currentFile) : null;

        long startTime = System.nanoTime();
        startTimeInNano = startTime;

        //===== create and put valid values in 'Z/mZ' and 'Z/mZ*' set =====
        Set<Integer> ZmmZ = new HashSet<>();
        Set<Integer> ZmmZStar = new HashSet<>();
        
        generateZmmZAndZmmZs(m, ZmmZ, ZmmZStar);

        Set<Tuple> VSet = new HashSet<>(); // contains all tuples from the B set that are valid for the V set
        
        findAllValidAlphaCombinations(VSet, ZmmZStar, m, d);
        
        Set<Tuple> allPairs = new HashSet<>();  // contains all tuples from the V set that have  ONLY PAIRS  adding up to m
        Set<Tuple> somePairs = new HashSet<>(); // contains all tuples from the V set that have  SOME PAIRS  adding up to m (but not all pairs)
        Set<Tuple> noPairs = new HashSet<>();   // contains all tuples from the V set that have  NO PAIRS    adding up to m
        Set<Tuple> indecomposables = new HashSet<>(); // contains all tuples from the V set that have  NO SUBSETS  adding up to m
        Set<Tuple> decomposableNoPairs = new HashSet<>(); // contains all tuples from the V set that HAVE SUBSETS & NO PAIRS  adding up to m
        Set<Tuple> exceptionalCycles = new HashSet<>(); // contains all tuples from the V set that are not made up of exclusively pairs
        
        populateAllSomeNoneExceptionalSets(VSet, m, allPairs, somePairs, noPairs, exceptionalCycles);

        StringBuilder noPairPrintBuffer = new StringBuilder();
        
        // ===== indecomposable set and decomposable but no pairs set =====
        
        populateIndecomposablesAndDecomposableButNoPairsSets(m, d, noPairs, indecomposables, decomposableNoPairs, isPrintingOutputs, noPairPrintBuffer);

        long endTime = System.nanoTime();
        String formattedElapsedTime = getFormattedElapsedTime(startTime, endTime);

        // outputs
        System.out.println();
        System.out.println("Summary:");
        System.out.println("given " + redString("m = ", m) + ", " + redString("d = ", d));
        System.out.println("Calculations took " + formattedElapsedTime + ".");
        System.out.println("All " + redString("ascending & non-repeating") + " tuple (" + redString("size ", 2*d) + ") combinations possible for the U set: " + findNumOfAscendingNonrepeatingTuplesInUSet(ZmmZ, d));
        System.out.println("                      " + redString("V") + " set: contains " + redString(VSet.size()) + " tuples");
        System.out.println("          " + redString("all") + " pairs" + " set: contains " + redString(allPairs.size()) + " tuples");
        System.out.println("         " + redString("none") + " pairs" + " set: contains " + redString(noPairs.size()) + " tuples");
        System.out.println("         " + redString("indecomposables") + " set: contains " + redString(indecomposables.size()) + " tuples");
        System.out.println("     " + redString("exceptional") + "_cycles" + " set: contains " + redString(exceptionalCycles.size()) + " tuples");

        if (isPrintingOutputs) pw.println("Running method generateForMAndD(m = " + m + ", d = " + d + ") and generating using " + generatingMethod + ":\n");
        if (isPrintingOutputs) pw.println("Summary:");
        if (isPrintingOutputs) pw.println("given m = " + m + ", d = " + d);
        if (isPrintingOutputs) pw.println("Calculations took " + formattedElapsedTime + ".");
        if (isPrintingOutputs) pw.println("Z/mZ:  " + ZmmZ.toString());
        if (isPrintingOutputs) pw.println("Z/mZ*: " + ZmmZStar.toString());
        if (isPrintingOutputs) pw.println("All ascending & non-repeating tuple (size " + 2*d + ") combinations possible for the U set: " + findNumOfAscendingNonrepeatingTuplesInUSet(ZmmZ, d));
        if (isPrintingOutputs) pw.println("                      V set: contains " + VSet.size() + " tuples");
        if (isPrintingOutputs) pw.println("          all pairs set: contains " + allPairs.size() + " tuples");
        if (isPrintingOutputs) pw.println("         some pairs set: contains " + somePairs.size() + " tuples");
        if (isPrintingOutputs) pw.println("         no pairs set: contains " + noPairs.size() + " tuples");
        if (isPrintingOutputs) pw.println("         indecomposables set: contains " + indecomposables.size() + " tuples");
        if (isPrintingOutputs) pw.println("decomposable & no pairs set: contains " + decomposableNoPairs.size() + " tuples");
        if (isPrintingOutputs) pw.println("     exceptional cycles set: contains " + exceptionalCycles.size() + " tuples");

        if (isPrintingOutputs && VSet.size() <= 10000) pw.println("\nPrint V set (contains " + VSet.size() + " tuples): " + toStringSorted(VSet, "\n")); //DEBUG
        if (isPrintingOutputs && allPairs.size() <= 10000) pw.println("\nPrint all pairs (contains " + allPairs.size() + " tuples): " + toString(allPairs, "\n")); //DEBUG
        if (isPrintingOutputs && somePairs.size() <= 10000) pw.println("\nPrint some pairs (contains " + somePairs.size() + " tuples): " + toString(somePairs, "\n")); //DEBUG
        if (isPrintingOutputs && noPairs.size() <= 20000) pw.println("\nPrint no pairs (contains " + noPairs.size() + " tuples): " + toString(noPairs, "\n")); //DEBUG
        if (isPrintingOutputs && indecomposables.size() <= 20000) pw.println("\nPrint indecomposables (contains " + indecomposables.size() + " tuples): " + toString(indecomposables, "\n")); //DEBUG
        if (isPrintingOutputs && decomposableNoPairs.size() <= 20000) pw.println("\nPrint decomposable but no pairs (contains " + decomposableNoPairs.size() + " tuples): " + toString(decomposableNoPairs, "\n")); //DEBUG
        if (isPrintingOutputs && exceptionalCycles.size() <= 20000) pw.println("\nPrint exceptional cycles (contains " + exceptionalCycles.size() + " tuples): " + toString(exceptionalCycles, "\n")); //DEBUG

        if (printingExceptionalCycles) {
            if (exceptionalCycles.size() == 0) {
                System.out.println("There are no Exceptional Cycles for m = " + m + ", d = " + d + ".");
            } else {
                System.out.println("Printing Exceptional Cycles output file for m = " + m + ", d = " + d + ": ");
                FileHelper.createFolderIfNotExist(new File(outputPath), "exceptional_cycle_csvs");
                String ecFilePath = outputPath + "\\exceptional_cycle_csvs";
                String ecFileName = "\\m_" + m + "\\m_" + m + "_d_" + d + ".csv";
                FileHelper.createFolderIfNotExist(new File(ecFilePath), "m_" + m);
                File ecCurrentFile = new File(ecFilePath + ecFileName);
                if (!ecCurrentFile.exists() || isOverwritingOutputs) {
                    PrintWriter ecPw = new PrintWriter(ecCurrentFile);
                    long ecLineCount = printTupleAsCsv(exceptionalCycles, ecPw, true);
                    ecPw.close();
                    System.out.println(ecLineCount + " Exceptional Cycles are printed into the output file.");
                }
            }
        }

        System.out.println("\nmethod generateForMAndD(" + redString(m) + ", " + redString(d) + ") ran to completion.");
        if (isPrintingOutputs) pw.println("\nmethod generateForMAndD(" + m + ", " + d + ") ran to completion.");

        if (isPrintingOutputs) pw.close();
        return;
    }

    /** Makes sure m and d are valid 
     * 
     * @param m where m is: odd, or equal to p*q (where p and q are different primes), or equal to p^n where n >= 2
     * @param d where d is in the range: 1 <= d <= (m-1)/2
     * @throws CustomException if m or d is invalid
     */
    public void validateMAndD(int m, int d) throws CustomException {
        if (m % 2 == 0) throw new CustomException("m is not odd: m = " + m);
        if (d < 1) throw new CustomException("d is not greater than or equal to 1: d = " + d);
        if (d > (m-1)/2) throw new CustomException("d is not less than or equal to (m-1)/2: d = " + d + ", (m-1)/2 = " + (m-1)/2);
    }

    public void generateZmmZAndZmmZs(int m, Set<Integer> ZmmZ, Set<Integer> ZmmZStar) {
        for (int i = 0; i < m; ++i) {
            ZmmZ.add(i);
            if (gcd(i, m) == 1) {
                ZmmZStar.add(i);
            }
        }
    }

    /** Calls the recursive algorithm to find all valid ascending combinations of alpha tuple
     * @param VSet
     * @param m - the upper limit (exclusive) of Z/mZ when finding combinations
     * @throws IOException 
     */
    public void findAllValidAlphaCombinations(Set<Tuple> VSet, Set<Integer> ZmmZStar, int m, int d) throws CustomException, IOException {
        int alphaLength = 2 * d;
        int[] thisCombination = new int[alphaLength];
        if (isValidatingSplitWhenUsingHalves || isCheckingTupleSum) {
            recursivelyFindAllAndCheck(VSet, ZmmZStar, m, thisCombination, 0, 1, (alphaLength-2)/2+1);
        } else {
            if (generatingMethod == "recursion") { // original recursive implementation
                findAllRecursively(VSet, ZmmZStar, m, thisCombination, 0, 1, (alphaLength-2)/2+1);
            } else if (generatingMethod == "halves") { // newer, generate using half-tuples
                findAllUsingHalves(VSet, ZmmZStar, m, alphaLength, (alphaLength-2)/2+1);
            } else { // default: generate using halves
                findAllUsingHalves(VSet, ZmmZStar, m, alphaLength, (alphaLength-2)/2+1);
            }
        }
    }

    private void findAllUsingHalves(Set<Tuple> VSet, Set<Integer> ZmmZStar, int m, int alphaLength, int nHalvedPlusOne) throws CustomException, IOException {
        int d = alphaLength/2;
        int mMinusOneDividedByTwo = (m-1)/2;
        int hold = 10;
        ArrayList<Tuple> firstHalves = new ArrayList<>(hold);
        HashMap<Integer, ArrayList<Tuple>> inversedSumTuplesMap = new HashMap<>(hold);
        int[] thisCombination = new int[d];
        findHalvesRecursively(firstHalves, inversedSumTuplesMap, m, mMinusOneDividedByTwo, thisCombination, 0, 1);
        
        for (int i = 0; i < firstHalves.size(); i++) {
            Tuple curr = firstHalves.get(i);
            int sum = curr.sum();
            if (!inversedSumTuplesMap.containsKey(sum)) {
                continue;
            }
            int left = curr.get(curr.size()-1);
            for (Tuple alpha : inversedSumTuplesMap.get(sum)) {
                if (left == alpha.get(alpha.size()-1)) {
                    continue;
                }
                putInVSetIfValid(VSet, ZmmZStar, curr.merge(alpha), m, nHalvedPlusOne);
            }
        }
    }
    
    private void findHalvesRecursively(ArrayList<Tuple> firstHalves, HashMap<Integer, ArrayList<Tuple>> inversedSumTuplesMap, int m, int mMinusOneDividedByTwo, int[] thisCombination, int sum, int depth) {
        if (depth > thisCombination.length) {
            return;
        }
        
        int prev = 0;
        if (depth > 1) prev = thisCombination[depth-2];
        for (int i = Integer.max(depth, prev + 1); i <= mMinusOneDividedByTwo - thisCombination.length + depth; ++i) {
            thisCombination[depth-1] = i;
            sum += i;

            if (depth == thisCombination.length) {
                Tuple thisTuple = new Tuple(thisCombination);
                firstHalves.add(thisTuple);

                if (!inversedSumTuplesMap.containsKey(sum)) {
                    ArrayList<Tuple> emptyList = new ArrayList<>();
                    inversedSumTuplesMap.put(sum, emptyList);
                }
                inversedSumTuplesMap.get(sum).add(thisTuple.inverse(m));
            }
            
            findHalvesRecursively(firstHalves, inversedSumTuplesMap, m, mMinusOneDividedByTwo, thisCombination, sum, depth+1);
            
            sum -= i;
        }
    }

    /** Recursively find all valid ascending combinations of alpha tuple, using the values of Z/mZ (excluding 0)
     * 
     * @param VSet
     * @param m 
     * @param thisCombination - an array that contains all the elements in the alpha tuple for the current loop
     * @param sum - stores the sum of the elements in thisCombination
     * @param depth - the current level of the recursion method
     */
    private void findAllRecursively(Set<Tuple> VSet, Set<Integer> ZmmZStar, int m, int[] thisCombination, int sum, int depth, int nHalvedPlusOne) throws CustomException {
        if (depth > thisCombination.length) {
            // System.out.println("depth = " + depth + "   returning"); // DEBUG
            return;
        }
        
        int prev = 0;
        if (depth > 1) prev = thisCombination[depth-2];
        for (int i = Integer.max(depth, prev + 1); i <= m - 1 - thisCombination.length + depth; ++i) {
            thisCombination[depth-1] = i;
            sum += i;

            if (depth == thisCombination.length && sum % m == 0) {
                putInVSetIfValid(VSet, ZmmZStar, thisCombination, m, nHalvedPlusOne);
            }
            
            findAllRecursively(VSet, ZmmZStar, m, thisCombination, sum, depth+1, nHalvedPlusOne);
            
            sum -= i;
        }
    }

    private void recursivelyFindAllAndCheck(Set<Tuple> VSet, Set<Integer> ZmmZStar, int m, int[] thisCombination, int sum, int depth, int nHalvedPlusOne) throws CustomException, FileNotFoundException {
        if (depth > thisCombination.length) {
            return;
        }
        
        int prev = 0;
        if (depth > 1) prev = thisCombination[depth-2];
        for (int i = Integer.max(depth, prev + 1); i <= m - 1 - thisCombination.length + depth; ++i) {
            thisCombination[depth-1] = i;
            sum += i;

            if (depth == thisCombination.length && sum % m == 0) {
                putInVSetIfValidAndCheckInfo(VSet, ZmmZStar, thisCombination, m, nHalvedPlusOne);
            }
            
            recursivelyFindAllAndCheck(VSet, ZmmZStar, m, thisCombination, sum, depth+1, nHalvedPlusOne);
            sum -= i;
        }
    }

    public void putInVSetIfValid(Set<Tuple> VSet, Set<Integer> ZmmZStar, int[] alpha, int m, int nHalvedPlusOne) throws CustomException {
        if (timeLimit > 0) { // has time limit
            terminateIfLongerThanNSeconds(timeLimit);
        }
        boolean thisAlphaIsValid = true;
        // System.out.println(nHalvedPlusOne);
        for (int t : ZmmZStar) {

            List<Integer> tTimesAlphaReducedElements = new ArrayList<>();
            double tTimesAlphaReducedSum = 0;

            for (int i = 0; i < alpha.length; ++i) {
                int reducedModM = (t * alpha[i]) % m;
                tTimesAlphaReducedElements.add(reducedModM);
                tTimesAlphaReducedSum += reducedModM;
            }
            tTimesAlphaReducedSum /= m;

            if (tTimesAlphaReducedSum != nHalvedPlusOne) {
                thisAlphaIsValid = false;
                break; // if |t * alpha| != n/2 +1 for just one t, this alpha tuple is not valid for the B set
            }
        }
        if (thisAlphaIsValid) {
            VSet.add(new Tuple(alpha));
        }
    }

    public void putInVSetIfValid(Set<Tuple> VSet, Set<Integer> ZmmZStar, Tuple alpha, int m, int nHalvedPlusOne) throws CustomException {
        if (timeLimit > 0) { // has time limit
            terminateIfLongerThanNSeconds(timeLimit);
        }
        boolean thisAlphaIsValid = true;
        for (int t : ZmmZStar) {

            List<Integer> tTimesAlphaReducedElements = new ArrayList<>();
            double tTimesAlphaReducedSum = 0;

            for (int i = 0; i < alpha.size(); ++i) {
                int reducedModM = (t * alpha.get(i)) % m;
                tTimesAlphaReducedElements.add(reducedModM);
                tTimesAlphaReducedSum += reducedModM;
            }
            tTimesAlphaReducedSum /= m;

            if (tTimesAlphaReducedSum != nHalvedPlusOne) {
                thisAlphaIsValid = false;
                break; // if |t * alpha| != n/2 +1 for just one t, this alpha tuple is not valid for the B set
            }
        }
        if (thisAlphaIsValid) {
            VSet.add(alpha);
        }
    }

    public void putInVSetIfValidAndCheckInfo(Set<Tuple> VSet, Set<Integer> ZmmZStar, int[] alpha, int m, int nHalvedPlusOne) throws CustomException {
        if (timeLimit > 0) { // has time limit
            terminateIfLongerThanNSeconds(timeLimit);
        }
        boolean thisAlphaIsValid = true;
        for (int t : ZmmZStar) {

            List<Integer> tTimesAlphaReducedElements = new ArrayList<>();
            double tTimesAlphaReducedSum = 0;

            for (int i = 0; i < alpha.length; ++i) {
                int reducedModM = (t * alpha[i]) % m;
                tTimesAlphaReducedElements.add(reducedModM);
                tTimesAlphaReducedSum += reducedModM;
            }
            tTimesAlphaReducedSum /= m;

            if (tTimesAlphaReducedSum != nHalvedPlusOne) {
                thisAlphaIsValid = false;
                break; // if |t * alpha| != n/2 +1 for just one t, this alpha tuple is not valid for the B set
            }
        }
        if (thisAlphaIsValid) {
            if (isValidatingSplitWhenUsingHalves) validateSplitHalves(alpha, m); // DEBUG
            if (isCheckingTupleSum) validateTupleSum(alpha, m); //
            VSet.add(new Tuple(alpha));
        }
    }

    public void validateTupleSum(int[] tupleArray, int m) throws CustomException {
        int sum = sumOf(tupleArray);
        int d = tupleArray.length / 2;
        int mTimesD = m * d;
        if (sum == mTimesD) { // normal
            validTupleSumCount++;
            if (validTupleSumCount % 1000 == 0) {
                System.out.println(validTupleSumCount + " tuples checked are valid halves.");
            }
        } else { // anomaly
            System.out.printf("For m = %d, d = %d, the tuple %s with sum %d does not equal m*d = %d, after %d normal combinations in a row.\n", m, tupleArray.length, toString(tupleArray), sum, mTimesD, validTupleSumCount);
            validTupleSumCount = 0;
        }
    }

    public void validateSplitHalves(int[] tupleArray, int m) throws CustomException {
        int n = tupleArray.length;
        int mMinusOneDividedByTwo = (m-1)/2;
        int invalidIndex = -1;
        for (int j = 0; j < n; j++) {
            if (j < n/2) { // left half
                if (tupleArray[j] > mMinusOneDividedByTwo) {
                    invalidIndex = j;
                    break;
                }
            } else { // right half
                if (tupleArray[j] < mMinusOneDividedByTwo) {
                    invalidIndex = j;
                    break;
                }
            }
        }
        if (invalidIndex == -1) { // normal
            validateSplitWhenUsingHalvesCount++;
            if (validateSplitWhenUsingHalvesCount % 1000 == 0) {
                System.out.println(validateSplitWhenUsingHalvesCount + " tuples checked are valid halves.");
            }
        } else { // anomaly
            System.out.printf("For m = %d, d = %d, the tuple %s at index %d violates the two halves observation, after %d normal combinations in a row.\n", m, tupleArray.length, toString(tupleArray), invalidIndex, validateSplitWhenUsingHalvesCount);
            validateSplitWhenUsingHalvesCount = 0;
        }
    }

    public void populateAllSomeNoneExceptionalSets(Set<Tuple> VSet, int m, Set<Tuple> allPairs, Set<Tuple> somePairs, Set<Tuple> noPairs, Set<Tuple> exceptionalCycles) {
        for (Tuple tuple : VSet) { // for each tuple

            // some boolean variables to keep track of each tuple's traits
            boolean hasAllPairs = true;  // assume true, if any element don't have a pair, set to false
            boolean hasOnePair = false; // assume false, if any element have a pair, set to true
            boolean hasNoPairs = true;   // assume true, if any element have a pair, set to false

            for (int i = 0; i < tuple.size(); ++i) { // for an element at i of tuple
                boolean ithElementHasPair = false;

                // for any element at i, j loop makes sure to set ithElementHasPair to true if found a pair, or ithElementHasPair remains false, which means the tuple is an exceptional cycle
                for (int j = 0; j < tuple.size(); ++j) { // check every element (as j)
                    if ((tuple.get(i) + tuple.get(j)) % m == 0) {
                        ithElementHasPair = true;
                        break;
                    }
                }

                if (ithElementHasPair) { // if just one element has a pair then
                    hasNoPairs = false; 
                    hasOnePair = true; // we assume theres at least one pair
                } else { // an element don't have a pair
                    hasAllPairs = false;
                }
            }

            if (hasAllPairs) { // hasAllPairs remains true if every element has a pair
                allPairs.add(tuple);
            } else { // not every element have a pair:
                exceptionalCycles.add(tuple);
                if (hasOnePair) { // if there is at least one pair (but not all elements are pairs) then SOME elements are pairs
                    somePairs.add(tuple);
                } else if (hasNoPairs) { // if no pairs but has subsets, then only add to the no pairs set
                    noPairs.add(tuple);
                }
            }
        }
    }

    public void populateIndecomposablesAndDecomposableButNoPairsSets(int m, int d, Set<Tuple> noPairs, Set<Tuple> indecomposables, Set<Tuple> decomposableNoPairs, boolean isPrintingOutputs, StringBuilder noPairPrintBuffer) throws CustomException {
        int minSubsetSize = 4;
        int maxSubsetSize = 2 * d - 2;

        // ===== indecomposables set and decomposable but no pairs set =====
        // when d = 1,2 (d=1 => 2 elements = all pairs, d=2 => both pairs or no pairs)
        if (d == 2) {
            for (Tuple alpha : noPairs) {
                // if the first element have a pair, then the other two numbers are also pairs.
                if (alpha.get(0) + alpha.get(1) == m || 
                    alpha.get(0) + alpha.get(2) == m || 
                    alpha.get(0) + alpha.get(3) == m
                ) {
                    continue;
                }
                // else there is no pairs in alpha. when d = 2, only alpha_1 and alpha_2 would be pairs. therefore at this point it is indecomposables
                if (isPrintingOutputs) noPairPrintBuffer.append("adding to indecomposables set: " + alpha + "\n");
                indecomposables.add(alpha);
            }
        }
        if (d >= 3) { // when d = 2 or less, alpha have at most 4 elements, so there is no indecomposables nor decomposable but no pairs 
            for (Tuple alpha : noPairs) { // each element is an alpha with no pairs
                boolean dividesM = false;
                Tuple subtuple = Tuple.EMPTY_TUPLE;
                for (int size = minSubsetSize; size <= maxSubsetSize; size+=2) { // for each possible subtuple length:
                    // go though each possible subtuple combination from alpha to find a subtuple that adds to multiple of m

                    // init subtuple
                    subtuple = alpha.getSubtuple(0, size);

                    // check init values divides m
                    if (subtuple.sum() % m == 0) {
                        dividesM = true;
                        break;
                    }

                    // check every subset combinations if they divides m
                    while (subtuple != null) {
                        if (subtuple.sum() % m == 0) {
                            dividesM = true;
                            break;
                        }
                        subtuple = alpha.getNextAscendingTupleAfter(subtuple);
                    }

                    if (dividesM) break;
                }
            // back to for each alpha
                if (dividesM) {
                    if (isPrintingOutputs) noPairPrintBuffer.append("adding to decomposable but no pairs set: " + alpha + ", since subtuple = " + subtuple + "\n");
                    decomposableNoPairs.add(alpha);
                    continue;
                } else {
                    if (isPrintingOutputs) noPairPrintBuffer.append("adding to indecomposables set: " + alpha + "\n");
                    indecomposables.add(alpha);
                    continue;
                }
            }
        }
    }

    public void terminateIfLongerThanNSeconds(int n) throws CustomException {
        if (startTimeInNano == -1) {
            throw new CustomException("Error: StartTimeInNano was never set.");
        }
        long elapsedInNano = System.nanoTime() - startTimeInNano;
        long elapsedInSeconds = elapsedInNano / 1000000000L;
        if (elapsedInSeconds > n) {
            skipThisMAndD = true;
            if (terminateIfTimeLimit) throw new CustomException("Time limit exceeded " + n + " seconds.");
        }
    }

    public static String getFormattedElapsedTime(long startTime, long endTime) {
        long elapsedTime = endTime - startTime; // in nano seconds (10^-9)
        
        // derive from nanoseconds elapsed from the start of the calculation operations of the program 
        long allNanoSec  = elapsedTime;
        long allMicroSec =  elapsedTime / 1000;
        long allMiliSec  =  elapsedTime / (1000*1000);
        long allSec      = (elapsedTime / (1000*1000)) / 1000;
        long allMin      = (elapsedTime / (1000*1000)) / (1000*60);
        long allHour     = (elapsedTime / (1000*1000)) / (1000*60*60);
        long allDay      = (elapsedTime / (1000*1000)) / (1000*60*60*24);
        
        long nanoSec     =   allNanoSec % 1000; // in  nano seconds (10^-9)
        long microSec    =  allMicroSec % 1000; // in micro seconds (10^-6)
        long miliSec     =   allMiliSec % 1000; // in  mili seconds (10^-3)
        long sec         =       allSec % 60;
        long min         =       allMin % 60;
        long hour        =      allHour % 24;
        return String.format(
            "%d day%s, %d hour%s, %d minute%s, %d second%s, %d milisecond%s, %d microsecond%s, %d nanosecond%s", 
            allDay, plural(allDay), hour, plural(hour), min, plural(min), sec, plural(sec), 
            miliSec, plural(miliSec), microSec, plural(microSec), nanoSec, plural(nanoSec)
        );
    }

    public static long printTupleAsCsv(Set<Tuple> tuples, PrintWriter pw, boolean sort) {
        long lineCount = 0;
        if (sort) tuples = new TreeSet<Tuple>(tuples);
        
        for (Tuple alpha : tuples) { // for each tuple in the (possibly sorted) set
            pw.println(alpha.toCsvString());
            lineCount++;
        }

        return lineCount;
    }
    
    public static String redString(Object obj) {
        String str = "";
        if (obj instanceof String) str = obj.toString();
        if (obj instanceof Integer) str = String.valueOf(obj);
        return "\u001b[31m" + str + "\u001b[0m";
    }
    
    public static String redString(String str, int num) {
        return "\u001b[31m" + str + num + "\u001b[0m";
    }

    public static String toStringSorted(Set<Tuple> set) {
        return toString(new TreeSet<Tuple>(set), ", ");
    }

    public static String toStringSorted(Set<Tuple> set, String delimiter) {
        return toString(new TreeSet<Tuple>(set), delimiter);
    }

    public static String toString(Set<Tuple> set) {
            return toString(set, ", ");
    }

    public static String toString(Set<Tuple> set, String delimiter) {
        StringBuilder sb = new StringBuilder("{");
        Iterator<Tuple> iter = set.iterator();
        if (iter.hasNext()) {
            sb.append(delimiter).append(iter.next().toString()); // element first
        }
        while (iter.hasNext()) {
            sb.append(delimiter).append(iter.next().toString()); // delimiter if theres another element
        }
        sb.append("\n}");
        return sb.toString();
    }
    
    public static String toString(int[] arr) throws CustomException {
        return toString(arr, arr.length);
    }

    /**
     * @param arr
     * @param cutOff exclusive
     * @return
     * @throws CustomException
     */
    public static String toString(int[] arr, int cutOff) throws CustomException {
        if (cutOff < 0 || cutOff > arr.length) {
            throw new CustomException("cut off = " + cutOff + " is invalid");
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < cutOff-1; ++i) {
            sb.append(arr[i]).append(", ");
        }
        return sb.append(arr[cutOff-1]).append("]").toString();
    }

    public static String findNumOfAscendingNonrepeatingTuplesInUSet(Set<Integer> ZmmZ, int d) {
        BigInteger result = nCr(BigInteger.valueOf(ZmmZ.size()-1), BigInteger.valueOf(2*d));
        String str = result.toString();
        return str;
    }

    public static int sumOf(List<Integer> list) {
        int sum = 0;
        for (int i = 0; i < list.size(); ++i) {
            sum += list.get(i);
        }
        return sum;
    }

    public static int sumOf(int[] arr) {
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum;
    }

    public static String plural(long num) {
        return num == 1 ? "" : "s";
    }

    public static String plural(int num) {
        return num == 1 ? "" : "s";
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

    public static BigInteger factorial(BigInteger n) {
        if (n.compareTo(BigInteger.ONE) < 1) return BigInteger.ONE;
        return n.multiply(factorial(n.subtract(BigInteger.ONE)));
    }

    public static BigInteger nCr(BigInteger n, BigInteger r) {
        BigInteger numer = factorial(n);
        BigInteger denom = (factorial(r).multiply(factorial(n.subtract(r))));
        return numer.divide(denom);
    }
}
