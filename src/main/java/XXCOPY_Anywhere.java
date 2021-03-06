import java.io.*;
import java.util.*;

import java.io.*;
import java.security.MessageDigest;

/*
    Data structure is as follows
    A Map with key=fileSize and value=ArrayList of filenames
*/
public class XXCOPY_Anywhere {
  private static String SRC_BASE_FOLDER_FINAL = "";
  private static String DST_BASE_FOLDER_FINAL = "";
  private static String SRC_BASE_FOLDER       = "";
  private static String DST_BASE_FOLDER       = "";
  private static String OTHER_PARAMS = "";
  private static String command = "";
  private static String SYNC_COMMAND_BATCH_FILE = "xxco.bat";
  private static FileOutputStream batchFileOutputStream = null;
  private static List<String> drivesList = new ArrayList<String>();
  /******************************************************************************************/
  public static void main(String[] args) throws Exception {
    populateDrivesList();
    setFolderNames();
      
	batchFileOutputStream = new FileOutputStream (new File(SYNC_COMMAND_BATCH_FILE));
  
    if(SRC_BASE_FOLDER_FINAL.equals("")) {
        System.out.println("No folders matched in Configuration File");
        command = "rem No folders matched in Configuration File...";
        batchFileOutputStream.write((command + "\r\n").getBytes());
	} else {
		batchFileOutputStream.write("REM - XXCOPY command here ....\r\n".getBytes());
        command = "XXCOPY \"" + SRC_BASE_FOLDER_FINAL + "\" \"" + DST_BASE_FOLDER_FINAL + "\" /clone/ff/yy " + OTHER_PARAMS;
        batchFileOutputStream.write((command + "\r\n").getBytes());
	}
    System.out.println("REM - command = "+command);
    System.out.println("REM   -------- program end");
    
  }
  /******************************************************************************************/
  /******************************************************************************************/
  private static void setFolderNames() throws Exception {
      
    boolean correctPathFound = false;
    String propertyFilePath = locatePropertiesFile();
    System.out.println("propertyFilePath="+propertyFilePath);
    List<String> configFileLineArray = PropertiesLoader.readListFromFile(propertyFilePath);
    String userCurrentDir = System.getProperty("user.dir")+"\\";
    System.out.println("REM - userCurrentDir = "+userCurrentDir);
    for(int j=0;j<configFileLineArray.size();j++) {
        if(correctPathFound) {
            break;
        }
        SRC_BASE_FOLDER               = "";
        DST_BASE_FOLDER               = "";
        //OTHER_PARAMS                  = "";

        String oneLine = configFileLineArray.get(j);
		oneLine = oneLine.trim();
		if(oneLine.equals("")) {
			continue;
		}
		if(oneLine.startsWith("#")) {
			continue; //it is a comment line, skip it
		}

        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println("oneLine = "+oneLine );
        String[] stringArray = oneLine.split("=");
        if(stringArray.length >= 1) {
            SRC_BASE_FOLDER = stringArray[0].trim();
        }
        if(stringArray.length >= 2) {
            DST_BASE_FOLDER = stringArray[1].trim();
        }
        if(SRC_BASE_FOLDER.equals("EXCLUDE")) {
            OTHER_PARAMS = DST_BASE_FOLDER;
        }
        
        System.out.println("From Config File - SRC_BASE_FOLDER          = "+SRC_BASE_FOLDER );
        System.out.println("From Config File - DST_BASE_FOLDER          = "+DST_BASE_FOLDER );
        System.out.println("From Config File - OTHER_PARAMS             = "+OTHER_PARAMS    );

        if(SRC_BASE_FOLDER.contains("?")) {
            SRC_BASE_FOLDER = replaceLogicalWithPhysicalDriveLetter(SRC_BASE_FOLDER);
        } else {
            System.out.println("SRC Physical path   :"+SRC_BASE_FOLDER);
        }
        if(DST_BASE_FOLDER.contains("?")) {
            DST_BASE_FOLDER = replaceLogicalWithPhysicalDriveLetter(DST_BASE_FOLDER);
        } else {
            System.out.println("DST Physical path   :"+DST_BASE_FOLDER);
        }

        if(SRC_BASE_FOLDER.endsWith("\\")) {
            SRC_BASE_FOLDER = SRC_BASE_FOLDER.substring(0,SRC_BASE_FOLDER.length()-1);
        }
        if(DST_BASE_FOLDER.endsWith("\\")) {
            DST_BASE_FOLDER = DST_BASE_FOLDER.substring(0,DST_BASE_FOLDER.length()-1);
        }
        System.out.println("From Config File - SRC_BASE_FOLDER_2        = "+SRC_BASE_FOLDER );
        System.out.println("From Config File - DST_BASE_FOLDER_2        = "+DST_BASE_FOLDER );
    

        int i = userCurrentDir.indexOf(SRC_BASE_FOLDER);
		if(i == -1) {
			SRC_BASE_FOLDER               = "";
			DST_BASE_FOLDER               = "";
			//OTHER_PARAMS                  = "";
			continue;
		}
        if(i > -1) {
            System.out.println("Source folder found. Checking destination folder...");
        
            File temp1 = new File(DST_BASE_FOLDER);
            if (temp1.exists()) {
                System.out.println("Destination folder found. using this config !! ");
            } else {
                System.out.println("Destination folder does not exist. skipping this config !! ");
                continue;
			}

            if(!SRC_BASE_FOLDER.endsWith("\\")) {
                SRC_BASE_FOLDER = SRC_BASE_FOLDER + "\\";
            }
            if(!DST_BASE_FOLDER.endsWith("\\")) {
                DST_BASE_FOLDER = DST_BASE_FOLDER + "\\";
            }
            String subDir = SRC_BASE_FOLDER + userCurrentDir.substring(SRC_BASE_FOLDER.length());
            String dstDir = DST_BASE_FOLDER + userCurrentDir.substring(SRC_BASE_FOLDER.length());
            System.out.println("REM - Final SRC Dir = " + subDir);
            System.out.println("REM - FInal DST Dir = " + dstDir);
            SRC_BASE_FOLDER_FINAL = subDir;
            DST_BASE_FOLDER_FINAL = dstDir;
            correctPathFound = true;
        }
    } //end of for loop
    System.out.println("**************************************************************************");
    System.out.println("REM - Final : SRC_BASE_FOLDER_FINAL = "+SRC_BASE_FOLDER_FINAL  );
    System.out.println("REM - Final : DST_BASE_FOLDER_FINAL = "+DST_BASE_FOLDER_FINAL  );
    System.out.println("**************************************************************************");
  }
  /******************************************************************************************/
  public static String locatePropertiesFile() {
	  List<String> list = new ArrayList<String>();
      list.add("C:\\FOLDER_SYNC_PROPERTIES.TXT");
      list.add("D:\\FOLDER_SYNC_PROPERTIES.TXT");
      list.add("D:\\Programs_Portable_GIT\\Java_Utils\\FOLDER_SYNC_PROPERTIES.TXT");
      list.add("D:\\Program_Files_Portable\\Java_Utils\\FOLDER_SYNC_PROPERTIES.TXT");
	  
	  for(String filePath : list) {
		if((new File(filePath)).exists()) {
			return filePath;
		}
	  }
            
      return "";
  }
  /******************************************************************************************/
    /******************************************************************************************/
    public static String getLogicalDriveLetter(String driveLetter) throws Exception {
        Map hm = PropertiesLoader.load(driveLetter+":/DRIVE_NAME.TXT");
        String logicalDriveLetter = (String)hm.get("LOGIAL_DRIVE_LETTER");
        return logicalDriveLetter.trim();
    }
    /******************************************************************************************/
    public static void populateDrivesList() throws Exception {
        File[] paths;
        paths = File.listRoots();
        for(File path:paths) {
            //System.out.println("Drive Letter: "+path.toString().substring(0,1));
            drivesList.add(path.toString().substring(0,1));
        }
        System.out.println("Drive Letters: "+drivesList);
    }
    /******************************************************************************************/
    public static String replaceLogicalWithPhysicalDriveLetter(String inputFolder) {
        for(String driveLetter : drivesList) {
            String tempInputFolder = inputFolder.replaceAll("\\?",driveLetter);
            File file = new File(tempInputFolder);
            if(file.exists()) {
                System.out.println("Input path      : "+inputFolder);
                System.out.println("Modified path   : "+tempInputFolder);
                return tempInputFolder;
            }
        }
        System.out.println("Unmodified path : "+inputFolder);
        return inputFolder;
    }
    /******************************************************************************************/
}
