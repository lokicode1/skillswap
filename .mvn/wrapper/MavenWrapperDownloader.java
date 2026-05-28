import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;

public class MavenWrapperDownloader {

  private static final String WRAPPER_VERSION = "3.3.2";
  private static final String DEFAULT_DOWNLOAD_URL =
      "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/" + WRAPPER_VERSION + "/maven-wrapper-" + WRAPPER_VERSION + ".jar";

  private static final String MAVEN_WRAPPER_PROPERTIES_PATH = ".mvn/wrapper/maven-wrapper.properties";
  private static final String MAVEN_WRAPPER_JAR_PATH = ".mvn/wrapper/maven-wrapper.jar";

  public static void main(String[] args) {
    System.out.println("- Downloading Maven Wrapper JAR...");
    File baseDirectory = new File(args.length > 0 ? args[0] : "");
    File mavenWrapperPropertyFile = new File(baseDirectory, MAVEN_WRAPPER_PROPERTIES_PATH);
    String downloadUrl = DEFAULT_DOWNLOAD_URL;

    if (mavenWrapperPropertyFile.exists()) {
      try (InputStream in = new java.io.FileInputStream(mavenWrapperPropertyFile)) {
        Properties props = new Properties();
        props.load(in);
        downloadUrl = props.getProperty("wrapperUrl", downloadUrl);
      } catch (IOException ignored) {
      }
    }

    File wrapperJar = new File(baseDirectory, MAVEN_WRAPPER_JAR_PATH);
    wrapperJar.getParentFile().mkdirs();

    try {
      URL website = new URL(downloadUrl);
      try (ReadableByteChannel rbc = Channels.newChannel(website.openStream());
           FileOutputStream fos = new FileOutputStream(wrapperJar)) {
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
      }
      System.out.println("- Maven Wrapper JAR downloaded to " + wrapperJar.getAbsolutePath());
    } catch (IOException e) {
      System.err.println("- ERROR downloading Maven Wrapper JAR: " + e.getMessage());
      System.exit(1);
    }
  }
}

