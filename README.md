Spring Security Example
=======================

### Technologies
* Spring Security - http://projects.spring.io/spring-security/
* Spring Security SAML - http://projects.spring.io/spring-security-saml/

### Prereqs
* Java 1.7
* Maven 3

##### Run the application locally
To start the application execute the following command and then open your browser to http://localhost:8080
> `./start`

To stop the application execute the following command:
> `./stop`

### Overview
This application is based upon the sample project that is included with Spring Security SAML (https://github.com/spring-projects/spring-security-saml).
The Spring security context file has been updated to remove some of the unnecessary stuff included in the sample application. We also added support for stubbing
out a SAML2 IDP so that automated/integration tests can run without OAM.

The application has two pages - a landing page and an admin page. The landing page requires authentication and shows some basic information about the user.
The admin page (http://localhost:8080/admin) demonstrates how you can use a simple login form to secure pages with a different authentication provider.

### security-context.xml
Here are some items to note from the Spring security context file:

````
<security:http entry-point-ref="samlEntryPoint" use-expressions="true">
  <security:intercept-url pattern="/logout" access="permitAll"/>
  <security:intercept-url pattern="/resources/**" access="permitAll"/>
  <security:intercept-url pattern="/error" access="permitAll"/>
  <security:intercept-url pattern="/samlstubs/**" access="permitAll"/>

  <security:intercept-url pattern="/**" access="isAuthenticated()"/>
  <security:custom-filter before="FIRST" ref="metadataGeneratorFilter"/>
  <security:custom-filter after="BASIC_AUTH_FILTER" ref="samlFilter"/>
</security:http>
````

This section details which URLs require authentication. URLs for assets, error pages, etc are set to be excluded.

````
<bean id="successRedirectHandler" class="org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler">
  <property name="defaultTargetUrl" value="/"/>
</bean>
````

The defaultTargetUrl property specifies where the user will be sent after a successful login.

````
<bean id="failureRedirectHandler" class="org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler">
  <property name="useForward" value="true"/>
  <property name="defaultFailureUrl" value="/error"/>
</bean>
````

The defaultFailureUrl property specifies where the user will be sent after a failed login.

````
<bean id="successLogoutHandler" class="org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler">
  <property name="defaultTargetUrl" value="/"/>
  <property name="alwaysUseDefaultTargetUrl" value="true"/>
</bean>
````

The defaultTargetUrl property specifies where the user will be sent after logging out. For the sample app, it points to the landing page which will result in the user
being sent back to the IDP's login page.

````
<security:authentication-manager alias="authenticationManager">
  <security:authentication-provider ref="samlAuthenticationProvider"/>
  <security:authentication-provider>
    <security:user-service id="adminInterfaceService">
      <security:user name="admin" password="password" authorities="ROLE_ADMIN"/>
    </security:user-service>
  </security:authentication-provider>
</security:authentication-manager>
````

The application has two authentication providers defined - one for SAML and another for the admin page. A user is defined (username: admin, password: password) for the ROLE_ADMIN.

````
<bean id="keyManager" class="org.springframework.security.saml.key.JKSKeyManager">
  <constructor-arg value="classpath:security/exampleKeystore.jks"/>
  <constructor-arg type="java.lang.String" value="nalle123"/>
  <constructor-arg>
    <map>
      <entry key="apollo" value="nalle123"/>
    </map>
  </constructor-arg>
  <constructor-arg type="java.lang.String" value="apollo"/>
</bean>
````

The key manager holds all the x509 certificates for OAM (required to fetch the IDP metadata over https) as well as the private key/cert that will be included in the
SAML SP metadata for your application. The first constructor-arg is the name of the keystore, the second is the password to the keystore. The third constructor-arg is a map containing the passwords used
to access the private keys and the last argument is the default key. The default key is the one that will be used when generating SP metadata.

````
<bean id="metadataGeneratorFilter" class="org.springframework.security.saml.metadata.MetadataGeneratorFilter">
  <constructor-arg>
    <bean class="org.springframework.security.saml.metadata.MetadataGenerator">
      <property name="entityId" value="${saml.sp.entity.id}"/>
      <property name="wantAssertionSigned" value="${saml.sp.metadata.sign.assertion}"/>
      <property name="requestSigned" value="${saml.sp.metadata.sign.requests}"/>
      <property name="entityBaseURL" value="${saml.sp.entity.base.url}"/>
    </bean>
  </constructor-arg>
</bean>
````

This filter is used to generate SAML SP metadata for your application. You can hit http://localhost:8080/saml/metadata to obtain the generated XML.

````
<bean id="samlAuthenticationProvider" class="org.springframework.security.saml.SAMLAuthenticationProvider">
  <property name="userDetails" ref="springSecurityExampleUserDetailsService" />
</bean>
````

Here you can specify a custom implementation of SAMLUserDetailsService if you need to perform custom processing for the attributes of the SAML assertion. This project includes an example,
SpringSecurityExampleUserDetailsService, as well as a custom extension of Spring security's User class.

````
<bean id="contextProvider" class="org.springframework.security.saml.context.SAMLContextProviderLB">
  <property name="scheme" value="${scheme}"/>
  <property name="serverName" value="${example.server.name}"/>
  <property name="serverPort" value="${example.server.port}"/>
  <property name="includeServerPortInRequestURL" value="${include.port.in.request}"/>
  <property name="contextPath" value="/"/>
</bean>
````

This bean is used to specify information about the front end URL when running behind a load balancer. Note that load balancers should be configured to use sticky sessions.

````
<bean id="metadata" class="org.springframework.security.saml.metadata.CachingMetadataManager">
  <constructor-arg>
    <list>
      <bean class="org.springframework.security.saml.metadata.ExtendedMetadataDelegate">
        <constructor-arg ref="metadataProvider"/>
        <constructor-arg>
          <bean class="org.springframework.security.saml.metadata.ExtendedMetadata">
          </bean>
        </constructor-arg>
        <property name="metadataTrustCheck" value="false"/>
      </bean>
    </list>
  </constructor-arg>
</bean>
````

This is where you configure how to get metadata for SAML IDP(s). There are two different definitions for "metadataProvider" - one that is active for the "development" profile
and another that is active for the "production" and "uat" profiles. The production/uat instance fetches metadata via https and the development instance loads some stubbed
metadata from a local file.

### SAML IDP Stubs
When the application is started with the default environment (development), a local file named "idp_metadata_stub_development.xml" is loaded for the IDP's metadata.
All of the service URLs in this file point back to routes located under /samlstubs on localhost. These routes are serviced by a controller named SAMLStubsController that is only
initialized when running under the "development" profile. Instead of a standard login page this controller displays a form where you can specify the values for SAML attributes directly.
If you use the SAML Tracer plug-in for Firefox (https://addons.mozilla.org/en-US/firefox/addon/saml-tracer/) you can see the full SAML exchange taking place.

### Integrating with OAM
In order to get your application added as an SP with OAM you'll need to generate SP metadata so that you can send a request to IAM support.

The application includes a keystore located under src/main/resources/security. This store already includes the SSL certs required when fetching IDP metadata from OAM in pre-prod and prod.
 Before generating your metadata, you'll want to generate a new keypair for your application:

> `keytool -genkeypair -alias <app> -keypass <password> -validity 3650 -keystore exampleKeystore.jks`

The password for the included keystore is "nalle123". You can change this password and also delete the "apollo" key with the following commands:

> `keytool -delete -alias apollo -keystore exampleKeystore.jks`
> `keytool -storepasswd -new <password> -keystore exampleKeystore.jks`

After you've updated your keystore, you'll need to update the "keyManager" bean in security-context.xml to include your new keypair, passwords, etc.

While running the application locally, you can hit http://localhost:8080/saml/metadata in order to generate the SP metadata xml. Be sure to check the URLs and entity ids in the xml
make sure that they are correct before you send a request off to IAM support. If you need to generate data for a specific environment, you can override the "entityBaseURL" and "entityId"
properties of the "metadataGeneratorFilter" bean in security-context.xml