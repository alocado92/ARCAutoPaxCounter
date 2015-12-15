package com.example.cristina.arc_autopaxcounter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Cristina on 11/7/2015.
 */
public class ExternalStorage {

    public static final String SD_CARD = "sdCard";
    public static final String EXTERNAL_SD_CARD = "externalSdCard";

    /**
     * @return True if the external storage is available. False otherwise.
     */
    public static boolean isAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getSdCardPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/";
    }

    /**
     * @return True if the external storage is writable. False otherwise.
     */
    public static boolean isWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;

    }

    /**
     * @return A map of all storage locations available
     * For 4.3+ the vold.fstab is not readable (/system/etc/vold.fstab is not accessible in android 4.3+)
     * This fails on devices that doesn't have 2 SD cards. It assumes 1st found is internal, and 2nd found is external.
     */
    public static Map<String, File> getAllStorageLocations() {
        Map<String, File> map = new HashMap<String, File>(10);

        List<String> mMounts = new ArrayList<String>(10);
        List<String> mVold = new ArrayList<String>(10);
        mMounts.add("/mnt/sdcard");
        mVold.add("/mnt/sdcard");

        try {
            File mountFile = new File("/proc/mounts");
            if(mountFile.exists()){
                Scanner scanner = new Scanner(mountFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("/dev/block/vold/")) {
                        String[] lineElements = line.split(" ");
                        String element = lineElements[1];

                        // don't add the default mount path
                        // it's already in the list.
                        if (!element.equals("/mnt/sdcard"))
                            mMounts.add(element);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File voldFile = new File("/system/etc/vold.fstab");
            if(voldFile.exists()){
                Scanner scanner = new Scanner(voldFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("dev_mount")) {
                        String[] lineElements = line.split(" ");
                        String element = lineElements[2];

                        if (element.contains(":"))
                            element = element.substring(0, element.indexOf(":"));
                        if (!element.equals("/mnt/sdcard"))
                            mVold.add(element);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        for (int i = 0; i < mMounts.size(); i++) {
            String mount = mMounts.get(i);
            if (!mVold.contains(mount))
                mMounts.remove(i--);
        }
        mVold.clear();

        List<String> mountHash = new ArrayList<String>(10);

        for(String mount : mMounts){
            File root = new File(mount);
            if (root.exists() && root.isDirectory() && root.canWrite()) {
                File[] list = root.listFiles();
                String hash = "[";
                if(list!=null){
                    for(File f : list){
                        hash += f.getName().hashCode()+":"+f.length()+", ";
                    }
                }
                hash += "]";
                if(!mountHash.contains(hash)){
                    String key = SD_CARD + "_" + map.size();
                    if (map.size() == 0) {
                        key = SD_CARD;
                    } else if (map.size() == 1) {
                        key = EXTERNAL_SD_CARD;
                    }
                    mountHash.add(hash);
                    map.put(key, root);
                }
            }
        }

        mMounts.clear();

        if(map.isEmpty()){
            map.put(SD_CARD, Environment.getExternalStorageDirectory());
        }
        return map;
    }

    /** Given any file/folder inside an sd card, this will return the path of the sd card */
    private static String getRootOfInnerSdCardFolder(File file)
    {
        if(file==null)
            return null;
        final long totalSpace=file.getTotalSpace();
        while(true)
        {
            final File parentFile=file.getParentFile();
            if(parentFile==null||parentFile.getTotalSpace()!=totalSpace)
                return file.getAbsolutePath();
            file=parentFile;
        }
    }

    /**
     * returns a list of all available sd cards paths, or null if not found.
     *
     * @param includePrimaryExternalStorage set to true if you wish to also include the path of the primary external storage
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static List<String> getSdCardPaths(final Context context,final boolean includePrimaryExternalStorage)
    {
        final File[] externalCacheDirs= ContextCompat.getExternalCacheDirs(context);
        if(externalCacheDirs==null||externalCacheDirs.length==0)
            return null;
        if(externalCacheDirs.length==1)
        {
            if(externalCacheDirs[0]==null)
                return null;
            final String storageState= EnvironmentCompat.getStorageState(externalCacheDirs[0]);
            if(!Environment.MEDIA_MOUNTED.equals(storageState))
                return null;
            if(!includePrimaryExternalStorage&& Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB&&Environment.isExternalStorageEmulated())
                return null;
        }
        final List<String> result=new ArrayList<>();
        if(includePrimaryExternalStorage||externalCacheDirs.length==1)
            result.add(getRootOfInnerSdCardFolder(externalCacheDirs[0]));
        for(int i=1;i<externalCacheDirs.length;++i)
        {
            final File file=externalCacheDirs[i];
            if(file==null)
                continue;
            final String storageState=EnvironmentCompat.getStorageState(file);
            if(Environment.MEDIA_MOUNTED.equals(storageState))
                result.add(getRootOfInnerSdCardFolder(externalCacheDirs[i]));
        }
        if(result.isEmpty())
            return null;
        return result;
    }
}
