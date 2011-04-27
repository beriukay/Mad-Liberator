/* Class: MabLiberator
 * Author: Paul Gentemann
 * Date: 4/18/11
 * Description: This class leverages the objects of a helper to convert any text into
 * a mad lib, allowing a user to manually enter new words or let the helper decide from the
 * same list of nouns/adjectives/verbs/numbers.
 */

import java.io.*;    //To access PrintStream.
import java.util.*;  //To use ArrayList to store the input file.

public class MadLiberator {
  public static void main(String[] args) {
   
    //Introduction
    System.out.println("Welcome to the Mad Libberator!");
    
    //Initialize a SimpleInput object and get input file.
    SimpleInput keyboard = new SimpleInput();
    System.out.println("Just give me the name of the input file, and we can begin.");
    System.out.print("Input: ");
    String inputfilename = keyboard.nextLine();
           
    LibLibrary madlib = new LibLibrary(inputfilename);     //Initialize a helper class object to do the dictionary tasks.
    HashMap<String, int[]> hashindex = madlib.getIndex();  //Get access to the index that holds all the important info
    int index_size = hashindex.size();                     //How many unique words to be replaced.
    
    //Get the user to supply the next words, or choose the autofill option for a fast and easy job.
    System.out.println("There are a total of " + index_size + " words to replace.");
    System.out.println("I will supply you with the part of speech needed for each word. If you want to switch to");
    System.out.println("Autocomplete, or get help remembering what each part of speech is, just type -1");
    keyboard.nextLine();                                   //This is just a line break and a pause.
    
    String[] the_list = madlib.getScrubList();             //The source array that will be changed and exported to text.
    int completed = 0;                                     //How many words have already been replaced.
    
    for (int i = 0; i < the_list.length; i++) {            //For each term in the source array...
      if (hashindex.containsKey(the_list[i])){             //If the HashMap stores the given term...
        System.out.println("There are " + (index_size - completed) + " words to left to replace.");
        String part_of_speech = madlib.getNextVal(i);                                         //Get the of index associated with that row's part of speech.
        System.out.println("word: " + (completed + 1) + ", " + part_of_speech + ", please");  //Then ask the user to input something of the same type.
        String replacement = keyboard.nextWord();
        
        boolean same = madlib.compareWords(replacement, i);           //It does a quick search to see if the user has provided a word of the right type.
        
        while (!same){                                                //If it appears that they did not, then go into loop.
          System.out.println(replacement);
          if (replacement.equals("-1")){                              //This is the sentinel value to go into menu.
            menu(madlib, keyboard, replacement, i);  //Menu keeps the LibLibrary, SimpleInput, intended replacement string, value of i, and size of the_list.
          }
          System.out.println("That word does not appear to be " + part_of_speech + ".\n1)Use it anyway.\n2)Try a different word.");  //Warning menu.
          int fix = keyboard.nextInt();
          if (fix == 1) {                        //Choosing 1. indicates that the user knows better than the program; or frankly, my dear, doesn't give a damn.
            same = true;                         //So we break the loop.
          } else if (fix == 2) {                 //Otherwise...
            replacement = keyboard.nextWord();   //Give them the opportunity to pick a different replacement.
          }
        }
        boolean wordset = madlib.setNextWord(replacement, the_list[i]);  //When the word matches our table, or the user tells us to, we set the replacement in the desired location(s).
        if (wordset){                                                    //If it worked, increment the counter.
          completed ++;
        }
      }
    }
        
    
    //Now that the for loop has run its course, the index has been entirely seeded with new replacement strings. We just have to replace the proper spaces in the...
    //master list, export the new file, and we are done!
    String output_text;                           //Declare a string that will be written from the_list.
    if (the_list[0] != null){                     //Handles the trivial case of an empty list.
      output_text = the_list[0];                  //Fencepost problem, get first post.
      for (int i = 1; i < the_list.length; i++){  //For the rest of the items in the_list:
        if (the_list[i].equals(",") || the_list[i].equals(".") || the_list[i].equals(";") || the_list[i].equals(":") || the_list[i].equals("-")){
          output_text = output_text + the_list[i];  //Do special thing if next item is punctuation.
        } else {
          output_text = output_text + " " + the_list[i];  //Otherwise, add a space and the next word.
        }
      }
    } else {
      output_text = "";  //The trivial case will write an empty string.
    }
    System.out.println(output_text);                  //Show the output info in the console.
    
    
    String outputfilename = "Mad " + inputfilename;  //Make a string that takes the input file name and inserts "Mad" at the front of it.
    PrintStream outfile;                             //Declare a printstream...
    try {                                            //Make sure that the file gets created.
      outfile = new PrintStream(outputfilename);
    }
    catch (FileNotFoundException e) {
      return;
    }
    
    outfile.print(output_text);                       //And finally put output_text into outfile.
  }
  
  
  //The Menu.
  public static void menu(LibLibrary madlib, SimpleInput keyboard, String nextword, int num){ 
    System.out.println("MENU:");
    System.out.println("1. Switch to autocomplete mode\n2. What is a noun?\n3. What is an adjective?\n4. What is a verb?\n5. Back");
    int menu_choice = keyboard.nextInt();  //Choose a number corresponding to an option.
    if (menu_choice == 1){
      autoComplete(madlib, num);   //Activates autofill mode.
    } else if (menu_choice == 2){  //The rest just detail the part of speech and give examples.
      System.out.println("A noun is anything that can be described as a person, place, idea, or thing.");
      System.out.println("Examples include: Bob, Mount Everest, house, friendship, and poop.");
    } else if (menu_choice == 3){
      System.out.println("An adjective is a word that describes a noun.");
      System.out.println("Examples include: brown, tall, unwieldy, and hopeful.");
    } else if (menu_choice == 4){
      System.out.println("A verb is a word that communicates doing or being.");
      System.out.println("Examples include: run, walk, think, play, grab, and photosynthesize.");
    }   
    
  }
  
  //AutoComplete mode.
  public static void autoComplete(LibLibrary madlib, int number){
    String[] list = madlib.getScrubList();        //Get the list with remaining terms.
    for (int i = number; i < list.length; i++){   //For each term remaining...
      String replacement = madlib.autoFill(i);    //use the helper's autoFill option...
      madlib.setNextWord(replacement, list[i]);   //and set replacement as the next word.
    }
  }
  
} 
