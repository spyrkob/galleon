/*
 * Copyright 2016-2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.galleon.config;

import java.util.Map;

import org.jboss.galleon.ProvisioningDescriptionException;
import org.jboss.galleon.universe.UniverseSpec;
import org.jboss.galleon.util.StringUtils;

/**
 * The configuration of the installation to be provisioned.
 *
 * @author Alexey Loubyansky
 */
public class ProvisioningConfig extends FeaturePackDepsConfig {

    public static class Builder extends FeaturePackDepsConfigBuilder<Builder> {

        private Builder() {
        }

        private Builder(ProvisioningConfig provisioningConfig) throws ProvisioningDescriptionException {
            if(provisioningConfig != null) {
                if (provisioningConfig.defaultUniverse != null) {
                    setDefaultUniverse(provisioningConfig.defaultUniverse);
                }
                for (Map.Entry<String, UniverseSpec> universe : provisioningConfig.universeSpecs.entrySet()) {
                    addUniverse(universe.getKey(), universe.getValue());
                }
                for (FeaturePackConfig fp : provisioningConfig.getFeaturePackDeps()) {
                    addFeaturePackDep(provisioningConfig.originOf(fp.getLocation().getChannel()), fp);
                }
                init(provisioningConfig);
            }
        }

        public ProvisioningConfig build() throws ProvisioningDescriptionException {
            return new ProvisioningConfig(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Allows to build a provisioning configuration starting from the passed in
     * initial configuration.
     *
     * @param provisioningConfig  initial state of the configuration to be built
     * @return  this builder instance
     * @throws ProvisioningDescriptionException  in case the config couldn't be built
     */
    public static Builder builder(ProvisioningConfig provisioningConfig) throws ProvisioningDescriptionException {
        return new Builder(provisioningConfig);
    }

    private final Builder builder;

    private ProvisioningConfig(Builder builder) throws ProvisioningDescriptionException {
        super(builder);
        this.builder = builder;
    }

    public Builder getBuilder() {
        return builder;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((fpDeps == null) ? 0 : fpDeps.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProvisioningConfig other = (ProvisioningConfig) obj;
        if (fpDeps == null) {
            if (other.fpDeps != null)
                return false;
        } else if (!fpDeps.equals(other.fpDeps))
            return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder().append('[');
        if(defaultUniverse != null) {
            buf.append("default-universe=").append(defaultUniverse);
        }
        if(!universeSpecs.isEmpty()) {
            if(defaultUniverse != null) {
                buf.append(' ');
            }
            buf.append("universes=[");
            StringUtils.append(buf, universeSpecs.entrySet());
            buf.append("] ");
        }
        StringUtils.append(buf, fpDeps.values());
        append(buf);
        return buf.append(']').toString();
    }
}
