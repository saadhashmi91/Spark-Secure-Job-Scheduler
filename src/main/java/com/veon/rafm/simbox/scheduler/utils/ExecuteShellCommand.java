package com.veon.rafm.simbox.scheduler.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ExecuteShellCommand {

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            String s;
           BufferedReader br= new BufferedReader(new InputStreamReader(inputStream));
           try {
               while ((s = br.readLine()) != null)
                   System.out.println("line: " + s);
           } catch (Exception ex)
           {
               ex.printStackTrace();
           }
        }
    }

    public String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;

        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        try {
            p = builder.start();
                    //Runtime.getRuntime().exec(command);

            StreamGobbler streamGobbler =
                    new StreamGobbler(p.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = p.waitFor();
            assert exitCode == 0;
           // p.waitFor();
           // BufferedReader reader =
            //        new BufferedReader(new InputStreamReader(p.getInputStream()));

           // String line = "";
           // while ((line = reader.readLine())!= null) {
           //     output.append(line + "\n");
           // }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }
}
