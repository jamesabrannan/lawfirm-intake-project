package com.demo.legalfirm.config;

import com.demo.legalfirm.logging.LogSummary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import javax.sql.DataSource;

/**
 * Configures JDBC authentication and stateless API access rules.
 *
 * <p>Demo security: HTTP Basic identifies the caller, permissions authorize memorandum preparation,
 * and CORS illustrates approved browser GUI origins. CORS does not replace authentication or CSRF
 * protection. Spring's default CSRF protection is currently enabled; a client token endpoint is not
 * implemented.
 *
 * <p>Production choices depend on the client architecture:
 *
 * <ul>
 *   <li>CSRF (Cross-Site Request Forgery) protection uses a client-supplied token to reject
 *       unwanted requests made with credentials a browser sends automatically, such as session
 *       cookies or browser-managed Basic authentication.
 *   <li>A JWT (JSON Web Token) can be a signed access token carrying identity and permissions. An
 *       API validates its signature, issuer, audience and expiration. With bearer-only
 *       authentication explicitly supplied in the Authorization header, and no authentication
 *       cookies or Basic authentication, API CSRF protection is generally unnecessary. A JWT stored
 *       in an automatically sent cookie still requires CSRF defenses.
 * </ul>
 *
 * <p>CSRF protection and JWT authentication solve different problems; they are not interchangeable
 * security features. Use HTTPS in production. For browser architecture options, including a backend
 * for frontend that keeps tokens server-side, see the <a
 * href="https://www.rfc-editor.org/rfc/rfc10017.html#section-6">IETF OAuth 2.0 for Browser-Based
 * Applications: Application Architecture Patterns</a>.
 *
 * @file SecurityConfig.java
 * @author James Brannan
 * @date 2026-11-22
 * @version 0.0.1-SNAPSHOT
 * @since 0.0.1-SNAPSHOT
 * @copyright Copyright (c) 2026 James Brannan.
 * @license MIT License. See LICENSE in the project root for full terms. SPDX-License-Identifier:
 *     MIT
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        LOGGER.debug("userDetailsService(dataSource={}) entered", LogSummary.describe(dataSource));
        try {
            JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
            // Prefix the existing plain-text demo passwords for the delegating encoder.
            users.setUsersByUsernameQuery(
                    "SELECT email, '{noop}' || password, 1 FROM users WHERE email = ?");
            users.setAuthoritiesByUsernameQuery("SELECT email, role FROM users WHERE email = ?");

            var loggedResult = users;
            LOGGER.debug("userDetailsService returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "userDetailsService exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        LOGGER.debug("passwordEncoder() entered");
        try {

            var loggedResult = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            LOGGER.debug("passwordEncoder returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "passwordEncoder exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        LOGGER.debug("corsConfigurationSource() entered");
        try {
            // CORS (Cross-Origin Resource Sharing) permits browser calls from approved
            // GUI origins when the GUI and API have different schemes, hosts, or ports.
            // Demo: allow a GUI served from HTTP/HTTPS localhost on any port. Production
            // should list the exact approved GUI origins, such as https://gui.example.com.
            // This is an origin policy, not a caller-IP restriction or authentication.
            LOGGER.info("Configuring demo CORS for localhost GUI origins");
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOriginPatterns(
                    List.of("http://localhost:[*]", "https://localhost:[*]"));
            configuration.setAllowedMethods(List.of("POST"));
            configuration.setAllowedHeaders(
                    List.of("Authorization", "Content-Type", "X-CSRF-TOKEN", "X-XSRF-TOKEN"));
            configuration.setAllowCredentials(true);
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/api/memorandums/**", configuration);

            var loggedResult = source;
            LOGGER.debug("corsConfigurationSource returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "corsConfigurationSource exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        LOGGER.debug("securityFilterChain(http={}) entered", LogSummary.describe(http));
        try {

            LOGGER.info("Configuring HTTP Basic, CSRF, CORS and MEMORANDUM_PREPARER authorization");
            var loggedResult =
                    http.cors(Customizer.withDefaults())
                            .sessionManagement(
                                    session ->
                                            session.sessionCreationPolicy(
                                                    SessionCreationPolicy.STATELESS))
                            .authorizeHttpRequests(
                                    authorize ->
                                            authorize
                                                    .requestMatchers("/api/memorandums/**")
                                                    .hasAuthority("MEMORANDUM_PREPARER")
                                                    .anyRequest()
                                                    .authenticated())
                            .httpBasic(Customizer.withDefaults())
                            .build();
            LOGGER.debug("securityFilterChain returning {}", LogSummary.describe(loggedResult));
            return loggedResult;

        } catch (Exception loggingFailure) {
            LOGGER.trace(
                    "securityFilterChain exited exceptionally; type={}",
                    loggingFailure.getClass().getSimpleName());
            throw loggingFailure;
        }
    }
}
