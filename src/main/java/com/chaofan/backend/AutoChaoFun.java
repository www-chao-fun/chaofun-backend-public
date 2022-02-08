package com.chaofan.backend;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 炒饭直播漫游工具
 */
public class AutoChaoFun {

    static ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(20);

    static String CHAO_FUN_PREFIX = "https://chao.fun/";

    static Long currentPostId = 1083378L;
    static Long newPostId = currentPostId;
    static Long newPostCurrentTimestamp = 0L;


    static WebDriver webDriver;

    public static void initDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        webDriver = new ChromeDriver(options);    //Chrome浏览器
    }

    public static void walk() {
        getLastPost();
        getLastCommentPost();
        if (!newPostId.equals(currentPostId)) {
            currentPostId = newPostId;
            webDriver.get(CHAO_FUN_PREFIX + "p/" + newPostId);
        }
    }

    public static void getLastPost() {
        List<JSONObject> posts = getPostList("https://chao.fun/api/v0/list_combine?onlyNew=false&pageSize=5&marker=&order=new&range=1year&forumId=all");
        for (JSONObject post : posts) {
            if (post.getLong("gmtCreate").compareTo(newPostCurrentTimestamp) > 0) {
                newPostCurrentTimestamp = post.getLong("gmtCreate");
                newPostId = post.getLong("postId");
            }
        }
    }

    public static void getLastCommentPost() {
        List<JSONObject> posts = getPostList("https://chao.fun/api/v0/list_combine?onlyNew=false&pageSize=30&order=comment&range=1year&forumId=all");
        for (JSONObject post : posts) {
            if (post.getLong("gmtComment").compareTo(newPostCurrentTimestamp) > 0) {
                newPostCurrentTimestamp = post.getLong("gmtComment");
                newPostId = post.getLong("postId");
            }
        }
    }

    public static List<JSONObject> getPostList(String url) {
        JSONObject result = HttpUtil.doGetJson(url);

        List<JSONObject> posts = new ArrayList<>();
        JSONArray jsonArray = result.getJSONObject("data").getJSONArray("posts");

        for (Object o : jsonArray) {
           posts.add((JSONObject) o);
        }
        
        return posts;
    }


    public static void main(String[] args) throws InterruptedException {
        // 传入 chrome driver path
        System.setProperty("webdriver.chrome.driver", args[0]);

        initDriver();


        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                walk();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }
}
