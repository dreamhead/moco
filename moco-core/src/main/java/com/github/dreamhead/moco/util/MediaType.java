package com.github.dreamhead.moco.util;

import io.netty.handler.codec.http.HttpUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.github.dreamhead.moco.util.Preconditions.checkArgument;

/**
 * A media type, replacing Guava's {@code com.google.common.net.MediaType}.
 *
 * <p>{@link #toString()} is a wire value - it becomes the {@code Content-Type} response header -
 * so the rendering must stay byte-identical to Guava's: parameters separated by {@code "; "} and
 * the {@code charset} value lower-cased.
 *
 * <p>Parameters other than {@code charset} are preserved, because a recorded multipart request
 * round-trips its {@code boundary} through {@link #parse(String)} and back onto the wire.
 *
 * <p>One deliberate divergence from Guava: trailing whitespace such as
 * {@code "text/plain; charset=utf-8  "} is accepted here, where Guava raised
 * {@code IllegalArgumentException}. Netty already trims header values, so this is practically
 * unreachable, and leniency only ever avoids a spurious fallback - it cannot introduce a throw
 * where Guava succeeded.
 */
public final class MediaType {
    private static final String CHARSET = "charset";

    /** RFC 2045 separators, which along with space and non-printables cannot appear in a token. */
    private static final String SEPARATORS = "()<>@,;:\\\"/[]?=";

    public static final MediaType PLAIN_TEXT_UTF_8 = create("text", "plain")
            .withCharset(StandardCharsets.UTF_8);
    public static final MediaType PNG = create("image", "png");
    public static final MediaType JPEG = create("image", "jpeg");
    public static final MediaType GIF = create("image", "gif");
    public static final MediaType TIFF = create("image", "tiff");
    public static final MediaType PDF = create("application", "pdf");
    public static final MediaType ZIP = create("application", "zip");
    public static final MediaType TAR = create("application", "x-tar");
    public static final MediaType GZIP = create("application", "x-gzip");
    public static final MediaType APPLICATION_BINARY = create("application", "binary");
    public static final MediaType FORM_DATA = create("application", "x-www-form-urlencoded");

    private final String type;
    private final String subtype;
    private final Map<String, String> parameters;

    private MediaType(final String type, final String subtype, final Map<String, String> parameters) {
        this.type = type;
        this.subtype = subtype;
        this.parameters = parameters;
    }

    public static MediaType create(final String type, final String subtype) {
        return new MediaType(normalizeToken(type), normalizeToken(subtype), Collections.emptyMap());
    }

    /**
     * Parses a {@code Content-Type} value, throwing {@link IllegalArgumentException} if it is not
     * a valid media type. Callers rely on that throw as their fallback signal, so the validation
     * here is deliberate: Netty's {@code HttpUtil.getMimeType} alone is lenient and would accept
     * a value with no {@code type/subtype} at all.
     */
    public static MediaType parse(final String value) {
        Objects.requireNonNull(value, "Content type should not be null");

        CharSequence mimeType = HttpUtil.getMimeType(value);
        if (mimeType == null) {
            throw new IllegalArgumentException("Invalid media type: " + value);
        }

        MediaType type = fromMimeType(mimeType.toString().trim());
        int separator = value.indexOf(';');
        if (separator < 0) {
            return type;
        }

        return new MediaType(type.type, type.subtype, parseParameters(value.substring(separator + 1)));
    }

    private static MediaType fromMimeType(final String mimeType) {
        int slash = mimeType.indexOf('/');
        checkArgument(slash > 0 && slash < mimeType.length() - 1 && mimeType.indexOf('/', slash + 1) < 0,
                "Invalid media type: " + mimeType);
        return create(mimeType.substring(0, slash), mimeType.substring(slash + 1));
    }

    private static Map<String, String> parseParameters(final String text) {
        Map<String, String> result = new LinkedHashMap<>();
        for (String parameter : splitParameters(text)) {
            String trimmed = parameter.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            int equals = trimmed.indexOf('=');
            checkArgument(equals > 0, "Invalid media type parameter: " + parameter);

            String name = normalizeToken(trimmed.substring(0, equals).trim());
            String value = unquote(trimmed.substring(equals + 1).trim());
            result.put(name, CHARSET.equals(name) ? value.toLowerCase(Locale.ROOT) : value);
        }

        return Collections.unmodifiableMap(result);
    }

    // Splits on the semicolons that separate parameters, ignoring any inside a quoted value.
    private static List<String> splitParameters(final String text) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (quoted && c == '\\' && i < text.length() - 1) {
                current.append(c).append(text.charAt(i + 1));
                i = i + 1;
                continue;
            }

            if (c == '"') {
                quoted = !quoted;
            }

            if (c == ';' && !quoted) {
                result.add(current.toString());
                current.setLength(0);
                continue;
            }

            current.append(c);
        }

        result.add(current.toString());
        return result;
    }

    public String type() {
        return this.type;
    }

    public String subtype() {
        return this.subtype;
    }

    public Optional<Charset> charset() {
        return Optional.ofNullable(this.parameters.get(CHARSET)).map(Charset::forName);
    }

    public MediaType withCharset(final Charset charset) {
        Objects.requireNonNull(charset, "Charset should not be null");

        Map<String, String> merged = new LinkedHashMap<>(this.parameters);
        merged.put(CHARSET, charset.name().toLowerCase(Locale.ROOT));
        return new MediaType(this.type, this.subtype, Collections.unmodifiableMap(merged));
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof MediaType that)) {
            return false;
        }

        return this.type.equals(that.type)
                && this.subtype.equals(that.subtype)
                && this.parameters.equals(that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.subtype, this.parameters);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(this.type).append('/').append(this.subtype);
        for (Map.Entry<String, String> parameter : this.parameters.entrySet()) {
            result.append("; ").append(parameter.getKey()).append('=').append(quoteIfNeeded(parameter.getValue()));
        }

        return result.toString();
    }

    private static String normalizeToken(final String token) {
        checkArgument(token != null && isToken(token), "Invalid media type token: " + token);
        return token.toLowerCase(Locale.ROOT);
    }

    private static boolean isToken(final String value) {
        if (value.isEmpty()) {
            return false;
        }

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c <= ' ' || c >= 0x7f || SEPARATORS.indexOf(c) >= 0) {
                return false;
            }
        }

        return true;
    }

    private static String quoteIfNeeded(final String value) {
        if (isToken(value)) {
            return value;
        }

        StringBuilder result = new StringBuilder(value.length() + 2).append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '"' || c == '\\') {
                result.append('\\');
            }

            result.append(c);
        }

        return result.append('"').toString();
    }

    private static String unquote(final String value) {
        if (value.length() < 2 || value.charAt(0) != '"' || value.charAt(value.length() - 1) != '"') {
            return value;
        }

        StringBuilder result = new StringBuilder(value.length() - 2);
        for (int i = 1; i < value.length() - 1; i++) {
            char c = value.charAt(i);
            if (c == '\\' && i < value.length() - 2) {
                i = i + 1;
                c = value.charAt(i);
            }

            result.append(c);
        }

        return result.toString();
    }
}
