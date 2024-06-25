import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NewCrypt {
    // Define constants and data structures
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String SUBSTITUTION;
    private static final Map<Character, Integer> ALPHABET_MAP = new HashMap<>();
    static List<String> oddKassemBlocks = new ArrayList<>();

    // Initialize ALPHABET_MAP with character-to-index mappings
    static {
        for (int i = 0; i < ALPHABET.length(); i++) {
            ALPHABET_MAP.put(ALPHABET.charAt(i), i + 1);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //Scanner keyboard = new Scanner(System.in);

        while (true) {



        try{
            // Display the main menu
            System.out.println("Choose an option:");
            System.out.println("1. Encrypt");
            System.out.println("2. Decrypt");
            System.out.println("3. Cryptanalysis");
            System.out.println("4. Exit");
            System.out.print("Enter your choice (1/2/3/4): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                // Encrypting process
                case 1:
                    System.out.print("Enter the name of the file to encrypt: ");
                    String filename = scanner.nextLine();
                    try {
                        // Read lines from the specified file
                        List<String> lines = Files.readAllLines(Paths.get(filename));
                        if (lines.size() < 1) {
                            System.out.println("File is empty or doesn't have a key.");
                            break;
                        }
                        String substitutionKey = lines.get(0).toUpperCase();
                        if (substitutionKey.length() != 26) {
                            System.out.println("Invalid key length. Key should be 26 characters long.");
                            break;
                        }
                        // Update the SUBSTITUTION constant with the new key
                        SUBSTITUTION = substitutionKey;

                        // Combine the remaining lines to form the plaintext
                        String plaintext = String.join("\n", lines.subList(1, lines.size())).toUpperCase().trim();
                        String encrypted = encrypt(plaintext);
                        System.out.println("Encrypted: " + encrypted.toLowerCase());

                        // Save encrypted text to encrypted.txt
                        Files.write(Paths.get("encrypted.txt"), encrypted.getBytes());

                    } catch (Exception e) {
                        System.out.println("Error processing the file: " + e.getMessage());
                    }
                    break;
                // Decrypting process
                case 2:
                    try {
                        // Read encrypted text from the encrypted.txt
                        String encryptedText = new String(Files.readAllBytes(Paths.get("encrypted.txt"))).toUpperCase()
                                .trim();
                        String decrypted = decrypt(encryptedText);
                        System.out.println("Decrypted: " + decrypted);

                        // Save decrypted text to decrypted.txt
                        Files.write(Paths.get("decrypted.txt"), decrypted.getBytes());

                    } catch (Exception e) {
                        System.out.println("Error processing the file: " + e.getMessage());
                    }
                    break;

                // The cryptanalysis process
                case 3:
                    try {
                        String DictionaryPath = "dictionary.txt";
                        // Read encrypted text from the encrypted.txt
                        String encryptedText = new String(Files.readAllBytes(Paths.get("encrypted.txt"))).toUpperCase()
                                .trim();
                        String decrypted = Cryptanalysis(encryptedText, DictionaryPath);
                        System.out.println("\nDecrypted String: " + decrypted);

                        // Save decrypted text to decrypted.txt
                        Files.write(Paths.get("decrypted.txt"), decrypted.getBytes());

                    } catch (Exception e) {
                        System.out.println("Error processing the file: " + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please choose again.");
            }}catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                scanner.close();
                break;
            }
        }
    }
     
     public static String encrypt(String plaintext) {
         StringBuilder encryptedText = new StringBuilder();
         char lastOriginalChar = ' ';
         int i = 0, count = 0;
 
         while (i < plaintext.length()) {
             StringBuilder block = new StringBuilder();
             int charCount = 0;
 
             while (charCount < 20 && i < plaintext.length()) {
                 char currentChar = plaintext.charAt(i);
                 if (Character.isLetter(currentChar)) { // Only consider letters
                     block.append(Character.toUpperCase(currentChar));
                     charCount++;
                 } else {
                     block.append(currentChar); // Include non-alphabetic characters as is
                 }
                 i++;
             }
 
             // In the encryption loop:
             if (count % 2 == 0) {
                 encryptedText.append(substitute(block.toString()));
                 lastOriginalChar = block.charAt(block.length() - 1);
             } else {
                 int shift = ALPHABET_MAP.get(lastOriginalChar);
                 encryptedText.append(caesarCipher(block.toString(), shift, lastOriginalChar));
             }
             count++;
         }
         return encryptedText.toString();
     }
 
     private static String substitute(String block) {
         StringBuilder result = new StringBuilder();
         StringBuilder explanation = new StringBuilder();
 
         for (char c : block.toCharArray()) {
             int index = ALPHABET.indexOf(c);
             if (index != -1) {
                 char substitutedChar = SUBSTITUTION.charAt(index);
                 result.append(substitutedChar);
                 explanation.append("    " + c + " -> " + substitutedChar + "\n");
             } else {
                 result.append(c);
             }
         }
 
         if (explanation.length() > 0) {
             System.out.println("Monoalphabetic Substitution for \"" + block
                     + "\":\n" + explanation.toString()
                     + "Result: \"" + block + "\" became \""
                     + result.toString() + "\"\n");
         }
 
         return result.toString();
     }
 
     private static String caesarCipher(String block, int shift, char lastDecryptedChar) {
         StringBuilder result = new StringBuilder();
         StringBuilder explanation = new StringBuilder();
 
         int lastCharIndex = ALPHABET.indexOf(lastDecryptedChar) + 1; // +1 because we're using 1-based indexing in the
                                                                      // explanation
 
         explanation.append("Shift is based on the index of the last letter from the unencrypted part: "
                 + lastDecryptedChar + " (Index: " + lastCharIndex + ")\n");
 
         for (char c : block.toCharArray()) {
             int index = ALPHABET.indexOf(c);
             if (index != -1) {
                 char shiftedChar = ALPHABET.charAt((index + shift) % ALPHABET.length());
                 result.append(shiftedChar);
                 explanation.append("    " + c + " shifted by " + shift + " positions -> " + shiftedChar + "\n");
             } else {
                 result.append(c);
             }
         }
 
         if (explanation.length() > 0) {
             System.out.println("Caesar Cipher Shift for \""
                     + block + "\":\n" + explanation.toString()
                     + "Result: \"" + block + "\" became \""
                     + result.toString() + "\"\n");
         }
 
         return result.toString();
     }
 
     /*
      * Now that we finished the Encrypting part, We should reverse the process to
      * decrypt the message.
      */
     public static String ReverseCaesarCipher(String block, int shift, char lastEncryptedChar) {
         StringBuilder result = new StringBuilder();
         StringBuilder explanation = new StringBuilder();
 
         int lastCharIndex = ALPHABET.indexOf(lastEncryptedChar) + 1; // +1 because we're using 1-based indexing in the
                                                                      // explanation
 
         explanation.append("Shift is based on the index of the last letter from the encrypted part: "
                 + lastEncryptedChar + " (Index: " + lastCharIndex + ")\n");
 
         for (char c : block.toCharArray()) {
             int index = ALPHABET.indexOf(c);
             if (index != -1) {
                 char shiftedChar = ALPHABET.charAt((index - shift + ALPHABET.length()) % ALPHABET.length());
                 result.append(shiftedChar);
                 explanation.append("    " + c + " shifted back by " + shift + " positions -> " + shiftedChar + "\n");
             } else {
                 result.append(c);
             }
         }
 
         if (explanation.length() > 0) {
             System.out.println("Reverse Caesar Cipher Shift for \""
                     + block + "\":\n" + explanation.toString()
                     + "Result: \"" + block + "\" became \""
                     + result.toString() + "\"\n");
         }
 
         return result.toString();
     }
 
     public static String ReverseSubstitution(String block) {
         StringBuilder result = new StringBuilder();
         StringBuilder explanation = new StringBuilder();
 
         for (char c : block.toCharArray()) {
             int index = SUBSTITUTION.indexOf(c);
             if (index != -1) {
                 char originalChar = ALPHABET.charAt(index);
                 result.append(originalChar);
                 explanation.append("    " + c + " -> " + originalChar + "\n");
             } else {
                 result.append(c);
             }
         }
 
         if (explanation.length() > 0) {
             System.out.println("Reverse Monoalphabitical Substitution for \"" + block
                     + "\":\n" + explanation.toString()
                     + "Result: \"" + block + "\" became \""
                     + result.toString() + "\"\n");
         }
 
         return result.toString();
     }
 
     /*
      * We will create a Cryptanlysis method to break the Ceaser cipher encryption
      * first, so depending on the rest of the code First twenty letters are
      * encrypted using monoalphabetic and the next twenty letters are encrypted in
      * Caesar cipher with the shift based on the last letter of the monoalphabetic
      * encryption.
      */
     public static String Cryptanalysis(String encryptedText, String DictionaryPath) throws IOException {
         StringBuilder finalText = new StringBuilder(encryptedText); // Copy of the original text
         int i = 0;
         int count = 0;
 
         while (i < encryptedText.length()) {
             int blockStart = i; // Starting index of the current block
             StringBuilder block = new StringBuilder();
             int charCount = 0;
 
             // Collect a block of 20 letters or until the end of the string
             while (charCount < 20 && i < encryptedText.length()) {
                 char currentChar = encryptedText.charAt(i);
                 if (Character.isLetter(currentChar)) {
                     block.append(currentChar);
                     charCount++;
                 } else {
                     block.append(currentChar); // Include non-alphabetic characters as is
                 }
                 i++;
             }
 
             if (count % 2 != 0) { // Apply decryption to every second block
                 String decryptedBlock = CeaserBruteForce(block.toString(), DictionaryPath);
                 // Replace the encrypted block with the decrypted one in the final text
                 finalText.replace(blockStart, i, decryptedBlock);
             }
             count++;
         }
 
         processLine(encryptedText, oddKassemBlocks);
         // Print oddKassemBlocks or perform further processing
         for (String block : oddKassemBlocks) {
             System.out.println(block);
         }
 
         Map<Character, Integer> frequencyMap = calculateLetterFrequency(oddKassemBlocks);
 
         List<FrequencyPair> frequencyList = new ArrayList<>(frequencyMap.entrySet()).stream()
                 .map(entry -> new FrequencyPair(entry.getKey(), entry.getValue()))
                 .sorted(Collections.reverseOrder())
                 .toList();
 
         System.out.println("\nLetter Frequency (Descending Order):");
         printFrequencyDescending(frequencyList);
 
         Map<Character, Character> replacementDict = createReplacementDict(frequencyList);
 
         System.out.println("\nReplacement Dictionary:");
         printReplacementDict(replacementDict);
 
         String finalReplacedLetters = printFinalReplacedLetters(oddKassemBlocks, replacementDict);
 
         // Perform word analysis
         analyzeWords(finalReplacedLetters);
 
         // Write the final text to "AnalysisDecrypt.txt"
         Files.write(Paths.get("AnalysisDecrypt.txt"), finalText.toString().getBytes());
 
         return finalText.toString();
     }
 
     // Brute force decryption method
     public static String CeaserBruteForce(String block, String dictionaryFilePath) {
         Set<String> knownWords = new HashSet<>();
         try {
             // Read the dictionary file and store words in the set
             knownWords.addAll(Files.lines(Paths.get(dictionaryFilePath))
                     .map(String::toLowerCase)
                     .collect(Collectors.toSet()));
         } catch (IOException e) {
             return "Error reading dictionary file: " + e.getMessage();
         }
 
         int bestKey = -1;
         double highestPercentage = 0.0;
         String bestDecryptedBlock = "";
 
         for (int key = 0; key < 26; key++) {
             StringBuilder decryptedBlock = new StringBuilder();
             for (int j = 0; j < block.length(); j++) {
                 char ch = block.charAt(j);
                 if (Character.isLetter(ch)) {
                     if (Character.isUpperCase(ch)) {
                         decryptedBlock.append((char) ((ch - key + 26) % 26 + 'A'));
                     } else {
                         decryptedBlock.append((char) ((ch - key + 26) % 26 + 'a'));
                     }
                 } else {
                     decryptedBlock.append(ch);
                 }
             }
 
             double currentPercentage = DictionaryMatch(decryptedBlock.toString(), knownWords);
             if (currentPercentage > highestPercentage) {
                 highestPercentage = currentPercentage;
                 bestKey = key;
                 bestDecryptedBlock = decryptedBlock.toString();
             }
         }
         // Output the decryption details
         System.out.println("\nEncrypted Block: " + block + "\nBest Key Found: " + bestKey);
         System.out.println("Match Percentage: " + String.format("%.2f%%", highestPercentage * 100));
         System.out.println("Decrypted Text: " + bestDecryptedBlock + "\n");
 
         // Return only the decrypted text
         return bestDecryptedBlock;
     }
 
     private static double DictionaryMatch(String text, Set<String> knownWords) {
         String[] words = text.split("\\s+"); // Split the text into words
         int totalWords = words.length;
         int knownWordCount = 0;
 
         for (String word : words) {
             if (knownWords.contains(word.toLowerCase())) {
                 knownWordCount++; // Increment count for each known word
             }
         }
 
         return totalWords > 0 ? (double) knownWordCount / totalWords : 0.0;
     }
 
     // Now we should proceed with reversing the encryption
     public static String decrypt(String encryptedText) {
         StringBuilder decryptedText = new StringBuilder();
         char lastDecryptedChar = ' '; // This will store the last character of the decrypted block
         int i = 0, count = 0;
 
         while (i < encryptedText.length()) {
             StringBuilder block = new StringBuilder();
             int charCount = 0;
 
             while (charCount < 20 && i < encryptedText.length()) {
                 char currentChar = encryptedText.charAt(i);
                 if (Character.isLetter(currentChar)) { // Only consider letters
                     block.append(Character.toUpperCase(currentChar));
                     charCount++;
                 } else {
                     block.append(currentChar); // Include non-alphabetic characters as is
                 }
                 i++;
             }
 
             if (count % 2 == 0) {
                 String decryptedBlock = ReverseSubstitution(block.toString());
                 decryptedText.append(decryptedBlock);
                 if (decryptedBlock.length() > 0) {
                     lastDecryptedChar = decryptedBlock.charAt(decryptedBlock.length() - 1);
                 }
             } else {
                 int shift = ALPHABET_MAP.get(lastDecryptedChar);
                 String decryptedBlock = ReverseCaesarCipher(block.toString(), shift, lastDecryptedChar);
                 decryptedText.append(decryptedBlock);
                 if (decryptedBlock.length() > 0) {
                     lastDecryptedChar = decryptedBlock.charAt(decryptedBlock.length() - 1);
                 }
             }
             count++;
         }
         return decryptedText.toString();
     }
 
     private static Map<Character, Integer> calculateLetterFrequency(List<String> blocks) {
         Map<Character, Integer> frequencyMap = new HashMap<>();
 
         for (String block : blocks) {
             for (char c : block.toCharArray()) {
                 if (Character.isLetter(c)) {
                     c = Character.toUpperCase(c);
                     frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
                 }
             }
         }
 
         return frequencyMap;
     }
 
     private static void printFrequencyDescending(List<FrequencyPair> frequencyList) {
         for (FrequencyPair pair : frequencyList) {
             System.out.println(pair.letter + ": " + pair.frequency);
         }
     }
 
     private static class FrequencyPair implements Comparable<FrequencyPair> {
         private final char letter;
         private final int frequency;
 
         public FrequencyPair(char letter, int frequency) {
             this.letter = letter;
             this.frequency = frequency;
         }
 
         @Override
         public int compareTo(FrequencyPair other) {
             return Integer.compare(this.frequency, other.frequency);
         }
     }
 
  private static Map<Character, Character> createReplacementDict(List<FrequencyPair> frequencyList) {
     Map<Character, Character> replacementDict = new HashMap<>();
     String Frequency_letters = "ETAOINSRHDLUCMFWGYPBVKJXQZ";
 
     Scanner scanner = new Scanner(System.in);
     int orderIndex = 0; // Index to track the current position in the replacement order
 
     for (int i = 0; i < frequencyList.size(); i++) {
         FrequencyPair pair = frequencyList.get(i);
         char originalLetter = pair.letter;
         int frequency = pair.frequency;
 
         // Check if there are multiple letters with the same frequency
         if (i < frequencyList.size() - 1 && frequencyList.get(i + 1).frequency == frequency) {
             List<Character> sameFrequencyLetters = new ArrayList<>();
             sameFrequencyLetters.add(originalLetter);
 
             // Collect all letters with the same frequency
             while (i < frequencyList.size() - 1 && frequencyList.get(i + 1).frequency == frequency) {
                 i++;
                 sameFrequencyLetters.add(frequencyList.get(i).letter);
             }
 
             // Display letters with the same frequency
             System.out.println("\nLetters with the same frequency (" + frequency + "): " + sameFrequencyLetters);
 
             // Prompt the user to decide the order for letters with the same frequency
             System.out.println("Enter the replacement order for the above letters (e.g., ABC), or type 'stop' to finish: ");
             String replacementOrder = scanner.next();
 
             if (replacementOrder.equalsIgnoreCase("stop")) {
                 break; // Stop the replacement process
             }
 
             // Use the replacement order provided by the user
             for (char replacementLetter : replacementOrder.toCharArray()) {
                 int index = sameFrequencyLetters.indexOf(replacementLetter);
                 if (index != -1) {
                     char replacedLetter = sameFrequencyLetters.remove(index);
                     replacementDict.put(replacedLetter, Frequency_letters.charAt(orderIndex));
                     System.out.println(
                             "Replacing " + replacedLetter + " with: " + Frequency_letters.charAt(orderIndex));
                     orderIndex++;
                 }
             }
         } else {
             char replacementLetter = Frequency_letters.charAt(orderIndex);
             replacementDict.put(originalLetter, replacementLetter);
             System.out.println("Replacing " + originalLetter + " with: " + replacementLetter);
             orderIndex++;
         }
     }
 
     // Print the final replacement dictionary
     printReplacementDict(replacementDict);
 
     // Ask the user if they are satisfied with the replacement process
     System.out.println("Are you satisfied with the replacement process? (yes/no)");
     String satisfactionResponse = scanner.next();
 
     while (!satisfactionResponse.equalsIgnoreCase("yes")) {
         // User is not satisfied, allow custom substitutions
         System.out.println("Enter two letters for custom substitution (e.g., AB), or type 'stop' to finish: ");
         String customSubstitution = scanner.next();
 
         if (customSubstitution.equalsIgnoreCase("stop")) {
             break; // Stop the custom substitution process
         }
 
         if (customSubstitution.length() == 2) {
             char original = customSubstitution.charAt(0);
             char substitute = customSubstitution.charAt(1);
             replacementDict.put(original, substitute);
             System.out.println("Custom substitution: Replacing " + original + " with " + substitute);
         } else {
             System.out.println("Invalid input. Please enter two letters or 'stop'.");
         }
 
         // Ask the user again if they are satisfied
         System.out.println("Are you satisfied with the replacement process? (yes/no)");
         satisfactionResponse = scanner.next();
     }
 
     scanner.close();
     return replacementDict;
 }
 
     private static void printReplacementDict(Map<Character, Character> replacementDict) {
         System.out.println("\nReplacement Dictionary:");
         for (Map.Entry<Character, Character> entry : replacementDict.entrySet()) {
             System.out.println(entry.getKey() + " -> " + entry.getValue());
         }
     }
 
     private static String printFinalReplacedLetters(List<String> blocks, Map<Character, Character> replacementDict) {
         StringBuilder finalReplacedLetters = new StringBuilder();
 
         for (String block : blocks) {
             for (char c : block.toCharArray()) {
                 if (Character.isLetter(c)) {
                     c = Character.toUpperCase(c);
                     finalReplacedLetters.append(replacementDict.getOrDefault(c, c));
                 } else {
                     finalReplacedLetters.append(c);
                 }
             }
         }
 
         System.out.println("\nFinal Replaced Letters:");
         System.out.println(finalReplacedLetters.toString());
 
         return finalReplacedLetters.toString();
     }
 
     private static void analyzeWords(String finalReplacedLetters) {
         Pattern twoLetterWordPattern = Pattern.compile("\\b[a-zA-Z]{2}\\b");
         Pattern threeLetterWordPattern = Pattern.compile("\\b[a-zA-Z]{3}\\b");
 
         Matcher twoLetterMatcher = twoLetterWordPattern.matcher(finalReplacedLetters);
         Matcher threeLetterMatcher = threeLetterWordPattern.matcher(finalReplacedLetters);
 
         int twoLetterCount = 0;
         int threeLetterCount = 0;
 
         while (twoLetterMatcher.find()) {
             twoLetterCount++;
         }
 
         while (threeLetterMatcher.find()) {
             threeLetterCount++;
         }
 
         System.out.println("\nWord Analysis:");
         System.out.println("Number of 2-letter words: " + twoLetterCount);
         System.out.println("Number of 3-letter words: " + threeLetterCount);
 
         int totalWords = twoLetterCount + threeLetterCount;
         double twoLetterPercentage = (double) twoLetterCount / totalWords * 100;
         double threeLetterPercentage = (double) threeLetterCount / totalWords * 100;
 
         System.out.println("Percentage of 2-letter words: " + twoLetterPercentage + "%");
         System.out.println("Percentage of 3-letter words: " + threeLetterPercentage + "%");
     }
 
     private static void processLine(String line, List<String> oddKassemBlocks) {
         int i = 0;
         int blockIndex = 0;
 
         while (i < line.length()) {
             StringBuilder block = new StringBuilder();
             int charCount = 0;
 
             while (charCount < 20 && i < line.length()) {
                 char currentChar = line.charAt(i);
                 if (Character.isLetter(currentChar)) {
                     block.append(Character.toUpperCase(currentChar));
                     charCount++;
                 } else {
                     block.append(currentChar);
                 }
                 i++;
             }
 
             // Check if the block should be added to the list
             if (blockIndex % 2 == 0) {
                 oddKassemBlocks.add(block.toString());
             }
 
             blockIndex++;
         }
     }
 }