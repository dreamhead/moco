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
-keep public class com.github.dreamhead.moco.resource.reader.TemplateRequest{*;}
-keep public class com.github.dreamhead.moco.resource.reader.TemplateRequest$TemplateClient{*;}
-keep public class com.github.dreamhead.moco.Moco{*;}
-keep public class com.github.dreamhead.moco.MocoRest{*;}
-keep public class com.github.dreamhead.moco.HttpMethod{*;}
-keep public class com.github.dreamhead.moco.Runner{*;}
-keep public class com.github.dreamhead.moco.Runnable{*;}
-keep public class com.github.dreamhead.moco.MocoRunner{*;}
-keep public class com.github.dreamhead.moco.handler.*{*;}
-keep public class com.github.dreamhead.moco.dumper.*{*;}
-keep public class com.github.dreamhead.moco.MocoJsonRunner{*;}
-keep public class com.github.dreamhead.moco.util.Jsons{*;}
-keep public class com.github.dreamhead.moco.runner.ShutdownRunner{
    public int shutdownPort();
}

-keep public class org.apache.http.conn.ssl.SSLConnectionSocketFactory{*;}
-keep public class org.apache.http.impl.client.HttpClientBuilder{*;}
-keep public class org.apache.http.client.fluent.Executor{*;}
-keep public class org.apache.http.impl.conn.PoolingHttpClientConnectionManager{*;}
-keep public class org.apache.http.client.protocol.HttpClientContext{*;}
-keep public class org.apache.http.client.HttpClient{*;}
-keep public class org.apache.http.impl.client.InternalHttpClient{*;}
-keep public class org.apache.http.entity.ContentType{*;}
-keep public class org.apache.http.entity.ByteArrayEntity{*;}
-keep public class org.apache.http.util.EntityUtils{*;}
-keep public class com.google.common.io.Files{*;}
-keep public class com.google.common.collect.ImmutableMultimap{*;}
-keep public class org.slf4j.LoggerFactory{*;}
-keep public class ch.qos.logback.**{*;}
-keep public class org.apache.commons.logging.impl.SimpleLog{*;}
-keep public class org.apache.commons.logging.impl.LogFactoryImpl{*;}
-keep public class com.fasterxml.jackson.core.type.TypeReference{*;}

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class io.netty.channel.DefaultChannelPipeline{*;}
-keep public class io.netty.handler.codec.http.cookie.DefaultCookie{*;}
-keep public class com.ctc.wstx.stax.WstxInputFactory{*;}
-keep public class com.ctc.wstx.stax.WstxOutputFactory{*;}

#jce.jar
-dontwarn org.apache.http.impl.auth.**
#jsse.jar
-dontwarn org.apache.http.conn.**
-dontwarn org.apache.http.impl.**

-dontwarn io.netty.**
-dontwarn com.jayway.jsonpath.spi.impl.JacksonProvider
-dontwarn com.jayway.jsonpath.spi.json.JsonOrgJsonProvider
-dontwarn com.jayway.jsonpath.spi.json.JettisonProvider**
-dontwarn com.jayway.jsonpath.spi.json.TapestryJsonProvider
-dontwarn com.jayway.jsonpath.spi.mapper.JsonOrgMappingProvider
-dontwarn ch.qos.logback.core.**
-dontwarn ch.qos.logback.classic.**
-dontwarn freemarker.**
-dontwarn org.slf4j.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn com.google.**
-dontwarn net.sf.cglib.**
-dontwarn org.osgi.framework.**
-dontwarn com.ctc.wstx.**
-dontwarn jakarta.json.**
-dontwarn org.conscrypt.**