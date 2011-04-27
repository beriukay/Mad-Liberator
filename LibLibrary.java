/* Class: MabLiberator
 * Author: Paul Gentemann
 * Date: 4/18/11
 * Description: This is a helper class that does most of the heavy lifting for the MadLiberator client.
 * It takes a string input (the name of a file), and reads the file's text into an array, keeping track of
 * punctuation. It then compares each word with an array of arrays that lists known nouns, adjectives, and
 * verbs. Using this info, it creates a HashMap linking the critical words to an array of indices that map
 * to scrublist's use of each word. This can be used by the client to replace the words of the source text
 * while keeping continuity of equivalent words. There are several other methods to allow for flexibility 
 * on the client side.
 * 
 * Mutators:
 * setDictionaryArray(), scrubList(String inputfilename), setIndex(), setInput(),
 * setNextWord(String next_word, String keyword), autoComplete(int number), 
 * 
 * Accessors:
 * getIndex(), getDic(), getScrubList(), getNextVal(int number), compareWords(String word, int index_number), 
 * findArrayIndexes(String word), getLibSize(), hasDuplicates(int number), isItInDic(String location), 
 * inNounArray(String location), inAdjArray(String location), inVerbArray(String location)
 */

import java.util.*;

public class LibLibrary {
  
  private String[] scrublist;
  private String[][] dic = new String[3][];
  private HashMap<String, int[]> index;
  
  
  //CONSTRUCTORS
  public LibLibrary(String inputfilename) {
    this.dic[0] = new String[90962];  //Array of nouns (with number of entries in the document).
    this.dic[1] = new String[28479];  //Array of adjectives (same).
    this.dic[2] = new String[30800];  //Array of verbs (same).
    setDictionaryArray();             //sets up the dictionary.
    scrubList(inputfilename);         //Sets up the array that will eventually be the PrintStream source.
    setIndex();                       //Sets up array of words to be replaced, and their associated part of speech.
  }
  //What to do with an empty argument.
  public LibLibrary() {
    setInput();
  }
  

  //MUTATORS
  //Populate the dictionary array from the appropriate text files.
  public void setDictionaryArray(){
    String[] name = {"dic_noun.txt", "dic_adj.txt", "dic_verb.txt"};
    
    for (int i = 0; i < 3; i++) {                //For each of the 3 dic[] arrays...
      SimpleInput f = new SimpleInput(name[i]);  
      for (int j = 0; j < dic[i].length; j++) {  //...Insert the next entry from the source file.
        dic[i][j] = f.nextLine();                //Each line is a new entry to the dictionary array.
      }
      Arrays.sort(dic[i]);                       //Just in case the source file is out of order.
    }
  }
  
  
  //Takes the same input file and scrubs it of punctuation marks so that the words can be properly analyzed.
  public void scrubList(String inputfilename){
    SimpleInput infile = new SimpleInput(inputfilename);
    String text;                                    //Initialize text to read source.
    boolean done = false;                           //Set up test condition to break away when done reading.                                
    while(!done) {
      try {                                         //Fencepost problem: must try/catch twice to make sure...
        text = infile.nextWord();                   //We don't have an empty infile, and then read til done.
        while(!done) {
          try{
            text = text + " " + infile.nextWord();  //Separates each word with a space.
          }
          catch (RuntimeException f) {
            done = true;                            //At this point, we are done reading.
            text = text.replace(",", " , ");        //Modify the text to add space between words and punctuation.
            text = text.replace(".", " . ");
            text = text.replace(";", " ; ");
            text = text.replace(":", " : ");
            text = text.replace("(", " ( ");
            text = text.replace(")", " ) ");
          }
          scrublist = text.split("\\s+");           //So set scrublist as an array using the string .split method.
          }                                         //"\\s+" Splits between whitespaces.
      }
      catch (RuntimeException e) {                  //If we hit a runtime exception, then the file was empty to start with.
        done = true;
      }
    } 
  } 
     

