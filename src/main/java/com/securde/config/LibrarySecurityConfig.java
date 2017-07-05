package com.securde.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
public class LibrarySecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private LibraryAuthenticationSuccessHandler libraryAuthenticationSuccessHandler;

    @Value("${spring.queries.users-query}")
    private String usersQuery;

    @Value("${spring.queries.roles-query}")
    private String rolesQuery;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.
                jdbcAuthentication()
                .usersByUsernameQuery(usersQuery)
                .authoritiesByUsernameQuery(rolesQuery)
                .dataSource(dataSource)
                .passwordEncoder(bCryptPasswordEncoder);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /*http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/home", "/about").permitAll()
                .antMatchers("/admin*//**").hasAnyRole("ADMINISTRATOR")
                .antMatchers("/user*//**").hasAnyRole("USER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler);*/

        http
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/test").permitAll()
//                .antMatchers("/text").permitAll()
                .antMatchers("/search/**").permitAll()
//                .antMatchers("/text/**").hasAuthority("STUDENT")
//                .antMatchers("/text/**").hasAuthority("FACULTY")
                .antMatchers("/user/**").hasAuthority("STUDENT")
                .antMatchers("/user/**").hasAuthority("FACULTY")
                .antMatchers("/manager/**").hasAuthority("MANAGER")
                .antMatchers("/staff/**").hasAuthority("STAFF")
                .antMatchers("/admin/**").hasAuthority("ADMINISTRATOR")
                .anyRequest()
                .authenticated()
                .and()
                /*.csrf()
                .disable()*/
                .formLogin()
                .loginPage("/login").failureUrl("/login?error=true")
//                .successHandler(libraryAuthenticationSuccessHandler)
                .defaultSuccessUrl("/home")
                .usernameParameter("username")
                .passwordParameter("password")
                .and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/").and().exceptionHandling()
                .accessDeniedPage("/home");
    }
}
