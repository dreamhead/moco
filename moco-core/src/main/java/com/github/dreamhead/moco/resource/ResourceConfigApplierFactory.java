package com.github.dreamhead.moco.resource;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.resource.reader.Variable;
import com.google.common.collect.ImmutableMap;

import static com.github.dreamhead.moco.resource.ResourceFactory.cookieResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.fileResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.jsonResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.templateResource;
import static com.github.dreamhead.moco.resource.ResourceFactory.uriResource;

public final class ResourceConfigApplierFactory {
    public static final ResourceConfigApplier DO_NOTHING_APPLIER = new ResourceConfigApplier() {
        @Override
        public Resource apply(final MocoConfig config, final Resource resource) {
            return resource;
        }
    };

    public static ResourceConfigApplier fileConfigApplier(final String id, final Resource file) {
        return new SelfResourceConfigApplier(id) {
            @Override
            @SuppressWarnings("unchecked")
            protected Resource newResource(final MocoConfig config) {
                return fileResource(file, null, config);
            }
        };
    }

    public static ResourceConfigApplier cookieConfigApplier(final String key, final Resource cookieResource) {
        return new EmbeddedResourceConfigApplier(cookieResource) {
            @Override
            protected Resource newResource(final MocoConfig config) {
                return cookieResource(key, cookieResource.apply(config));
            }
        };
    }

    public static ResourceConfigApplier templateConfigApplier(
            final ContentResource template,
            final ImmutableMap<String, ? extends Variable> variables) {
        return new EmbeddedResourceConfigApplier(template) {
            @Override
            protected Resource newResource(final MocoConfig config) {
                return templateResource((ContentResource) template.apply(config), variables);
            }
        };
    }

    public static ResourceConfigApplier uriConfigApplier(final String id, final String uri) {
        return new SelfResourceConfigApplier(id) {
            @Override
            @SuppressWarnings("unchecked")
            protected Resource newResource(final MocoConfig config) {
                return uriResource((String) config.apply(uri));
            }
        };
    }

    public static ResourceConfigApplier jsonConfigApplier(final Resource resource) {
        return new EmbeddedResourceConfigApplier(resource) {
            @Override
            protected Resource newResource(final MocoConfig config) {
                return jsonResource(resource.apply(config));
            }
        };
    }

    private abstract static class BaseResourceConfigApplier implements ResourceConfigApplier {
        protected abstract Resource newResource(MocoConfig config);

        protected abstract String id();

        @Override
        public Resource apply(final MocoConfig config, final Resource resource) {
            if (config.isFor(id())) {
                return newResource(config);
            }

            return resource;
        }
    }

    private abstract static class SelfResourceConfigApplier extends BaseResourceConfigApplier {
        private String id;

        private SelfResourceConfigApplier(final String id) {
            this.id = id;
        }

        @Override
        protected String id() {
            return id;
        }
    }

    private abstract static class EmbeddedResourceConfigApplier extends BaseResourceConfigApplier {
        private Resource resource;

        private EmbeddedResourceConfigApplier(final Resource resource) {
            this.resource = resource;
        }

        @Override
        protected String id() {
            return resource.id();
        }
    }

    private ResourceConfigApplierFactory() {
    }
}
