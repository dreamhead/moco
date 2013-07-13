package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;

import java.io.File;

import static com.github.dreamhead.moco.resource.ResourceFactory.*;

public class ResourceConfigApplierFactory {
    public static ResourceConfigApplier fileConfigApplier(final String id, final File file) {
        return new ResourceConfigApplier() {
            @Override
            public Resource apply(MocoConfig config, Resource resource) {
                if (config.isFor(id)) {
                    return fileResource(new File(config.apply(file.getName())));
                }

                return resource;
            }
        };
    }

    public static ResourceConfigApplier headerConfigApplier(final String key) {
        return new ResourceConfigApplier() {
            @Override
            public Resource apply(MocoConfig config, Resource resource) {
                if (config.isFor(resource.id())) {
                    return headerResource(key, resource.apply(config));
                }

                return resource;
            }
        };
    }

    public static ResourceConfigApplier templateConfigApplier(final ContentResource template) {
        return new ResourceConfigApplier() {
            @Override
            public Resource apply(MocoConfig config, Resource resource) {
                if (config.isFor(template.id())) {
                    return templateResource((ContentResource) template.apply(config));
                }

                return null;
            }
        };
    }

    public static ResourceConfigApplier uriConfigApplier(final String uri) {
        return new ResourceConfigApplier() {
            @Override
            public Resource apply(MocoConfig config, Resource resource) {
                if (config.isFor("uri")) {
                    return uriResource(config.apply(uri));
                }

                return resource;
            }
        };
    }

    private ResourceConfigApplierFactory() {}
}
