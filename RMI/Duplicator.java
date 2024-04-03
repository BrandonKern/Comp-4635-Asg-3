public class Duplicator {
    /*
    Purpose: The purpose of Duplicator acts as a function that repeats a request. It is used to test server idempotence of functions
    Input: Takes in a CrissCrossPuzzleServer interface Object and a specific function from the CrissCrossPuzzleServerImpl to run.
    Output: None
     */
    static void runInterface( CrissCrossPuzzleServer duplicate, ConnectionFunction function)
    {
        // Call the method of the functional interface
       function.run(duplicate);

    }
}
