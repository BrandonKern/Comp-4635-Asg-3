import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private int attempts;
    private int difficulty;
    private String stem;
    private ArrayList<Pair<Integer, String>> solvedPuzzle;
    private ArrayList<Pair<Integer, String>> unsolvedPuzzle;

    Game(int attempts, int diff, WordRepo wordRepo) {
        this.attempts = attempts;
        this.difficulty = diff;
        this.stem = null;
        this.solvedPuzzle = new ArrayList<>();
        this.unsolvedPuzzle = new ArrayList<>();

        createPuzzle(wordRepo);
    }

    /*
     * Name: guessLetter
     * Purpose: Checks to see if the passed letter is part of the puzzle, if it is the
     * unsolved puzzle is updated.
     * Explanation: If the user has enough attempts (>0) the solved puzzle is looped though
     * each word to check if the letter appears all the words. If the letters appears in any
     * words the word is then looped through updating all appearances of that word in the unsolved
     * puzzle until all words are checked. If no instances of the letter is found the attempts is decremented.
     * Input: letter - the letter (char) being guessed by the user
     * Return: false - if the guess was incorrect
     *         true - if the guess was correct
     */
    public Boolean guessLetter(char letter) {
        Boolean correct = false;
        if (attempts > 0) {
            for (int i = 0; i < this.solvedPuzzle.size(); i++) {
                Pair<Integer, String> curPair = this.solvedPuzzle.get(i);
                String currString = curPair.getValue();
                if (currString.indexOf(letter) != -1) {
                    correct = true;
                    String newWord = this.unsolvedPuzzle.get(i).getValue();
                    for (int j = 0; j < currString.length(); j++) {
                        if (currString.charAt(j) == letter) {
                            if (j == 0) {
                                newWord = letter + newWord.substring(j+1);
                            } else {
                                newWord = newWord.substring(0, j) + letter + newWord.substring(j+1);
                            }
                        }
                    }
                    this.unsolvedPuzzle.get(i).setValue(newWord);
                }
            }
            if (!correct) {
                this.attempts--;
            }
        }


        return correct;
    }

    /*
     * Name guessWord
     * Purpose: Checks to see if the passed word is part of the puzzle, if it is the
     * unsolved puzzle is updated.
     * Explanation: If the user has enough attemps (>0) then each word is looped through
     * in the solved puzzle, checking to see if the word matches. If it does then it is updated
     * in the unsolved puzzle to no longer to be blank, if not then the attemps on the game is
     * decremented
     * Input: word - the word being guessed by the user
     * Return: false - if the guess was incorrect
     *         true - if the guess was correct
     */
    public Boolean guessWord(String word) {
        Boolean correct = false;

        if (attempts > 0) {
            for (int i = 0; i < this.solvedPuzzle.size(); i++) {
                Pair<Integer,String> currPair = this.solvedPuzzle.get(i);
                if (currPair.getValue().equalsIgnoreCase(word)) {
                    correct = true;
                    this.unsolvedPuzzle.set(i, new Pair<Integer, String>(currPair.getKey(), word));

                    if (currPair.getKey() == -1) { //adding updating the word into the puzzle
                        Boolean right = true;
                        for (int j = 1; j < word.length()-1; j++) {
                            if (this.unsolvedPuzzle.size() > 1) {
                                Pair<Integer,String> leafPair =  this.unsolvedPuzzle.get(j);
                                String newWord = leafPair.getValue();
                                String replaceWord = newWord;
    
                                if (right) {
                                    replaceWord = word.charAt(leafPair.getKey()) + newWord.substring(1, newWord.length());
                                    right = false;
                                } else {
                                    replaceWord = newWord.substring(0, newWord.length()-1) + word.charAt(leafPair.getKey());
                                    right = true;
                                }
                                leafPair.setValue(replaceWord);
                            }

                        }
                    } else {
                        int index = currPair.getKey();
                        Pair<Integer, String> solvedPair = this.solvedPuzzle.get(0);
                        Pair<Integer, String> unsolevedPair = this.unsolvedPuzzle.get(0);
                        String solvedReplaceWord = solvedPair.getValue();
                        String replaceWord = unsolevedPair.getValue();
                        String firstHalf = "";
                        if (index != 0) {
                            firstHalf = replaceWord.substring(0,index);
                        }
                        String secondHalf = replaceWord.substring(index + 1);
                        System.out.println(this.toString());
                        replaceWord = firstHalf + solvedReplaceWord.charAt(index) + secondHalf;
                        unsolevedPair.setValue(replaceWord);
                    }
                }
            }
            if (!correct) {
                this.attempts--;
            }
        }


        return correct;
    }

    /*
     * Name: checkLoss
     * Purpose: determines if the user has lost the game, by checking if attemps equal 0.
     * Return: Boolean that indicates whether the game is over.
     */
    public boolean checkLoss() {
        Boolean lose = false;
        if (attempts == 0) {
            lose = true;
        }
        System.out.println("Loss is: " + lose);
        return lose;
    }

    /*
     * Name: checkWin
     * Purpose: determine if the user has won the game, by comparing the solved and unsolved puzzles
     * Return: Boolean that indicates in the puzzle is solved
     */
    public boolean checkWin() {
        Boolean win = true;

        for (int i = 0; i < this.solvedPuzzle.size(); i++) {
            if (!this.solvedPuzzle.get(i).getValue().equalsIgnoreCase(this.unsolvedPuzzle.get(i).getValue())) {
                win = false;
            }
        }
        System.out.println("Win is: " + win);
        return win;
    }

    /*
     * Name: toString
     * Return: a string of the unsolved puzzle
     */
    public String toString() {
        return constructPuzzleStringWithNewLine(this.unsolvedPuzzle) + "Counter: " +this.attempts;
    }

    /*
     * Name: createPuzzle
     * Purpose: Creates set a puzzles represented of arrayList pairs of integers and strings
     * Explanation: First requests the stem to start the build. It then requests words based on random
     * unique indexs of the stem word. Everything is then stored in an array list of pairs with the integer
     * representing the index on the stem (-1 means the stem) and the string the actual word. After completing
     * the first puzzle a second one that is a copy of the first but the letters are instead '_', creating a
     * puzzle the user can guess with.
     */
    private void createPuzzle(WordRepo wordRepo) {
        //The scanner and prompts will be replaced with UDP calls to the Word Repo

        String stemWord = requestWord(this.difficulty-1, ' ', ' ', wordRepo);

        this.stem = stemWord;
        this.solvedPuzzle.add(new Pair<Integer, String>(-1, stemWord));

        ArrayList<Integer> random = generateUniqueRandomNumbers();

        //Creating Solved Puzzle (has all the words)
        if(difficulty > 1) {
            Boolean first = true;
            for(int i = 0; i < random.size(); i++) {
                int index = random.get(i);

                if (first == true) {
                    this.solvedPuzzle.add(new Pair<Integer, String>(index, requestWord(0, stemWord.charAt(index), ' ', wordRepo)));
                    first = false;
                } else {
                    this.solvedPuzzle.add(new Pair<Integer, String>(index, requestWord(0, ' ', stemWord.charAt(index), wordRepo)));
                    first = true;
                }
            }
        }

        //creating the unsolved puzzle
        for (int i = 0; i < this.solvedPuzzle.size(); i++) {
            Pair<Integer,String> currPair = this.solvedPuzzle.get(i);
            String blankWord = "";
            for (int j = 0; j < currPair.getValue().length(); j++) {
                blankWord += "_";
            }
            this.unsolvedPuzzle.add(new Pair<Integer,String>(currPair.getKey(), blankWord));
        }

        // gameScanner.close(); causes issues with multiple calls as it also closes system.in
    }

    /*
     * Name: requestWord
     * purpose: Requests a word from the word repository
     * Inputs: minLength - min length of the requested word
     *         firstLetter - the first letter of the requested word "" means param not requested
     *         lastLetter - last letter of the requested word "" means no param not requested
     *         scanner - the input scanner to read from the console
     * Return: the word given by the user
     */
    private String requestWord(int minLength, char firstLetter, char lastLetter, WordRepo wordRepo) {
        // Scanner wordGetter = new Scanner(System.in);
        String word = "";

        String message = "rw";
        
        if (firstLetter != ' ') {
            message += ",sl," + firstLetter;
        } else {
            message += ",sl,0"; 
        }
        if (lastLetter != ' ') {
            message += ",el," + lastLetter;
        } else {
            message += ",el,0";
        }
        if (minLength != 0) {
            message += ",wl," + minLength;
        } else {
            message += ",wl,0";
        }
        try {
            word = wordRepo.requestWord(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (word.length() >= minLength) {
            return word;
        } else {
            return "InvalidWord";
        }        
    }

    /*
     * Name: generateUniqueRandomNumbers
     * Purpose: Generates and returns a sort array of random integers that is the length of difficulty - 1
     * where the integers are bounded between the stem length and 0.
     * Return: a arrayList with a unqiue set of numbers between the stem length and 0
     */
    private ArrayList<Integer> generateUniqueRandomNumbers() {
        ArrayList<Integer> uniqueIntegers = new ArrayList<>();
        int listLength = difficulty - 1;

        if (this.stem.length() >= listLength) {
            Random random = new Random();

            while (uniqueIntegers.size() < listLength) {
                int randomNumber = random.nextInt(this.stem.length());
                if (!uniqueIntegers.contains(randomNumber)) {
                    uniqueIntegers.add(randomNumber);
                }
            }
            Collections.sort(uniqueIntegers);

        } else {
            System.err.println("Stem is too short for difficulty");
        }
        return uniqueIntegers;
    }

    /*
     * Name: displayPuzzle
     * Purpose: Outputs the solved and the hidden puzzles.
     */
    public void displayPuzzle() {

        System.out.println(constructPuzzleStringWithNewLine(this.unsolvedPuzzle));
        System.out.println(constructPuzzleStringWithNewLine(this.solvedPuzzle));

        return;
    }

    /*
     * Name: constructPuzzleString
     * Purpose: creates a string that represents a puzzle
     * Explanation: Constructs a string of to represent the board, each row is either empty or contains a
     * horizontal row, each row is the wides of the longest word * 2 - 1. The empty row just contains the stem
     * work in the middle and is surrounded '.' and the row ends with a '+'. For the none empty rows, they rotated
     * between being on the right and left of the stem. If the word is on the left the '.' spacers would be put first
     * then the word till the stem and finally the spacers after till the end of the row. If the word is on the right,
     * the same as as the left happens but in the opposite order. Spacers, word from stem, then end spacers from word.
     * Input: puzzle - an array list of tuples with a integer which is the row and a string the word
     */
    private String constructPuzzleString(ArrayList<Pair<Integer, String>> puzzle) {
        String puzzleString = "";

        int height = this.stem.length();
        int longestWord = calcLongestWord();

        Boolean right = true;
        Boolean containsWord = false;

        String sideSpacers = createPeriodSpacer(longestWord-1);

        for (int i = 0; i < this.stem.length(); i++) {
            for (int j = 1; j < puzzle.size(); j++) {
                Pair<Integer, String> currPair = puzzle.get(j);
                if (currPair.getKey() == i) {

                    containsWord = true;
                    String wordSpacer = createPeriodSpacer(longestWord - (currPair.getValue().length()));

                    if (right) { //different orders need to be separate
                        puzzleString += sideSpacers;
                        puzzleString += currPair.getValue();
                        puzzleString += wordSpacer;
                        right = false;
                    } else {
                        puzzleString += wordSpacer;
                        puzzleString += currPair.getValue();
                        puzzleString += sideSpacers;
                        right = true;
                    }
                }
            }

            if (!containsWord) {
                puzzleString += sideSpacers;
                puzzleString += puzzle.get(0).getValue().charAt(i);
                puzzleString += sideSpacers;
            }
            puzzleString += "+,";
            containsWord = false;
        }

        return puzzleString;
    }

    private String constructPuzzleStringWithNewLine(ArrayList<Pair<Integer, String>> puzzle) {
        String puzzleString = "";

        int height = this.stem.length();
        int longestWord = calcLongestWord();

        Boolean right = true;
        Boolean containsWord = false;

        String sideSpacers = createPeriodSpacer(longestWord-1);

        for (int i = 0; i < this.stem.length(); i++) {
            for (int j = 1; j < puzzle.size(); j++) {
                Pair<Integer, String> currPair = puzzle.get(j);
                if (currPair.getKey() == i) {

                    containsWord = true;
                    String wordSpacer = createPeriodSpacer(longestWord - (currPair.getValue().length()));

                    if (right) { //different orders need to be separate
                        puzzleString += sideSpacers;
                        puzzleString += currPair.getValue();
                        puzzleString += wordSpacer;
                        right = false;
                    } else {
                        puzzleString += wordSpacer;
                        puzzleString += currPair.getValue();
                        puzzleString += sideSpacers;
                        right = true;
                    }
                }
            }

            if (!containsWord) {
                puzzleString += sideSpacers;
                puzzleString += puzzle.get(0).getValue().charAt(i);
                puzzleString += sideSpacers;
            }
            puzzleString += "+,\n";
            containsWord = false;
        }

        return puzzleString;
    }

    /*
     * Name: createPeriodSpacer
     * Purpose: Helper function that creates a string '.'
     * Input: numSpacer - the number of '.'
     * Return: A string of '.'
     */
    private String createPeriodSpacer(int numSpacers) {
        String spacers = "";
        for (int j = 0; j < numSpacers; j++) {
            spacers += ".";
        }
        return spacers;
    }

    /*
     * Name: calcLongestWord
     * Purpose: returns the longest word in the puzzle besides the stem
     * Returns: Integer of the longest word
     */
    private int calcLongestWord() {
        int longestWord = 0;
        int wordlength = 0;
        for (int i = 1; i < this.solvedPuzzle.size(); i++) {
            wordlength = this.solvedPuzzle.get(i).getValue().length();
            if (longestWord < wordlength) {
                longestWord = wordlength;
            }

        }
        return longestWord;
    }
}