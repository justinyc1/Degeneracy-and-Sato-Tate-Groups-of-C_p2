"""
description -- This program generates all indecomposable tuples for a given m and d value.
               The main method can be customized based on which m values we are interested in (i.e. m = p*q or m = p^2).
               The main method is currently written for m = p^2.
summary --
    1. The zmodmzstarset() function is called and the program generates this set for the given m value.
    2. The v_set() function is called and the program generates the V set for the given m and d value
       using the z_star set produced by zmodmzstar() function.
       This function calls other functions:
        - verify_not_all_pairs() that checks whether a tuple consists of all pairs. If this is true, the
          tuple is not an exceptional cycle and is not part of the exceptional cycles set. For efficiency, this tuple is
          not added to the V set either. This significantly reduces the number of tuples in the V set.
            * This means that there are fewer tuples that have to be checked with the verify_v_property() function, which
              requires a lot of mathematical computations.
        - verify_v_property() that checks if the tuple satisfies the B set property from our project summary document.
            * Our code treats the V and B set as the same set. (B set contains only ascending tuples from the V set).
    3. The e_set() function is called which finds all exceptional cycles in the V set. It separates the tuples into
       some pairs and no pairs.
    4. The indecomposable_set() function is called which finds all tuples in the no pairs set that are indecomposable.

"""

# IMPORTS
import math
import time
from itertools import combinations

# for folder organization
from pathlib import Path

def zmodmzstarset(m):
    """
    This function computes the z mod m z star set given an m value. This set contains integers ranging from [1,m).
    The integers in this set satisfy the following property: gcd(i, m) == 1.

    :param m: an odd integer which represents the degree in C_m: y^2=x^m-1
    :return: The set representing the z mod m z star set for the given m value.
    """
    z_star = []
    for i in range(1, m):
        if math.gcd(i, m) == 1:
            z_star.append(i)
    return z_star


