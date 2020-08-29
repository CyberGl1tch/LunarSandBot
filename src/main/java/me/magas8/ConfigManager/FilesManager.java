package me.magas8.ConfigManager;

import me.magas8.LunarSandBot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class FilesManager {

    private LunarSandBot plugin;
    private HashMap<String, ConfigFile> filesMap = new HashMap<String, ConfigFile>();

    //------------------------------[ Manager Constructors Start ]-----------------------------//
    //We're making the constructors that initializes the files manager.
    //We're initializing the files manager by specifying the plugin.
    public FilesManager(LunarSandBot plugin) {
        this.plugin = plugin;
    }
    //-------------------------------[ Manager Constructors End ]------------------------------//

    //--------------------------------[ Folder Creation Start ]--------------------------------//
    //We're creating a folder with the specified path(s).
    public void folderSetup(String folderName, String... folderPathName) {
        String path = "";
        for(String s : folderPathName) {
            path += "/" + s;
        }
        File folder = new File(plugin.getDataFolder() + "/" + path, folderName);
        if(!folder.exists()) {
            folder.mkdirs();
        }
    }

    //We're creating a folder on the specified path.
    public void folderSetup(String folderName, String folderPathName) {
        File folder = new File(plugin.getDataFolder() + "/" + folderPathName, folderName);
        if(!folder.exists()) {
            folder.mkdirs();
        }
    }

    //We're creating a folder on the default path.
    public void folderSetup(String folderName) {
        File folder = new File(plugin.getDataFolder(), folderName);
        if(!folder.exists()) {
            folder.mkdirs();
        }
    }
    //---------------------------------[ Folder Creation End ]---------------------------------//

    //---------------------------------[ File Creation Start ]---------------------------------//
    //Using this method we create a file, specifying a the path of the folder too.
    public ConfigFile fileSetup(String fileName, String folderName) {
        ConfigFile file = new ConfigFile(plugin, fileName, folderName);
        filesMap.put("default/" + folderName + "/" + fileName + ".yml", file);
        return file;
    }

    //Using this method we create a new file on the default folder.
    public ConfigFile fileSetup(String fileName) {
        ConfigFile file = new ConfigFile(plugin, fileName);
        filesMap.put("default/" + fileName + ".yml", file);
        return file;
    }
    //----------------------------------[ File Creation End ]----------------------------------//

    //------------------------------------[ Methods Start ]------------------------------------//
    //Used to delete a configuration file.
    public void deleteFile(ConfigFile fileName) {
        if(fileName.getConfigFile().exists()) {
            fileName.getConfigFile().delete();
            filesMap.remove(fileName.getFolderPath());
            //Bukkit.getConsoleSender().sendMessage("Successfully deleted file. [" + fileName.getFileName() + "]");
        }
        else {
            //Bukkit.getConsoleSender().sendMessage("Unable to delete file. [" + fileName.getFileName() + "]");
        }
    }

    //Used to return a specific configuration file.
    public ConfigFile getFile(String fileName) {
        return filesMap.get(fileName);
    }

    //Used to return all configuration files as a HashMap.
    public HashMap<String, ConfigFile> getAllRegisteredConfigs() {
        return filesMap;
    }

    //Used to return a HashMap with all the paths and configs on the specified folder.
    public HashMap<String, ConfigFile> getAllConfigs(String filesPath) {
        HashMap<String, ConfigFile> map = new HashMap<>();
        for(Map.Entry file : filesMap.entrySet()) {
            String fileName = filesMap.get(file.getKey()).getConfigFile().getName();
            String path = ((String)file.getKey()).replace("/" + fileName, "");
            if(path.equals(filesPath)) {
                map.put(file.getKey() + "", (ConfigFile)file.getValue());
            }
        }
        return map;
    }

    //Used to return a HashMap with all the paths and configs of the specified folder even if they are not registered.
    public HashMap<String, ConfigFile> getAllFiles(String filesPath) {
        HashMap<String, ConfigFile> map = new HashMap<>();
        String folderPath;

        if(filesPath.contains("/")) {
            folderPath = filesPath.replace("default/", "");
        }
        else {
            folderPath = "default";
        }

        File folder;

        if(folderPath.equals("default")) {
            folder = new File(plugin.getDataFolder() + "");
            for(File file : folder.listFiles()) {
                if(!file.isDirectory()) {
                    ConfigFile confFile = fileSetup(file.getName().replace(".yml", ""));
                    map.put(folderPath + "/" + file.getName(), confFile);
                    filesMap.put(folderPath + "/" + file.getName(), confFile);
                }
            }
        }
        else {
            folder = new File(plugin.getDataFolder() + "/" + folderPath);
            for(File file : folder.listFiles()) {
                if(!file.isDirectory()) {
                    ConfigFile confFile = fileSetup(file.getName().replace(".yml", ""), folderPath);
                    map.put("default/" + folderPath + "/" + file.getName(), confFile);
                    filesMap.put("default/" + folderPath + "/" + file.getName(), confFile);
                }
            }
        }
        return map;
    }
    //Used to return a LinkedHashMap with all the paths and configs of the specified folder even if they are not registered by date created order.
    public LinkedHashMap<String, ConfigFile> getAllFilesByDateCreated(String filesPath) {
        LinkedHashMap<String, ConfigFile> map = new LinkedHashMap<>();
        String folderPath;

        if(filesPath.contains("/")) {
            folderPath = filesPath.replace("default/", "");
        }
        else {
            folderPath = "default";
        }

        File folder;

        if(folderPath.equals("default")) {
            folder = new File(plugin.getDataFolder() + "");
            File[] files = folder.listFiles();
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            for(File file : files) {
                if(!file.isDirectory()) {
                    ConfigFile confFile = fileSetup(file.getName().replace(".yml", ""));
                    map.put(folderPath + "/" + file.getName(), confFile);
                    filesMap.put(folderPath + "/" + file.getName(), confFile);
                }
            }
        }
        else {
            folder = new File(plugin.getDataFolder() + "/" + folderPath);
            File[] files = folder.listFiles();
            // Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            sortFilesByDateCreated(files);
            for(File file : files) {
                if(!file.isDirectory()) {
                    ConfigFile confFile = fileSetup(file.getName().replace(".yml", ""), folderPath);
                    map.put("default/" + folderPath + "/" + file.getName(), confFile);
                    filesMap.put("default/" + folderPath + "/" + file.getName(), confFile);
                }
            }
        }
        return map;
    }
    //Used to return a LinkedHashMap with all the paths and configs of the specified folder even if they are not registered by last modified order.
    public LinkedHashMap<String, ConfigFile> getAllFilesByLastModified(String filesPath) {
        LinkedHashMap<String, ConfigFile> map = new LinkedHashMap<>();
        String folderPath;

        if(filesPath.contains("/")) {
            folderPath = filesPath.replace("default/", "");
        }
        else {
            folderPath = "default";
        }

        File folder;

        if(folderPath.equals("default")) {
            folder = new File(plugin.getDataFolder() + "");
            File[] files = folder.listFiles();
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            for(File file : files) {
                if(!file.isDirectory()) {
                    ConfigFile confFile = fileSetup(file.getName().replace(".yml", ""));
                    map.put(folderPath + "/" + file.getName(), confFile);
                    filesMap.put(folderPath + "/" + file.getName(), confFile);
                }
            }
        }
        else {
            folder = new File(plugin.getDataFolder() + "/" + folderPath);
            File[] files = folder.listFiles();
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            for(File file : files) {
                if(!file.isDirectory()) {
                    ConfigFile confFile = fileSetup(file.getName().replace(".yml", ""), folderPath);
                    map.put("default/" + folderPath + "/" + file.getName(), confFile);
                    filesMap.put("default/" + folderPath + "/" + file.getName(), confFile);
                }
            }
        }
        return map;
    }
    public static void sortFilesByDateCreated (File[] files) {
        Arrays.sort(files, new Comparator<File>() {
            public int compare (File f1, File f2) {
                long l1 = getFileCreationEpoch(f1);
                long l2 = getFileCreationEpoch(f2);
                return Long.valueOf(l1).compareTo(l2);
            }
        });
    }

    public static long getFileCreationEpoch (File file) {
        try {
            BasicFileAttributes attr = Files.readAttributes(file.toPath(),
                    BasicFileAttributes.class);
            return attr.creationTime()
                    .toInstant().toEpochMilli();
        } catch (IOException e) {
            throw new RuntimeException(file.getAbsolutePath(), e);
        }
    }

    //-------------------------------------[ Methods End ]-------------------------------------//
}
