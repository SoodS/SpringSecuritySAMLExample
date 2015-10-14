package com.ssood.example.service;

import com.ssood.example.domain.SpringSecurityExampleUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class SpringSecurityExampleUserDetailsService implements SAMLUserDetailsService {

    private Log log = LogFactory.getLog(SpringSecurityExampleUserDetailsService.class);

    @Override
    public Object loadUserBySAML(SAMLCredential credential) {
        SpringSecurityExampleUser user = new SpringSecurityExampleUser(credential.getNameID().toString(), parseAuthorities(credential));
        user.setFirstName(credential.getAttributeAsString("FirstName"));
        user.setLastName(credential.getAttributeAsString("LastName"));
        user.setEmailAddress(credential.getAttributeAsString("EmailAddress"));
        return user;
    }

    private Collection<SimpleGrantedAuthority> parseAuthorities(SAMLCredential credential) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList();
        String[] appRoles = credential.getAttributeAsStringArray("AppRoles");
        if (!isAttributeStringArrayEmpty(appRoles)) {
            for (String authority : appRoles) {
                authorities.add(new SimpleGrantedAuthority(authority.toUpperCase()));
            }
        }
        return authorities;
    }


    private boolean isAttributeStringArrayEmpty(String[] attributeArray) {
        if (attributeArray != null) {
            if (attributeArray[0] == null) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

}