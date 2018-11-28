package edu.nyu.cs9053.homework10;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.Map;

/**
 * User: blangel
 * Date: 11/16/14
 * Time: 3:50 PM
 */
public class ThreadedFilesWordCounter extends AbstractConcurrencyFactorProvider implements FilesWordCounter {
    private final List<Thread> threads;
    private static AtomicInteger curIdx;
    private static long count;

    public ThreadedFilesWordCounter(int concurrencyFactor) {
        super(concurrencyFactor);
        this.threads = new ArrayList<Thread>();
        this.curIdx = new AtomicInteger(0);
        this.count = 0l;
    }

    @Override public void count(Map<String, String> files, String word, Callback callback){
        // TODO - implement this class using Thread objects; one Thread per {@link #concurrencyFactor}
        // HINT - break up {@linkplain fileContents} and distribute the work across the threads
        // HINT - do not create the Thread objects in this method
        String[] fileNames = files.keySet().toArray(new String[files.keySet().size()]);
        String[] fileContents = files.values().toArray(new String[files.values().size()]);
        setupThreads(fileNames, fileContents, word, callback);
        for (Thread thread : threads) {
            thread.start();
        }
    }

    @Override public void stop() {
        // TODO - stop the threads
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    private void setupThreads(String[] fileNames, String[] fileContents, String word, Callback callback) {
        for (int i = 0; i < getConcurrencyFactor(); i++) {
            Thread counterThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!Thread.currentThread().isInterrupted() && curIdx.get() <= fileContents.length - 1) {
                        String[] words = fileContents[curIdx.getAndIncrement()].split("\\s+");
                        int curFileCount = ThreadedFilesWordCounter.countEachFile(words, word);
                        callback.counted(fileNames[curIdx.get() - 1], curFileCount);
                    }
                }
            });
            threads.add(counterThread);
        }
    }

    public static int countEachFile(String[] words, String word) {
        int count = 0;
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(word)) {
                count++;
            }
        }
        return count;
    }

    public long getCount() {
        return count;
    }


}

package edu.nyu.cs9053.homework10;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: blangel
 * Date: 11/16/14
 * Time: 3:16 PM
 */
public class ThreadedWordCounter extends AbstractConcurrencyFactorProvider implements WordCounter {
    private final List<Thread> threads;
    private static AtomicInteger curIdx;
    private static long count;

    public ThreadedWordCounter(int concurrencyFactor) {
        super(concurrencyFactor);
        this.threads = new ArrayList<Thread>();
        this.curIdx = new AtomicInteger(0);
        this.count = 0l;
    }

    @Override public void count(String fileContents, String word, Callback callback) {
        // TODO - implement this class using Thread objects; one Thread per {@link #concurrencyFactor}
        // HINT - break up {@linkplain fileContents} and distribute the work across the threads
        // HINT - do not create the Thread objects in this method
        String[] words = fileContents.split("\\s+");
        System.out.println(Arrays.toString(words));
        setupCounters(words, word, callback);
        for (Thread thread : threads) {
            thread.start();
        }
    }

    @Override public void stop() {
        // TODO - stop the threads
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    private void setupCounters(String[] words, String word, Callback callback) {
        for (int i = 0; i < getConcurrencyFactor(); i++) {
            Thread counterThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!Thread.currentThread().isInterrupted() && curIdx.get() <= words.length - 1) {
                        ThreadedWordCounter.countEachWord(words, word);
                        callback.counted(count);
                    }
                }
            });
            threads.add(counterThread);
        }
    }

    public static void countEachWord(String[] words, String word) {
        if (words[curIdx.getAndIncrement()].equals(word)) {
            count++;
        }
    }

    public long getCount() {
        return count;
    }



}
