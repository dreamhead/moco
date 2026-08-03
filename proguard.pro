-printusage shrinking.output

-dontobfuscate
-dontoptimize

-keepattributes *Annotation*,EnclosingMethod

-keep public class com.github.dreamhead.moco.bootstrap.Main {
    public static void main(java.lang.String[]);
}

-keep public class com.github.dreamhead.moco.parser.deserializer.*{*;}
-keep public class com.github.dreamhead.moco.parser.model.*{*;}
-keep public class com.github.dreamhead.moco.parser.model.websocket.*{*;}
-keep public class com.github.dreamhead.moco.matcher.**{*;}
-keep public class com.github.dreamhead.moco.extractor.**{*;}
-keepclassmembers class com.github.dreamhead.moco.matcher.** {
    *;
}
-keepclassmembers class com.github.dreamhead.moco.extractor.** {
    *;
}
-keep public class com.github.dreamhead.moco.config.**{*;}
-keepclassmembers class com.github.dreamhead.moco.config.** {
    *;
}
-keep public class com.github.dreamhead.moco.handler.failover.**{*;}
-keepclassmembers class com.github.dreamhead.moco.handler.failover.** {
    *;
}
-keep public class com.github.dreamhead.moco.recorder.**{*;}
-keepclassmembers class com.github.dreamhead.moco.recorder.** {
    *;
}
-keep public class com.github.dreamhead.moco.resource.reader.TemplateRequest{*;}
-keep public class com.github.dreamhead.moco.resource.reader.TemplateRequest$TemplateClient{*;}
-keep public class com.github.dreamhead.moco.Moco{*;}
-keep public class com.github.dreamhead.moco.MocoRest{*;}
-keep public class com.github.dreamhead.moco.HttpMethod{*;}
-keep public class com.github.dreamhead.moco.Runner{*;}
-keep public class com.github.dreamhead.moco.Runnable{*;}
-keep public class com.github.dreamhead.moco.HttpRequest{*;}
-keep public class com.github.dreamhead.moco.HttpRequest{*;}
-keep public class com.github.dreamhead.moco.DefaultHttpRequest{*;}
-keepclassmembers class com.github.dreamhead.moco.DefaultHttpRequest {
    *;
}
-keep public class com.github.dreamhead.moco.handler.**{*;}
-keepclassmembers class com.github.dreamhead.moco.handler.** {
    *;
}
-keep public class com.github.dreamhead.moco.sse.SseEvent{
    private *;
}
-keepclassmembers class com.github.dreamhead.moco.sse.SseEvent {
    public *;
    public static ** newEvent(...);
}
-keep public class com.github.dreamhead.moco.sse.SseEvent$*{
    *;
}
-keep public class com.github.dreamhead.moco.model.DefaultHttpResponse{
    private *;
}
-keepclassmembers class com.github.dreamhead.moco.model.DefaultHttpResponse {
    public java.util.List getSseEvents();
    public void setSseEvents(java.util.List);
}
-keep public class com.github.dreamhead.moco.model.DefaultHttpMessage{*;}
-keep public class com.github.dreamhead.moco.model.HttpMessage{*;}
-keep public class com.github.dreamhead.moco.model.HttpResponse{*;}
-keep public class com.github.dreamhead.moco.model.MessageContent{*;}

-keep public class com.github.dreamhead.moco.dumper.*{*;}
-keep public class com.github.dreamhead.moco.MocoJsonRunner{*;}
-keep public class com.github.dreamhead.moco.util.Jsons{*;}
-keep public class com.github.dreamhead.moco.resource.Transformer{*;}
-keep public class com.github.dreamhead.moco.runner.ShutdownRunner{
    public int shutdownPort();
}


-keep public class org.slf4j.LoggerFactory{*;}
-keep public class org.tinylog.**{*;}
-keep public class com.fasterxml.jackson.core.type.TypeReference{*;}

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class io.netty.channel.DefaultChannelPipeline{*;}
-keep public class io.netty.util.concurrent.ConcurrentSkipListIntObjMultimap{*;}
-keep public class io.netty.handler.codec.http.cookie.DefaultCookie{*;}
-keep public class io.netty.handler.codec.http.cookie.CookieHeaderNames{*;}
-keep public class io.netty.handler.codec.http.cookie.CookieHeaderNames$SameSite{*;}
-keep public class com.ctc.wstx.stax.WstxInputFactory{*;}
-keep public class com.ctc.wstx.stax.WstxOutputFactory{*;}


-dontwarn io.netty.**
-dontwarn org.slf4j.**
-dontwarn org.slf4j.event.**
-dontwarn com.jayway.jsonpath.spi.impl.JacksonProvider
-dontwarn com.jayway.jsonpath.spi.json.JsonOrgJsonProvider
-dontwarn com.jayway.jsonpath.spi.json.JettisonProvider*
-dontwarn com.jayway.jsonpath.spi.json.TapestryJsonProvider
-dontwarn com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider
# json-path ships optional Gson backends that Moco does not bundle.
-dontwarn com.google.gson.**
-dontwarn com.jayway.jsonpath.spi.json.GsonJsonProvider
-dontwarn com.jayway.jsonpath.spi.mapper.GsonMappingProvider*
# Nullness annotations are compileOnly, so they are absent from the shrunk jar by design.
-dontwarn org.jspecify.**
-dontwarn ch.qos.logback.**
-dontwarn org.tinylog.**
-dontwarn freemarker.**
-dontwarn org.slf4j.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.logging.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn net.sf.cglib.**
-dontwarn org.osgi.framework.**
-dontwarn com.ctc.wstx.**
-dontwarn jakarta.json.**
-dontwarn org.conscrypt.**
-dontwarn org.brotli.dec.**
-dontwarn edu.umd.cs.findbugs.annotations.**
-dontwarn com.sun.jna.**
-dontwarn tools.jackson.**
-dontwarn com.fasterxml.jackson.core.**
-dontwarn com.fasterxml.jackson.annotation.**
-dontwarn com.fasterxml.jackson.databind.**