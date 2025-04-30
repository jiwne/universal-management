package com.jin.java.universaloauth.config;

import com.jin.java.universalcommon.exception.BusinessException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * authorization
 * @author：jin
 * @date：2025/4/30
 */
@Slf4j
@Configuration
public class ManageAuthorizationConfig {

    /**
     * 协议端点的 Spring Security 过滤器链。
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception Exception
     */
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity (http);
        http.getConfigurer (OAuth2AuthorizationServerConfigurer.class)
                //配置 OpenID Connect 1.0 支持（默认处于禁用状态）
                .oidc (Customizer.withDefaults ());
        http
                // 未从授权端点进行身份验证时重定向到登录页面
                .exceptionHandling ((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor (
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                //接受用户信息和/或客户端注册的访问令牌
                .oauth2ResourceServer ((resourceServer) -> resourceServer
                        .jwt (Customizer.withDefaults ()));

        return http.build();
    }

    /**
     *  	用于身份验证的 Spring Security 过滤器链。
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests( authorizeRequests -> authorizeRequests
                .requestMatchers(new AntPathRequestMatcher("/actuator/**"),
                        new AntPathRequestMatcher("/oauth2/**"),
                        new AntPathRequestMatcher("/login")).permitAll()
                .anyRequest().authenticated()
        )
                .formLogin(Customizer.withDefaults ());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 一个 UserDetailsService 实例，用于检索用户进行身份验证。
     * @return  UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService () {
        UserDetails userDetails = User.withUsername ("user")
                .password (passwordEncoder().encode ("user"))
                .roles ("USER")
                .build ();
        return new InMemoryUserDetailsManager(userDetails);
    }

    /**
     * 	用于管理客户端的 RegisteredClientRepository 实例。
     *
     * @return RegisteredClientRepository
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository (JdbcTemplate jdbcTemplate,PasswordEncoder passwordEncoder) {
        RegisteredClient registeredClient = RegisteredClient.withId (UUID.randomUUID ().toString ())
                .clientId ("oidc-client")
                .clientSecret (passwordEncoder.encode ("123456"))
                // 客户端认证基于请求头
                .clientAuthenticationMethod (ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                // 配置授权的支持方式
                .authorizationGrantType (AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType (AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType (AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri ("https://www.baidu.com")
                .scope ("user")
                .scope ("admin")
                // 客户端设置，设置用户需要确认授权
                .clientSettings (ClientSettings.builder().requireAuthorizationConsent (true).build ())
                .build ();
        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository (jdbcTemplate);
        RegisteredClient repositoryByClientId = registeredClientRepository.findByClientId (registeredClient.getClientId ());
        if (repositoryByClientId == null) {
            registeredClientRepository.save (registeredClient);
        }
        return registeredClientRepository;

    }


    /**
     * 用于对访问令牌进行签名的实例
     *
     * @return JWKSource
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource () {
        KeyPair keyPair = generateRsaKey ();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic ();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate ();
        RSAKey rsaKey = new RSAKey.Builder (publicKey)
                .privateKey (privateKey)
                .keyID (UUID.randomUUID ().toString ())
                .build ();
        JWKSet jwkSet = new JWKSet (rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * 创建RsaKey
     *
     * @return KeyPair
     */
    private static KeyPair generateRsaKey () {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance ("RSA");
            keyPairGenerator.initialize (2048);
            keyPair = keyPairGenerator.generateKeyPair ();
        } catch (Exception e) {
            log.error ("generateRsaKey Exception", e);
            throw new BusinessException("generateRsaKey Exception");
        }
        return keyPair;
    }

    /**
     * 用于解码已签名访问令牌的 JwtDecoder 实例
     *
     * @param jwkSource jwkSource
     * @return JwtDecoder
     */
    @Bean
    public JwtDecoder jwtDecoder (JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder (jwkSource);
    }

    /**
     * 用于配置 Spring Authorization Server 的 AuthorizationServerSettings 实例。
     * @return AuthorizationServerSettings
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings () {
        return AuthorizationServerSettings.builder ().build ();
    }


}
