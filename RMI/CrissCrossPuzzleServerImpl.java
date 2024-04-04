import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
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
    private static final ReadWriteLock gameLock = new ReentrantReadWriteLock();
    private static final ReadWriteLock recordsLock = new ReentrantReadWriteLock();
    private static final ReadWriteLock seqNumLock = new ReentrantReadWriteLock();
    private HashMap <Integer, ClientStateRecord> clientRecords = new HashMap<>();
    private HashMap <Integer, Integer> clientSequenceNum = new HashMap<>();

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

    public void keepMyNameWhileAlive(int user_id) throws RemoteException {
        recordsLock.writeLock().lock();
        if (!clientRecords.containsKey(user_id)) {
            clientRecords.put(user_id, new ClientStateRecord(user_id));
            clientSequenceNum.put(user_id, 0);
        }
        recordsLock.writeLock().unlock();
    }

    public boolean heartBeat(int user_id) throws RemoteException {
        recordsLock.writeLock().lock();
		ClientStateRecord r = clientRecords.get(user_id);
        
		if (r != null) {
			r.setIsActive(true);
		}
        recordsLock.writeLock().unlock();
		return false;
	}

    //is not a remote function call (called by server)
    public Iterator<Map.Entry<Integer, ClientStateRecord>> getEntrySet () {
		return clientRecords.entrySet().iterator();
	}

    //is not a remote function call (called by server)
    public void removeClientRecord(int user_id) {
        recordsLock.writeLock().lock();
        try {
            setUserInactive(user_id);
            pEndGame(user_id);
        } catch (RemoteException e) {
            System.out.print("unable to remove client state in system");
            e.printStackTrace();
        }
		clientRecords.remove(user_id);
        recordsLock.writeLock().unlock();
	}

    @Override
    public String addWord(String word, int user_id, int seq) throws RemoteException {
        if (checkSeqNum(user_id, seq)) {
            if (wordRepo.addWord(word)) {
                return "Word added";
            }
            return "Word not added";
        }
        System.out.println(user_id + " repeated call");
        return "repeated call";

    }

    @Override
    public String removeWord(String word, int user_id, int seq) throws RemoteException {
        if (checkSeqNum(user_id, seq)) {
            if (wordRepo.deleteWord(word)) {
                return "Word deleted";
            }
            return "Word not deleted";
        }  
        System.out.println(user_id + " repeated call"); 
        return "repeated call";
    }

    @Override
    public boolean checkWord(String word, int user_id, int seq) throws RemoteException {
        if (checkSeqNum(user_id, seq)) {
            boolean check = wordRepo.checkWord(word);
            System.out.println("Check word " + word + " is " + check);
            return check;
        }   
        System.out.println(user_id + " repeated call");
        return true;
    }

    @Override
    public String checkScore(int user_id, int seq) throws RemoteException {
        if (checkSeqNum(user_id, seq)) {
            return userAccounts.checkUserScore(String.valueOf(user_id));
        }  
        System.out.println(user_id + " repeated call"); 
        return "repeated call";

    }

    @Override
    public boolean checkUser(int user_id, int seq) throws RemoteException {
        if (checkSeqNum(user_id, seq)) {
            return userAccounts.checkUser(String.valueOf(user_id));
        }
        System.out.println(user_id + " repeated call");   
        return true;   
    }

    @Override
    public String updateUserScore(int user_id, int seq) throws RemoteException {
        if (checkSeqNum(user_id, seq)) {
            return userAccounts.updateUserScore(String.valueOf(user_id));
        }   
        System.out.println(user_id + " repeated call");
        return "repeated call";
    }

    @Override
    public boolean endGame(int user_id, int seq) throws RemoteException {
        if (checkSeqNum(user_id, seq)) {
            return pEndGame(user_id);
        }   
        System.out.println(user_id + " repeated call");
        return true;   
    }

    @Override
    public String startGame(int user_id, int difficulty, int failed_attempts, int seq) throws RemoteException {
        if (checkSeqNum(user_id, seq)) {
            gameLock.writeLock().lock();

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
            gameLock.writeLock().unlock();
    
            if (games.get(user_id) != null) {
                System.out.println("Successfully created");
            } else {
                return "failure to start new game";
            }
            System.out.println("New Game made for user " + user_id);
            game.displayPuzzle();
            return game.toString();
        }   
        System.out.println(user_id + " repeated call");
        return "repeated call";
    }

    @Override
    public boolean guessLetter(int user_id, char letter, int seq) throws RemoteException {
        if (checkSeqNum(user_id, seq)) {
            gameLock.readLock().lock();
            Game game = games.get(user_id);
            gameLock.readLock().unlock();
            
            if (game != null) {
                return game.guessLetter(letter);
            } else {
                System.out.println("Game does not exist for this user");
            }
            return false;
        } 
        System.out.println(user_id + " repeated call");
        return true;   
    }

    @Override
    public boolean guessWord(int user_id, String word, int seq) throws RemoteException {
        if (checkSeqNum(user_id, seq)) {
            gameLock.readLock().lock();
            Game game = games.get(user_id);
            gameLock.readLock().unlock();
            if (game != null) {
                return game.guessWord(word);
            } else {
                System.out.println("Game does not exist for this user");
            }
            return false;
        }   
        System.out.println(user_id + " repeated call");
        return true;   

    }

    @Override
    public boolean checkWin(int user_id, int seq) {
        if (checkSeqNum(user_id, seq)) {
            gameLock.readLock().lock();
            Game game = games.get(user_id);
            gameLock.readLock().unlock();
            if (game != null) {
                return game.checkWin();
            } else {
                System.out.println("Game does not exist for this user");
            }
            return false;
        }   
        System.out.println(user_id + " repeated call");
        return false; 

    }

    @Override
    public boolean checkLoss(int user_id, int seq) {
        if (checkSeqNum(user_id, seq)) {
            gameLock.readLock().lock();
            Game game = games.get(user_id);
            gameLock.readLock().unlock();
            if (game != null) {
                return game.checkLoss();
            } else {
                System.out.println("Game does not exist for this user");
            }
            return false;
        }   
        System.out.println(user_id + " repeated call");
        return false; 

    }

    @Override
    public String displayGame(int user_id, int seq) throws RemoteException {
        if (checkSeqNum(user_id, seq)) {
            gameLock.readLock().lock();
            Game game = games.get(user_id);
            gameLock.readLock().unlock();
            if (game != null) {
                return game.toString();
            } else {
                System.out.println("Game does not exist for this user");
            }
            return "Game does not exist";
        }   
        System.out.println(user_id + " repeated call");
        return "repeat call"; 

    }

    @Override
    public boolean setUserInactive(int user_id) throws RemoteException {
        return userAccounts.setUserInactive(String.valueOf(user_id));
    }

    private boolean pEndGame(int user_id) {
        gameLock.writeLock().lock();
        Game game = games.get(user_id);
        if (game != null) {
            gameLock.writeLock().unlock();
            return true;
        } else {
            if (games.remove(user_id) != null) {
                gameLock.writeLock().unlock();
                return true;
            }
        }
        gameLock.writeLock().unlock();
        return false;
    }

    private boolean checkSeqNum(int user_id, int seqNum) {
        seqNumLock.readLock().lock();

        int oldSeq = clientSequenceNum.get(user_id);

        seqNumLock.readLock().unlock();
        System.out.println("Sequence num: " + seqNum);
        if (seqNum > oldSeq) {
            seqNumLock.writeLock().lock();

            clientSequenceNum.remove(user_id);
            clientSequenceNum.put(user_id, seqNum);

            seqNumLock.writeLock().unlock();
            return true;
        }
        return false;
    }
}

