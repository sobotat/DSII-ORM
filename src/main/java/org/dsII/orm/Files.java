package org.dsII.orm;

import java.io.*;

public class Files {
    
    public static void writeString(String fileName, String text) {
        try {
            // Opening File
            File file = new File(fileName);
            if (file.getParentFile().mkdirs()){
                System.out.println("Dir was created");
            }

            FileWriter fileWriter;

            fileWriter = new FileWriter(file);

            fileWriter.flush();
            fileWriter.write(text);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("WriteString IOException -> " + fileName);
            e.printStackTrace();
        }
    }

    public static String readString(String fileName) {
        StringBuilder result = new StringBuilder();

        try{
            File file = new File(fileName);
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                while (br.ready()) {
                    result.append(br.readLine()).append("\n");
                }
                br.close();
            }else{
                Files.writeString(fileName, "");
            }
        } catch (Exception e){
            System.out.println("Read String -> " + fileName + "\n" + e.getMessage());
        }

        return result.toString();
    }
}
