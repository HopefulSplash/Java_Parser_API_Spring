package com.HClewlow.task;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;


@RestController
@RequestMapping(value = "/JPT", produces = MediaType.TEXT_PLAIN_VALUE)
public class Java_Programming_Test {

    // for output
    StringBuilder sb;
    //total word count
    public  int word_count =0;
    //total number of chars
    public  int total_chars=0;
    //average word length
    public  float avg_word_length = 0.000F;
    //an array of length count (index 0 = length of 0, index 1 = how many words of length 1, index 102 = over 100)
    public  int[] word_length_count = new int[102];
    //location of file to read
    public  String file_path = "src/main/resources/Example File.txt";
    //text to process from the file
    public  String text_to_process;
    //lists of the index and the max value
    public  ArrayList<Integer> max = new ArrayList<>();
    public  ArrayList<Integer> max_index = new ArrayList<>();
    private String user_entered_file_path;

    //getter for file path
    public String get_file_path() {
        return file_path;
    }

    //a function to return the string from a file
    public String get_file_as_string(String file_path) throws IOException {

        Path path = Path.of(file_path);
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(s -> process_file(s));
        } catch (IOException ex) {
            // do something or re-throw...
        }

        return text_to_process;
    }

    //a function to split words according to the rule of a new word is when there is a space
    public int  process_file(String string) {
        String[] data = string.split(" ");
        //for each for we then add to the count of total words and process it for averages etc
        Arrays.stream(data).forEach(datum -> {
            add_count(1);
            process_word(datum);
        });
        return word_count;
    }

    //a function to add to work count
    public  int add_count(int add) {
        return word_count = (word_count + add);
    }

    //a function to check the total length and track the size of the word
    public  String  process_word(String word) {
        //if the word  has any punctuation we remove it
        //regex for removing the special chars and punctuation but keeping selected chars - Dependent on clients need
        //To add complexity we can use a more detailed search but checking if it's a number, date, etc. (brief said don't over complicate it :) )
        word = word.replaceAll("[\\p{Punct}&&[^-/:&$+=%#@]]+", "");

        int word_length = word.length();
        total_chars = total_chars + word_length;
        track_length(word_length);

        return word;
    }

    //a function to add to the count of words that have the same length
    public void  track_length(int word_len) {
        int temp;
        //if the length is more than 0 and less than 10
        if (word_len != 0 && word_len <= word_length_count.length-1) {
            temp = word_len;
            word_length_count[temp]++;
        }
        //extra functionality to track words with a length hire than 10
        else if (word_len != 0) {
            word_length_count[word_length_count.length-1]++;
        }
    }

    //a function to print out the number of words length x
    public void print_word_length_count() {
        for (int i = 0; i < word_length_count.length; i++) {
            if (i <= word_length_count.length-2) {
                if (word_length_count[i] >= 1) {
                    sb.append("Number of words of length " + i + " is " + word_length_count[i]);
                    sb.append(System.getProperty("line.separator"));

                }
            } else {
                if (word_length_count[i] > 0) {
                    sb.append("Number of words of greater than length 100 is " + word_length_count[i]);
                    sb.append(System.getProperty("line.separator"));

                }
            }
        }
    }

    // a function to print the most frequently occurring lengths
    public  void print_most_freq_occur() {
        int i;

        //setting the first max as the first element in the array of counts
        max.add(word_length_count[0]);

        for (i = 0; i < word_length_count.length; i++) {

            if (word_length_count[i] > max.get(0)) {
                //higher than max clear and set index 0
                max.clear();
                max_index.clear();
                max.add(word_length_count[i]);
                max_index.add(i);
            } else if (word_length_count[i] == max.get(0)) {
                //same as max then just add to the list
                max.add(word_length_count[i]);
                max_index.add(i);
            }
            //lower than max do nothing
        }

        StringBuilder most_freq_ocur_length = new StringBuilder("The most frequently occurring word length is " + max.get(0) + ", for word lengths of ");
        for (i = 0; i < max_index.size(); i++) {
            if (i == 0) most_freq_ocur_length.append(max_index.get(i));
            else {
                most_freq_ocur_length.append(" & ").append(max_index.get(i));
            }
        }

        sb.append(most_freq_ocur_length);
        sb.append(System.getProperty("line.separator"));

    }
        @GetMapping(value = "/parse_File")
    public String run_application(@RequestParam("path") String path) {
        this.user_entered_file_path = path;

        sb = new StringBuilder("");
        word_count =0;
        total_chars=0;
        avg_word_length = 0.000F;
        word_length_count = new int[102];
        text_to_process = "";
        max = new ArrayList<>();
        max_index = new ArrayList<>();

        //input the file name
        if (user_entered_file_path == null) {
            Scanner myObj = new Scanner(System.in);  // Create a Scanner object
            System.out.println("Enter A File Path: ");
            user_entered_file_path = myObj.nextLine();  // Read user input
        }

        File file = new File(user_entered_file_path);
        if (file.exists()) {

            // reading the file into the application as a string
            try {
                get_file_as_string(user_entered_file_path);
            } catch (IOException e) {
                sb.append("\nFile Path Is Not Valid, Please Try Again!");
            }
            sb.append("Word count = " + word_count);
            sb.append(System.getProperty("line.separator"));

            //calculating the average work count
            avg_word_length = ((float) total_chars) / word_count;
            String formattedString = String.format("%.3f", avg_word_length);

            String float_rest = "Average word length = " + formattedString;
            sb.append(float_rest);
            sb.append(System.getProperty("line.separator"));
            //printing a list of the length of words
            print_word_length_count();
            //printing the most frequently occurring word lengths
            print_most_freq_occur();
        } else {
            sb.append("\nFile Does Not Exist");
        }
        return sb.toString();
    }
}







