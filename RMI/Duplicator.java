public class Duplicator {
    /*
    Purpose: The purpose of Duplicator acts as a function that repeats a request. It is used to test server idempotence of functions
    Input: Takes in a CrissCrossPuzzleServer interface Object and a specific function from the CrissCrossPuzzleServerImpl to run.
    Output: None
     */
    static void runInterface( CrissCrossPuzzleServer duplicate, ConnectionFunction function)
    {
        // Generate a random number between 0 and 1
        double randomNum = Math.random();

        // Checks if the random number is greater than 0.5
        if(randomNum >=0.5) {
            // Call the method of the functional interface
            function.run(duplicate);
        }
        else {
            System.out.println(" \n Duplicator was not called ");
        }


    }
}
