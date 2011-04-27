import java.io.*;
import java.util.StringTokenizer;

    /** A helper class that allows words, numbers, and booleans
     *  to be read from an input stream. This can be either System.in
     *  or a named file.
     *  All methods throw a RuntimeException object if EOF is reached
     *  when a value is requested.
     *  @author David J. Barnes (d.j.barnes@ukc.ac.uk)
     *  @version Version 1.4s (10th Feb 1999) A simplified version
     *  without protected methods for Chapter 7.<br>
     *  Version 1.4 is the full version.
     *  Version 1.3 did not include a constructor taking a File argument.<br>
     *  Version 1.2 did not include the discardLine method.<br>
     *  Version 1.1 did not correctly discard the current line when
     *  nextLine() was used.<br>
     *  Version 1.0 used a StreamTokenizer as the basis for its input.
     *  This has been replaced by a StringTokenizer to make parsing
     *  of floating point numbers more flexible.<br>
     */

public class SimpleInput {
    /** Read input from System.in.
     */
    public SimpleInput(){
        // Use standard input.
    }

    /** Read input from the given file.
     *  @param file Read input from the named file.
     *  @throws java.lang.RuntimeException if file cannot be opened.
     */
    public SimpleInput(String file) throws RuntimeException {
        if(file == null){
            throw new RuntimeException("null file passed to SimpleInput.");
        }
        // Try to make sure it exists.
        File details = new File(file);
        if(!details.exists()){
            throw new RuntimeException(file+" does not exist.");
        }
        else if(!details.canRead()){
            throw new RuntimeException(file+" exists but is unreadable.");
        }
        else if(!details.isFile()){
            throw new RuntimeException(file+" is not a regular file.");
        }
        else{
            // We should be ok.
            try{
                setReader(new BufferedReader(new FileReader(details)));
            }
            catch(FileNotFoundException e){
                throw new RuntimeException("Failed to open "+file+
                        " for an unknown reason.");
            }
        }
    }

    /** Read input from the given file.
     *  @param details Read input from the given file.
     *  @throws java.lang.RuntimeException if file cannot be opened.
     */
    public SimpleInput(File details) throws RuntimeException {
        // Try to make sure it exists.
        if(details == null){
            throw new RuntimeException(
                        "null file details passed to SimpleInput.");
        }
        else if(!details.exists()){
            throw new RuntimeException(details.getName()+" does not exist.");
        }
        else if(!details.canRead()){
            throw new RuntimeException(details.getName()+
                                       " exists but is unreadable.");
        }
        else if(!details.isFile()){
            throw new RuntimeException(details.getName()+
                                       " is not a regular file.");
        }
        else{
            // We should be ok.
            try{
                setReader(new BufferedReader(new FileReader(details)));
            }
            catch(FileNotFoundException e){
                throw new RuntimeException("Failed to open "+
                                           details.getName()+
                                           " for an unknown reason.");
            }
        }
    }

    /** @return The next number from the input stream as a short.
     *  Non-numerical input is skipped. A floating point number on the
     *  input is truncated.
     *  @throws java.lang.RuntimeException if end-of-file has been reached.
     */
    public short nextShort() throws RuntimeException {
        return (short) nextNumber();
    }

    /** @return The next number from the input stream as an int.
     *  Non-numerical input is skipped. A floating point number on the
     *  input is truncated.
     *  @throws java.lang.RuntimeException if end-of-file has been reached.
     */
    public int nextInt() throws RuntimeException {
        return (int) nextNumber();
    }

    /** @return The next number from the input stream as a long.
     *  Non-numerical input is skipped. A floating point number on the
     *  input is truncated.
     *  @throws java.lang.RuntimeException if end-of-file has been reached.
     */
    public long nextLong() throws RuntimeException {
        return (long) nextNumber();
    }

    /** @return The next floating point number from the input stream
     *  as a float.
     *  Non-numerical input is skipped. A floating point number on the
     *  input is truncated.
     *  @throws java.lang.RuntimeException if end-of-file has been reached.
     */
    public float nextFloat() throws RuntimeException {
        return (float) nextNumber();
    }

    /** @return The next floating point number from the input stream
     *  as a double.
     *  Non-numerical input is skipped.
     *  @throws java.lang.RuntimeException if end-of-file has been reached.
     */
    public double nextDouble() throws RuntimeException {
        return nextNumber();
    }

