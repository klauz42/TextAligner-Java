package ru.klauz42.textformatter;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args){

        String path = "/home/claus/testdir/testfull.txt";
        String pathToSave = "/home/claus/testdir/output.txt";

        try {
            String string;
            string = alignText(50, path);
            System.out.println(string);

            Writer writer = openStreamToWrite(pathToSave);
            writer.write(string);
            writer.close();
        }

        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    static String alignText(int n, String path) throws IOException{
        ArrayList<String> words = fileToList(path);
        StringBuilder text = new StringBuilder();

        if (n < 1) throw new ArrayIndexOutOfBoundsException();

        if( n == 1){
            for (String string: words){
                for (char ch: string.toCharArray()){
                    text.append(ch + "\n");
                }
            }
        }

        else {
            int lettersInLine;        //количество символов в строке без пробелов
            int spacesInLine;        //сколько промежутков между строк
            int unusedCharsInLine;    //оставшиеся символы, когда новое слово уже не влезет
            int charsPerSpace;          //сколько символов на один пробел
            int extraSpaces;            //сколько слов

            int i = 0; //счетчик слов в тексте
            boolean isLineWritten = false;
            boolean isLineCompleted = false;
            boolean isTextCompleted = false;
            boolean slashNCase = false;
            ArrayList<String> wordsInString = null;

            while (!isTextCompleted) {
                wordsInString = new ArrayList<>();
                isLineCompleted = false;
                isLineWritten = false;
                slashNCase = false;
                spacesInLine = 0;
                lettersInLine = 0;
                unusedCharsInLine = 0;
                charsPerSpace = 0;
                extraSpaces = 0;
                spacesInLine = 0;


                while (!isLineWritten) {
                    //первое слово, без пробела в начале
                    if (lettersInLine == 0) {
                        lettersInLine += words.get(i).length();
                        if (words.get(i).startsWith("\n")) {
                            i++;
                            isLineCompleted = true;
                            isLineWritten = true;
                            break;
                        } else if (lettersInLine < n) {
                            wordsInString.add(words.get(i));
                            i++;
                        } else {
                            ArrayList<String> splittedWords = splitWord(words.get(i), n);
                            wordsInString.add(splittedWords.get(0));
                            isLineCompleted = true;
                            i++;
                            words.add(i, splittedWords.get(1));
                        }
                    }
                    //слова после первого
                    else {
                        if (words.get(i).startsWith("\n")) {
                            i++;
                            slashNCase = true;
                            isLineCompleted = true;
                        }
                        //проверка, уместится следующее слово в строку
                        //дополнитей пробел учитывается, т.к. строгое неравенство
                        else if ((spacesInLine + lettersInLine + words.get(i).length()) < n) {
                            lettersInLine += words.get(i).length();
                            spacesInLine++;
                            wordsInString.add(words.get(i));
                            //проверка, есть еще слова в листе
                            //и дополняем еще одним пробелом, для последней строчки
                            if (i + 1 == words.size()) {
                                spacesInLine++;
                                continue;
                            } else {
                                i++;
                            }
                        } else {
                            isLineCompleted = true;
                        }
                    }

                    if (isLineCompleted) {
                        //раскидываем пробелы
                        if (spacesInLine < 1) {
                            text.append(wordsInString.get(0) + "\n");
                            if(slashNCase) text.append("\n");
                            isLineWritten = true;
                        } else {
                            if (slashNCase) {
                                for (int j = 0; j < wordsInString.size() - 1; j++ ){
                                    text.append(wordsInString.get(j) + " ");
                                }
                                text.append(wordsInString.get(wordsInString.size() - 1) + "\n\n");
                            }
                            else {
                                unusedCharsInLine = n - lettersInLine;
                                extraSpaces = unusedCharsInLine % spacesInLine;
                                charsPerSpace = unusedCharsInLine / spacesInLine;
                                StringBuilder bigSpace = new StringBuilder();
                                for (int j = 0; j < charsPerSpace; j++) {
                                    bigSpace.append(" ");
                                }
                                //заполняем с доп пробелами
                                for (int j = 0; j < extraSpaces; j++) {
                                    text.append(wordsInString.get(j) + bigSpace + " ");
                                }
                                for (int j = extraSpaces; j < spacesInLine; j++) {
                                    text.append(wordsInString.get(j) + bigSpace);
                                }
                                text.append(wordsInString.get(wordsInString.size() - 1) + "\n");
                            }

                        }

                        isLineWritten = true;
                    }
                }
                if (i >= words.size() - 1) {
                    isTextCompleted = true;
                }
            }
        }
        return text.toString();
    }

    static ArrayList<String> splitWord(String word, int width) {
        char ch;
        ArrayList<String> splittedWords = new ArrayList<>();
        String vowels = "[AEIOUYaeiouyАОЭИУЫЕЁЮЯаэоиуыеёюя]";
        String substring1, substring2;
        int indexOfLastChar = width - 1;
        //учитывается дефис
        for (int i = width - 2; i > 0; i--){
            ch = word.charAt(i);
            if (String.valueOf(ch).matches(vowels)){
                indexOfLastChar = i + 1;
                break;
            }
        }
        substring1 = word.substring(0, indexOfLastChar) + "-";
        substring2 = word.substring(indexOfLastChar);
        splittedWords.add(substring1);
        splittedWords.add(substring2);

        return splittedWords;
    }
    static ArrayList<String> fileToList(String pathToFile) throws IOException{
        BufferedReader bufferedReader = openStreamToRead(pathToFile);
        ArrayList<String> words = new ArrayList<>();
        String str = null;
        while ((str = bufferedReader.readLine()) != null){
            words.addAll(Arrays.asList(str.split("\\s")));
            words.add("\n");
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
    static BufferedWriter openStreamToWrite(String pathToFile) throws IOException{
        File fileToWrite = new File(pathToFile);
        if(fileToWrite.canWrite()){
            return new BufferedWriter(new FileWriter(fileToWrite));
        }
        else throw new IOException("Can't write to file");
    }

}
