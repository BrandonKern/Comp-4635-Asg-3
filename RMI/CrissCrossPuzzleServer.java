import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CrissCrossPuzzleServer extends Remote {
    



    void keepMyNameWhileAlive(int user_id) throws RemoteException;
    boolean heartBeat(int user_id) throws RemoteException;

    /**
     * Name: addWord   
     * Purpose: Adds a word to the WordRepo.
     * Input: word the word to be added
     * Output: a message indicating whether the word was successfully added or not
     * RemoteException if an error occurs during remote method invocation
     */
    String addWord(String word, int user_id, int seq) throws RemoteException;
    
    /**
     * Name: removeWord
     * Purpose: Removes a word from the WordRepo.
     * Input: word the word to be removed
     * Output: a message indicating whether the word was successfully removed or not
     * RemoteException if an error occurs during remote method invocation
     */
    String removeWord(String word, int user_id, int seq) throws RemoteException;
    
    /**
     * Name: checkWord
     * Purpose: Checks if a word exists in the WordRepo.
     * Input: word the word to be checked
     * Output: true if the word exists, false otherwise
     * RemoteException if an error occurs during remote method invocation
     */
    boolean checkWord(String word, int user_id, int seq) throws RemoteException; //already idempotent?
    
    /**
    * NameL checkScore
    * Purpose: Checks the score of the user identified by the user_id.
    * Input: user_id the ID of the user whose score is to be checked
    * Output: a string representing the score of the user
    * RemoteException if an error occurs during remote method invocation
    */
    String checkScore(int user_id, int seq) throws RemoteException; //already idempotent?
    
    /**
    * NameL checkUser
    * Purpose: Checks if a user exists in the UserAccounts.
    * Input: user_id the ID of the user to check
    * Output: true if the user exists, false otherwise
    * RemoteException if an error occurs during remote method invocation
    */
    boolean checkUser(int user_id, int seq) throws RemoteException; //already idempotent?
    
    /**
    * NameL updateUserScore
    * Purpose: Updates the score of the user identified by the user_id.
    * Input: user_id the ID of the user whose score is to be updated
    * Output: a message indicating whether the score was successfully updated or not
    * RemoteException if an error occurs during remote method invocation
    */
    String updateUserScore(int user_id, int seq) throws RemoteException;
    
    /**
    * Name: endGame
    * Purpose: Ends the game associated with the user identified by user_id.
    * Input: user_id the ID of the user whose game is to be ended
    * Output: a message indicating the result of the game ending process
    * RemoteException if an error occurs during remote method invocation
    */
    boolean endGame(int user_id, int seq) throws RemoteException;
    
    /**
     * Name: startGame
     * Purpose: Starts a new game for the user identified by user_id.
     * Input 1: user_id the ID of the user for whom a new game is to be started
     * Input 2: difficulty the difficulty level of the game
     * Input 3: failed_attempts the number of failed attempts allowed in the game
     * Output: a message indicating the result of starting the new game
     * RemoteException if an error occurs during remote method invocation
     */
    String startGame(int user_id, int difficulty, int failed_attempts, int seq) throws RemoteException;
    
    /**
     * name: guessLetter
     * Purpose: Allows the user identified by user_id to guess a letter in the current game.
     * Input 1: user_id the ID of the user guessing the letter
     * Input 2: letter the letter guessed by the user
     * Output: a message indicating the result of the letter guess
     * RemoteException if an error occurs during remote method invocation
     */
    boolean guessLetter(int user_id, char letter, int seq) throws RemoteException;
    
    /**
     * Name: guessWord
     * Purpose: Allows the user identified by user_id to guess a word in the current game.
     * Input 1: user_id the ID of the user guessing the word
     * Input 2: word the word guessed by the user
     * Output: a message indicating the result of the word guess
     * RemoteException if an error occurs during remote method invocation
     */
    boolean guessWord(int user_id, String word, int seq) throws RemoteException;
    
    /**
     * Name: checkWin
     * Purpose: Checks if the user identified by user_id has won their current game.
     * Input 1: user_id the ID of the user playing the game
     * Ouptut: true if they have won the game, false otherwise
     * RemoteException if an error occurs during remote method invocation
     */
    boolean checkWin(int user_id, int seq) throws RemoteException; //already idempotent?
    
    /*
     * Name: checkLose
     * Purpose: Checks if the user identified by user_id has lost their current game.
     * Input 1: user_id the ID of the user playing the game
     * Ouptut: true if they have lost the game, false otherwise
     * RemoteException if an error occurs during remote method invocation
     */
    boolean checkLoss(int user_id, int seq) throws RemoteException; //already idempotent?
    
    /*
     * Name: displayGame
     * Purpose: to return a string representation of the current game
     * Input 1: user_id the ID of the user playing
     * Output: a string representation of the players game
     * RemoteException if an error occurs during remote method invocation
     */
    String displayGame(int userId, int seq) throws RemoteException;
    
    /**
     * Name: setUserInactive
     * Purpose: Sets the user identified by user_id as inactive in the UserAccounts.
     * Input: user_id the ID of the user to be set as inactive
     * Output: true if the user was successfully set as inactive, false otherwise
     * RemoteException if an error occurs during remote method invocation
     */
    boolean setUserInactive(int user_id) throws RemoteException;
}
