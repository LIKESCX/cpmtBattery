package com.cpit.cpmt.biz.dao.security.mongodb;



import org.springframework.data.mongodb.repository.MongoRepository;

import com.cpit.cpmt.biz.dto.BmsHot;






public interface BmsInfoMDao extends MongoRepository<BmsHot, String>{

   /* List<OriginalAuth> findAll();

    OriginalAuth getUser(Integer id);

    void update(OriginalAuth user);

    void insert(OriginalAuth user);

    void insertAll(List<OriginalAuth> users);

    void remove(Integer id);
    */
}
