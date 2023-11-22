package com.learnjava.forkjoin;

import com.learnjava.util.DataSet;
import com.learnjava.util.LoggerUtil;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static com.learnjava.util.CommonUtil.delay;
import static com.learnjava.util.CommonUtil.stopWatch;

public class ForkJoinPoolExample  {

    ForkJoinPool forkJoinPool = new ForkJoinPool();
    
    /**
     * This method takes list of string as an input and return List<String> as response with some processing inbetween.
     * In output list, each string is of following format: "{sizeOfInputString}-inputString". This method illustrates 
     * how it can be done using the ForkJoinPool.
     * 
     * ForkJoinTask is created and following the Divide and Conquer approach, the input is divided into smaller size (forks)
     * Later once the transformation is complete, using the join() method the response list is constructed again.
     * @param inputList
     * @return
     */

    public List<String> compute(List<String> inputList){

        return  forkJoinPool.invoke(new RecursiveTask<>() {
            @Override
            protected List<String> compute() {
                List<ForkJoinTask<String>> forkJoinTasks = new ArrayList<>();
                List<String> responseList = new ArrayList<>();

                inputList.forEach((item) -> {
                    forkJoinTasks.add(new RecursiveTask<String>() {
                        @Override
                        protected String compute() {
                            return transform(item);
                        }
                    }.fork()); // This adds the task to the forkjoin deque
                });
                //Joining RecursiveTask
                forkJoinTasks.forEach(task -> {
                    responseList.add(task.join()); //This retrieves the result
                });

                return responseList;
            }
        });
    }

    private String transform(String s) {
        delay(500);
        return s.length()+" - " + s;
    }

    public static void main(String[] args) {

        ForkJoinPoolExample forkJoinPoolExample = new ForkJoinPoolExample();
        stopWatch.start();

        List<String> resultList = forkJoinPoolExample.compute(DataSet.namesList());
        LoggerUtil.log("resultList : "+ resultList);

        stopWatch.stop();
        LoggerUtil.log("Total time taken : "+ stopWatch.getTime());
    }
}
