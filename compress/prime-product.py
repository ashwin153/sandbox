from itertools import izip
import random
import math

# List of the first 168 prime numbers courtesy of http://primos.mat.br/indexen.html.
primes = [
    -1, 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 
    97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 
    193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 
    307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 
    421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 
    547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 
    659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 
    797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 
    929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997
]


def simulate(n, length):
    """
    Perform a simulation of n trials of bit strings of the specified length. Prints the average
    compression ratio and the percentage of bit strings that were compressable.

    :param n: Number of trials.
    :param length: Length of bit string.
    """
    def step():
        # Construct and encode a random sequence of bits.
        r = [bool(random.getrandbits(1)) for i in range(length)]
        e = encode(r)

        # Ensure that the decoding is consistent with the original.
        assert r == decode(*e)

        # Calculate the size of the encoding.
        return math.ceil(math.log(abs(e[1]) + 1, 2)) + 1

    s = [step() for i in range(n+1)]
    print("Average bits: " + str(sum(s) / len(s)))
    print("Compressable: " + str(sum(map(lambda x: 1.0 if x < length else 0.0, s)) / len(s)))


def encode(bits):
    """
    Encodes the specified bits as the product of the prime numbers corresponding to the indices in
    a bit string of all zeroes or all ones (depending on which is smaller), that need to be flipped
    to generate the bits.
    
    :param bits: Bits to encode.
    :return: Compressed bits.
    """
    copy = [False] * len(bits)
    flip = []

    # Determine the necessary bit flips required to copy the bit string.
    for i, (b, c) in enumerate(izip(bits, copy)):
        if b != c:
            flip.append(i)
            for j in range(i, len(copy), i + 1):
                copy[j] = not copy[j]
    
    if not flip:
        # If there are no bit flips, then return 0.
        return (len(bits), 0)
    else:
        # Otherwise, return the product of the prime numbers associated with each index.
        product = reduce(lambda x, y: x * y, map(lambda x: primes[x], flip))
        return (len(bits), product)


def decode(length, product):
    """
    Decode the prime product by finding its prime factorization and performing the required bit
    flips on a bit string of all zeroes or all ones (depending on the sign of the product).

    :param length: Length of bit string.
    :param product: Prime product.
    :return: Decoded bits.
    """
    # Find the prime factorization of the product to determine the bit flips.
    if product == 0:
        flip = []
    else:
        flip = [0] if product < 0 else []
        flip.extend([i for i in range(1, length) if abs(product) % primes[i] == 0])
    
    # Perform the bit flips in order on the bit string.
    bits = [False] * length
    for f in flip:
        for i in range(f, length, f + 1):
            bits[i] = not bits[i]
    return bits
