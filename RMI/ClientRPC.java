import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Time;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/*
* The ClientRPC class provides functionality for the client side of the CrissCrossPuzzle game using Remote Method Invocation (RMI).
* It establishes a connection to the server, prompts the user for login information, and manages the game interaction.
* Connects the client to the server, prompts the user for login information, and manages game interaction.
*/

public class ClientRPC implements Runnable {
    private static final int TIMELIMIT_SECONDS = 5;
    static private int seqNum;
    static CrissCrossPuzzleServer connection;
    static int user_id;


    public void run () {
        while(true) {
            try {
                TimeUnit.SECONDS.sleep(TIMELIMIT_SECONDS);
                    connection.heartBeat(user_id);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }



    public static void main(String [] args)
    {
        if( args.length > 0) {
            try {
                Scanner scan = new Scanner(System.in);


                seqNum = 1;
                user_id = loginPrompt(scan);


                String Url = "rmi://" + InetAddress.getLocalHost().getHostAddress() + args[0];
                connection = (CrissCrossPuzzleServer) Naming.lookup(Url);

                connection.keepMyNameWhileAlive(user_id);

                (new Thread(new ClientRPC())).start();

                primaryHandler(scan); //primaryHandler(scan,connection);



            } catch (UnknownHostException | MalformedURLException | NotBoundException | RemoteException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println(" The argument you passed was not valid");
        }
    }


    /*
     * Name: loginPrompt
     * Purpose: Prompts the user to enter their login ID for accessing the crossword puzzle game.
     * Input: scan the Scanner object for user input
     * Output: connection the CrissCrossPuzzleServer connectio
     * There is also a restriction, where if the user's login is already active, then they cannot proceed with the game.
     * RemoteException if an error occurs during remote method invocation
     */
    public static int loginPrompt( Scanner scan) throws RemoteException {


        System.out.println(" Hi Welcome to the Best Crossword Puzzle of the Century!!! The Puzzler");
        System.out.print(" Please enter your Login ID: ");
        int login_id = scan.nextInt();
         //System.out.print(connection.login(login_id));

        
        
        return login_id;
    }

    /*
     * Name: startGamePrompt
     * Purpose: Asks the user to specify the difficulty level and the number of failed attempts allowed for the game.
     * Input: scan the Scanner object for user input
     * Output: An integer array containing two elements - the first element represents the difficulty level (number of words in the crossword puzzle), and the second element represents the number of failed attempts allowed.
     */
    public static int[] startGamePrompt(Scanner scan ) {
        // the first entry in the array is difficulty
        // the second entry in the array is failed attempts allowed


        int[] playerDecisions = new int[2];;
        System.out.println();
        System.out.print(" Please specify a difficulty by choosing the number of words in the crossword puzzle: ");
        int difficulty = scan.nextInt();
        System.out.println();
        System.out.print(" Please specify the number of failed attempts allowed: ");
        int failed_attempts = scan.nextInt();

        playerDecisions[0] = difficulty;
        playerDecisions[1] = failed_attempts;

        return playerDecisions;
    }


    /*
     * Name: promptBeforeStartingGame
     * Purpose: Displays a menu of options to the user before starting the game and performs corresponding actions based on user input.
     * Input: An integer representing the user's ID. A SingleRequestClient object called user represents a single user's clientSocket
     * Output: None (void). Displays a menu of options and performs actions based on user input.
     * Menu Options:
     *  <A> - Add a Word to Repository
     *  <R> - Remove a Word from the Repository
     *  <C> - Check a Word from the Repository
     *  <S> - Check your Score
     *  <G> - Start game
     *  <Q> - Quit
     */
    public static void promptBeforeStartingGame(Scanner scan) throws RemoteException {
        String option = "";
        do {
            System.out.println("\nPlease choose one of the following options:");
            System.out.println();
            System.out.println(" <A> - Add a Word to Repository");
            System.out.println();
            System.out.println(" <R> - Remove a Word from the Repository");
            System.out.println();
            System.out.println(" <C> - Check a Word from the Repository");
            System.out.println();
            System.out.println(" <S> - Check your Score");
            System.out.println();
            System.out.println(" <G> - Start game");
            System.out.println();
            System.out.println(" <Q> - Quit");
            System.out.println();
            System.out.print(" Enter your choice here: ");

            option = scan.next().toUpperCase();
            switch (option) {
                case "A":
                    System.out.println();
                    System.out.print(" Please enter the word you would like to add: ");
                    String addWord = scan.next();
                    System.out.println(connection.addWord(addWord, user_id, seqNum));
                   // calling duplicator
                    Duplicator.runInterface(connection,connection -> {
                        try {
                            connection.addWord(addWord, user_id, seqNum);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    seqNum++;
                    break;

                case "R":
                    System.out.println();
                    System.out.print(" Please enter the word you would like to remove: ");
                    String deleteWord = scan.next();
                    System.out.println(connection.removeWord(deleteWord, user_id, seqNum));
                    // calling Duplicator
                    Duplicator.runInterface(connection,connection -> {
                        try {
                            connection.removeWord(deleteWord, user_id, seqNum);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    seqNum++;

                    break;

                case "C":
                    System.out.println();
                    System.out.print(" Please enter the word you would like to check: ");
                    String checkWord = scan.next();
                    if (connection.checkWord(checkWord, user_id, seqNum)) {
                        // calling Duplicator
                        Duplicator.runInterface(connection, connection -> {
                            try {
                                connection.checkWord(checkWord, user_id, seqNum);
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                            seqNum++;
                        });
                        System.out.println("The word does exist.");
                    } else {
                        System.out.println("The word does not exist.");
                    }
                    break;

                case "S":

                    System.out.println(connection.checkScore(user_id, seqNum));
                    // Duplicator for checkScore
                    Duplicator.runInterface(connection, connection -> {
                        try {
                            connection.checkScore(user_id, seqNum);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    seqNum++;

                    break;

                case "G":
                    gameHandler(scan);
                    break;
                case "Q":
                    System.out.println("User quit is: " + connection.setUserInactive(user_id));
                    System.exit(1);
                    break;


            }
        } while (!(option.equals("Q")) && !(option.equals("G")));


    }

    /*
     * Name: primaryHandler
     * Purpose: Orchestrates the sequence of functions to display necessary information to the user.
     * Input: scan the Scanner object for user input
     * Input: connection the CrissCrossPuzzleServer connection
     * Output: None (void). Calls functions to display login prompt, greet the user, and prompt for game options.
     * RemoteException if an error occurs during remote method invocation
     */
    public static void primaryHandler( Scanner scan) throws RemoteException {
        if (connection.checkUser(user_id, seqNum)) {
            Duplicator.runInterface(connection, connection -> {
                try {
                    connection.checkUser(user_id, seqNum);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });
            seqNum++;
            promptBeforeStartingGame(scan);
        }

        else { System.out.print("User is already active or error occured in registering the user."); }

    }


    /*
     * Name: gameHandler
     * Purpose: Manages the game after the user selects to start the game.
     * Input: user_id the user's ID
     * Input: scan the Scanner object for user input
     * Input: connection the CrissCrossPuzzleServer connection
     * Output: None (void). Initiates game setup and interaction with the server.
     * Details: This function prompts the user to specify the difficulty level and the number of failed attempts allowed for the game. It then sends a request to the server with these parameters to initialize the game. After setting up the game, it enters a loop where it displays the game menu to the user and handles user inputs until the game is completed or the user quits.
     * RemoteException if an error occurs during remote method invocation
     */
    public static void gameHandler(Scanner scan) throws RemoteException {
        int[] arr = startGamePrompt(scan);

        //Sending start to server and reading/displaying game puzzle
        connection.startGame(user_id,arr[0],arr[1], seqNum);
        // calling duplicator
        Duplicator.runInterface(connection, connection -> {
            try {
                connection.startGame(user_id,arr[0],arr[1], seqNum);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        seqNum++;

        System.out.println(connection.displayGame(user_id, seqNum));
        //calling duplicator
        Duplicator.runInterface(connection,connection -> {
            try {
                connection.displayGame(user_id, seqNum);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        seqNum++;


        Boolean exit = false;
        do {
            exit = gameMenu(scan);

        } while (!exit);
        promptBeforeStartingGame(scan);

    }




    /*
    * Name: gameMenu
    * Purpose: Displays the game menu options, reads user input, and performs corresponding actions based on the choice.
    * Input: user_id the user's ID
    * Input: scan the Scanner object for user input
    * Input: connection the CrissCrossPuzzleServer connection
    * Output: true if the user chooses to quit, false otherwise
    * RemoteException if an error occurs during remote method invocation
    */
    public static Boolean gameMenu(Scanner scan) throws RemoteException {

        System.out.println();
        System.out.println();
        System.out.print("  <L> - guess letter");
        System.out.print("  <W> - guess word");
        System.out.print("  <C> - Check word");
        System.out.println("  <Q> - Quit");
        System.out.print(" Enter your choice here: ");
        String option = scan.next().toUpperCase();
        System.out.println();
        Boolean exit = false;

        switch (option) {
            case "L":
                System.out.println();
                System.out.print(" Please enter the letter your are guessing: ");
                exit = guessLetterHandler(scan.next().charAt(0));
                //exit = readAndPrintResponseToGAME(user,createMessage("gl", scan.next()), user_id);


                break;
            case "W":
                System.out.println();
                System.out.print(" Please enter the word you would like to check: ");
                exit = guessWordHandler(scan.next());
                // exit =readAndPrintResponseToGAME(user,createMessage("gw", scan.next()), user_id);
                break;
            case "C":
                System.out.println();
                System.out.print(" Please enter the word you would like to check: ");
                String checkWord = scan.next();
                connection.checkWord(checkWord, user_id, seqNum);
                //calling Duplicator
                Duplicator.runInterface(connection, connection -> {
                    try {
                        connection.checkWord(checkWord, user_id, seqNum);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
                seqNum++;
                //sendRequestToServer(user,createMessage("cw", scan.next()));
                break;
            case "Q":
                exit = true;
                //break;
        }

        return exit;


    }

    /*
     * Name: guessWordHandler
     * Handles all interaction for guessWord
     * Input: user_id the user's ID
     * Input: word the word being guessed
     * Input connection the CrissCrossServer connection
     * Ouput: true - if game has ended, false otherwise
     * RemoteException if an error occurs during remote method invocation
     */
    private static Boolean guessWordHandler(String word) throws RemoteException {
        Boolean quit = false;
        boolean success = connection.guessWord(user_id, word, seqNum);
        // Duplicator for guessWord
        Duplicator.runInterface(connection, connection -> {
            try {
                connection.guessWord(user_id, word, seqNum);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        seqNum++;

        System.out.println(connection.displayGame(user_id, seqNum));
        // Duplicator for displayGame
        Duplicator.runInterface(connection,connection -> {
            try {
                connection.displayGame(user_id, seqNum);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        seqNum++;

        if (success) {
            System.out.println("Correct guess");
            if (connection.checkWin(user_id, seqNum)) {
                // Duplicator for CheckWin
                Duplicator.runInterface(connection, connection -> {
                    try {
                        connection.checkWin(user_id, seqNum);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
                seqNum++;
                System.out.println("You have won the game!");

                connection.updateUserScore(user_id, seqNum);
                // Duplicator for UpdateUserScore
                Duplicator.runInterface(connection, connection -> {
                    try {
                        connection.updateUserScore(user_id, seqNum);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
                seqNum++;
                Duplicator.runInterface(connection, connection -> {
                    try {
                        connection.endGame(user_id, seqNum);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
                return true;
            }
            seqNum++;
        } else {
            System.out.println("Incorrect guess");
            if (connection.checkLoss(user_id, seqNum)) {
                // Duplicator for CheckLoss
                Duplicator.runInterface(connection, connection -> {
                    try {
                        connection.checkLoss(user_id, seqNum);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
                seqNum++;

                System.out.println("You have lost the game!");

                connection.endGame(user_id, seqNum);
                // Duplicator for endGame
                Duplicator.runInterface(connection, connection -> {
                    try {
                        connection.endGame(user_id, seqNum);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
                seqNum++;
                return true;
            }
        }
        return quit;
    }

    /*
     * Name: guessLetterHandler
     * Handles all interaction for guessWord
     * Input: user_id the user's ID
     * Input: letter the letter being guessed
     * Input connection the CrissCrossServer connection
     * Ouput: true - if game has ended, false otherwise
     * RemoteException if an error occurs during remote method invocation
     */
    private static Boolean guessLetterHandler(char letter) throws RemoteException {
        Boolean quit = false;
        boolean success = connection.guessLetter(user_id, letter, seqNum);
        // Calling Duplicator for guessLetter
        Duplicator.runInterface(connection,connection -> {
            try {
                connection.guessLetter(user_id, letter, seqNum);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        seqNum++;

        System.out.println(connection.displayGame(user_id, seqNum));
        // Calling Duplicator for displayGame
        Duplicator.runInterface(connection, connection -> {
            try {
                connection.displayGame(user_id, seqNum);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        seqNum++;

        if (success) {
            System.out.println("Correct guess");
            if (connection.checkWin(user_id, seqNum)) {
                // Duplicator for CheckWin
                Duplicator.runInterface(connection, connection -> {
                    try {
                        connection.checkWin(user_id, seqNum);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
                seqNum++;

                System.out.println("You have won the game!");
                connection.updateUserScore(user_id, seqNum);
                // Duplicator for UpdateUserScore
                Duplicator.runInterface(connection, connection -> {
                    try {
                        connection.updateUserScore(user_id, seqNum);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
                seqNum++;

                connection.endGame(user_id, seqNum);
                // Duplicator for endGame
                Duplicator.runInterface(connection, connection -> {
                    try {
                        connection.endGame(user_id, seqNum);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
                seqNum++;
                return true;
            }
            seqNum++;
        } else {
            System.out.println("Incorrect guess");
            if (connection.checkLoss(user_id, seqNum)) {
                // Duplicator for CheckLoss
                Duplicator.runInterface(connection, connection -> {
                    try {
                        connection.checkLoss(user_id, seqNum);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
                seqNum++;
                System.out.println("You have lost the game!");

                connection.endGame(user_id, seqNum);
                // Duplicator for endGame
                Duplicator.runInterface(connection, connection -> {
                    try {
                        connection.endGame(user_id, seqNum);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
                seqNum++;
                return true;
            }
        }
        return quit;
    }

}
