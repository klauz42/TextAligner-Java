package ru.klauz42.textformatter;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String path = "/home/claus/testdir/testfull.txt";
        try {
            String string;
            string = alignText(50, path);
            System.out.println(string);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("check");

    }

    static String alignText(int n, String path) throws IOException{
        ArrayList<String> words = fileToList(path);
        StringBuilder text = new StringBuilder();
        int lettersInLine;        //количество символов в строке без пробелов
        int spacesInLine;        //сколько промежутков между строк
        int unusedCharsInLine;    //оставшиеся символы, когда новое слово уже не влезет
        int charsPerSpace;          //сколько символов на один пробел
        int extraSpaces;            //сколько слов

        int i = 0; //счетчик слов в тексте
        boolean isLineCompleted = false;
        boolean isTextCompleted = false;
        ArrayList<String> wordsInString = null;

        while (!isTextCompleted)
        {
            wordsInString = new ArrayList<>();
            isLineCompleted = false;
            spacesInLine = 0;
            lettersInLine = 0;
            unusedCharsInLine = 0;
            charsPerSpace = 0;
            extraSpaces = 0;
            spacesInLine = 0;

            while (!isLineCompleted){
                //первое слово, без пробела в начале
                if(lettersInLine == 0) {
                    lettersInLine += words.get(i).length();
                    if (lettersInLine < n) {
                        wordsInString.add(words.get(i));
                        i++;
                    }
                    else {                                    //task: обработать отдельно, когда ни слова не помещается, сейчас просто вставляем
                        text.append(words.get(i) + "\n");
                        i++;
                        isLineCompleted = true;
                        break;
                    }
                }
                //слова после первого
                else {
                    //проверка, уместится следующее слово в строку
                    //дополнитей пробел учитывается, т.к. строгое неравенство
                    if((spacesInLine + lettersInLine + words.get(i).length()) < n){
                        lettersInLine += words.get(i).length();
                        wordsInString.add(words.get(i));
                        //проверка, есть еще слова в листе
                        //и дополняем еще одним пробелом, для последней строчки
                        if(i + 1 == words.size()) {
                            spacesInLine++;
                            continue;
                        }

                        else {
                            i++;
                            spacesInLine++;
                        }
                    }
                    else { //раскидываем пробелы
                        //spacesInLine--;//crutchy
                        unusedCharsInLine = n - lettersInLine;
                        extraSpaces = unusedCharsInLine % spacesInLine;
                        charsPerSpace = unusedCharsInLine / spacesInLine;
                        StringBuilder bigSpace = new StringBuilder();
                        for (int j = 0; j < charsPerSpace; j++){
                            bigSpace.append(" ");
                        }
                        //заполняем с доп пробелами
                        for (int j = 0; j < extraSpaces ; j++){
                            text.append(wordsInString.get(j) + bigSpace + " ");
                        }
                        for (int j = extraSpaces; j < spacesInLine; j++){
                            text.append(wordsInString.get(j) + bigSpace);
                        }
                        text.append(wordsInString.get(wordsInString.size() - 1) + "\n");
                        isLineCompleted = true;
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
