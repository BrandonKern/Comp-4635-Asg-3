import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * WordRepoImpl class implements the WordRepo interface and provides functionality for
 * checking, adding, deleting, and requesting words from a repository.
 */

public interface WordRepo extends Remote {
    
    /**
     * Name: checkWord
     * Purpose: Checks if a word exists in the repository.
     * Input: word the word to be checked
     * Output: true if the word exists, false otherwise
     * RemoteException if an error occurs during remote method invocation
     */ 
    Boolean checkWord(String word) throws RemoteException;
    
    /**
     * Name: deleteWord
     * Purpose: Deletes a word from the repository.
     * Input: word the word to be deleted
     * Output: true if the word is deleted successfully, false otherwise
     * RemoteException if an error occurs during remote method invocation
     */
    Boolean deleteWord(String word) throws RemoteException;
    
    /**
     * Name: addWord
     * Purpose: Adds a word to the repository.
     * Input: word the word to be added
     * Output: true if the word is added successfully, false otherwise
     * RemoteException if an error occurs during remote method invocation
     */
    Boolean addWord(String word) throws RemoteException;
    
    /**
     * Name: requestWord
     * Purpose: Requests a word from the repository based on specified constraints.
     * Input: constraints a string containing constraints for word selection
     * Output: a word that meets the specified constraints or an appropriate message
     * RemoteException if an error occurs during remote method invocation
     */
    String requestWord(String constraints) throws RemoteException;
}