  //Create an index array to find all the list items that will be scrubbed and replaced (might need optimizing or tweaking).
  public void setIndex(){
    int n = getLibSize();                                        //Determine the number of unique words we are working with.
    HashMap<String, int[]> hm = new HashMap<String, int[]> (n);  //Make map linking each word to an array of its locations.
    for (int i = 0; i < scrublist.length; i++){                  //For each term in scrublist...
      if (isItInDic(scrublist[i]) > 0) {                         //If it is in the dictionary of parts of speech...
        int[] blah = findArrayIndexes(scrublist[i]);             //Call arrayIndex to find all the indices associated with given word.
        hm.put(scrublist[i], blah);                              //When the array is built and populated, link it to the word in the HashMap.
      }
    }
    index = hm;                                                  //In case testing is needed: println(Arrays.deepToString(hm.values().toArray()))
  }
        
  
  //Sets manual input mode, for a freestyle mad lib experience.
  public void setInput(){
    SimpleInput keyboard = new SimpleInput();        //Get manual text input.
    String inlib = keyboard.nextLine();
    SimpleInput textcount = new SimpleInput(inlib);  //One input stream to count, to set up an array of the proper size.
    SimpleInput text = new SimpleInput(inlib);       //One input stream to transcribe the info to the array. 
    String nextword;                                 //Initialize string to read the text.
    
    boolean done = false;                            //Sets up condition to end while loop.
    int count = 0;                                   //Sets up condition to end while loop, and counts the size of the manual input text.
    
    while(!done){                                    //This is the counting loop.
      try{
        nextword = textcount.nextWord();
        count ++;
      }
      catch (RuntimeException e) {
        done = true;
      }    
      done = false;                                  //Prime done again for the next loop.
    }
    
    String[] usertext = new String[count];           //Initialize array to store the text.

    while(!done){                                    //This is the transcription loop.
      try{
        for (int i = 0; i < count; i++) {
          nextword = text.nextWord();
          usertext[i] = nextword;
        }
      }
      catch (RuntimeException e) {
        done = true;
      }
    }
    this.scrublist = usertext;                       //Puts the data into list.
  }
  


  //Replace index word with the argument. If argument is empty, return false so the client can set up autofill
  public boolean setNextWord(String next_word, String keyword){  
    boolean success;                              //Return whether replacement worked.
    int[] array;                                  //Declare array to get the locations of all words that will change.

      array = index.get(keyword);                 //Set array to equal the value of the HashMap keyword.
      try {                                       //This is for error checking, though ideally not needed.
        for (int i = 0; i < array.length; i++){   //For each term in the array (each location in scrublist)...
          scrublist[array[i]] = next_word;        //Replace the word with next_word.                  
        }
        index.remove(keyword);                    //When all words have been replaced, get rid of the keyword from the HashMap.
        success = true;
      }
      catch (NullPointerException e){             //Before removing the HashMap keyword, this was a problem.
       success = false; 
      }
    return success;  
  }

  
  //Calls for next word's index in scrublist, looks up random word in proper dictionary, and fills index location with that word
  public String autoFill(int number){
    int dic_size;
    String replacement = "";                                         //Declare string, and initialize to empty string in case not found.
    if (index.containsKey(scrublist[number])){                       //If the HashMap stores the given term...
      int where = isItInDic(scrublist[number]) - 1;                  //Get the of proper dic index.
        dic_size = dic[where].length;          
        
        Random generator = new Random();                             //Use random number method.
        int choice = generator.nextInt(dic_size);                    //Pick an element from the proper dic[]
        
        System.out.println("Selected word: " + dic[where][choice]);  //Show what was selected.
        replacement = dic[where][choice];                            //replace the word.
    }    
    return replacement;
  }
  
  
  //ACCESSORS
  
  //Method to share the values contained in index upon request.
  public HashMap<String, int[]> getIndex() {
    return index;
  }
  //Method to share the values contained in dic upon request.
  public String[][] getDic() {
    return dic;
  }
  //Method to share the values contained in scrublist upon request.
  public String[] getScrubList(){
    return scrublist;
  }
   
  //Method to return a happy string instead of a number to identify which part of the dictionary
  //the given indexed word can be found under. We don't care about index location in this method.
  public String getNextVal(int number){
   int value = isItInDic(scrublist[number]);
   if (value == 1){
     return "a Noun";
   } else if (value == 2) {
     return "an Adjective";
   } else {
     return "a Verb";
     }    
  }
  
