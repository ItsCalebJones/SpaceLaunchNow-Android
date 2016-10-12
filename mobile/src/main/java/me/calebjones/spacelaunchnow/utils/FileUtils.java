package me.calebjones.spacelaunchnow.utils;

import android.content.Context;

import net.vrallev.android.cat.Cat;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.calebjones.spacelaunchnow.BuildConfig;

/**
 * @author rwondratschek
 */
@SuppressWarnings("unused")
public final class FileUtils {

    private FileUtils() {
        // no op
    }

    public static byte[] readFile(File file) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];

            if (buffer.length != fis.read(buffer)) {
                return null;
            } else {
                return buffer;
            }

        } finally {
            close(fis);
        }
    }

    public static void writeFile(File file, String text, boolean append) throws IOException {
        if (file == null || text == null) {
            throw new IllegalArgumentException();
        }

        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            throw new IOException("Could not create parent directory");
        }

        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Could not create file");
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(file, append);

            writer.write(text);

        } finally {
            close(writer);
        }
    }

    public static void delete(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                delete(file1);
            }
        }
        if (!file.delete()) {
            throw new IOException("could not delete file " + file);
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                Cat.e(e);
            }
        }
    }

    public static void saveSuccess(boolean success, String msg, Context context) {
        if(BuildConfig.DEBUG) {
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
            String text = DATE_FORMAT.format(new Date()) + "\t\t" + success + " " + msg + '\n';
            try {
                FileUtils.writeFile(getSuccessFile(context), text, true);
            } catch (IOException e) {
                Cat.e(e);
            }
        }
    }

    public static File getSuccessFile(Context context) {
        return new File(context.getCacheDir(), "success.txt");
    }
}
