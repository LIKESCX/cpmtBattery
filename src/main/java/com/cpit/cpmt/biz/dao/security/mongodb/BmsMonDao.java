package com.cpit.cpmt.biz.dao.security.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cpit.cpmt.dto.security.mongodb.BmsMon;

public interface BmsMonDao extends MongoRepository<BmsMon, String>{

}
