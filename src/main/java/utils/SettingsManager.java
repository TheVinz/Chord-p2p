package utils;

import com.google.gson.Gson;

import java.io.*;

public class SettingsManager {
    private static NetworkSettings networkSettings = null;

    public static NetworkSettings getNetworkSettings(){
        if(networkSettings==null){
            String filename = "../settings/network_settings.json";
            String dirname = "../settings";
            File file = new File(filename);
            File dir = new File(dirname);
            if(!file.exists()){
                if(!dir.exists())
                    dir.mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try(FileWriter fileWriter = new FileWriter(file);
                    PrintWriter printWriter = new PrintWriter(fileWriter)){
                    networkSettings = new NetworkSettings();
                    Gson gson = new Gson();
                    printWriter.println(gson.toJson(networkSettings));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try(FileReader fileReader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(fileReader)){
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while((line = bufferedReader.readLine())!= null){
                        stringBuilder.append(line);
                    }
                    networkSettings = (new Gson()).fromJson(stringBuilder.toString(), NetworkSettings.class);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return networkSettings;
    }
}
