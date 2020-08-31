package com.cpit.cpmt.biz.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.cpit.cpmt.biz.main.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.NONE)
public class TestCache {
	
	@Autowired
	LocalCacheUtil localCacheUtil;
	
	@Test
	public void work() throws Exception{
		System.out.println(localCacheUtil.get("hello"));
		System.out.println(localCacheUtil.get("hello"));
		System.out.println(localCacheUtil.get("hello"));
		localCacheUtil.set("hello","ok");
		System.out.println(localCacheUtil.get("hello"));
		TimeUnit.MINUTES.sleep(2);
		System.out.println(localCacheUtil.get("hello"));
		//System.out.println(localCacheUtil.get("hello"));

	}
	
	public static void main(String[] args) {
		Date date = new Date(1579065363L*1000);
		System.out.println("---->"+date);
	}

}
