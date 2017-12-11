package ru.klauz42.textformatter;

import com.sun.xml.internal.ws.util.StringUtils;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String path = "/home/claus/testdir/testfull.txt";
        try {
            ArrayList<String> arrayList = fileToList(path);
            String string;
            string = alignText(50, arrayList);
            System.out.println(string);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("check");

    }

    static String alignText(int n, ArrayList<String> words){
        StringBuilder text = new StringBuilder();
        int lettersInString = 0;
        int singleSpacesInString = 0;
        int spacesInString = 0;
        int unusedCharsInString = 0;
        int charsPerSpace = 0;
        int extraSpaces = 0;

        int i = 0; //counter
        boolean isStringCompleted = false;
        boolean isTextCompleted = false;
        ArrayList<String> wordsInString = null;

        while (!isTextCompleted)
        {
            wordsInString = new ArrayList<>();
            isStringCompleted = false;
            singleSpacesInString = 0;
            lettersInString = 0;
            unusedCharsInString = 0;
            charsPerSpace = 0;
            extraSpaces = 0;
            singleSpacesInString = 0;

            while (!isStringCompleted){
                if(lettersInString == 0) {
                    lettersInString += words.get(i).length();
                    if (lettersInString < n) {
                        wordsInString.add(words.get(i));
                        i++;
                    }
                    else {                                    //task: обработать отдельно, когда ни слова не помещается, сейчас просто вставляем
                        text.append(words.get(i) + "\n");
                        i++;
                        isStringCompleted = true;
                        break;
                    }
                }
                else {
                    singleSpacesInString++;
                    if((singleSpacesInString + lettersInString + words.get(i).length()) < n){
                        lettersInString += words.get(i).length();
                        wordsInString.add(words.get(i));
                        if(i + 1 == words.size()) continue;
                        else i++;
                    }
                    else { //раскидываем пробелы
                        unusedCharsInString = n - lettersInString;
                        extraSpaces = unusedCharsInString % singleSpacesInString;
                        charsPerSpace = unusedCharsInString / singleSpacesInString;
                        StringBuilder bigSpace = new StringBuilder();
                        for (int j = 0; j < charsPerSpace; j++){
                            bigSpace.append(" ");
                        }
                        //заполняем с доп пробелами
                        for (int j = 0; j < extraSpaces ; j++){
                            text.append(wordsInString.get(j) + bigSpace + " ");
                        }
                        for (int j = extraSpaces; j < singleSpacesInString - 1; j++){
                            text.append(wordsInString.get(j) + bigSpace);
                        }
                        text.append(wordsInString.get(wordsInString.size() - 1) + "\n");
                        isStringCompleted = true;
                    }
                }
            }
            if(i >= words.size() - 1){
                isTextCompleted = true;
            }
        }

        return text.toString();
    }

    static ArrayList<String> fileToList(String pathToFile) throws IOException{
        BufferedReader bufferedReader = openStreamToRead(pathToFile);
        ArrayList<String> words = new ArrayList<>();
        String str = null;
        while ((str = bufferedReader.readLine()) != null){
            words.addAll(Arrays.asList(str.split("\\s")));
        }
        bufferedReader.close();
        return words;
    }
    static BufferedReader openStreamToRead(String pathToFile) throws IOException{
        File fileToRead = new File(pathToFile);
        if(fileToRead.exists()){
            if(fileToRead.canRead()){
                return new BufferedReader(new FileReader(fileToRead));
            }
            else throw new IOException("Can't read file");
        }
        else throw new FileNotFoundException("Can't find file");

    }
    static BufferedWriter openStreamToWritr(String pathToFile) throws IOException{
        File fileToWrite = new File(pathToFile);
        if(fileToWrite.canWrite()){
            return new BufferedWriter(new FileWriter(fileToWrite));
        }
        else throw new IOException("Can't write to file");
    }

}
