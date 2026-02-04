# Java Approach

This folder contains the Java programs that can be used to generate tuples for any values of $m$ and $d$, as well as the output files for these data.

## Installation

### Prerequisites

Ensure the following are installed:

- Java JDK 21 or higher

    ```bash
    java -version
    ```

- Git

    ```bash
    git --version
    ```

### Clone the Repository

```bash
git clone https://github.com/justinyc1/Degeneracy-and-Sato-Tate-Groups-of-C_p2.git
cd Degeneracy-and-Sato-Tate-Groups-of-C_p2
```

## Usage

### Compile

Navigate to project root and run:

```bash
cd identity_component/java_approach/src/
javac *.java
```

### Run

- To generate indecomposable tuples of any $p$ using Shioda's Lemma 5.5:

    ```bash
    java ShiodaTupleGenerator pStart pEnd
    ```
    > Replace pStart and pEnd (both inclusive) with desired ranges for $p$.

- To generate tuples of any $m$ and $d$ values via brute force:

    ```bash
    java TupleGenerator mStart mEnd dStart dEnd
    ```
    > Replace mStart, mEnd, dStart, and dEnd (all inclusive) with desired ranges for $m$ and $d$.

- For more advanced flags that are not yet accessible via command-line arguments, you must modify them directly in the main() method.

## File Structure

### `src/`:

- `ShiodaTupleGenerator.java` is a program that is used to generate indecomposable tuples using Lemma 5.5 from Shioda's paper. 
- `TupleGenerator.java` is a program that is used to generate tuples via brute force.
- `CustomException.java` is a small custom exception class used throughout the other programs.
- `FileHelper.java` contains helper functions used to assist with maintaining folder structure.
- `Tuple.java` is a custom integer tuple class with a bunch of helper functions used in the tuple generator programs.

### `outputs/`:

- `indecomposable_csvs` contains indecomposable tuples for each $m,d$, where $m=p^2$.
- `modified_csvs` contains selected and modified indecomposable tuple(s) as stated in **Remark 3.23**.
- `modified_max_csvs` contains the absolute max value of the modified tuple(s) in `modified_csvs`.
- `modified_without_max_csvs` contains the sub-tuple(s) without the absolute value of the modified tuple(s).
- `relation_csvs` contains the relation(s) for each $m,d$ similar to **Example 3.27**.