    /** @return The next floating point number from the input stream
     *  as a double.
     *  Non-numerical input is skipped.
     *  @throws java.lang.RuntimeException if end-of-file has been reached.
     */
    private double nextNumber() throws RuntimeException {
        // Number will refer to an appropriate Double when one is found.
        // Return number.doubleValue();
        Double number = null;
        do{
            String numString = null;
            try{
                numString = nextToken();
                // See if it is a proper number.
                number = new Double(numString);
            }
            catch(NumberFormatException e){
                // That wasn't a recognised number.
                // Try replacing 'd/D' with 'e'.
                numString = numString.toLowerCase();
                numString = numString.replace('d','e');
                try{
                    number = new Double(numString);
                }
                catch(NumberFormatException ex){
                    // Failed again.
                }
            }
        } while(number == null);
        return number.doubleValue();
    }

    /** @return The next token from the input stream as a String.
     *  This does not distinguish numbers from words.
     *  @throws java.lang.RuntimeException if end-of-file has been reached.
     *  @see java.util.StringTokenizer
     */
    public String nextWord() throws RuntimeException {
        return nextToken();
    }

    /** @return A boolean value corresponding to the next boolean
     *  word on the input stream.
     *  This will be either true, false, t, or f, ignoring case.
     *  Numerical input and non-boolean words are skipped.
     *  @throws java.lang.RuntimeException if end-of-file has been reached.
     */
    public boolean nextBoolean() throws RuntimeException {
        for(; ;){
            String s = nextWord();
            if(s.equalsIgnoreCase("t") || s.equalsIgnoreCase("true")){
                return true;
            }
            else if(s.equalsIgnoreCase("f") || s.equalsIgnoreCase("false")){
                return false;
            }
        }
    }

    /** @return The next line on the input stream as a string.
     *  This will discard the remainder of the current line, if any.
     *  @throws java.lang.RuntimeException if end-of-file has been reached.
     */
    public String nextLine() throws RuntimeException {
        try{
            discardLine();
            BufferedReader reader = getReader();
            String line = reader.readLine();
            // Check for EOF.
            if(line == null){
                throw new RuntimeException("End of input.");
            }
            return line;
        }
        catch(IOException e){
            // Pass it on as an unchecked exception.
            throw new RuntimeException(e.getMessage());
        }
    }

    /** This will discard the remainder of the current line, if any.
     *  @since Version 1.3
     */
    public void discardLine(){
        setTokenizer(new StringTokenizer(""));
    }

    /** @return The delimiters currently in use by the string
     *  tokenizer.
     *  By default these are " \t".
     */
    public String getDelimiters(){
        return delimiters;
    }

    /** Mutator for the delimiters to be used in tokenizing the input.
     *  @param The new string of delimiters.
     *  By default these are " \t".
     */
    public void setDelimiters(String d){
        if((d != null) && (d.length() > 0)){
            delimiters = d;
        }
    }

    /** @return The next token from the input. Tokens are
     *  delimited by the current set of delimiters.
     *  @throws java.lang.RuntimeException if end-of-file has been reached.
     */
    private String nextToken() throws RuntimeException {
        StringTokenizer t = getTokenizer();
        final String delimiters = getDelimiters();
        if(!t.hasMoreTokens()){
            do{
                String line = nextLine();
                t = new StringTokenizer(line,delimiters);
                setTokenizer(t);
            } while(!t.hasMoreTokens());
        }
        return t.nextToken(delimiters);
    }

    /** Accessor for the buffered reader associated with the input stream.
     *  @see java.io.BufferedReader
     */
    private BufferedReader getReader(){
        return reader;
    }

    /** Mutator for the buffered reader associated with the input stream.
     *  @param The reader.
     *  @see java.io.BufferedReader
     */
    private void setReader(BufferedReader r){
        reader = r;
    }

    /** @return The internal string tokenizer.
     *  @see java.util.StringTokenizer
     */
    private StringTokenizer getTokenizer(){
        return tokenizer;
    }

    /** Mutator to set the internal string tokenizer.
     *  @param The new string tokenizer.
     *  @see java.util.StringTokenizer
     */
    private void setTokenizer(StringTokenizer t){
        tokenizer = t;
    }

    // A reader for System.in that is shared by all objects
    // that are created with the no-arg constructor
    private static final BufferedReader stdinReader =
                    new BufferedReader(new InputStreamReader(System.in));
    // By default use standard input.
    private BufferedReader reader = stdinReader;
    // The delimiters used in tokenizing.
    private String delimiters = " \t";
    // The tokenizer.
    private StringTokenizer tokenizer = new StringTokenizer("");
}
