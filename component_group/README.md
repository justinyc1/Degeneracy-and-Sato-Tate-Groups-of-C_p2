# Component Group $\text{ST}(\text{Jac}(\text{C}_{p^2}))/\text{ST}^{0}(\text{Jac}(\text{C}\_{p^2}))$

This folder contains the code used pertaining to the component group of $\text{ST}(\text{Jac}(\text{C}_{p^2}))$.

## File Structure

- `component_group_generator.sage` contains the function used to compute a generator of the component group.
- `example.ipynb` is a demonstration of computing a component group generator for $p=5, 7$.

## Installation

### Prerequisites  

Ensure the following are installed:

- SageMath 10.7 or higher
    > Note: Installing Sage/SageMath via ```pip install sagemath``` is insufficient, as a limited package of Sage/SageMath will be installed. Instead, please install the *full* Sage/SageMath distribution from its [official website](https://www.sagemath.org/).

- Git

    ```bash
    git --version
    ```

- **(Optional)** Jupyter Notebook for `example.ipynb`

    ```bash
    pip install notebook
    ```

### Clone the Repository

```bash
git clone https://github.com/justinyc1/Degeneracy-and-Sato-Tate-Groups-of-C_p2.git
cd Degeneracy-and-Sato-Tate-Groups-of-C_p2
```

## Usage

- Navigate to the folder where the code is located.

    ```bash
    cd component_group/
    ```

- **(Optional)** To run `example.ipynb` locally:

    1. Install Jupyter Notebook from Sage's shell

        ```bash
            sage -pip install jupyter
            sage -n jupyter
        ```
    
    2. Follow the instructions on the screen

    3. Open `example.ipynb` and ensure SageMath is selected as the kernel.

    > Note: There is more than one way to run the notebook locally. We provide the simplest method to do so.
