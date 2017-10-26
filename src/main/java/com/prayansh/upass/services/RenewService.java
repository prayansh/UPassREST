package com.prayansh.upass.services;

import com.prayansh.upass.models.RenewJob;
import com.prayansh.upass.models.RenewJobPayload;
import com.prayansh.upass.models.Status;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Prayansh on 2017-10-21.
 */
public class RenewService {

    private ExecutorService jobPool;
    private Map<String, RenewJob> jobList;

    public RenewService() {
        jobPool = Executors.newFixedThreadPool(2);
        jobList = new HashMap<>();
    }

    public RenewJob createJob(RenewJobPayload payload) {
        RenewJob response = new RenewJob(payload);
        jobPool.submit(response);
        jobList.put(response.getJobId(), response);
        return response;
    }

    public boolean jobExists(RenewJobPayload payload) {
        for (RenewJob job : jobList.values()) {
            if (job.getPayload().equals(payload) && job.getStatus() == Status.RUNNING) {
                return true;
            }
        }
        return false;
    }

    public RenewJob getJob(String id) {
        return jobList.get(id);
    }

    @PreDestroy
    public void shutdown() {
        jobPool.shutdown();
    }

}