  //Method to return boolean of whether two words are both the same part of speech.
  public boolean compareWords(String word, int index_number){
    boolean same = false;                                       //Sets return value to be false if all of the conditions fail.
    String word2 = scrublist[index_number];            
    //System.out.println(index_number + ", " + word);                            
    if ((inNounArray(word) > 0) && (inNounArray(word2) > 0)){   //If both words can be found in the same dictionary array...
      same = true;                                              //Whatever the list, then the user is probably justified...
    }                                                           //In selecting the word, so we shouldn't harass them about it.
    if ((inAdjArray(word) > 0) && (inAdjArray(word2) > 0)){     //Only when none of these conditions apply should we declare
      same = true;                                              //That the words are not the same case.
    }
    if ((inVerbArray(word) > 0) && (inVerbArray(word2) > 0)){
      same = true;
    }
    return same;
  }
  
  
  //Takes input word and returns all the locations within scrublist that match that string.
  public int[] findArrayIndexes(String word){
    int counter = 0;                                       //Start a counter to see how big we need to make the array.
    for (int i = 0; i < scrublist.length; i++){            //For all the elements in scrublist...
      if (scrublist[i].equals(word)){                      //If that word matches the word in question...
        counter++;                                         //Increment the counter.
      }
    }
    if (counter > 0){                                      //If there are more than 0 such elements...
      int[] array = new int[counter];                      //Make an array of size = counter.
      int previous = 0;                                    //Initialize rachet that keeps track of the previous j-value.
      for(int i = 0; i < counter; i++){                    //For each of the terms in the array...
        for(int j = previous; j < scrublist.length; j++){  //Look for the index of the next repeat
          if(scrublist[j].equals(word)){
            if (array[i] == 0){                            //If array[i] has not yet been populated...
              array[i] = j;                                //Populate it with j, the index of scrublist.
              previous = j + 1;                            //Tighten the ratchet.
            }
          }
        }
      }
      return array;
    } else {
      int[] array = {-1};                                  //If there are no such terms, make an array of one element, with a negative value.
      return array;                                        //This should never be needed if the string comes from scrublist.
    }
  }
    
  
  //Sum up all the terms in list that are also in dic, skipping all duplicates
  public int getLibSize() {
    ArrayList<String> dupetracker = new ArrayList<String>();
    int count = 0;                                                           //Initialize counter.
    for (int i = 0; i < scrublist.length; i++){              
      if ((isItInDic(scrublist[i]) > -1) && (hasDuplicates(i) == 0)) {       //Short-circuit test: if in Dictionary, then check for duplicate...
        count ++;                                                            //If no duplicate is found, only then do we increment the counter.
      } else if ((isItInDic(scrublist[i]) > -1) && (hasDuplicates(i) > 0)){  //If it IS one of the words with duplicates...
        if (!dupetracker.contains(scrublist[i])){                            //If the word is not already contained in dupetracker...
          dupetracker.add(scrublist[i]);                                     //Add it to the arraylist. 
        } 
      }
    }
    int a = dupetracker.size();                                              //Now we have an array list whose size is equal to the number of words...
    count += a;                                                              //That have duplications. So add them to the count, and return that value.
    return count; 
  }

  
  
  //Method that returns an integer value showing how many times that word is duplicated in the source material.
  //Maybe should be turned into a boolean return, since there's a better way to keep track of duplicates in getLibSize.
  public int hasDuplicates(int number){
    String temp = scrublist[number];                                //Store the term we are checking.
    String[] copy = Arrays.copyOf(scrublist, scrublist.length);     //Copy the array and sort it for searching.
    Arrays.sort(copy);
    number = Arrays.binarySearch(copy, temp);                       //Get the index of term that matches the selected word.
    
    int dupe_counter = 0;                                           //Set up a counter for how many duplicates were counted.
    boolean done = false;                                           //A test for when we are done.
    
    while (!done){
      int prior = 1;                                                //A number to represent how far back we are looking in the sorted array.
      while (number - prior >= 0){                                  //But be sure not to check an index outside the array's limits.
        if (copy[number].equalsIgnoreCase(copy[number - prior])){   //If the string is the same word as the one before it...
          dupe_counter++;                                           //Then it is a dupe, and we need to look to see if there is another.
          prior++;
        } else {                                                    //If it isn't a dupe, then we are done looking backwards.
          prior = number + 1;
        }
      }
      int latter = 1;                                               //Now we look for matches that come later in the array.
      while (number + latter < copy.length){                        //But be sure not to look past the array's actual limits.
        if (copy[number].equalsIgnoreCase(copy[number + latter])){  //If the strings match...
          dupe_counter++;                                           //Then it is a dupe, and we need to look further ahead for more matches.
          latter++; 
        } else {                                                    //Otherwise, it isn't a dupe, and we are done looking.
          latter = copy.length;
        }
      }
      done = true;
    }
    return dupe_counter;                                            //return the total number of dupes counted.
  }
  
  
  //Asks the in*Array methods if the list item associated with the given number is contained in them, one at...
  //a time. It then returns a number depending on which array it is under, or a negative number if none.
  public int isItInDic(String location) {
    if (inNounArray(location) > 0) {
      return 1;
    } else if (inAdjArray(location) > 0) {
      return 2;
    } else if (inVerbArray(location) > 0) {
      return 3;
    } else {
      return -1;
    }   
  }

  //Binary search if the list item associated with the given number is in the NOUN array (and says where it can be found).
  public int inNounArray(String location){
    int results = Arrays.binarySearch(dic[0], location);
    return results;
  }
  //Binary search if the list item associated with the given number is in the ADJECTIVE array (and says where it can be found).
  public int inAdjArray(String location){
    int results = Arrays.binarySearch(dic[1], location);
    return results;
  }
  //Binary search if the list item associated with the given number is in the VERB array (and says where it can be found).
  public int inVerbArray(String location){
    int results = Arrays.binarySearch(dic[2], location);
    return results;
  }
  
  
}