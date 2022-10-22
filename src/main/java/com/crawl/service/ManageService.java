package com.crawl.service;

import com.crawl.pojo.ItemStoringJob;
import com.crawl.pojo.RestaurantCode;
import com.crawl.util.HttpUtil;
import com.crawl.util.JsonUtil;
import com.crawl.util.LogUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.crawl.util.RestaurantConstant.*;
import static com.crawl.util.ThreadUtils.THREAD_POOL;
public class ManageService {

    private static final AtomicInteger jobNum = new AtomicInteger(0);
    private static final AtomicInteger successJobNum = new AtomicInteger(0);
    private static final AtomicInteger failJobNum = new AtomicInteger(0);

    private static final int CONSUMER_CONCURRENCY = 50;
    private static String URL_NAME = MY_USER_NAME;

    public static void main(String[] args) {

        if(args.length>0){
            URL_NAME = args[0];
        }


        String allRestaurants = HttpUtil.get(BASE_URL + RESTAURANT);
        List<RestaurantCode> restaurantCodes = JsonUtil.toObject(allRestaurants, new TypeReference<List<RestaurantCode>>() {});

        LinkedBlockingQueue<ItemStoringJob> jobBlockQueue = new LinkedBlockingQueue<>();
        ConcurrentHashMap<ItemStoringJob, Boolean> jobStatus = new ConcurrentHashMap<>();

        if (restaurantCodes == null||restaurantCodes.isEmpty()) {
            LogUtil.printOnConsole("Fetching restaurant code fails.");
            return;
        }

        CountDownLatch countDownLatch = new CountDownLatch(restaurantCodes.size());

        startProducer(jobBlockQueue,restaurantCodes,countDownLatch,jobStatus);
        startConsumer(jobStatus,jobBlockQueue);

        checkAndTerminate(countDownLatch,jobStatus);

    }

    private static void startProducer(LinkedBlockingQueue<ItemStoringJob> jobBlockQueue,List<RestaurantCode> restaurantCodes,CountDownLatch countDownLatch,ConcurrentHashMap<ItemStoringJob, Boolean> jobStatus){
        //producer
        for (RestaurantCode restaurantItem : restaurantCodes) {
            THREAD_POOL.execute(new Runnable() {
                @Override
                public void run() {
                    LogUtil.printOnConsole("Getting Menu: " + restaurantItem.getCode());
                    String oneMenuResponse = HttpUtil.get(BASE_URL + RESTAURANT + SLASH + restaurantItem.getCode());
                    JsonNode jsonNode = JsonUtil.toJsonNode(oneMenuResponse);
                    if (jsonNode != null && jsonNode.has("data")) {
                        List<ItemStoringJob> jobs = transformJsonDataToJob(jsonNode);
                        jobs.forEach(oneJob->jobStatus.put(oneJob,false));
                        jobBlockQueue.addAll(jobs);
                        jobNum.addAndGet(jobs.size());

                        countDownLatch.countDown();
                    } else {
                        LogUtil.printOnConsole("Validation Error:" + oneMenuResponse);
                    }
                }
            });
        }
    }
    private static void startConsumer(ConcurrentHashMap<ItemStoringJob, Boolean> jobStatus,LinkedBlockingQueue<ItemStoringJob> jobBlockQueue){
        int i = CONSUMER_CONCURRENCY;
        while (i-- != 0) {
            THREAD_POOL.execute(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        ItemStoringJob oneJob = null;
                        try {
                            oneJob = jobBlockQueue.take();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        LogUtil.printOnConsole(String.format("Adding item %s (%s) from %s to the storage.",oneJob.getItem_id(),oneJob.getItem_name(),oneJob.getRestaurantCode()));
                        String jobString = JsonUtil.toJsonString(oneJob);
                        String response = HttpUtil.post(BASE_URL + URL_NAME + ITEMS, jobString);

                        if(checkResponse(response)){
                            jobStatus.put(oneJob,true);
                            successJobNum.incrementAndGet();
                        }else{
                            failJobNum.incrementAndGet();
                        }
                        jobNum.decrementAndGet();

                    }
                }
            });
        }
    }

    private static void checkAndTerminate(CountDownLatch countDownLatch,ConcurrentHashMap<ItemStoringJob,Boolean> jobStatus){
        while (true){
            try {
                //to ensure producer have produced all jobs.
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //to ensure consumer have consumed all jobs.
            if(jobNum.get()==0){
                printFinalOutPut(jobStatus);
                System.exit(0);
            }
        }
    }




    private static void printFinalOutPut(ConcurrentHashMap<ItemStoringJob, Boolean> jobStatusMap){
        LogUtil.printOnConsole("------------------------");
        LogUtil.printOnConsole("Terminating task.");
        LogUtil.printOnConsole(String.format("Save %d items, %d failure:",successJobNum.get(),failJobNum.get()));
        LogUtil.printOnConsole("---");
        LogUtil.printOnConsole("Restaurant, Category, Item, Success");
        for (Map.Entry<ItemStoringJob, Boolean> jobEntry : jobStatusMap.entrySet()) {
            ItemStoringJob oneJob = jobEntry.getKey();
            LogUtil.printOnConsole(String.format("%s, %s, %s, %s",oneJob.getRestaurant(),oneJob.getCategory(),oneJob.getItem_name(),jobEntry.getValue()));
        }
    }

    private static boolean checkResponse(String response){
        try {
            JsonNode jsonNode = JsonUtil.toJsonNode(response);
            if(!jsonNode.get("created").asBoolean()){
                LogUtil.printOnConsole("Item creation fails. response is :"+response);
                return false;
            }
            return true;
        }catch (Exception e){
            LogUtil.printOnConsole("Check item storing response fails.");
            return false;
        }
    }

    private static List<ItemStoringJob> transformJsonDataToJob(JsonNode jsonNode) {

        LinkedList<ItemStoringJob> itemStoringJobs = new LinkedList<>();
        try {
            JsonNode dataNode = jsonNode.get("data");
            String restaurantName = dataNode.get("name").asText();
            String restaurantCode = dataNode.get("code").asText();
            LogUtil.printOnConsole(String.format("Loaded menu: %s - %s", restaurantCode ,restaurantName));
            double rating = dataNode.get("rating").asDouble();
            JsonNode menus = dataNode.get("menus");

            for (JsonNode category : menus) {
                JsonNode menuCategories = category.get("menu_categories");

                for (JsonNode menuCategory : menuCategories) {
                    String categoryName = menuCategory.get("name").asText();
                    JsonNode products = menuCategory.get("products");
                    for (JsonNode productItem : products) {
                        JsonNode productVariations = productItem.get("product_variations");
                        String itemName = productItem.get("name").asText();
                        for (JsonNode productVariation : productVariations) {
                            String itemId = productVariation.get("id").asText();
                            double itemPrice = productVariation.get("price").asDouble();

                            itemStoringJobs.add(new ItemStoringJob(rating, restaurantName, categoryName, itemId, itemName, itemPrice,restaurantCode));
                        }
                    }
                }
            }
        }catch (Exception e){
            LogUtil.printOnConsole("Parsing jsonNode fails. Parameter is :"+JsonUtil.toJsonString(jsonNode));
            e.printStackTrace();
        }
        return itemStoringJobs;
    }
}
