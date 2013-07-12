package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;

import java.io.File;

public class ResourceConfigApplierFactory {
    public static ResourceConfigApplier fileConfigApplier(final String id, final File file) {
        return new ResourceConfigApplier() {
            @Override
            public Resource apply(MocoConfig config, Resource resource) {
                if (config.isFor(id)) {
                    return ResourceFactory.fileResource(new File(config.apply(file.getName())));
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
                    return ResourceFactory.headerResource(key, resource.apply(config));
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
                    return ResourceFactory.templateResource((ContentResource) template.apply(config));
                }

                return null;
            }
        };
    }

    private ResourceConfigApplierFactory() {}
}
