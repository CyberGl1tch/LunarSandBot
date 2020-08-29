package me.magas8.Utils;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;


public class Updater
{
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0";
    // Direct download link
    private String downloadLink;
    // Provided plugin
    private Plugin plugin;
    // The folder where update will be downloaded
    private File updateFolder;
    // The plugin file
    private File file;
    // ID of a project
    private int id;
    // return a page
    private int page = 1;
    // Set the update type
    private UpdateType updateType;
    // Get the outcome result
    private Result result = Result.SUCCESS;
    // If next page is empty set it to true, and get info from previous page.
    private boolean emptyPage;
    // Version returned from spigot
    private String version;
    // If true updater is going to log progress to the console.
    private boolean logger;
    // Updater thread
    private Thread thread;

    private static String DOWNLOAD = "";
    private static final String VERSIONS = "/versions";
    private static final String PAGE = "?page=";
    private static String versionID = "";
    private static final String API_RESOURCE = "https://api.spiget.org/v2/resources/";
    private static final String DOWNLOAD_LINK = "https://api.spiget.org/v2/resources/";

    public Updater(Plugin plugin, int id, File file, UpdateType updateType, boolean logger)
    {
        this.plugin = plugin;
        this.updateFolder = plugin.getServer().getUpdateFolderFile();
        this.id = id;
        this.file = file;
        this.updateType = updateType;
        this.logger = logger;

        downloadLink = DOWNLOAD_LINK + id;

        thread = new Thread(new UpdaterRunnable());
        thread.start();
    }

    public enum UpdateType
    {
        // Checks only the version
        VERSION_CHECK,
        // Downloads without checking the version
        DOWNLOAD,
        // If updater finds new version automatically it downloads it.
        CHECK_DOWNLOAD

    }

    public enum Result
    {

        UPDATE_FOUND,

        NO_UPDATE,

        SUCCESS,

        FAILED,

        BAD_ID
    }

    /**
     * Get the result of the update.
     *
     * @return result of the update.
     * @see Result
     */
    public Result getResult()
    {
        waitThread();
        return result;
    }

    /**
     * Get the latest version from spigot.
     *
     * @return latest version.
     */
    public String getVersion()
    {
        waitThread();
        return version;
    }

    /**
     * Check if id of resource is valid
     *
     * @param link link of the resource
     * @return true if id of resource is valid
     */
    private boolean checkResource(String link)
    {
        try
        {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);

            int code = connection.getResponseCode();

            if(code != 200)
            {
                connection.disconnect();
                result = Result.BAD_ID;
                return false;
            }
            connection.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Checks if there is any update available.
     */
    private void checkUpdate()
    {
        try
        {
            String page = Integer.toString(this.page);

            URL url = new URL(API_RESOURCE+id+VERSIONS+PAGE+page);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            JsonElement element = new JsonParser().parse(reader);
            JSONArray jsonEl = (JSONArray) JSONValue.parseWithException(IOUtils.toString(new URL(String.valueOf(API_RESOURCE+id+VERSIONS+PAGE+page))));

            JsonArray jsonArray =  new JsonParser().parse(jsonEl.toJSONString()).getAsJsonArray();


            if(jsonArray.size() == 10 && !emptyPage)
            {
                connection.disconnect();
                this.page++;
                checkUpdate();
            }
            else if(jsonArray.size() == 0)
            {
                emptyPage = true;
                this.page--;
                checkUpdate();
            }
            else if(jsonArray.size() < 10)
            {
                element = jsonArray.get(jsonArray.size()-1);

                JsonObject object = element.getAsJsonObject();
                element = object.get("name");
                version = element.toString().replaceAll("\"", "").replace("v","");
                element = object.get("id");
                versionID = element.toString();
                DOWNLOAD = "/download?version="+versionID;
                if(logger)
                    plugin.getLogger().info("Checking for update...");
                if(shouldUpdate(version, plugin.getDescription().getVersion()) && updateType == UpdateType.VERSION_CHECK)
                {
                    result = Result.UPDATE_FOUND;
                    if(logger)
                        plugin.getLogger().info("Update found!");
                }
                else if(updateType == UpdateType.DOWNLOAD)
                {
                    if(logger)
                        plugin.getLogger().info("Downloading update... version not checked");
                    download();
                }
                else if(updateType == UpdateType.CHECK_DOWNLOAD)
                {
                    if(shouldUpdate(version, plugin.getDescription().getVersion()))
                    {
                        if(logger)
                            plugin.getLogger().info("Update found, downloading now...");
                        download();
                    }
                    else
                    {
                        if(logger)
                            plugin.getLogger().info("Update not found");
                        result = Result.NO_UPDATE;
                    }
                }
                else
                {
                    if(logger)
                        plugin.getLogger().info("Update not found");
                    result = Result.NO_UPDATE;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Checks if plugin should be updated
     * @param newVersion remote version
     * @param oldVersion current version
     */
    private boolean shouldUpdate(String newVersion, String oldVersion)
    {
        double newver = Double.parseDouble(newVersion);
        double oldver = Double.parseDouble(oldVersion);
        return newver>oldver;
    }

    /**
     * Downloads the file
     */
    private void download()
    {
        BufferedInputStream in = null;
        FileOutputStream fout = null;

        try
        {
            URL url = new URL(downloadLink);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");

            InputStream inp =connection.getInputStream();

            FileOutputStream out = new FileOutputStream(new File(updateFolder,file.getName()+"s"));

            final byte[] data = new byte[1024];
            int count;
            while ((count = inp.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);
            }
            inp.close();
            out.close();
        }
        catch (Exception e)
        {
            System.out.println(e);
            if(logger)
                plugin.getLogger().log(Level.SEVERE, "Updater tried to download the update, but was unsuccessful.");
            result = Result.FAILED;
        }
        finally {

            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, null, e);
                e.printStackTrace();
            }
            try {
                if (fout != null) {
                    fout.close();

                }
            } catch (final IOException e) {
                e.printStackTrace();
                this.plugin.getLogger().log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Updater depends on thread's completion, so it is necessary to wait for thread to finish.
     */
    private void waitThread()
    {
        if(thread != null && thread.isAlive())
        {
            try
            {
                thread.join();
            } catch (InterruptedException e) {
                this.plugin.getLogger().log(Level.SEVERE, null, e);
            }
        }
    }

    public class UpdaterRunnable implements Runnable
    {

        public void run() {
            if(checkResource(downloadLink))
            {
                downloadLink = downloadLink + DOWNLOAD;
                checkUpdate();
            }
        }
    }
}