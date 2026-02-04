"""
description -- This program generates the files in the "max_element_info_outputs" folder. It first generates the modified
               Shioda tuples (see folder labeled "modified_shioda_tuples_outputs"). It then identifies the element with the
               maximum absolute value and writes the rest of the elements in terms of this element. (This is used to
               generate the identity component matrices.)
"""

from pathlib import Path

def ind_tuple_generator(p, m):
    """ This function generates the Shioda tuples for a given p value. The tuples are then modified so that any entry n that
    is greater than (m-1)/2 is rewritten as n-m.

    :param p: a prime number from the text file "primes.txt"
    :param m: the value p**2
    :return: a list containing all modified Shioda tuples which are also all the indecomposable tuples
    """
    ind_tuple_list = []
    for i in range(1, p):
        ind_tuple = tuple()
        coeff = 0
        while coeff <= p-1:
            ind_tuple = ind_tuple + (i + coeff*p,)
            coeff = coeff + 1
        ind_tuple = ind_tuple + (m - (p*i),)
        ind_tuple = tuple(sorted(ind_tuple))
        new_tuple = tuple()
        for n in ind_tuple:
            if n > (m-1)/2:
                n = n - m
            new_tuple = new_tuple + (n,)
        ind_tuple_list.append(new_tuple)
    return ind_tuple_list

def main():
    # Files
    base_dir = Path(__file__).resolve().parent
    output_dir = base_dir.parent/"outputs"/"max_element_info_outputs"
    output_dir.mkdir(parents=True, exist_ok=True)

    # Create a list of primes
    list_of_primes = []
    primes_file = open(base_dir/"primes.txt", "r")
    for prime in primes_file:
        list_of_primes.append(int(prime))

    # Loop through the list of primes. For each prime, generate all indecomposable tuples and max element info.
    for p in list_of_primes:
        m = p**2
        d = (p+1)//2
        filename = f"m_{m}_max_element_output.txt"
        full_path = output_dir / filename

        if full_path.exists():
            print(f"Skipping m = {m}, d = {d} - file already exists.")
            continue

        ind_tuple_list = ind_tuple_generator(p, m)

        with open(full_path, "a") as file:
            file.write(f"Maximum values for m = {m} and d = {d}:\n")
            for ind_tuple in ind_tuple_list:
                max_value = max(ind_tuple, key=abs)
                new_tuple = tuple()
                for n in ind_tuple:
                    if n != max_value:
                        new_tuple = new_tuple + (-1*n,)
                new_tuple = sorted(new_tuple, key=lambda x: abs(x))
                file.write(f"{max_value} : {new_tuple}\n")

main()
