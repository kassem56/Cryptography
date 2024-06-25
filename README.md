# Cryptography



1. Class Definition and Initialization
Class Declaration: The NewCrypt class encapsulates all functionalities of encryption, decryption, and cryptanalysis.

Constants:

ALPHABET is a constant string containing all uppercase English letters.
SUBSTITUTION is a string that will hold the substitution key used for encryption and decryption.
ALPHABET_MAP is a HashMap that maps each letter of the alphabet to its position (1-based index).
oddKassemBlocks is a list intended to store specific blocks of text.
Static Block: The static block initializes the ALPHABET_MAP by iterating over the ALPHABET string and mapping each letter to its position.

2. Main Method and Menu Handling
Scanner Initialization: The Scanner object is initialized for reading user input from the console.
Menu Display and Input Handling:
A while loop continuously displays the menu until the user decides to exit.
The user is prompted to choose an option (Encrypt, Decrypt, Cryptanalysis, Exit).
3. Encryption Process
File Reading: The program prompts the user for the name of the file to encrypt. It reads the file, where the first line is expected to be a 26-character substitution key.
Key Validation: It checks if the substitution key length is exactly 26 characters.
Text Preparation: The remaining lines of the file are joined into a single string of plaintext, converted to uppercase, and trimmed of extra whitespace.
Encryption Execution: The encrypt method is called with the plaintext, and the result is written to encrypted.txt.
4. Decryption Process
File Reading: The program reads the encrypted.txt file, converts the text to uppercase, and trims whitespace.
Decryption Execution: The decrypt method is called with the encrypted text, and the result is written to decrypted.txt.
5. Cryptanalysis Process
File Reading: Similar to decryption, it reads the encrypted.txt file.
Dictionary Path: The path to the dictionary file (dictionary.txt) is defined.
Cryptanalysis Execution: The Cryptanalysis method is called with the encrypted text and dictionary path. The decrypted result is written to decrypted.txt.
6. Exit Option
Program Termination: If the user selects the exit option, the program prints a message and terminates.
7. Encryption Method Details
Block Processing: The encrypt method processes the plaintext in blocks of 20 letters.
Alternating Methods:
For even-indexed blocks, the method uses monoalphabetic substitution.
For odd-indexed blocks, it uses a Caesar cipher with a shift derived from the last character of the previous block.
Substitution: The substitute method replaces each letter in the block with its corresponding letter from the SUBSTITUTION string.
Caesar Cipher: The caesarCipher method shifts each letter by a specified amount and includes detailed explanation logging.
8. Decryption Method Details
Block Processing: The decrypt method processes the encrypted text in blocks of 20 letters.
Alternating Methods:
For even-indexed blocks, the method uses reverse monoalphabetic substitution.
For odd-indexed blocks, it uses a reverse Caesar cipher with a shift derived from the last character of the previous block.
Reverse Substitution: The ReverseSubstitute method reverses the substitution process.
Reverse Caesar Cipher: The ReverseCaesarCipher method shifts each letter back by a specified amount and includes detailed explanation logging.
9. Cryptanalysis Method Placeholder
Stub Implementation: The Cryptanalysis method is currently a placeholder, intended for implementing frequency analysis and dictionary-based attacks to break the cipher.
10. Supporting Methods
Logging: Both encryption and decryption methods include logging to explain the transformations applied to each block, aiding in understanding the process.
