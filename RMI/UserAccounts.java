import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * UserAccountsImpl class implements the UserAccounts interface and provides functionality for
 * managing user accounts and scores.
 */

public interface UserAccounts extends Remote {

     /**
     * Name: checkUser
     * Purpose: Checks if a user exists and updates its status to active.
     * Input: user_id the ID of the user to be checked
     * Output: true if the user exists and is updated successfully, false otherwise
     * RemoteException if an error occurs during remote method invocation. This method also checks for the user active flag where if the
     * user is already active, then they cannot continue with the game. 
     */
    Boolean checkUser(String user_id) throws RemoteException;

     /**
     * Name: setUserInactive
     * Purpose: Sets a user's status to inactive.
     * Input: user_id the ID of the user to be set inactive
     * Output: true if the user is set inactive successfully, false otherwise
     * RemoteException if an error occurs during remote method invocation
     */
    Boolean setUserInactive(String user_id) throws RemoteException;

    /**
     * Name: checkUserScore
     * Purpose: Checks the score of a user in the users.txt file.
     * Input: ser_id the ID of the user whose score to be checked
     * Output: a message indicating the user's score or if not found
     * RemoteException if an error occurs during remote method invocation
     */
    String checkUserScore(String user_id) throws RemoteException;

    /**
     * Name: updateUserScore
     * Purpose: Updates the score of a user in the users.txt file.
     * Input: user_id the ID of the user whose score to be updated
     * Output: a message indicating if the user's score is updated or not
     * RemoteException if an error occurs during remote method invocation
     */
    String updateUserScore(String user_id) throws RemoteException;
}
