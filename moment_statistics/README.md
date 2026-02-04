# Moment Statistics
This folder contains the moment statistics and the scripts used to compute them for $p = 3, 5, 7$. Moment statistics were computed from the matrices $U\gamma^b$ such that $0 \le b < p(p-1)$.

## Installation

### Prerequisites

Ensure the following are installed:

* Python 3.12.6
    ```bash
    python --version
    ```

* Git
    ```bash
    git --version
    ```

* Required Python Libraries
    * Numpy -- for numerical computations
    * SciPy -- for numerical integration
    * SymPy -- for symbolic computation and integration

    To install these libraries:

    ```bash
    pip install numpy scipy sympy
    ```


### Clone the Repository

```bash
git clone https://github.com/justinyc1/Degeneracy-and-Sato-Tate-Groups-of-C_p2.git
cd Degeneracy-and-Sato-Tate-Groups-of-C_p2
```

## Usage

### Run

Navigate to the src folder, where all the scripts are located.
```bash
cd moment_statistics/src/
```

To compute the moment statistics, run the desired Python script based on the $p$ value.

```bash
python m_9_moment_stats.py
```

> Output will print to the screen, but if desired, you may update the script to print to files. Data that has already been generated can be found in the `outputs` folder.

## Folder Structure

* `src`

    This folder contains the Python scripts used to compute the moment statistics for respective values of $p$. Please see the descriptions at the top of the scripts for more information about the implementation. 
    * `m_9_moment_stats.py`
    * `m_25_moment_stats.py` 
    * `m_49_moment_stats.py`

* `outputs`

    This folder contains the moment statistics computed for $p = 3, 5, 7$. Moment statistics were computed for each $b$ value and then averaged.
    * `m_9_moment_stats_output.txt`
    * `m_25_moment_stats_output.txt_` 
    * `m_49_moment_stats_output.txt_`