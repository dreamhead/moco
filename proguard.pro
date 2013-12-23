-libraryjars <java.home>/lib/rt.jar
-libraryjars <java.home>/lib/jce.jar
-printusage shrinking.output

-dontobfuscate
-dontoptimize

-keepattributes *Annotation*,EnclosingMethod

-keep public class com.github.dreamhead.moco.bootstrap.Main {
    public static void main(java.lang.String[]);
}

-keep public class com.github.dreamhead.moco.** {*;}
-keep public class org.apache.http.**{*;}
-keep public class com.google.common.io.Files{*;}
-keep public class org.apache.commons.io.FilenameUtils{*;}
-keep public class com.google.common.io.Resources{*;}
-keep public class org.slf4j.** {*;}
-keep public class ch.** {*;}
-keep public class org.apache.commons.logging.impl.**{*;}
-keep public class com.fasterxml.jackson.databind.**{*;}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-dontwarn io.netty.**
-dontwarn com.jayway.jsonpath.**
-dontwarn ch.qos.logback.**
-dontwarn freemarker.**
-dontwarn org.slf4j.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn com.google.**