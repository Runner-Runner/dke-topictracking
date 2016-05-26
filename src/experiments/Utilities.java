package experiments;

import java.io.File;

public class Utilities
{
  private Utilities()
  {
  }

  public static File getNextUnusedFile(File file)
  {
    String suffix = "_";
    int nr = 2;
    String path = file.getPath();
    int lastIndexOf = path.lastIndexOf(".");
    String pathWithoutType = path.substring(0, 
            lastIndexOf == -1 ? path.length() : lastIndexOf);
    String fileType = ".txt";

    while (file.exists())
    {
      file = new File(pathWithoutType + suffix + nr + fileType);
      nr++;
    }
    
    return file;
  } 
}
