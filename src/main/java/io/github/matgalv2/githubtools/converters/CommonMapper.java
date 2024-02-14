package io.github.matgalv2.githubtools.converters;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonMapper {

    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }
}
