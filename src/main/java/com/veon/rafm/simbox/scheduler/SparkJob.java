package com.veon.rafm.simbox.scheduler;

import com.veon.rafm.simbox.scheduler.batch.BatchSession;
import com.veon.rafm.simbox.scheduler.batch.CreateBatchRequest;
import com.veon.rafm.simbox.scheduler.utils.ExecuteShellCommand;
import org.quartz.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SparkJob implements Job{

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {

            SchedulerContext schedulerContext = jobExecutionContext.getScheduler().getContext();
            //Below line gets the value from context.
            String password = (String) schedulerContext.get("password");
            String mainClass=(String)schedulerContext.get("mainClass");
            String jarPath=(String) schedulerContext.get("jarPath");
            String encryptedPath=(String) schedulerContext.get("encryptedPath");

            final String[] commandArgs = new String[]{
                    //
                    "--jar",
                    jarPath,
                    //
                    "--files",
                    "/usr/hdp/current/spark2-client/conf/hive-site.xml",
                    //
                    "--executor-memory",
                    "2g",
                    //
                    "--executor-cores",
                    "2",
                    //
                    "--num-executors",
                    "20",
                    //
                    "--driver-memory",
                    "1g",
                    //
                    "--driver-cores",
                    "2",
                    //
                    "--class",
                    "com.veon.rafm.simbox.SparkJobRunner",

                    // argument 1 to my Spark program
                    "--arg",
                    password,

                    // argument 2 to my Spark program
                    "--arg",
                    mainClass,

                    // argument 3 to my Spark program
                    "--arg",
                    encryptedPath
            };
            CreateBatchRequest req= new CreateBatchRequest();
           ArrayList<String> args=new ArrayList<>();
           args.add(password);
           args.add(mainClass);
           args.add(encryptedPath);
           ArrayList jars=new ArrayList();
           jars.add(jarPath);

           ArrayList files=new ArrayList();
           files.add("/usr/hdp/current/spark2-client/conf/hive-site.xml");

            req.setRequestParams(
                    args,
                    "com.veon.rafm.simbox.SparkJobRunner",
                    jars,
                    files,
                    "1g",
                    2,
                    "2g",
                    2,
                    20);

            new BatchSession(req);

        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }
}