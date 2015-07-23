package test.maven.plugin;
import java.io.Serializable;
import java.util.List;

/**
 * Created by sanket on 7/23/2015.
 */
public class Mapping implements Serializable {
  private String file ;
    private List<String> urls;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    @Override
    public String toString() {
        return "Mapping{" +
                "file='" + file + '\'' +
                ", urls=" + urls +
                '}';
    }
}
