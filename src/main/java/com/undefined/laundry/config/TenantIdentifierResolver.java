package com.undefined.laundry.config;

import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

@Component
class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<Integer>, HibernatePropertiesCustomizer {

    @Override
    public Integer resolveCurrentTenantIdentifier() {
        try {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                            .getRequest();
            return Integer.parseInt(request.getHeader("floor"));
        } catch (Exception e){
            return -1;
        }
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }

    // empty overrides skipped for brevity
}
