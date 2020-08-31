package com.cpit.cpmt.biz.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;


@Configuration
public class MongoConfig {

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context, BeanFactory beanFactory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        try {
            mappingConverter.setCustomConversions(beanFactory.getBean(CustomConversions.class));
        } catch (NoSuchBeanDefinitionException ignore) {
        }

        // Don't save _class to mongo
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return mappingConverter;
    }
    
    @Bean(name="firstMongoProperties")
    @Primary
    @ConfigurationProperties(prefix="spring.data.mongodb.first")
    public MongoProperties firstMongoProperties() {
        //System.out.println("-------------------- firstMongoProperties init ---------------------");
        return new MongoProperties();
    }

    @Bean(name="secondMongoProperties")
    @ConfigurationProperties(prefix="spring.data.mongodb.second")
    public MongoProperties secondMongoProperties() {
        //System.out.println("-------------------- secondMongoProperties init ---------------------");
        return new MongoProperties();
    }
    @Bean(name="thirdMongoProperties")
    @ConfigurationProperties(prefix="spring.data.mongodb.third")
    public MongoProperties thirdMongoProperties() {
    	//System.out.println("-------------------- thirdMongoProperties init ---------------------");
    	return new MongoProperties();
    }
}
