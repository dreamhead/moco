-printusage shrinking.output

-dontobfuscate
-dontoptimize

-keepattributes *Annotation*,EnclosingMethod

-keep public class com.github.dreamhead.moco.bootstrap.Main {
    public static void main(java.lang.String[]);
}

-keep public class com.github.dreamhead.moco.model.*{*;}
-keep public class com.github.dreamhead.moco.parser.model.*{*;}
-keep public class com.github.dreamhead.moco.Moco{*;}
-keep public class com.github.dreamhead.moco.Runner{*;}
-keep public class com.github.dreamhead.moco.Runnable{*;}
-keep public class com.github.dreamhead.moco.MocoRunner{*;}
-keep public class com.github.dreamhead.moco.handler.*{*;}
-keep public class com.github.dreamhead.moco.dumper.*{*;}
-keep public class com.github.dreamhead.moco.MocoJsonRunner{*;}
-keep public class com.github.dreamhead.moco.runner.ShutdownRunner{
    public int shutdownPort();
}
-keepclassmembers enum com.jayway.jsonpath.Option {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class org.apache.http.**{*;}
-keep public class com.google.common.io.Files{*;}
-keep public class org.apache.commons.io.FilenameUtils{*;}
-keep public class com.google.common.io.Resources{*;}
-keep public class org.slf4j.** {*;}
-keep public class ch.** {*;}
-keep public class org.apache.commons.logging.impl.**{*;}
-keep public class com.fasterxml.jackson.databind.**{*;}

#jce.jar
-dontwarn org.apache.http.impl.auth.**
#jsse.jar
-dontwarn org.apache.http.conn.**
-dontwarn org.apache.http.impl.**

-dontwarn io.netty.**
-dontwarn com.jayway.jsonpath.spi.impl.JacksonProvider
-dontwarn ch.qos.logback.core.**
-dontwarn ch.qos.logback.classic.**
-dontwarn freemarker.**
-dontwarn org.slf4j.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn com.google.**
-dontwarn net.sf.cglib.**