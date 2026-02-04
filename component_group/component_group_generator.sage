def component_group_gen(p):
    """
    This function produces a generator of ST(Jac(C_p^2))/ST^0(Jac(C_p^2)). More specifically, the function outputs the non-zero entries of the component group generator (and the remaining entries are zero matrices). 
    Note that indexing starts at zero here, not one.
    
    :param p: The prime p in y^2 = x^{p^2} - 1
    """
    r = IntegerModRing(p^2).unit_gens()
    g = (p^2 - 1)/2
    
    exponents = [a for a in range(1,g+1)]
    newExponents = [(int(r[0])*a % (p^2)) for a in exponents]
    
    groupActEndo = []
    for a in newExponents:
        if a in exponents:
            groupActEndo.append(a)
        else:
            n = a - (p^2)
            groupActEndo.append(n)
    
    for i in exponents:
        for j in groupActEndo:
            if i == j:
                print(f"{groupActEndo.index(j), exponents.index(i)} = I")
            elif i == abs(j):
                print(f"{groupActEndo.index(j), exponents.index(i)} = J")