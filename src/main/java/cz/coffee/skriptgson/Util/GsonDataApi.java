package cz.coffee.skriptgson.Util;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings({"unused","uncheked"})
public class GsonDataApi extends GsonUtils
{
    final private File file;
    private FileWriter fw;

    private boolean exist;

    public GsonDataApi(String path) {
        super();
        this.file = new File(path);
    }
    Type DataType = new TypeToken<List<Data>>() {}.getType();
    public File createFile() {

        this.fw = null;
        List<Data> dts = null;

        if (this.file.exists()){
            if (this.file.length() != 0) {
                System.out.println("This file '" + this.file + "' already exist");
                return null;
            }
        }
        try {
            this.fw = new FileWriter(this.file);
            JsonElement je = GsonUtils.parsedString("{}");
            dts = new ArrayList<>();
            dts.add(new Data(je, new Date()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            prettyGson().toJson(dts, this.fw);
            assert this.fw != null;
            try {
                this.fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this.file;
    }

    public File gsonAppend(String insertedData) {
        this.fw = null;
        FileReader fr;
        List<Data> dts = null;
        try {
            fr = new FileReader(this.file);
            dts = newGson().fromJson(fr, DataType);
            try {
                fr.close();
                if ( dts == null ) {
                    dts = new ArrayList<>();
                }
                this.fw = new FileWriter(this.file);
                JsonElement je = parsedString(insertedData);
                dts.add(new Data(je, new Date()));
                try {
                    prettyGson().toJson(dts, this.fw);
                    this.fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            System.out.println(prettyGson().toJson(dts));
        }
        return this.file;
    }

    @Override
    public String toString() {
        return "";
    }
}
