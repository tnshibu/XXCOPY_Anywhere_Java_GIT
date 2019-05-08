import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Properties;
import java.util.Hashtable;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

public class PropertiesLoader {
    // =========================================================================================
    public static Properties load(String fileName) {
        Properties props = new Properties();
        try {
            String propertyFileContents = readContentFromFile(fileName);
            props.load(new StringReader(propertyFileContents.replace("\\", "\\\\")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return props;
    }
    // =========================================================================================
    public static Map<String,List<String>> loadToHashMap(String fileName) {
        Properties props = new Properties();
        Map<String,List<String>> hm = new HashMap<String,List<String>>();
        try {
            ArrayList<String> lineArray = readListFromFile(fileName);
            for(int i=0;i<lineArray.size();i++) {
                String line = lineArray.get(i);
                if(line.trim().equals("")) {
                    continue;
                }
                if(line.trim().startsWith("#")) {
                    continue;
                }
                String[] arr = line.split("=");
                String key = "";
                String val = "";
                if(arr.length == 2) {
                    key = arr[0];
                    val = arr[1];
                }
                if(key.trim().equals("")) {
                    continue;
                }
                if(val.trim().equals("")) {
                    continue;
                }
                List valueList = (List)hm.get(key);
                if(valueList == null) {
                    valueList = new ArrayList<String>();
                    hm.put(key, valueList);
                }
                if(val != null && !val.trim().equals("")) {
                    valueList.add(val);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return hm;
    }

    // =========================================================================================
    // =========================================================================================
    public static Hashtable<String,List<String>> loadMultipleValues(String fileName) {
        Hashtable<String,List<String>> props = new Hashtable<String,List<String>>();
        try {
            String propertyFileContents = readContentFromFile(fileName);
            String[] lines = propertyFileContents.split("\n");
            for(int i=0;i<lines.length; i++) {
                String oneLine = lines[i];
                oneLine = oneLine.trim();
                if(oneLine.startsWith("#")) {
                    continue; // its a comment, skip it
                }
                String[] keyValue = oneLine.split("=");
                if(keyValue.length > 1) {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    if(props.containsKey(key)) {
                        List list = props.get(key);
                        list.add(value);
                    } else {
                        List list = new ArrayList();
                        list.add(value);
                        props.put(key, list);
                    }
                }
            }
            System.out.println(props);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return props;
    }

    // =========================================================================================

    // =========================================================================================
    private static String readContentFromFile(String fileName) throws Exception {
        String returnString = "";
        BufferedReader input = new BufferedReader(new FileReader(new File(fileName)));
        try {
            String line = null; // not declared within while loop
            while ((line = input.readLine()) != null) {
                returnString = returnString + line + "\r\n";
            }
        } finally {
            input.close();
        }
        return returnString;
    }
    // =========================================================================================
    public static ArrayList<String> readListFromFile(String fileName) throws Exception {
        ArrayList<String> returnArray = new ArrayList<String>();
        BufferedReader input = new BufferedReader(new FileReader(new File(fileName)));
        try {
            String line = null; // not declared within while loop
            while ((line = input.readLine()) != null) {
                line = line.trim();
                returnArray.add(line);
            }
        } finally {
            input.close();
        }
        return returnArray;
    }
    // =========================================================================================

    // =========================================================================================
    public static void main(String args[]) {
        Properties props =PropertiesLoader.load("input.properties");
    }
}
