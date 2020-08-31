package com.cpit.cpmt.biz.config.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
@EnableMongoRepositories(mongoTemplateRef = "sndMongoTemplate")
public class SecondMongoTemplate {
	@Autowired
    @Qualifier("secondMongoProperties")
    private MongoProperties mongoProperties;
	
	@Bean(name = "sMappingConverter")
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory secondFactory, MongoMappingContext context, BeanFactory beanFactory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(secondFactory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        try {
            mappingConverter.setCustomConversions(beanFactory.getBean(CustomConversions.class));
        } catch (NoSuchBeanDefinitionException ignore) {
        }

        // Don't save _class to mongo
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return mappingConverter;
    }

    @Bean(name = "sndMongoTemplate")
    public MongoTemplate secondMongoTemplate(MappingMongoConverter sMappingConverter) throws Exception {
    	MongoDbFactory secondFactory = secondFactory(this.mongoProperties);
    	return new MongoTemplate(secondFactory,sMappingConverter);
    }
    
	@Bean
    public MongoDbFactory secondFactory(MongoProperties mongoProperties) throws Exception {
        ServerAddress serverAdress = new ServerAddress(mongoProperties.getHost(),mongoProperties.getPort());
        List<MongoCredential> mongoCredentials=new ArrayList<>();
        if (mongoProperties.getUsername() == null || mongoProperties.getAuthenticationDatabase() == null || mongoProperties.getPassword() == null){
            return new SimpleMongoDbFactory(new MongoClient(serverAdress,mongoCredentials), mongoProperties.getDatabase());
        }else {
            mongoCredentials.add(MongoCredential.createCredential(mongoProperties.getUsername(),mongoProperties.getAuthenticationDatabase(),mongoProperties.getPassword()));

            return new SimpleMongoDbFactory(new MongoClient(serverAdress,mongoCredentials), mongoProperties.getDatabase());
        }

    }

}