def v_set(m, d, z_star):
    """
    This function computes the V set for a given m and d value. The function generates all d length "half tuples" whose
    values range from [1, (m-1)/2]. It then combines each half tuple with every other half tuple, forming a tuple of length
    2*d. It checks if the tuple satisfies the properties for the U set and then for the V set. If it does, the tuple is
    appended to the list.
    Only the tuples that satisfy the following conditions are added to the list:
    1. the elements of the tuple are ascending
    2. the sum of the elements = m*d
    3. the tuple is not composed entirely of pairs that sum to m
    4. verify_v_property() returns True

    :param m: an odd integer which represents the degree in C_m: y^2=x^m-1
    :param d: an integer whose range is [1, (m-1)/2]. This integer defines the length of the tuples produced.
          the tuples have length 2*d.
    :param z_star: a list representing the z mod m z star set for the given m value that was returned by the zmodmzstar() function.
    :return: a list represent the V set for the given m and d values.
    """
    count = 0
    small_tuple_list = []
    v_list = []
    print("These are the half tuple(s) in the U set: ")
    for combo in combinations(range(1, ((m-1)//2)+1), d):
        small_tuple_list.append(combo)
        print(combo)
        count += 1

    print("These are the tuple(s) in the V set: ")
    for half_tuple in small_tuple_list:
        i = 0
        while i < len(small_tuple_list):
            reverse_tuple = tuple(m-alpha for alpha in reversed(small_tuple_list[i]))
            if sum(half_tuple) + sum(reverse_tuple) == m*d:
                if is_not_all_pairs(half_tuple, reverse_tuple, m, d):
                    new_tuple = half_tuple + reverse_tuple
                    if verify_v_property(new_tuple, m, d, z_star):
                        print(new_tuple)
                        v_list.append(new_tuple)
            i += 1
    # print("These are the tuple(s) in the V set: ")
    # print(tuple_list)
    print("The number of tuple(s) is", len(v_list))
    print()
    return v_list


def is_not_all_pairs(tuple_1, tuple_2, m, d):
    """This function verifies whether a tuple is not composed entirely of pairs that sum to m."""
    i = 0
    j = d-1
    pair_count = 0
    while i < d and j > -1:
        if tuple_1[i] + tuple_2[j] == m:
            pair_count += 1
        i += 1
        j -= 1

    if pair_count == d:
        return False
    return True


def verify_v_property(u_tuple, m, d, z_star):
    """This function verifies whether a tuple meets the conditions necessary to be part of the V set."""
    t_count = 0
    for t in z_star:
        i = 0
        sum = 0
        while i < 2 * d:
            sum += ((u_tuple[i] * t) % m)
            i += 1
        if (sum / m) == d:
            t_count = t_count + 1
    if t_count == len(z_star):
        return True
    return False


def e_set(m, v_list):
    """
    This function computes the E set (set containing exceptional tuples). It creates the following lists:
    1. no_pairs -- represents the set with tuples that contain no pairs of elements that add to m.
    2. some_pairs -- represents the set with tuples that contain some but not all pairs of elements that add to m.

    :param m: an odd integer which represents the degree in C_m: y^2=x^m-1
    :param v_list: a list representing the V set for the given m and d values that was returned by the v_set() function.
    :return: tuple_list: a list representing the set of all tuples in the V set that are exceptional cycles, no_pairs: a list representing the subset of the exceptional cycles set that contains tuples containing no pairs
    """
    tuple_list = v_list[:]
    no_pairs = []
    some_pairs = []
    count = len(v_list)
    # This checks for all possible pair combinations in the tuple.
    for v_tuple in v_list:
        pair_count = 0
        for combo in combinations(v_tuple, 2):
            if sum(combo) == m:
                pair_count += 1
        if pair_count == int(len(v_tuple) / 2):
            count -= 1
            tuple_list.remove(v_tuple)
        if pair_count == 0:
            no_pairs.append(v_tuple)
        else:
            some_pairs.append(v_tuple)

    # PRINTING
    print("These are the tuple(s) in the E set: ")
    for e_tuple in tuple_list:
        print(e_tuple)
    print("The number of tuple(s) is", count)
    print("These tuples have no pairs that add to m =", m, )
    for no_pairs_tuple in no_pairs:
        print(no_pairs_tuple)
    print("The number of tuple(s) is", len(no_pairs))
    print("These tuples have some (but not all) pairs that add to m =", m, )
    for some_pairs_tuple in some_pairs:
        print(some_pairs_tuple)
    print("The number of tuple(s) is", len(some_pairs))
    print()
    return tuple_list, no_pairs


def indecomposable_set(m, d, no_pairs):
    """This functions determines which tuples in the no_pairs set are indecomposable and returns a list of those tuples."""
    tuple_list = no_pairs[:]
    for e_tuple in no_pairs:
        for i in range(2, d, 2):
            if e_tuple in no_pairs:
                for combo in combinations(e_tuple, i):
                    if sum(combo) % m == 0:
                        tuple_list.remove(e_tuple)
                        break
    # PRINTING
    print("These are the exceptional tuple(s) that are indecomposable: ")
    for ind_tuple in tuple_list:
        print(ind_tuple)
    print("The number of tuple(s) is", len(tuple_list))
    return tuple_list


def main():
    # Files
    base_dir = Path(__file__).resolve().parent
    output_dir = base_dir.parent/"outputs"/"brute_force_tuples_outputs"
    output_dir.mkdir(parents=True, exist_ok=True)

    # Create a list of primes. This will be useful to loop through.
    list_of_primes = []
    primes_file = open(base_dir/"primes.txt", "r")
    for prime in primes_file:
        list_of_primes.append(int(prime))

    for p in list_of_primes:
        m = p**2
        for d in range(1, ((m-1)//2)+1):
            # d = (p+1)//2
            start_time = time.time()

            folder_name = f"m_{m}_output"
            folder_path = output_dir / folder_name
            folder_path.mkdir(parents=True, exist_ok=True)  # Create folder if it doesn't exist

            filename = f"m_{m}_d_{d}_output.txt"
            full_path = folder_path / filename


            z_star = zmodmzstarset(m)
            if full_path.exists():
                print(f"Skipping m = {m}, d = {d} — file already exists.")
                continue

            with open(full_path, "w") as file:
                file.write(f"For m = {m} and d = {d}\n")
                file.write(f"These are integers in the z mod m z star set for m = {m}\n {z_star}\n")
            v_tuple_list = v_set(m, d, z_star)
            e_tuple_list, no_pairs = e_set(m, v_tuple_list)
            indecomposable_list = indecomposable_set(m, d, no_pairs)
            total_time = time.time() - start_time

            # PRINTING
            with open(full_path, "a") as file:
                # PRINTING SUMMARY
                file.write(f"The program took {total_time} seconds to complete.\n")
                file.write(f"The number of tuple(s) in the V set is: {len(v_tuple_list)}\n")
                file.write(f"The number of tuple(s) in the E set is: {len(e_tuple_list)}\n")
                file.write(f"The number of tuple(s) with no pairs is: {len(no_pairs)}\n")
                file.write(f"The number of indecomposable tuple(s) is: {len(indecomposable_list)}\n\n")

                # PRINTING TUPLES
                # V set
                file.write(f"The tuples in the V set are:\n")
                for x in v_tuple_list:
                    file.write(f"{x}\n")
                file.write(f"The number of tuple(s) in the V set is: {len(v_tuple_list)}\n")
                file.write(f"\n")

                # E set
                file.write(f"The tuples in the E set are:\n")
                for x in e_tuple_list:
                    file.write(f"{x}\n")
                file.write(f"The number of tuple(s) in the E set is: {len(e_tuple_list)}\n")
                file.write(f"\n")

                # no pairs set
                file.write(f"The tuples in the no pairs set are:\n")
                for x in no_pairs:
                    file.write(f"{x}\n")
                file.write(f"The number of tuple(s) in the no pairs set is: {len(no_pairs)}\n")
                file.write(f"\n")

                # indecomposable set
                file.write(f"The indecomposable tuples are:\n")
                for x in indecomposable_list:
                    file.write(f"{x}\n")
                file.write(f"The number of indecomposable tuple(s) is: {len(indecomposable_list)}\n")
                file.write(f"\n")


main()


