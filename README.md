PressPlay makes it easy to play media from Java using the ffmpeg tools.

# Requirements

- ffmpeg must be installed on the machine where you want to play media
- Java 11 or higher is required
- PressPlay uses log4j2 for logging; your application must provide this dependency

# Maven

If you are using Maven, you can get the pressplay library with this dependency:

```
<dependency>
  <groupId>net.jeremybrooks</groupId>
  <artifactId>pressplay</artifactId>
  <version>1.0.5</version>
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

Releasing to Central requires some specific steps:

1. export some JDK options so Java 17 will work:

```
export JDK_JAVA_OPTIONS='--add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.desktop/java.awt.font=ALL-UNNAMED'
```

2. Update the version in pom.xml
3. Update the version in README.md
4. Do the release: `mvn clean deploy -Prelease`
5. If the release is successful:
    1. Commit and push
    2. tag the repo `git tag -a x.y.z`
    3. push the tag `git push origin --tags`
    4. update the version in pom.xml for the next snapshot
    5. commit and push
6. Clear your JDK options:

```
unset JDK_JAVA_OPTIONS
```


