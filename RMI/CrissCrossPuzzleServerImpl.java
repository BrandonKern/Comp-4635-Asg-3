import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
// import java.util.Scanner;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("serial")
public class CrissCrossPuzzleServerImpl extends UnicastRemoteObject implements CrissCrossPuzzleServer {
    private Map<Integer, Game> games = new HashMap<>();
    private WordRepo wordRepo;
    private UserAccounts userAccounts;
    private static final long serialVersionUID = 1L;
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Name: CrissCrossPuzzleServerImpl
     * Purpose: Constructor for the CrissCrossPuzzleServerImpl class.
     * Initializes the CrissCrossPuzzleServerImpl object and establishes connections to WordRepo and UserAccounts servers.
     * RemoteException if a communication-related exception occurs during remote method invocation
     */

    public CrissCrossPuzzleServerImpl() throws RemoteException {  
        super();
        try {
            String wordUrl = "rmi://" + InetAddress.getLocalHost().getHostAddress() + ":1099/WordRepo";
            wordRepo = (WordRepo) Naming.lookup(wordUrl);
            String userUrl  = "rmi://" + InetAddress.getLocalHost().getHostAddress() + ":1099/UserAccounts";
            userAccounts = (UserAccounts) Naming.lookup(userUrl);
        } catch (UnknownHostException | MalformedURLException | NotBoundException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String addWord(String word) throws RemoteException {
        if (wordRepo.addWord(word)) {
            return "Word added";
        }
        return "Word not added";
    }

    @Override
    public String removeWord(String word) throws RemoteException {
        if (wordRepo.deleteWord(word)) {
            return "Word deleted";
        }
        return "Word not deleted";
    }

    @Override
    public boolean checkWord(String word) throws RemoteException {
        boolean check = wordRepo.checkWord(word);
        System.out.println("Check word " + word + " is " + check);
        return check;
    }

    @Override
    public String checkScore(int user_id) throws RemoteException {
        return userAccounts.checkUserScore(String.valueOf(user_id));
    }

    @Override
    public boolean checkUser(int user_id) throws RemoteException {
        return userAccounts.checkUser(String.valueOf(user_id));
    }

    @Override
    public String updateUserScore(int user_id) throws RemoteException {
        return userAccounts.updateUserScore(String.valueOf(user_id));
    }

    @Override
    public boolean endGame(int user_id) throws RemoteException {
        lock.writeLock().lock();
        Game game = games.get(user_id);
        if (game != null) {
            return true;
        } else {
            if (games.remove(user_id) != null) {
                return true;
            }
        }
        lock.writeLock().unlock();
        return false;
    }

    @Override
    public String startGame(int user_id, int difficulty, int failed_attempts) throws RemoteException {
        lock.writeLock().lock();

        Game game = games.get(user_id);
        if (game != null) {
            if (games.remove(user_id) != null) {
                System.out.println("deleted old game for user " + user_id);
            } else {
                System.out.println("failure to start new game");
                return "failure to start new game";
            }
        }
        game = new Game(failed_attempts, difficulty, wordRepo);
        games.put(user_id, game);
        lock.writeLock().unlock();

        if (games.get(user_id) != null) {
            System.out.println("Successfully created");
        } else {
            return "failure to start new game";
        }
        System.out.println("New Game made for user " + user_id);
        game.displayPuzzle();
        return game.toString();
    }

    @Override
    public boolean guessLetter(int user_id, char letter) throws RemoteException {
        lock.readLock().lock();
        Game game = games.get(user_id);
        lock.readLock().unlock();
        if (game != null) {
            return game.guessLetter(letter);
        } else {
            System.out.println("Game does not exist for this user");
        }
        return false;
    }

    @Override
    public boolean guessWord(int user_id, String word) throws RemoteException {
        lock.readLock().lock();
        Game game = games.get(user_id);
        lock.readLock().unlock();
        if (game != null) {
            return game.guessWord(word);
        } else {
            System.out.println("Game does not exist for this user");
        }
        return false;
    }

    @Override
    public boolean checkWin(int user_id) {
        lock.readLock().lock();
        Game game = games.get(user_id);
        lock.readLock().unlock();
        if (game != null) {
            return game.checkWin();
        } else {
            System.out.println("Game does not exist for this user");
        }
        return false;
    }

    @Override
    public boolean checkLoss(int user_id) {
        lock.readLock().lock();
        Game game = games.get(user_id);
        lock.readLock().unlock();
        if (game != null) {
            return game.checkLoss();
        } else {
            System.out.println("Game does not exist for this user");
        }
        return false;
    }

    @Override
    public String displayGame(int userId) throws RemoteException {
        lock.readLock().lock();
        Game game = games.get(userId);
        lock.readLock().unlock();
        if (game != null) {
            return game.toString();
        } else {
            System.out.println("Game does not exist for this user");
        }
        return "Game does not exist";
    }

    @Override
    public boolean setUserInactive(String user_id) throws RemoteException {
        return userAccounts.setUserInactive(user_id);
    }
}

