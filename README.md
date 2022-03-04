PressPlay makes it easy to play media from Java using the ffmpeg tools.

# Requirements

- ffmpeg must be installed on the machine where you want to play media
- Java 11 or higher is required.

# Maven

If you are using Maven, you can get the pressplay library with this dependency:

```
<dependency>
  <groupId>net.jeremybrooks</groupId>
  <artifactId>pressplay</artifactId>
  <version>1.0.1</version>
</dependency>
```

# Location of ffmpeg

If the ffmpeg tools are not installed at /usr/local/bin, you will need to set a system property so pressplay knows where
to find the tools:

```
pressplay.ffmpeg.path=/full/path/to/tools/
```

---

# Releasing

In order to release to Central using Java 17, you need to export some env variables:

```
export JDK_JAVA_OPTIONS='--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED'
```

Make sure to update the version in pom.xml, then build with the release profile:

```
mvn clean deploy -Prelease
```

After releasing, tag the code and update the version in pom.xml for the next SNAPSHOT release.

