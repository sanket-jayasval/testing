package test.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sanket on 7/12/2015.
 */
@Mojo(name = "urlMapper")
public class UrlMapper extends AbstractMojo {


    @Parameter(property = "path")
    private String path;

    private List<Mapping> mappingList = new ArrayList<Mapping>();

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    Pattern pattern  =  Pattern.compile("@.*(\".*\")");
    public void traverse(String path){
        Path p = Paths.get(path);
        FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {

                ArrayList<String> reqMapList = new ArrayList<String>();
                String fileName = file.toString() ;

                if(fileName.contains("test.maven.plugin.UrlMapper")) {
                    return FileVisitResult.CONTINUE;
                }

                for (Path fil : file.getFileName()) {
                    Scanner scanner = new Scanner(file.toFile());
                    while (scanner.hasNextLine()) {
                       String s = scanner.nextLine();
                        Matcher matcher = pattern.matcher(s);
                        if (matcher.find()) {
                            reqMapList.add(matcher.group(1));
                        }
                    }
                    if(!reqMapList.isEmpty()) {
                        Mapping mapping = new Mapping();
                        getLog().info("Class \t" + fileName.substring(fileName.indexOf("src")));
                        mapping.setFile(fileName.substring(fileName.indexOf("src")));
                        mapping.setUrls((List<String>) reqMapList.clone());
                        reqMapList.clear();
                        mappingList.add(mapping);
                    }
                }

                return FileVisitResult.CONTINUE;
            }

        };

        try {
            Files.walkFileTree(p, fv);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("**** Hello ****\n\n");
        traverse(path);
        generateJson();
        getLog().info("**** end ****\n\n");
    }

    private void generateJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(project.getBuild().getOutputDirectory() + "/" + "requestMapper.json"), mappingList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
