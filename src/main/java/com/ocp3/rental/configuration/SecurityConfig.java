// Définition du package
package com.ocp3.rental.configuration;

// Importation des dépendances nécessaires
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.ocp3.rental.service.CustomUserDetailsService;
import com.ocp3.rental.service.JwtAuthenticationTokenFilter;

// Annotation pour indiquer que cette classe est une classe de configuration
@Configuration
// Active la sécurité web avec Spring Security
@EnableWebSecurity
public class SecurityConfig {

    // Injection de dépendances pour CustomUserDetailsService
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // Récupère la clé JWT du fichier de configuration
    @Value("${jwt.key}")
    private String jwtKey;

    // Crée un bean pour l'encodeur JWT
    @Bean
    public JwtEncoder jwtEncoderApi() {
        // Utilise NimbusJwtEncoder avec la clé secrète pour encoder les tokens JWT
        return new NimbusJwtEncoder(new ImmutableSecret<>(this.jwtKey.getBytes()));
    }

    // Crée un bean pour le décodeur JWT
    @Bean
    public JwtDecoder jwtDecoderApi() {
        // Crée une clé secrète spécifique pour le décodeur JWT
        SecretKeySpec secretKey = new SecretKeySpec(this.jwtKey.getBytes(), 0, this.jwtKey.getBytes().length,"RSA");
        // Utilise NimbusJwtDecoder avec la clé secrète et l'algorithme HS256 pour décoder les tokens JWT
        return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    // Crée un bean pour l'encodeur de mots de passe
    @Bean
    public PasswordEncoder PasswordEncoderApi() {
        // Utilise BCryptPasswordEncoder pour encoder les mots de passe
        return new BCryptPasswordEncoder();
    }

    // Crée un bean pour le gestionnaire d'authentification
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder PasswordEncoderApi) throws Exception {
        // Obtient le constructeur du gestionnaire d'authentification
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        // Configure le gestionnaire d'authentification pour utiliser CustomUserDetailsService et l'encodeur de mots de passe
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(PasswordEncoderApi);
        // Construit et retourne le gestionnaire d'authentification
        return authenticationManagerBuilder.build();
    } 

    // Crée un bean pour la chaîne de filtres de sécurité
    @Bean
    public SecurityFilterChain ApiSecurityFilterChain(HttpSecurity http, JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter) throws Exception {
        // Configure la sécurité HTTP
        return http
            // Désactive la protection CSRF
            .csrf(csrf -> csrf.disable())
            // Configure la gestion de session pour être sans état
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Autorise certaines requêtes en fonction de leur chemin
            .authorizeHttpRequests(authorize -> authorize
                // Autorise toutes les requêtes vers "/api/auth/login" et "/api/auth/register"
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                // Nécessite une authentification pour toutes les requêtes vers "/api/**"
                .requestMatchers("/api/**").authenticated()
                // Autorise toutes les requêtes vers les ressources statiques communes
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // Autorise toutes les autres requêtes
                .anyRequest().permitAll()
            )
            // Ajoute JwtAuthenticationTokenFilter avant UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationTokenFilter, BasicAuthenticationFilter.class)
            // Construit et retourne la chaîne de filtres de sécurité
            .build();
    }
}